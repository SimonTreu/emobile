package de.dfki.gs.model.elements

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Point
import de.dfki.gs.domain.CarType
import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.TrackEdge
import de.dfki.gs.domain.TrackEdgeType
import de.dfki.gs.model.elements.results.CarAgentResult
import de.dfki.gs.service.RouteService
import de.dfki.gs.simulation.CarStatus
import de.dfki.gs.simulation.SchedulerStatus
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.geotools.graph.structure.basic.BasicEdge
import org.geotools.graph.structure.basic.BasicNode

import java.util.concurrent.ConcurrentMap

/**
 * Created by glenn on 01.04.14.
 *
 * this class models a car agent with its behaviour and results
 *
 * it includes
 *      - a (today's) plan.
 *          - routes
 *          - targets
 *      - a car:
 *          - start loading level for battery
 *          - max loading limit for battery
 *          - battery loading limit for recharging [ in kW ]
 *
 *      - results:
 *          - total time for recharging the car
 *          - total time for extra-routes to eFillingStation
 *
 *      - state machine for behaviour
 *
 */
class CarAgent extends Agent {

    private static def log = LogFactory.getLog( CarAgent.class )

    def ctx = ServletContextHolder.servletContext.getAttribute( GrailsApplicationAttributes.APPLICATION_CONTEXT )
    RouteService routeService = ctx.routeService


    ConcurrentMap<Long, EFillingStationAgent> fillingStationsMap;

    private RoutingPlan routingPlan

    private ModelCar modelCar

    private CarAgentResult carAgentResult

    private Map<String,Double> gasolineStationFillingAmount

    private List<GasolineStation> gasolineStations;


    long simulationId


    CarStatus carStatus
    /**
     * if running out of energy and close gasolineStation was found, here we go
     */
    GasolineStation gasolineStation
    Long gasolineStationIdUsed;

    double energyPortionToFill = 0
    int currentEdgeIndex
    List<BasicEdge> routeToGasolineStation
    List<BasicEdge> routeBackToTarget
    boolean routeSent = true
    int currentKmh = 0
    int DEFAULT_KMH;
    double currentKm
    double kmDriven = 0
    double lastStepKm = 0

    double kmToDrive = 0;

    long totalTimeNeeded = 0;
    long timeForLoading = 0;
    int fillingStationsVisited = 0;
    double energyLoaded = 0;
    double energyConsumed = 0;


    /**
     *  set the timeStampForNextActionAllowed with calculated value of (km/kmh and other issues like traffic, trafficlight)
     *
     * when the currentTimestamp of scheduler is "meeting" timeStampForNextActionAllowed, the action goes on
     * id initially set by -> simulationObject.startTime
     */
    long timeStampForNextActionAllowed = 0
    long lastTimeStamp

    TrackEdge currentEdge


    /**
     * factory method for a given RoutingPlan and a given ModelCar
     *
     * @param routingPlan
     * @param modelCar
     * @param gasolineStationFillingAMount
     * @return carAgent
     */
    public static CarAgent createCarAgent(
                    RoutingPlan routingPlan,
                    ModelCar modelCar,
                    Map<String,Double> gasolineStationFillingAMount,
                    ConcurrentMap<Long, EFillingStationAgent> fillingStationsMap,
                    List<GasolineStation> gasolineStations,
                    long simulationId,
                    int defaultKmh ) {

        CarAgent carAgent = new CarAgent()
        carAgent.routingPlan = routingPlan;
        carAgent.modelCar = modelCar;
        carAgent.gasolineStationFillingAmount = gasolineStationFillingAMount
        carAgent.fillingStationsMap = fillingStationsMap

        carAgent.gasolineStations = gasolineStations
        carAgent.currentEdgeIndex = 0;
        carAgent.DEFAULT_KMH = defaultKmh;
        carAgent.simulationId = simulationId;

        // initials
        carAgent.timeStampForNextActionAllowed = 0;
        carAgent.carStatus = CarStatus.DRIVING_FULL;

        double sumKm = 0;
        double secondsPlanned = 0;
        for ( TrackEdge trackEdge : routingPlan.trackEdges ) {
            // v = s/t <-> t = s / v
            int v = trackEdge.kmh
            double s = trackEdge.km
            secondsPlanned += ( s / v ) * 60 * 60
            sumKm += trackEdge.km
        }
        carAgent.kmToDrive = sumKm;

        carAgent.carAgentResult = new CarAgentResult(
                carType: modelCar.carType,
                plannedDistance: sumKm,
                timeForPlannedDistance: Math.ceil( secondsPlanned ),
                simulationId: simulationId
        );

        return carAgent;
    }







    def step( long currentTimeStamp ) {

        switch ( carStatus ) {

            case CarStatus.DRIVING_FULL:

                moveCar( currentTimeStamp )

                if ( currentEdgeIndex == routingPlan.trackEdges.size() ) {
                    totalTimeNeeded = currentTimeStamp;
                    carAgentResult.timeForRealDistance = currentTimeStamp
                    carStatus = CarStatus.MISSION_ACCOMBLISHED;

                    // log.error( "accomblished... ${carStatus}" )

                } else {

                    double batChargePercentage = ( modelCar.getCurrentEnergy() / modelCar.getMaxEnergy() ) * 100

                    if ( batChargePercentage >= modelCar.getRelativeSearchLimit()
                            && modelCar.getCurrentEnergy() >= modelCar.getAbsoluteSearchLimit() ) {

                        carStatus = CarStatus.DRIVING_FULL

                    } else {

                        carStatus = CarStatus.DRIVING_RUNNING_OUT

                    }

                }





                break;
            case CarStatus.DRIVING_RUNNING_OUT:

                if ( !gasolineStation ) {
                    searchFillingStation()
                }

                moveCar( currentTimeStamp )

                if ( currentEdgeIndex == routingPlan.trackEdges.size() ) {
                    totalTimeNeeded = currentTimeStamp;
                    carAgentResult.timeForRealDistance = currentTimeStamp
                    carStatus = CarStatus.MISSION_ACCOMBLISHED;

                } else {

                    double batChargePercentage = ( modelCar.getCurrentEnergy() / modelCar.getMaxEnergy() ) * 100

                    if ( batChargePercentage >= 0 && gasolineStation && onStation() ) {

                        // log.error( "fill with: ${ (gasolineStationFillingAmount.get( gasolineStation.type.toString() )) / (60*60)}" )

                        energyPortionToFill = ( gasolineStationFillingAmount.get( gasolineStation.type.toString() ) ) / ( 60 * 60);

                        carStatus = CarStatus.WAITING_FILLING

                    } else if ( batChargePercentage < 0 ) {

                        if ( fillingStationsMap != null && !fillingStationsMap.isEmpty() && gasolineStationIdUsed != null ) {
                            EFillingStationAgent fillingAgent = fillingStationsMap.get( gasolineStationIdUsed )
                            if ( fillingAgent ) {
                                fillingAgent.setFillingStationStatus( FillingStationStatus.FREE )
                            }
                        }

                        carStatus = CarStatus.WAITING_EMPTY

                        //cancel()

                    }

                }



                break;
            case CarStatus.WAITING_FILLING:

                energyLoaded += energyPortionToFill;
                timeForLoading++;

                fillCar( energyPortionToFill )

                if ( modelCar.getCurrentEnergy() >= modelCar.getMaxEnergy() ) {

                    modelCar.setCurrentEnergy( modelCar.getMaxEnergy() );
                    carStatus = CarStatus.DRIVING_FULL
                    fillingStationsVisited++;

                    // free filling station
                    EFillingStationAgent fillingAgent = fillingStationsMap.get( gasolineStationIdUsed )
                    fillingAgent.setFillingStationStatus( FillingStationStatus.FREE )

                    log.error( "${personalId} - gasolineStation: ${gasolineStationIdUsed}  now free again" )


                } else {

                    carStatus = CarStatus.WAITING_FILLING

                }

                break;
            case CarStatus.WAITING_EMPTY:

                // holdCar( currentTimeStamp )

                break;

            case CarStatus.MISSION_ACCOMBLISHED:

                // log.error( "mission accomblished.." )
                // cancel();
                break;

        }

    }





    /**
     * this method searches a closest filling station (closest to the car's position)
     * calculates a route to station and a route back to the next via_target or target on the original route
     *
     * @return
     */
    private void searchFillingStation() {

        long millis = System.currentTimeMillis()

        // getting 10 nearest stations*.id
        List<GasolineStation> nearestFillingStations = routeService.findNClosestGasolineStations(
                currentEdge.toLat,
                currentEdge.toLon,
                gasolineStations,
                10
        )

        // log.error( "found gasoline stations: ${nearestFillingStations}" )

        // checking from first to last, which one is free to use
        int free = 1;
        if ( nearestFillingStations != null && !nearestFillingStations.empty && fillingStationsMap != null && !fillingStationsMap.isEmpty() ) {

            for ( GasolineStation gasoline : nearestFillingStations ) {

                EFillingStationAgent fillingAgent = fillingStationsMap.get( gasoline.id )


                if ( fillingAgent.fillingStationStatus.equals( FillingStationStatus.FREE ) ) {

                    log.debug( "${personalId} - after ${free} trials: take gasolineStation ${gasoline} - now in use" )

                    fillingAgent.fillingStationStatus = FillingStationStatus.IN_USE;

                    gasolineStation = gasoline
                    gasolineStationIdUsed = gasolineStation.id

                    break;
                } else {

                    log.debug( "${personalId} - gasolineStation ${gasoline} is in use already, try another one.." )
                    free++;
                }

            }

            // 1.) try to get the node of the closest, free filling station

            // gasolineStation = routeService.findClosestGasolineStation( currentEdge.toLat, currentEdge.toLon, gasolineStations );



            // 2.) try to get a route to that node
            //      and build  track-edges-list
            if ( gasolineStation ) {

                List<BasicEdge> routeToEnergy = routeService.routeToTarget(
                        currentEdge.toLat,
                        currentEdge.toLon,
                        gasolineStation.lat,
                        gasolineStation.lon
                )

                if ( routeToEnergy.size() == 0 ) {
                    // skip
                    gasolineStation = null

                } else {

                    // log.error( "currentPos: ${currentEdge.toLat} ${currentEdge.toLon}" )
                    // log.error( "energy    : ${gasolineStation.lat} ${gasolineStation.lon}" )




                    // 3.) try to get a route to the next "via_target" or "target"
                    //      and build  track-edges-list
                    TrackEdge backEdge = null

                    int myIndex
                    for ( myIndex = currentEdgeIndex; myIndex < routingPlan.trackEdges.size(); myIndex++ ) {

                        TrackEdge edge = routingPlan.trackEdges.get( myIndex )

                        if ( backEdge == null
                                && ( edge.type.equals( TrackEdgeType.via_target.toString() ) || edge.type.equals( TrackEdgeType.target.toString() ) ) ) {
                            backEdge = edge
                        }
                    }

                    List<BasicEdge> routeToTarget = routeService.routeToTarget(
                            gasolineStation.lat,
                            gasolineStation.lon,
                            backEdge.toLat,
                            backEdge.toLon
                    )

                    double toSubstract = 0;
                    for (  int k = currentEdgeIndex ; k < myIndex; k++ ) {
                        toSubstract += routingPlan.trackEdges.get( k ).km
                    }


                    // 4.) get currentEdge++ and append the track-edgeList (directed to the filling station) into simulationObjec.edges

                    List<TrackEdge> trackEdgesToStation = routeService.convertToUnsavedTrackEdges( routeToEnergy )
                    List<TrackEdge> trackEdgesBack = routeService.convertToUnsavedTrackEdges( routeToTarget )

                    double lll = 0;
                    for ( TrackEdge eee : trackEdgesToStation ) {
                        lll += eee.km
                    }
                    log.error( "${personalId} - need ${lll} km to energy" )

                    // log.error( "back to: ${backEdge.toLat} ${backEdge.toLon}" )
                    lll = 0;
                    for ( TrackEdge be : trackEdgesBack ) {
                        lll += be.km
                    }
                    log.error( "${personalId} - need ${lll} km back to next target" )



                    double sumToDriveToGasAndBack = 0;
                    for ( TrackEdge trackEdge : trackEdgesToStation ) {
                        sumToDriveToGasAndBack += trackEdge.km;
                    }
                    for ( TrackEdge trackEdge : trackEdgesBack ) {
                        sumToDriveToGasAndBack += trackEdge.km;
                    }
                    kmToDrive += sumToDriveToGasAndBack;

                    kmToDrive = kmToDrive - toSubstract;

                    log.debug( "found routes in ${System.currentTimeMillis() - millis} ms, try to append to route plan" )

                    millis = System.currentTimeMillis()

                    routeToGasolineStation = routeToEnergy
                    routeBackToTarget = routeToTarget

                    // have to remove old route from "currentEdgeIndex+1" to the next target or viaTarget
                    if ( backEdge.type.toString().equals( TrackEdgeType.target.toString() ) || backEdge.type.toString().equals( TrackEdgeType.via_target.toString() )  ) {

                        // remove from currentEdgeIndex+1 to next viaTarget/target
                        int toRemovePosTo   = routingPlan.trackEdges.indexOf( backEdge )
                        int toRemovePosFrom = currentEdgeIndex + 1

                        for ( int i = toRemovePosTo; i >= toRemovePosFrom; i-- ) {
                            routingPlan.trackEdges.remove( i )
                        }

                        // repair type of last trackEdgesBack to via_target
                        trackEdgesBack.get( trackEdgesBack.size() - 1 ).type = TrackEdgeType.via_target

                        // shift in at position currentEdgeIndex+1 both routes tracksToStation and trackBackToRoute
                        routingPlan.trackEdges.addAll( currentEdgeIndex+1, trackEdgesToStation )
                        routingPlan.trackEdges.addAll( currentEdgeIndex+1+routeToEnergy.size(), trackEdgesBack )

                        // repair type of very last edge of track to target
                        routingPlan.trackEdges.get( routingPlan.trackEdges.size() - 1 ).type = TrackEdgeType.target

                    }

                    // TODO: clacluate new kmToDrive from updated track-list

                    log.debug( "successfully appended routes to routplan in ${System.currentTimeMillis() - millis} ms" )

                    log.debug( "size now is ${routingPlan.trackEdges.size()}" )


                    // has to be reset to false after refill !!!
                    routeSent = false

                }





            }


        }


    }


    def moveCar( long currentTimeStamp ) {

        if ( currentTimeStamp == timeStampForNextActionAllowed
                && currentEdgeIndex < routingPlan.trackEdges.size() ) {

            // take the currentEdge as edge and get Edge information: ( km and kmh )
            currentEdge = routingPlan.trackEdges.get( currentEdgeIndex )

            currentKmh = currentEdge.kmh

            // repair currentKmh if something stupid is set
            if ( currentKmh && currentKmh > 0 ) {  }
            else {
                currentKmh = DEFAULT_KMH
            }


            double km  = currentEdge.km

            currentKm = km

            kmDriven = kmDriven + currentKm;


            // remember old Timestamp
            lastTimeStamp = timeStampForNextActionAllowed

            // recalculate time to wait
            // one long step is 1 sec: so multiply by 60min * 60sec
            timeStampForNextActionAllowed += Math.ceil( 3600 * km / currentKmh )
            if ( timeStampForNextActionAllowed == lastTimeStamp ) {
                // calculated movement is obviously false and zero
                // do it by hand
                timeStampForNextActionAllowed++
            }

            /**
             * energy consumption model
             * in kWh
             * calulate new batlevel!!
             */
            double energyUsed = modelCar.energyUsage( km, currentKmh, 20, 1 )
            energyConsumed += energyUsed;

            modelCar.setCurrentEnergy( modelCar.getCurrentEnergy() - energyUsed )

            lastStepKm = km

            // set index to next Edge
            currentEdgeIndex++;

        } else {
            // do nothing
            // log.error( "nothing to be done at ${currentTimeStamp}, just have to wait or edges size exeeds: ${simulationObject.edges?.size()} .. ${simulationObject.currentEdgeIndex}" )
        }

    }

    private void fillCar( double energyPortion ) {

        lastTimeStamp = timeStampForNextActionAllowed
        timeStampForNextActionAllowed += 1

        modelCar.currentEnergy = modelCar.currentEnergy + energyPortion

        gasolineStation = null
    }

    boolean onStation() {

        return ( gasolineStation && (
                ( currentEdge.fromLat == gasolineStation.lat && currentEdge.fromLon == gasolineStation.lon ) ||
                        ( currentEdge.toLat == gasolineStation.lat && currentEdge.toLon == gasolineStation.lon ) ) )
    }


    def finish() {
        carAgentResult.realDistance = kmDriven;
        carAgentResult.energyLoaded = energyLoaded;
        carAgentResult.timeForLoading = timeForLoading;
        carAgentResult.timeForDetour = ( carAgentResult.timeForRealDistance - carAgentResult.timeForPlannedDistance ) - timeForLoading
        carAgentResult.fillingStationsVisited = fillingStationsVisited;
        carAgentResult.energyConsumed = energyConsumed;
        carAgentResult.carAgentStatus = carStatus.toString();
    }



    CarAgentResult getCarAgentResult() {
        return carAgentResult
    }

    RoutingPlan getRoutingPlan() {
        return routingPlan
    }

    ModelCar getModelCar() {
        return modelCar
    }

    def dto() {

        def m = [ : ]

        m.speed = getCurrentKmh()

        if ( currentEdge ) {

            m.street = currentEdge.streetName

            m.kmDriven = kmDriven
            m.lat = currentEdge.fromLat
            m.lon = currentEdge.fromLon

        }

        if ( gasolineStation && routeToGasolineStation && routeBackToTarget && !routeSent ) {

            def routeToEnergy = [];
            def routeToTarget = [];

            Point currentStartPoint
            Point currentTargetPoint

            for ( BasicEdge edge : routeToGasolineStation ) {

                currentStartPoint  = (Point) ((BasicNode) edge.nodeA).getObject();
                currentTargetPoint = (Point) ((BasicNode) edge.nodeB).getObject();

                routeToEnergy << [
                        fromX : currentStartPoint.x,
                        fromY : currentStartPoint.y,
                        toX : currentTargetPoint.x,
                        toY : currentTargetPoint.y
                ]

            }

            for ( BasicEdge edge : routeBackToTarget ) {

                currentStartPoint  = (Point) ((BasicNode) edge.nodeA).getObject();
                currentTargetPoint = (Point) ((BasicNode) edge.nodeB).getObject();

                routeToTarget << [
                        fromX : currentStartPoint.x,
                        fromY : currentStartPoint.y,
                        toX : currentTargetPoint.x,
                        toY : currentTargetPoint.y
                ]

            }

            m.routeToEnergy = routeToEnergy
            m.routeBackToTarget = routeToTarget

            routeSent = true
        }


        return m
    }
}

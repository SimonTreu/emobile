package de.dfki.gs.model.elements

import com.vividsolutions.jts.geom.Point
import de.dfki.gs.domain.simulation.TrackEdge
import de.dfki.gs.domain.utils.TrackEdgeType
import de.dfki.gs.model.elements.results.CarAgentResult
import de.dfki.gs.ms2.execution.FillingStationAgentSyncronizer
import de.dfki.gs.service.RouteService
import de.dfki.gs.simulation.CarStatus
import de.dfki.gs.utils.Calculater
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


    private RoutingPlan routingPlan

    private ModelCar modelCar

    private CarAgentResult carAgentResult

    private FillingStationAgentSyncronizer syncronizer


    long configurationId

    long routeId

    CarStatus carStatus
    /**
     * if running out of energy and close gasolineStation was found, here we go
     */
    EFillingStationAgent currentFillingStationToRouteFor;


    double energyPortionToFill = 0
    int currentEdgeIndex
    List<BasicEdge> routeToGasolineStation
    List<BasicEdge> routeBackToTarget
    boolean routeSent = true
    int currentKmh = 0
    int DEFAULT_KMH;
    double currentKm

    // the real driven distance in km
    double kmDriven = 0
    double lastStepKm = 0

    double kmToDrive = 0;

    long totalTimeNeeded = 0;
    long timeForLoading = 0;
    int fillingStationsVisited = 0;
    double energyLoaded = 0;
    double energyConsumed = 0;

    long fleetId;

    List<Long> unroutableCurrentStations = new ArrayList<Long>()


    /**
     *  set the timeStampForNextActionAllowed with calculated value of (km/kmh and other issues like traffic, trafficlight)
     *
     * when the currentTimestamp of scheduler is "meeting" timeStampForNextActionAllowed, the action goes on
     * id initially set by -> simulationObject.startTime
     */
    long timeStampForNextActionAllowed
    long lastTimeStamp

    TrackEdge currentEdge


    public static CarAgent createCarAgentWithFillingStations(
            RoutingPlan routingPlan,
            ModelCar modelCar,
            long configurationId,
            int defaultKmh,
            long startTime,
            Double plannedDistance,
            long routeId,
            FillingStationAgentSyncronizer syncronizer,
            long fleetId ) {


        CarAgent carAgent = new CarAgent()
        carAgent.routingPlan = routingPlan;
        carAgent.modelCar = modelCar;

        carAgent.fleetId = fleetId

        carAgent.syncronizer = syncronizer

        carAgent.currentEdgeIndex = 0;
        carAgent.DEFAULT_KMH = defaultKmh;
        carAgent.configurationId = configurationId;

        // initials
        carAgent.timeStampForNextActionAllowed = startTime;
        carAgent.carStatus = CarStatus.DRIVING_FULL;

        carAgent.startTime = startTime

        carAgent.routeId = routeId

        Double sumKm = 0;

        double secondsPlanned = 0;

        Long simRouteId = null

        for ( TrackEdge trackEdge : routingPlan.trackEdges ) {

            if ( simRouteId == null ) {
                simRouteId = trackEdge.routeId
            }

            // v = s/t <-> t = s / v
            int v = trackEdge.kmh
            Double s = trackEdge.km
            secondsPlanned += ( s / v ) * 60 * 60
            sumKm = sumKm + trackEdge.km
        }
        carAgent.kmToDrive = plannedDistance;

        carAgent.currentFillingStationToRouteFor = null

        log.error( "simRoute: ${simRouteId} has ${sumKm} planned km to drive in ${(secondsPlanned *  ( 1 / ( 60 * 60 ) ) )} h" )

        carAgent.carAgentResult = new CarAgentResult(
                carType:                modelCar.carType,
                plannedDistance:        plannedDistance,
                timeForPlannedDistance: Math.ceil( secondsPlanned ),
                configurationId:        configurationId,
                relativeSearchLimit:    modelCar.relativeSearchLimit,
                fleetId:                fleetId
        );

        return carAgent;
    }





    def step( long currentTimeStamp ) {

        if ( startTime > 0 && startTime == currentTime ) {
            log.debug( "${this.id} belated thread: time: ${currentTime}" )
        }


        switch ( carStatus ) {

            case CarStatus.DRIVING_FULL:

                moveCar( currentTimeStamp )

                /**
                 * if, after moving, the last edge is arrived, the ride is finished and we
                 * can set carStatus to MISSION_ACCOMBLISHED
                 *
                 */
                if ( currentEdgeIndex == routingPlan.trackEdges.size() ) {

                    totalTimeNeeded = currentTimeStamp - startTime;
                    carAgentResult.timeForRealDistance = currentTimeStamp - startTime
                    carStatus = CarStatus.MISSION_ACCOMBLISHED;

                    // log.error( "accomblished... ${carStatus}" )

                } else {

                    double batChargePercentage = ( modelCar.getCurrentEnergy() / modelCar.getMaxEnergy() ) * 100

                    if ( batChargePercentage >= ( modelCar.getRelativeSearchLimit() * 100 )
                            && modelCar.getCurrentEnergy() >= modelCar.getAbsoluteSearchLimit() ) {

                        carStatus = CarStatus.DRIVING_FULL

                    } else {

                        log.debug( "car ${personalId} running out of energy: ${batChargePercentage}% < ${modelCar.getRelativeSearchLimit()*100}% or ${modelCar.getCurrentEnergy()} < ${modelCar.getAbsoluteSearchLimit()}" )
                        carStatus = CarStatus.DRIVING_RUNNING_OUT

                    }

                }

                break;
            case CarStatus.DRIVING_RUNNING_OUT:

                if ( currentFillingStationToRouteFor == null ) {


                    // TODO: try to exclude unroutable filling stations for the current search process
                    configureRouteToFillingStationAndBack()

                }

                // debug
                /*
                if ( currentFillingStationToRouteFor != null ) {

                    log.error( "FS: ${currentFillingStationToRouteFor.lat} ${currentFillingStationToRouteFor.lon}" )

                    StringBuilder sb = new StringBuilder();
                    int i = currentEdgeIndex;
                    boolean ff = true;
                    while ( ff ) {
                        sb.append( " ( " )
                        sb.append( routingPlan.trackEdges.get( i ).fromLon ).append( " " ).append( routingPlan.trackEdges.get( i ).fromLat )
                        sb.append( " ) " )

                        if ( routingPlan.trackEdges.get( i ).type.toString().equals( TrackEdgeType.via_target.toString() ) ) {
                            ff = false
                        }
                        i++
                    }

                    log.error( "track to.. : ${sb.toString()}" )

                }
                */

                moveCar( currentTimeStamp )

                if ( currentEdgeIndex == routingPlan.trackEdges.size() ) {

                    totalTimeNeeded = currentTimeStamp - startTime;
                    carAgentResult.timeForRealDistance = currentTimeStamp - startTime
                    carStatus = CarStatus.MISSION_ACCOMBLISHED;

                } else {

                    double batChargePercentage = ( modelCar.getCurrentEnergy() / modelCar.getMaxEnergy() ) * 100
                    // TODO: CHECK IF THAT COMES TRUE ??
                    if ( currentFillingStationToRouteFor == null ) {
                        log.error( "shit! that should never happen! (fs is null)" )
                    }


                    if ( batChargePercentage >= 0 && currentFillingStationToRouteFor && onStation() ) {

                        energyPortionToFill = currentFillingStationToRouteFor.fillingPortion


                        double neededEnergy = ( modelCar.maxEnergy - modelCar.currentEnergy );
                        long timeForNeededEnergy = Math.ceil( neededEnergy / energyPortionToFill )

                        log.debug( "car: ${modelCar.carName} - neededEnergy: ${neededEnergy} / energyPortion: ${energyPortionToFill} =  timeForLoading: ${timeForNeededEnergy} -- prev sum TimeForloading: ${timeForLoading}" )

                        timeForLoading += timeForNeededEnergy

                        log.debug( "    now sum timeForLoading: ${timeForLoading}" )

                        timeStampForNextActionAllowed = currentTimeStamp + timeForNeededEnergy

                        carStatus = CarStatus.WAITING_FILLING

                    } else if ( batChargePercentage < 0 ) {

                        log.debug( "Soc: ${batChargePercentage}" )

                        if ( currentFillingStationToRouteFor ) {
                            FillingStationAgentSyncronizer.setFillingStationToFree( currentFillingStationToRouteFor )
                        }

                        carStatus = CarStatus.WAITING_EMPTY

                    }

                }



                break;
            case CarStatus.WAITING_FILLING:

                // wait for calculated time,
                if ( currentTimeStamp >= timeStampForNextActionAllowed ) {

                    energyLoaded += ( modelCar.maxEnergy - modelCar.currentEnergy )

                    modelCar.currentEnergy = modelCar.maxEnergy
                    timeStampForNextActionAllowed = currentTimeStamp + 1

                    log.error( "car ${personalId} (${modelCar.carName})  fully loaded SOC ( ${( modelCar.getCurrentEnergy() / modelCar.getMaxEnergy() ) * 100} )" )

                    carStatus = CarStatus.DRIVING_FULL
                    fillingStationsVisited++;

                    // free filling station
                    FillingStationAgentSyncronizer.setFillingStationToFree( currentFillingStationToRouteFor )

                    currentFillingStationToRouteFor = null
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
     * this method tries to find a close and free fillingStation
     * tries to find a route to and back
     */
    private void configureRouteToFillingStationAndBack() {

        log.error( "${personalId} tries to get a filling station" )

        long millis = System.currentTimeMillis()

        EFillingStationAgent eFillingStationAgent = syncronizer.reserveFreeFillingStationAgentInRadius(
                this.personalId,
                this.currentEdge.toLon,
                this.currentEdge.toLat,
                30,                      // [ km ]
                this.unroutableCurrentStations
        )

        long millis2 = System.currentTimeMillis()
        log.debug( "needed ${ millis2 - millis } ms to find a close and free filling station" )

        // only if we found a fillingStationAgent
        if ( eFillingStationAgent != null ) {

            List<BasicEdge> routeToEnergy = routeService.routeToTarget(
                    currentEdge.toLat,
                    currentEdge.toLon,
                    eFillingStationAgent.lon,
                    eFillingStationAgent.lat
            )

            if ( routeToEnergy.size() == 0 ) {

                // skip, no route found to efillingStationAgent

                unroutableCurrentStations.add( eFillingStationAgent.stationId )

                FillingStationAgentSyncronizer.updateFailedToRouteCount( eFillingStationAgent, personalId )
                FillingStationAgentSyncronizer.setFillingStationToFree( eFillingStationAgent )

            } else {

                // 3.) try to get a route to the next "via_target" or "target"
                //      and build  track-edges-list
                TrackEdge backEdge = null

                // myIndex is calculated and set to the index of the edge, which is either the next via_target or the target
                int myIndex
                for ( myIndex = currentEdgeIndex; myIndex < routingPlan.trackEdges.size(); myIndex++ ) {

                    TrackEdge edge = routingPlan.trackEdges.get( myIndex )

                    if ( backEdge == null
                            && ( edge.type.equals( TrackEdgeType.via_target.toString() ) || edge.type.equals( TrackEdgeType.target.toString() ) ) ) {
                        backEdge = edge
                        break
                    }
                }

                if ( backEdge == null ) {
                    backEdge = routingPlan.trackEdges.get( routingPlan.trackEdges.size() - 1 );
                }

                List<BasicEdge> routeToTarget = routeService.routeToTarget(
                        eFillingStationAgent.lon,
                        eFillingStationAgent.lat,
                        backEdge.toLat,
                        backEdge.toLon
                )

                if ( routeToTarget.size() == 0 ) {

                    unroutableCurrentStations.add( eFillingStationAgent.stationId )

                    // skip, no route found back to next target
                    FillingStationAgentSyncronizer.updateFailedToRouteCount( eFillingStationAgent, personalId )
                    FillingStationAgentSyncronizer.setFillingStationToFree( eFillingStationAgent )

                } else {

                    double toSubstract = 0;
                    // take out the km-values, which are not driven anymore due to new route to filling station
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
                    log.debug( "${personalId} - need ${lll} km to energy" )

                    // log.error( "back to: ${backEdge.toLat} ${backEdge.toLon}" )
                    lll = 0;
                    for ( TrackEdge be : trackEdgesBack ) {
                        lll += be.km
                    }
                    log.debug( "${personalId} - need ${lll} km back to next target" )



                    double sumToDriveToGasAndBack = 0;
                    for ( TrackEdge trackEdge : trackEdgesToStation ) {
                        sumToDriveToGasAndBack += trackEdge.km;
                    }
                    for ( TrackEdge trackEdge : trackEdgesBack ) {
                        sumToDriveToGasAndBack += trackEdge.km;
                    }

                    log.error( "${personalId} : previously planned: ${carAgentResult.plannedDistance}  have to substract: ${toSubstract} km and to add: ${sumToDriveToGasAndBack} km" )

                    kmToDrive += sumToDriveToGasAndBack;

                    kmToDrive = kmToDrive - toSubstract;


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
                        if ( trackEdgesBack.size() > 0 ) {
                            trackEdgesBack.get( trackEdgesBack.size() - 1 ).type = TrackEdgeType.via_target
                        }


                        // shift in at position currentEdgeIndex+1 both routes tracksToStation and trackBackToRoute
                        routingPlan.trackEdges.addAll( currentEdgeIndex+1, trackEdgesToStation )
                        routingPlan.trackEdges.addAll( currentEdgeIndex+1+routeToEnergy.size(), trackEdgesBack )

                        // repair type of very last edge of track to target
                        routingPlan.trackEdges.get( routingPlan.trackEdges.size() - 1 ).type = TrackEdgeType.target

                    }

                    // TODO: clacluate new kmToDrive from updated track-list

                    log.debug( "size now is ${routingPlan.trackEdges.size()}" )


                    // has to be reset to false after refill !!!
                    routeSent = false
                    currentFillingStationToRouteFor = eFillingStationAgent

                    unroutableCurrentStations = new ArrayList<Long>()
                }

            }

        }

        log.debug( "needed ${ System.currentTimeMillis() - millis } ms for planning routes to FS and back" )

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

            // we assume, that the currentEdge is already driven, although
            // it's not correct, -> after timeStampForNextActionAllowed it turns true
            kmDriven = kmDriven + currentKm;


            // remember old Timestamp
            lastTimeStamp = timeStampForNextActionAllowed

            // recalculate time to wait
            // one long step is 1 sec: so multiply by 60min * 60sec
            timeStampForNextActionAllowed += Math.round( 3600 * ( km / currentKmh ) )
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

    /**
     *  close enough is defined by 30 m
     *  after switching all double datatypes to float, no car reached a filling station anymore
     *
     * @return true if car is close enough to fillingStation ( 30m )
     */
    boolean onStation() {

        /*
        log.error( "currentEdge x: ${currentEdge.fromLat}   y: ${currentEdge.fromLon}" )
        log.error( "currentEdge x: ${currentEdge.toLat}   y: ${currentEdge.toLon}" )
        log.error( "fs is:      x: ${currentFillingStationToRouteFor.lon}   y: ${currentFillingStationToRouteFor.lat}" )
        */


        return ( currentFillingStationToRouteFor && (
                    (Calculater.haversine(
                        currentFillingStationToRouteFor.lat, currentFillingStationToRouteFor.lon,
                        currentEdge.fromLon, currentEdge.fromLat
                    ) < 0.03)||
                    (Calculater.haversine(
                            currentFillingStationToRouteFor.lat, currentFillingStationToRouteFor.lon,
                            currentEdge.toLon, currentEdge.toLat
                    ) < 0.03)
        ) )
        /*
        return ( currentFillingStationToRouteFor && (
                ( currentEdge.fromLat == currentFillingStationToRouteFor.lon && currentEdge.fromLon == currentFillingStationToRouteFor.lat ) ||
                        ( currentEdge.toLat == currentFillingStationToRouteFor.lon && currentEdge.toLon == currentFillingStationToRouteFor.lat ) ) )
        */
    }


    def finish() {

        /*
        double realDistanceCalc = 0;
        for ( TrackEdge edge : routingPlan.trackEdges ) {
            realDistanceCalc += edge.km
        }

        carAgentResult.realDistance = realDistanceCalc;
        log.error( "realDistanceCheck ( ${this.simulationRouteId} ): calc: ${realDistanceCalc}   driven: ${kmDriven}" )
        */

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

        if ( currentFillingStationToRouteFor && routeToGasolineStation && routeBackToTarget && !routeSent ) {

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

package de.dfki.gs.simulation

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Point
import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.simulation.TrackEdge
import de.dfki.gs.domain.utils.TrackEdgeType
import de.dfki.gs.service.RouteService
import de.dfki.gs.utils.LatLonPoint
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.geotools.graph.structure.basic.BasicEdge
import org.geotools.graph.structure.basic.BasicNode

/**
 * @author: glenn
 * @since: 06.11.13
 */
class SimulationThreadTask extends Thread {

    private static def log = LogFactory.getLog( SimulationThreadTask.class )

    def ctx = ServletContextHolder.servletContext.getAttribute( GrailsApplicationAttributes.APPLICATION_CONTEXT )

    long currentTime = 0 // in s
    boolean canceled = false


    Long interval

    SchedulerStatus status = SchedulerStatus.create


    @Override
    public void run() {

        long timeMillis
        long timeNeeded

        log.debug( "started running thread: ${this.id}" )

        status = SchedulerStatus.play

        while( status != SchedulerStatus.finished && !canceled ) {

            /**
             * this can pause the thread
             */
            synchronized ( this ) {
                while ( status == SchedulerStatus.pause ) {
                    try {
                        wait();
                    } catch (Exception e) {
                        log.error( "cannot wait status is ${status}", e )
                    }
                }
            }

            timeMillis = System.currentTimeMillis()

            // the hard goes here!
            step( currentTime )

            timeNeeded = System.currentTimeMillis() - timeMillis

            // log.error( "timeNeeded: ${timeNeeded} ms " )

            try {
                // related to 1000ms
                // just wait for reduced interval
                sleep( Math.max( 0, interval - timeNeeded ) )
            } catch ( Exception e ) {
                log.error( "failed to wait..", e )
            }

            currentTime++

        }



    }


    void setInterval( int scaleValue ) {

        interval = new Long( scaleValue )

    }


    // time to search a filling station , get from config
    double SEARCH_LIMIT
    // time to stop, energy is empty, get from config
    double EMPTY_LIMIT
    // maximum of battery charge , get from simulationObject
    double MAX_LIMIT
    // current charge of battery ( 5 edges driven, batLevel contains energy for 5 driven edges )
    double batLevel

    double fillingSlow

    double fillingFast

    double fillingMiddle

    double energyPortionToFill = 0

    // for state machine
    CarStatus carStatus

    List<GasolineStation> gasolineStations

    /**
     * followings should be set during construct:
     */
    /**
     * energy use:  x kWh/100km
     */
    double energyDrain

    /**
     * costs of 1 kWh in ct == 1/100 Euro
     */
    double energyPrice

    /**
     * accumulated from visited edges, skip
     */
    double energyUsed = 0

    /**
     * accumulated from visited edges
     */
    double costs = 0

    /**
     * to have the km from lastStep for calc energy usage
     */
    double lastStepKm = 0

    /**
     * if running out of energy and close gasolineStation was found, here we go
     */
    GasolineStation gasolineStation

    List<BasicEdge> routeToGasolineStation
    List<BasicEdge> routeBackToTarget
    boolean routeSent = true

    TrackEdge currentEdge

    /**
     * for spontaneous rerouting
     */
    RouteService routeService = ctx.routeService

    /**
     * for other disturbing facts
     */
    // TrafficService trafficService = ctx.trafficService
    // TrafficLightService trafficLightService = ctx.trafficLightService

    SimulationObject simulationObject

    /**
     * how fast is the car driving just in the moment
     */
    int currentKmh = 0
    double currentKm

    double kmDriven = 0

    int DEFAULT_KMH = 50

    /**
     *  set the timeStampForNextActionAllowed with calculated value of (km/kmh and other issues like traffic, trafficlight)
     *
     * when the currentTimestamp of scheduler is "meeting" timeStampForNextActionAllowed, the action goes on
     * id initially set by -> simulationObject.startTime
     */
    long timeStampForNextActionAllowed = 0
    long lastTimeStamp

    /**
     * depends on current Edge.
     *
     * @return the kmh feature of current Edge
     */
    Integer getCurrentKmh() {
        return currentKmh
    }

    /**
     * depends on currentTime
     * calculates the current driven km since start of driving
     *
     * @param currentTime
     * @return
     */
    Double getDrivenKm( long currentTime ) {
        long from = lastTimeStamp
        long to = timeStampForNextActionAllowed

        if ( to == from ) {
            return kmDriven
        }

        double timeDriven = ( Double ) ( Math.abs( currentTime - from ) / Math.abs( to - from ) )

        if ( timeDriven > 1 ) {
            return kmDriven + currentKm
        }

        return currentKm * timeDriven + kmDriven
    }

    /**
     * depends on current time
     * calculates the current position of car
     *
     * @param currentTime
     * @return
     */
    LatLonPoint getCurrentPosition( long currentTime ) {

        /**
         * calculate factor
         */
        long from = lastTimeStamp
        long to = timeStampForNextActionAllowed

        if ( !currentEdge ) {
            return null
        }

        if ( from == to ) {
            return new LatLonPoint( x: currentEdge.fromLat, y: currentEdge.fromLon )
        }

        double timeDriven = ( Double ) ( Math.abs( currentTime - from ) / Math.abs( to - from ) )

        if (  timeDriven > 1 ) {
            return new LatLonPoint( x: currentEdge.toLat, y: currentEdge.toLon )
        }

        LatLonPoint p = new LatLonPoint(
                x: currentEdge.fromLat + ( timeDriven * ( currentEdge.toLat - currentEdge.fromLat ) ),
                y: currentEdge.fromLon + ( timeDriven * ( currentEdge.toLon - currentEdge.fromLon ) )
        )
        return p
    }



    def dto() {



        long from = lastTimeStamp
        long to = timeStampForNextActionAllowed

        double timeDriven = 0
        if ( to != from  ) {

            timeDriven = ( Double ) ( Math.abs( currentTime - from ) / Math.abs( to - from ) )
            //log.error( "\t\ttimeDriven: ${timeDriven}" )

        }

        def m = [ : ]

        m.speed = getCurrentKmh()

        if ( currentEdge ) {

            m.street = currentEdge.streetName

            if ( to == from || carStatus == CarStatus.WAITING_EMPTY ) {
                m.kmDriven = Math.round( kmDriven * 100) / 100
                m.lat = currentEdge.fromLat
                m.lon = currentEdge.fromLon

                // total amount of energy used
                def currentEnergyUsed = energyUsed

                m.currentEnergyUsed = Math.round(  currentEnergyUsed / 100 ) * 100

                // here we want to have the rounded percentage of battery charge:
                def batteryLevel = Math.round(  ( batLevel / simulationObject.maxEnergy ) * 100 * 10 ) / 10
                m.batteryLevel = batteryLevel>=0?batteryLevel:0

                // total amount of money we need ( depends on energyUsed )
                m.currentPrice = Math.round( currentEnergyUsed * energyPrice ) / 100

            } else if ( timeDriven > 1 ) {

                // just assume timeDriven is 1
                m.kmDriven = Math.round( ( kmDriven + currentKm ) *100 ) /100
                m.lat = currentEdge.toLat
                m.lon = currentEdge.toLon

                // total amount of energy used
                def currentEnergyUsed = ( energyUsed + ( currentEdge.km * (energyDrain/100) ) )
                m.currenEnergyUsed = Math.round( currentEnergyUsed * 100 ) / 100

                // here we want to have the percentage of battery charge:
                def tripEnergyUsed = ( batLevel - ( currentEdge.km * ( energyDrain/100 ) ) )
                def batteryLevel = Math.round ( ( ( tripEnergyUsed) / simulationObject.maxEnergy ) * 100 * 10 ) /10
                m.batteryLevel = batteryLevel>=0?batteryLevel:0

                m.currentPrice = Math.round( currentEnergyUsed * energyPrice ) / 100

            } else {
                m.kmDriven = Math.round(  (currentKm * timeDriven + kmDriven ) *100  ) /100
                m.lat = currentEdge.fromLat + ( timeDriven * ( currentEdge.toLat - currentEdge.fromLat ) )
                m.lon = currentEdge.fromLon + ( timeDriven * ( currentEdge.toLon - currentEdge.fromLon ) )

                // def currentEnergyUsed = ( energyUsed +   timeDriven * (   currentEdge.km * (energyDrain/100) ) )

                // total amount of energy used
                def currentEnergyUsed = energyUsed + ( (( timeDriven * currentEdge.km )/100) * energyDrain )

                m.currentEnergyUsed = Math.round( currentEnergyUsed * 100 ) / 100

                // here we want to have the percentage of battery charge:
                def tripEnergyUsed = batLevel - ( (( timeDriven * currentEdge.km )/100) * energyDrain )

                def batteryLevel = Math.round ( ( ( tripEnergyUsed )  / simulationObject.maxEnergy ) * 100 * 10 ) /10
                m.batteryLevel = batteryLevel>=0?batteryLevel:0

                m.currentPrice = Math.round( currentEnergyUsed * energyPrice ) / 100

            }
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


    def step( long currentTimeStamp ) {

        // log.error( "status: ${carStatus}" )

        switch ( carStatus ) {

            case CarStatus.DRIVING_FULL:

                moveCar( currentTimeStamp )

                def batChargePercentage = ( batLevel / simulationObject.maxEnergy ) * 100

                if ( batChargePercentage >= SEARCH_LIMIT ) {
                    carStatus = CarStatus.DRIVING_FULL
                } else {
                    carStatus = CarStatus.DRIVING_RUNNING_OUT
                }

                break;
            case CarStatus.DRIVING_RUNNING_OUT:

                if ( !gasolineStation ) {
                    searchFillingStation()
                }

                moveCar( currentTimeStamp )

                def batChargePercentage = ( batLevel / simulationObject.maxEnergy ) * 100

                if ( batChargePercentage >= EMPTY_LIMIT && gasolineStation && onStation() ) {


                    switch ( gasolineStation.type.toString() ) {

                        case GasolineStationType.fast.toString():
                            energyPortionToFill = fillingFast
                            log.debug( "uses fillingStation with type ${gasolineStation.type} : ${energyPortionToFill}" )
                            break;
                        case GasolineStationType.middle.toString():
                            energyPortionToFill = fillingMiddle
                            log.debug( "uses fillingStation with type ${gasolineStation.type} : ${energyPortionToFill}" )
                            break;
                        case GasolineStationType.slow.toString():
                            energyPortionToFill = fillingSlow
                            log.debug( "uses fillingStation with type ${gasolineStation.type} : ${energyPortionToFill}" )
                            break;
                        default:
                            energyPortionToFill = 0.001
                            log.debug( "uses fillingStation with type ${gasolineStation.type} : ${energyPortionToFill}" )
                            break

                    }


                    carStatus = CarStatus.WAITING_FILLING
                } else if ( batChargePercentage < EMPTY_LIMIT ) {
                    carStatus = CarStatus.WAITING_EMPTY
                }

                break;
            case CarStatus.WAITING_FILLING:

                fillCar( energyPortionToFill )

                if ( batLevel >= MAX_LIMIT ) {
                    batLevel = MAX_LIMIT
                    carStatus = CarStatus.DRIVING_FULL
                } else {
                    carStatus = CarStatus.WAITING_FILLING
                }

                break;
            case CarStatus.WAITING_EMPTY:

                // holdCar( currentTimeStamp )

                break;

        }

    }

    /**
     * this method searches a closest filling station (closest to the car's position)
     * calculates a route to station and a route back to the next via_target or target on the original route
     *
     * @return
     */
    def searchFillingStation() {

        long millis = System.currentTimeMillis()

        // 1.) try to get the node of the closest, free filling station
        def distance = 100000000;
        GasolineStation targetGasolineStation = null
        for ( GasolineStation gasolineStation : gasolineStations ) {

            def latToCheck = currentEdge.toLat
            def lonToCheck = currentEdge.toLon

            def currentDistance = Math.pow( latToCheck - gasolineStation.lat, 2 ) + Math.pow( lonToCheck - gasolineStation.lon, 2 )

            if ( currentDistance < distance ) {

                // found a closer station
                distance = currentDistance
                targetGasolineStation = gasolineStation
            }

        }

        gasolineStation = targetGasolineStation

        log.debug( "found a station in ${System.currentTimeMillis() - millis} ms, try to get a route and a route back" )

        millis = System.currentTimeMillis()

        // 2.) try to get a route to that node
        //      and build  track-edges-list
        List<BasicEdge> routeToEnergy = routeService.calculatePath(
                new Coordinate( currentEdge.toLon, currentEdge.toLat ),
                new Coordinate( targetGasolineStation.lon, targetGasolineStation.lat )
        )
        routeToEnergy = routeService.repairEdges( routeToEnergy )

        // 3.) try to get a route to the next "via_target" or "target"
        //      and build  track-edges-list
        TrackEdge backEdge = null
        int currentEdgeIndex = simulationObject.currentEdgeIndex

        for ( int i = currentEdgeIndex; i < simulationObject.edges.size(); i++ ) {
            TrackEdge edge = simulationObject.edges.get( i )

            // log.error( "edge: ${edge.type} ${i}" )

            if ( backEdge == null && ( edge.type.equals( TrackEdgeType.via_target.toString() ) || edge.type.equals( TrackEdgeType.target.toString() ) ) ) {
                backEdge = edge
            }
        }
        List<BasicEdge> routeToTarget = routeService.calculatePath(
                new Coordinate( targetGasolineStation.lon, targetGasolineStation.lat ),
                new Coordinate( backEdge.toLon, backEdge.toLat )
        )
        routeToTarget = routeService.repairEdges( routeToTarget )


        // 4.) get currentEdge++ and append the track-edgeList (directed to the filling station) into simulationObjec.edges
        List<TrackEdge> trackEdgesToStation = routeService.convertToUnsavedTrackEdges( routeToEnergy )
        List<TrackEdge> trackEdgesBack = routeService.convertToUnsavedTrackEdges( routeToTarget )

        log.debug( "found routes in ${System.currentTimeMillis() - millis} ms, try to append to route plan" )

        millis = System.currentTimeMillis()

        routeToGasolineStation = routeToEnergy
        routeBackToTarget = routeToTarget


        // have to remove old route from "currentEdgeIndex+1" to the next target or viaTarget
        if ( backEdge.type.toString().equals( TrackEdgeType.target.toString() ) || backEdge.type.toString().equals( TrackEdgeType.via_target.toString() )  ) {

            // remove from currentEdgeIndex+1 to next viaTarget/target
            int toRemovePosTo   = simulationObject.edges.indexOf( backEdge )
            int toRemovePosFrom = currentEdgeIndex + 1

            for ( int i = toRemovePosTo; i >= toRemovePosFrom; i-- ) {
                simulationObject.edges.remove( i )
            }

            // repair type of last trackEdgesBack to via_target
            trackEdgesBack.get( trackEdgesBack.size() - 1 ).type = TrackEdgeType.via_target

            // shift in at position currentEdgeIndex+1 both routes tracksToStation and trackBackToRoute
            simulationObject.edges.addAll( currentEdgeIndex+1, trackEdgesToStation )
            simulationObject.edges.addAll( currentEdgeIndex+1+routeToEnergy.size(), trackEdgesBack )

            // repair type of very last edge of track to target
            simulationObject.edges.get( simulationObject.edges.size() - 1 ).type = TrackEdgeType.target

        }

        log.debug( "successfully appended routes to routplan in ${System.currentTimeMillis() - millis} ms" )

        log.debug( "size now is ${simulationObject.edges.size()}" )

        /*
        for ( int j = simulationObject.currentEdgeIndex; j < simulationObject.edges.size(); j++ ) {
            log.error( "idx: ${j}  edge: ${simulationObject.edges.get( j ).type}  id: ${simulationObject.edges.get( j ).id}" )
        }
        */


        // has to be reset to false after refill !!!
        routeSent = false

    }

    def moveCar( long currentTimeStamp ) {

        int currentEdgeIndex = simulationObject.currentEdgeIndex

        if ( currentTimeStamp == timeStampForNextActionAllowed
                && currentEdgeIndex < simulationObject.edges?.size() ) {

            // take the currentEdge as edge and get Edge information: ( km and kmh )
            currentEdge = simulationObject.edges.get( simulationObject.currentEdgeIndex )

            currentKmh = currentEdge.kmh

            // repair currentKmh if something stupid is set
            if ( currentKmh && currentKmh > 0 ) {  }
            else currentKmh = DEFAULT_KMH

            // TODO: think about! maybe it's too early!
            kmDriven = kmDriven + currentKm;


            double km  = currentEdge.km

            currentKm = km

            // remember old Timestamp
            lastTimeStamp = timeStampForNextActionAllowed
            // recalculate time to wait
            timeStampForNextActionAllowed += Math.ceil( 3600 * km / currentKmh )
            if ( timeStampForNextActionAllowed == lastTimeStamp ) {
                // calculated movement is obviously false and zero
                // do it by hand
                timeStampForNextActionAllowed++
            }

            /**
             * energy usage model
             * in kWh
             */
            batLevel = batLevel - ( ( lastStepKm/100 ) * energyDrain )

            // accumulated consumed energy:
            energyUsed = energyUsed + ( (lastStepKm/100) * energyDrain )

            lastStepKm = km

            // set index to next Edge
            simulationObject.currentEdgeIndex = currentEdgeIndex + 1

        } else {
            // do nothing
            // log.error( "nothing to be done at ${currentTimeStamp}, just have to wait or edges size exeeds: ${simulationObject.edges?.size()} .. ${simulationObject.currentEdgeIndex}" )
        }

    }

    boolean onStation() {

        return ( gasolineStation && (
        ( currentEdge.fromLat == gasolineStation.lat && currentEdge.fromLon == gasolineStation.lon ) ||
                ( currentEdge.toLat == gasolineStation.lat && currentEdge.toLon == gasolineStation.lon ) ) )
    }


    def fillCar( double energyPortion ) {

        lastTimeStamp = timeStampForNextActionAllowed
        timeStampForNextActionAllowed += 1
        batLevel = batLevel + energyPortion

        gasolineStation = null
    }


    public void proceed() {

        status = SchedulerStatus.play

        synchronized( this ) {
            this.notify();
        }

    }

    public void cancel() {

        /**
         * first, free scheduler from pause status, to let while condition to be checked
         */
        if ( status == SchedulerStatus.pause ) {
            proceed()
        }

        canceled = true
        log.debug( "try to cancel" )

    }

    public void pause() {

        log.debug( "try to pause scheduler at ${currentTime}" )
        status = SchedulerStatus.pause

    }

}

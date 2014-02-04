package de.dfki.gs.service.simulation

import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.domain.Simulation
import de.dfki.gs.domain.SimulationRoute
import de.dfki.gs.domain.TrackEdge
import de.dfki.gs.simulation.CarStatus
import de.dfki.gs.simulation.Scheduler
import de.dfki.gs.simulation.SchedulerStatus
import de.dfki.gs.simulation.SimulationObject
import de.dfki.gs.simulation.SimulationTask
import de.dfki.gs.utils.LatLonPoint

import javax.annotation.PostConstruct

class SimulationFrameworkService {

    Scheduler scheduler
    def grailsApplication

    boolean initialized = false


    /*
    @PostConstruct
    void init() {
        scheduler = new Scheduler( status: SchedulerStatus.create )
    }
    */

    def initScheduler( long simulationId, initMap ) throws Exception {

        if ( scheduler == null ) {
            scheduler = new Scheduler( status: SchedulerStatus.create )
        }

        /**
         * take the desired simulation from db
         */
        Simulation simulation = Simulation.get( simulationId )

        List<GasolineStation> gasolineStations = GasolineStation.findAllBySimulation( simulation )

        /**
         * grab all SimulationRoutes from simulation
         */
        List<SimulationRoute> simulationRoutes = SimulationRoute.findAllBySimulation( simulation )



        HashMap<Long,SimulationTask> tasksMap = new HashMap<Long,SimulationTask>()
        for ( SimulationRoute simulationRoute : simulationRoutes ) {

            List<TrackEdge> edges = new ArrayList<TrackEdge>()
            List<TrackEdge> trackEdges = simulationRoute.track.edges

            double rememberToLat = 0;
            double rememberToLon = 0;

            trackEdges.each { TrackEdge trackEdge ->
                edges.add( TrackEdge.get( trackEdge.id ) )

                TrackEdge ddd = TrackEdge.get( trackEdge.id )

                if ( rememberToLat == 0 ) rememberToLat = ddd.fromLat
                if ( rememberToLon == 0 ) rememberToLon = ddd.fromLon


                if ( rememberToLat != ddd.fromLat || rememberToLon != ddd.fromLon ) {

                    // log.error( "wrong: old to: ( ${rememberToLat} x ${rememberToLon} )  new from ( ${ddd.fromLat} x ${ddd.fromLon} )  new to  ( ${ddd.toLat} x ${ddd.toLon} ) " )

                }


                // reset
                rememberToLon = ddd.toLon
                rememberToLat = ddd.toLat
            }


            SimulationObject simulationObject = new SimulationObject(
                    carType:        simulationRoute.carType,
                    edges:          edges,
                    currentEnergy:  simulationRoute.initialEnergy,
                    initialEnergy:  simulationRoute.initialEnergy,
                    maxEnergy:      simulationRoute.maxEnergy,
                    searchLimit:    grailsApplication.config.energyConfig.batteryLevelLimitToFill,
                    emptyLimit:     grailsApplication.config.energyConfig.batteryLevelLimitToStop
            )

            SimulationTask task = new SimulationTask(
                    timeStampForNextActionAllowed: 0,
                    simulationObject:                        simulationObject,
                    energyDrain:                             grailsApplication.config.energyConfig.batteryDrain,
                    energyPrice:                             grailsApplication.config.energyConfig.energyPrice,
                    gasolineStations:                        gasolineStations,
                    SEARCH_LIMIT:                            simulationObject.searchLimit,
                    MAX_LIMIT:                               simulationObject.maxEnergy,
                    EMPTY_LIMIT:                             simulationObject.emptyLimit,
                    batLevel:                                simulationObject.initialEnergy,
                    carStatus:                               CarStatus.DRIVING_FULL,
                    fillingSlow:                             grailsApplication.config.energyConfig.fillingSlow,
                    fillingFast:                             grailsApplication.config.energyConfig.fillingFast,
                    fillingMiddle:                           grailsApplication.config.energyConfig.fillingMiddle
            )

            tasksMap.put( simulationRoute.id, task )

        }

        /**
         * this automatically sets status of scheduler to "init"
         */
        scheduler.init( tasksMap, 20000, 50 )

    }


    def setScale( Integer scaleValue ) {


        if ( !scheduler ) {
            log.error( "scheduler is null, cannot scale anything" )
            return
        }

        if ( scheduler.status == SchedulerStatus.play || scheduler.status == SchedulerStatus.pause ) {
            scheduler.setInterval( scaleValue )
        }

    }

    /**
     * if scheduler.status is "init" , thread gets run
     *
     * @return
     */
    def runSimulation() {

        if ( !scheduler ) {
            log.error( "scheduler is null, nothing to run or to proceed here" )
            return
        }

        if ( scheduler.status == SchedulerStatus.init ) {
            boolean started = false
            int trials = 0

            while ( !started && trials++ <= 5 ) {

                try {
                    scheduler.start()
                    started = true;
                }  catch ( Exception e ) {
                    log.error( "${trials}. trial: failed to start scheduler", e )
                }

            }

        } else {

            // in all other states, we cant go for running or proceeding
            log.error( "scheduler has status: ${scheduler.status} nothing to run here"  )

        }


    }

    /**
     * only if scheduler.status is "play", we can switch to "pause"
     */
    def pauseSimulation() {

        if ( !scheduler ) {
            log.error( "scheduler is null, nothing to pause here" )
            return
        }

        if ( scheduler.status == SchedulerStatus.play ) {

            scheduler.pause()

        } else {
            // in all other states, we cant go for pausing
            log.error( "scheduler has status: ${scheduler.status} nothing to pause here"  )
        }

    }

    /**
     * only if scheduler.status is "pause", we can proceed here
     */
    def proceedSimulation() {

        if ( !scheduler ) {
            log.error( "scheduler is null, nothing to proceed here" )
            return
        }

        if ( scheduler.status == SchedulerStatus.pause ) {

            scheduler.proceed()

        } else {
            // in all other states, we cant go for proceeding
            log.error( "scheduler has status: ${scheduler.status} nothing to proceed here"  )
        }

    }

    /**
     * stops scheduler
     *
     * @return
     */
    def stopSimulation( long simulationId ) {

        if ( scheduler ) {
            scheduler.cancel()
        }

        scheduler = null

        // initScheduler( simulationId, null )

    }


    def getTime() {

        def time = ""

        if ( scheduler ) {

            time = scheduler.getTime()

        }

        return time
    }

    def getCarInfos( long simulationRouteId ) {

        def m = [ : ]

        if ( scheduler ) {

            m = scheduler.getAllCarInfos( simulationRouteId )

            /*
            m.speed = scheduler.getCurrentSpeed( simulationRouteId )

            m.kmDriven = ( Math.round( scheduler.getDrivenKm( simulationRouteId )  * 100 ) ) / 100

            LatLonPoint point = scheduler.getCurrentPosition( simulationRouteId )
            m.lat = point.x
            m.lon = point.y
            */
        }

        return m
    }

    LatLonPoint getPositionForSimulationRoute( long simulationRouteId ) {

        if ( scheduler ) {
            return scheduler.getCurrentPosition( simulationRouteId )
        }

        return null
    }

    /**
     *
     *
     * @return the status of the Scheduler
     */
    SchedulerStatus getSchedulerStatus() {

        if ( scheduler ) {
            return scheduler.status
        } else {
            return SchedulerStatus.isNull
        }

    }
}

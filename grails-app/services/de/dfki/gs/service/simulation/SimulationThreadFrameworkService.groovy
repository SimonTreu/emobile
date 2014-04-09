package de.dfki.gs.service.simulation

import de.dfki.gs.domain.CarType
import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.Simulation
import de.dfki.gs.domain.SimulationRoute
import de.dfki.gs.domain.TrackEdge
import de.dfki.gs.model.elements.CarAgent
import de.dfki.gs.model.elements.EnergyConsumptionModel
import de.dfki.gs.model.elements.ModelCar
import de.dfki.gs.model.elements.RoutingPlan
import de.dfki.gs.simulation.CarStatus
import de.dfki.gs.simulation.SchedulerStatus
import de.dfki.gs.simulation.SimulationObject

import de.dfki.gs.simulation.SimulationThreadTask


import grails.transaction.Transactional

import java.util.concurrent.ConcurrentHashMap

@Transactional
class SimulationThreadFrameworkService {


    def grailsApplication

    boolean initialized = false

    ConcurrentHashMap<String, Map<Long, SimulationThreadTask>> threadsForSession = new ConcurrentHashMap<String, Map<Long, SimulationThreadTask>>()
    ConcurrentHashMap<String, SchedulerStatus> statusForSession = new ConcurrentHashMap<String, SchedulerStatus>()

    ConcurrentHashMap<String, Map<Long, CarAgent>> carAgentsForSession = new ConcurrentHashMap<String, Map<Long, CarAgent>>()



    // Map<Long, SimulationThreadTask> threadMap

    SchedulerStatus status


    def init2( long simulationId, initMap, String sessionId ) throws Exception {

        log.debug( "try to init simulation framework with simulation ${simulationId} for session: ${sessionId}" )

        /**
         * take the desired simulation from db
         */
        Simulation simulation = Simulation.get( simulationId )

        List<GasolineStation> gasolineStations = GasolineStation.findAllBySimulation( simulation )
        Map<String, Double> gasolineStationFillingPortions = new HashMap<String, Double>()
        gasolineStationFillingPortions.put( GasolineStationType.AC_2_3KW.toString(), 2.3 );
        gasolineStationFillingPortions.put( GasolineStationType.AC_3_7KW.toString(), 3.7 );
        gasolineStationFillingPortions.put( GasolineStationType.AC_11KW.toString(), 11 );
        gasolineStationFillingPortions.put( GasolineStationType.AC_22KW.toString(), 22 );
        gasolineStationFillingPortions.put( GasolineStationType.AC_43KW.toString(), 43 );
        gasolineStationFillingPortions.put( GasolineStationType.DC_50KW.toString(), 50 );


        /**
         * grab all SimulationRoutes from simulation
         */
        // List<SimulationRoute> simulationRoutes = SimulationRoute.findAllBySimulation( simulation )

        // here we store all threads
        HashMap<Long, CarAgent> threadMap = new HashMap<Long, CarAgent>()

        StringBuilder sb = new StringBuilder()

        for ( SimulationRoute simulationRoute : simulation.simulationRoutes ) {

            if ( simulationRoute ) {

                List<TrackEdge> edges = new ArrayList<TrackEdge>()
                for ( TrackEdge trackEdge : simulationRoute.track.edges ) {
                    if ( trackEdge ) {
                        edges.add( trackEdge )
                    }
                }

                RoutingPlan routingPlan = RoutingPlan.createRoutingPlan( edges );

                CarType carType = simulationRoute.carType

                // TODO: create with type! and energy küsdk
                ModelCar modelCar = ModelCar.createModelCar( new EnergyConsumptionModel(), carType, 5, 20 );

                CarAgent carAgent = CarAgent.createCarAgent(
                        routingPlan,
                        modelCar,
                        gasolineStationFillingPortions,
                        gasolineStations,
                        35
                )

                threadMap.put( simulationRoute.id, carAgent )

            }

        }
        log.debug( "added ${threadMap.size()} tasks to simulation: ${sb.toString()}" )


        carAgentsForSession.put( sessionId, threadMap )
        statusForSession.put( sessionId, SchedulerStatus.init )

        log.debug( "simulation framework is initiated for session: ${sessionId}" )

    }


    def init( long simulationId, initMap, String sessionId ) throws Exception {

        log.debug( "try to init simulation framework with simulation ${simulationId} for session: ${sessionId}" )

        /**
         * take the desired simulation from db
         */
        Simulation simulation = Simulation.get( simulationId )

        List<GasolineStation> gasolineStations = GasolineStation.findAllBySimulation( simulation )

        /**
         * grab all SimulationRoutes from simulation
         */
        // List<SimulationRoute> simulationRoutes = SimulationRoute.findAllBySimulation( simulation )

        // here we store all threads
        HashMap<Long, SimulationThreadTask> threadMap = new HashMap<Long, SimulationThreadTask>()

        StringBuilder sb = new StringBuilder()

        for ( SimulationRoute simulationRoute : simulation.simulationRoutes ) {

            if ( simulationRoute ) {

                List<TrackEdge> edges = new ArrayList<TrackEdge>()
                for ( TrackEdge trackEdge : simulationRoute.track.edges ) {
                    if ( trackEdge ) {
                        edges.add( trackEdge )
                    }
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

                SimulationThreadTask task = new SimulationThreadTask(
                        timeStampForNextActionAllowed:           0,
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
                        fillingMiddle:                           grailsApplication.config.energyConfig.fillingMiddle,
                        interval:                                30
                )

                sb.append( "${simulationRoute.id}, " )



                threadMap.put( simulationRoute.id, task )
            }

        }
        log.debug( "added ${threadMap.size()} tasks to simulation: ${sb.toString()}" )


        threadsForSession.put( sessionId, threadMap )
        statusForSession.put( sessionId, SchedulerStatus.init )

        log.debug( "simulation framework is initiated for session: ${sessionId}" )

    }

    def setScale2( Integer scaleValue, String sessionId ) {

        Map<Long, CarAgent> threadMap = carAgentsForSession.get( sessionId )

        if ( threadMap ) {

            for ( CarAgent task : threadMap.values() ) {

                if ( task.status == SchedulerStatus.play || task.status == SchedulerStatus.pause ) {
                    task.setInterval( scaleValue )
                }
            }

        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }


    }

    def setScale( Integer scaleValue, String sessionId ) {

        Map<Long, SimulationThreadTask> threadMap = threadsForSession.get( sessionId )

        if ( threadMap ) {

            for ( SimulationThreadTask task : threadMap.values() ) {

                if ( task.status == SchedulerStatus.play || task.status == SchedulerStatus.pause ) {
                    task.setInterval( scaleValue )
                }
            }

        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }


    }


    def runSimulation2( String sessionId ) {

        Map<Long, CarAgent> threadMap = carAgentsForSession.get( sessionId )

        if ( threadMap ) {
            /**
             * stepping through all simulationTasks  and say start!
             */
            for ( CarAgent task : threadMap.values() ) {
                task.start()
            }

            statusForSession.put( sessionId, SchedulerStatus.play )

            log.debug( "simulation started for session: ${sessionId}" )

        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }



    }


    /**
     * if scheduler.status is "init" , thread gets run
     *
     * @return
     */
    def runSimulation( String sessionId ) {

        Map<Long, SimulationThreadTask> threadMap = threadsForSession.get( sessionId )

        if ( threadMap ) {
            /**
             * stepping through all simulationTasks  and say start!
             */
            for ( SimulationThreadTask task : threadMap.values() ) {
                task.start()
            }

            statusForSession.put( sessionId, SchedulerStatus.play )

            log.debug( "simulation started for session: ${sessionId}" )

        } else {
           log.error( "no threads found for session: ${sessionId}" )
        }



    }


    def pauseSimulation2( String sessionId ) {

        Map<Long, CarAgent> threadMap = carAgentsForSession.get( sessionId )

        if ( threadMap ) {

            for (  CarAgent task : threadMap.values()  ) {
                task.pause()
            }

            statusForSession.put( sessionId, SchedulerStatus.pause )

            log.debug( "simulation paused for session: ${sessionId}" )

        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }



    }

    /**
     *
     */
    def pauseSimulation( String sessionId ) {

        Map<Long, SimulationThreadTask> threadMap = threadsForSession.get( sessionId )

        if ( threadMap ) {

            for (  SimulationThreadTask task : threadMap.values()  ) {
                task.pause()
            }

            statusForSession.put( sessionId, SchedulerStatus.pause )

            log.debug( "simulation paused for session: ${sessionId}" )

        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }



    }


    def proceedSimulation2( String sessionId ) {

        Map<Long, CarAgent> threadMap = carAgentsForSession.get( sessionId )

        if ( threadMap ) {

            for ( CarAgent task : threadMap.values()  ) {
                task.proceed()
            }

            statusForSession.put( sessionId, SchedulerStatus.play )

            log.debug( "simulation proceeded for session: ${sessionId}" )

        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }


    }

    /**
     *
     */
    def proceedSimulation( String sessionId ) {

        Map<Long, SimulationThreadTask> threadMap = threadsForSession.get( sessionId )

        if ( threadMap ) {

            for (  SimulationThreadTask task : threadMap.values()  ) {
                task.proceed()
            }

            statusForSession.put( sessionId, SchedulerStatus.play )

            log.debug( "simulation proceeded for session: ${sessionId}" )

        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }


    }

    SchedulerStatus getSchedulerStatus( String sessionId ) {

        SchedulerStatus status = statusForSession.get( sessionId )

        if ( status ) {
            return status
        } else {
            return SchedulerStatus.isNull
        }

    }


    def stopSimulation2( Long simulationId, String sessionId ) {

        Map<Long, CarAgent> threadMap = carAgentsForSession.get( sessionId )

        if ( threadMap ) {

            for (  CarAgent task : threadMap.values()  ) {
                task.cancel()
            }

            statusForSession.put( sessionId, SchedulerStatus.stop )

            log.debug( "simulation stopped for session: ${sessionId}" )

        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }


    }


    /**
     * stops scheduler
     *
     * @return
     */
    def stopSimulation( Long simulationId, String sessionId ) {

        Map<Long, SimulationThreadTask> threadMap = threadsForSession.get( sessionId )

        if ( threadMap ) {

            for (  SimulationThreadTask task : threadMap.values()  ) {
                task.cancel()
            }

            statusForSession.put( sessionId, SchedulerStatus.stop )

            log.debug( "simulation stopped for session: ${sessionId}" )

        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }


    }

    def getTime2( String sessionId ) {

        Map<Long, CarAgent> threadMap = carAgentsForSession.get( sessionId )

        if ( threadMap ) {

            CarAgent taskToAsk = threadMap.values().getAt( 0 )

            def currentTime = taskToAsk.currentTime

            def seconds = currentTime as BigInteger
            def secD = ( seconds.remainder( 60g ) ) as BigInteger
            seconds = seconds - secD
            def minutes = ( seconds / 60 ) as BigInteger
            def minD = minutes.remainder( 60g )
            minutes = minutes - minD
            def hours = ( minutes / 60 ) as BigInteger
            def hoursD = hours.remainder( 60g )

            def secDisplay = secD<10?"0${secD}":secD
            def minDisplay = minD<10?"0${minD}":minD

            def res = "${hoursD}:${minDisplay}:${secDisplay}"

            return res

        } else {
            return "00:00:00"
        }


    }


    def getTime( String sessionId ) {

        Map<Long, SimulationThreadTask> threadMap = threadsForSession.get( sessionId )

        if ( threadMap ) {

            SimulationThreadTask taskToAsk = threadMap.values().getAt( 0 )

            def currentTime = taskToAsk.currentTime

            def seconds = currentTime as BigInteger
            def secD = ( seconds.remainder( 60g ) ) as BigInteger
            seconds = seconds - secD
            def minutes = ( seconds / 60 ) as BigInteger
            def minD = minutes.remainder( 60g )
            minutes = minutes - minD
            def hours = ( minutes / 60 ) as BigInteger
            def hoursD = hours.remainder( 60g )

            def secDisplay = secD<10?"0${secD}":secD
            def minDisplay = minD<10?"0${minD}":minD

            def res = "${hoursD}:${minDisplay}:${secDisplay}"

            return res

        } else {
            return "00:00:00"
        }


    }


    def getCarInfos2( long simulationRouteId, String sessionId ) {

        Map<Long, CarAgent> threadMap = threadsForSession.get( sessionId )

        if ( threadMap ) {

            CarAgent task = threadMap.get( new Long( simulationRouteId ) )
            return task.dto()

        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }



    }


    def collectInfo( String sessionId ) {

        def l = []

        // simulationRouteId -> CarAgent
        Map<Long, CarAgent> threadMap = carAgentsForSession.get( sessionId )

        for ( Map.Entry<Long,CarAgent> carAgentEntry : threadMap ) {

            def m = [ : ]
            CarAgent carAgent = carAgentEntry.value


            // put car name:
            m.carName = carAgent.modelCar.carName
            double currentEnergy = carAgent.modelCar.currentEnergy
            double maxEnergy = carAgent.modelCar.maxEnergy
            m.batteryFilledPercentage = ( currentEnergy / maxEnergy ) * 100


            int currentEdgeIndex = carAgent.currentEdgeIndex
            int lastEdgeIndex = carAgent.routingPlan.trackEdges.size() - 1
            double routeDrivenPercentage = ( currentEdgeIndex / lastEdgeIndex ) * 100 ;
            m.routeDrivenPercentage = routeDrivenPercentage

            m.carStatus = carAgent.carStatus.toString()

            m.drivenKm = carAgent.kmDriven;
            m.totalKmToDrive = carAgent.kmToDrive;

            l << m
        }

        return l;
    }

    def getCarInfos( long simulationRouteId, String sessionId ) {

        Map<Long, SimulationThreadTask> threadMap = threadsForSession.get( sessionId )

        if ( threadMap ) {

            SimulationThreadTask task = threadMap.get( new Long( simulationRouteId ) )
            return task.dto()

        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }



    }



}

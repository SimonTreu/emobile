package de.dfki.gs.service.simulation

import de.dfki.gs.domain.simulation.CarType
import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.simulation.Simulation
import de.dfki.gs.domain.simulation.SimulationRoute
import de.dfki.gs.domain.simulation.Track
import de.dfki.gs.domain.simulation.TrackEdge
import de.dfki.gs.model.elements.Agent
import de.dfki.gs.model.elements.CarAgent
import de.dfki.gs.model.elements.EFillingStationAgent
import de.dfki.gs.model.elements.EnergyConsumptionModel
import de.dfki.gs.model.elements.ModelCar
import de.dfki.gs.model.elements.RoutingPlan
import de.dfki.gs.model.elements.results.CarAgentResult
import de.dfki.gs.model.elements.results.EFillingStationAgentResult
import de.dfki.gs.service.ExperimentDataService
import de.dfki.gs.service.FetchTrackEdgeServiceTask
import de.dfki.gs.simulation.CarStatus
import de.dfki.gs.simulation.SchedulerStatus
import de.dfki.gs.simulation.SimulationObject

import de.dfki.gs.simulation.SimulationThreadTask
import de.dfki.gs.threadutils.NotifyingBlockingThreadPoolExecutor
import de.dfki.gs.threadutils.ThreadPoolExecutorUtils
import grails.async.Promise
import grails.gorm.DetachedCriteria
import org.hibernate.FetchMode
import org.hibernate.annotations.Fetch

import javax.persistence.FetchType

import static grails.async.Promises.*

import grails.transaction.Transactional

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.CyclicBarrier


@Transactional
class SimulationThreadFrameworkService {


    def grailsApplication
    def experimentDataService
    def experimentStatsService

    boolean initialized = false

    ConcurrentHashMap<String, Map<Long, SimulationThreadTask>> threadsForSession = new ConcurrentHashMap<String, Map<Long, SimulationThreadTask>>()
    ConcurrentHashMap<String, SchedulerStatus> statusForSession = new ConcurrentHashMap<String, SchedulerStatus>()

    ConcurrentHashMap<String, Map<Long, CarAgent>> carAgentsForSession = new ConcurrentHashMap<String, Map<Long, CarAgent>>()
    ConcurrentHashMap<String, Map<Long, EFillingStationAgent>> fillingStationAgentsForSession = new ConcurrentHashMap<String, Map<Long, EFillingStationAgent>>()



    // Map<Long, SimulationThreadTask> threadMap

    SchedulerStatus status


    def init2( long simulationId, initMap, String sessionId, Double relativeSearchLimit ) throws Exception {

        log.debug( "try to init simulation framework with simulation ${simulationId} for session: ${sessionId}" )

        /**
         * take the desired simulation from db
         */
        Simulation simulation = Simulation.get( simulationId )

        List<GasolineStation> gasolineStations = GasolineStation.findAllBySimulation( simulation )



        ConcurrentMap<Long, EFillingStationAgent> fillingStationMap = new ConcurrentHashMap<Long, EFillingStationAgent>()

        long mmm = System.currentTimeMillis();
        for ( GasolineStation gasolineStation : gasolineStations ) {

            fillingStationMap.put( gasolineStation.id, EFillingStationAgent.createFillingStationAgent( gasolineStation ) )

        }
        log.error( "filling station agents created in ${(System.currentTimeMillis()-mmm)} ms" )


        /**
         * grab all SimulationRoutes from simulation
         */
        // List<SimulationRoute> simulationRoutes = SimulationRoute.findAllBySimulation( simulation )

        // here we store all threads
        HashMap<Long, CarAgent> threadMap = new HashMap<Long, CarAgent>()

        StringBuilder sb = new StringBuilder()

        // TODO: async: @link ( "http://grails.org/doc/2.3.0.M1/guide/async.html#asyncGorm" )


        long m1 = System.currentTimeMillis()


        List<Promise> proms = new ArrayList<Promise>()

        List<Long> ids = simulation.simulationRoutes*.id

        log.error( "ids: ${ids}" )
        Map<Long,List<TrackEdge>> simRouteMap = new HashMap<Long,List<TrackEdge>>()

        /*
        NotifyingBlockingThreadPoolExecutor executor = ThreadPoolExecutorUtils.createThreadPoolExecutor( 90 );
        List<FetchTrackEdgeServiceTask> tasks = new ArrayList<FetchTrackEdgeServiceTask>()
        for ( long id : ids ) {
            tasks.add( new FetchTrackEdgeServiceTask(
                    id
            ) )
        }

        for ( FetchTrackEdgeServiceTask task : tasks ) {
            executor.execute( task )
        }

        ThreadPoolExecutorUtils.waiting( executor )


        for( Runnable r : executor.getRunnables() ) {

            long simRouteId = ((FetchTrackEdgeServiceTask) r).simulationRouteId
            List<TrackEdge> trackEdges = ((FetchTrackEdgeServiceTask)r).trackEdgeList

            simRouteMap.put( simRouteId, trackEdges )
        }
        */

        // split into portions of 100
        def portion = 90;

        def idCounter = 0;
        def toMax = ids.size() - 1;
        def localTo = idCounter + ( portion - 1 );

        def hua = []

        while ( hua.size() != ids.size() ) {

            if ( localTo > toMax ) {
                localTo = toMax
            }

            log.error( "from ${idCounter} to ${localTo}" )

            ids[ idCounter..localTo ].each { Long l ->
                def promise = SimulationRoute.async.task {

                    log.error( "fetching for ${l}" )

                    def tes = TrackEdge.withCriteria {
                        eq( "simulationRouteId", l)
                    }

                }

                proms.add( promise )
            }

            hua = waitAll( proms )

            idCounter += portion;
            localTo = idCounter + ( portion - 1 );

        }

        hua.flatten().each { TrackEdge te ->

            // TODO: rethink
            List<TrackEdge> l = simRouteMap.get( te.simulationRouteId )

            if ( l != null ) {
                l.add( te )
            } else {
                List<TrackEdge> newL = new ArrayList<TrackEdge>()
                newL.add( te )
                simRouteMap.put( te.simulationRouteId, newL )
            }

        }


        /*





        log.error( "filled tracks in ${(System.currentTimeMillis()-m1)} ms" )
        m1 = System.currentTimeMillis()

        log.error( "...." )




        */

        log.error( "filled track map in ${(System.currentTimeMillis()-m1)} ms" )
        m1 = System.currentTimeMillis()


        /**
         * distribute all routes to an interval of 0..(60*60*4) -> 4h
         */
        long intervalStep =  Math.floor( ( 60 * 60 * 4 ) / simulation.simulationRoutes.size() )
        long runningStartTime = 0;

        for ( SimulationRoute simulationRoute : simulation.simulationRoutes ) {

            if ( simulationRoute ) {

                // List<TrackEdge> edges = hua.flatten().findAll { TrackEdge te -> te.simulationRouteId == simulationRoute.id }

                List<TrackEdge> edges = simRouteMap.get( simulationRoute.id )

                RoutingPlan routingPlan = RoutingPlan.createRoutingPlan( edges );

                CarType carType = simulationRoute.carType

                ModelCar modelCar = ModelCar.createModelCar( new EnergyConsumptionModel(), carType, 1, relativeSearchLimit );

                CarAgent carAgent = CarAgent.createCarAgent(
                        routingPlan,
                        modelCar,
                        fillingStationMap,
                        gasolineStations,
                        simulationId,
                        35,
                        runningStartTime,
                        simulationRoute.plannedDistance,
                        simulationRoute.id
                )

                threadMap.put( simulationRoute.id, carAgent )

                runningStartTime += intervalStep
            }

        }
        log.error( "loaded agents : ${(System.currentTimeMillis()-m1)} ms" )

        log.debug( "added ${threadMap.size()} tasks to simulation: ${sb.toString()}" )


        carAgentsForSession.put( sessionId, threadMap )
        fillingStationAgentsForSession.put( sessionId, fillingStationMap )


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
        Map<Long, EFillingStationAgent> fillingStationAgentMap = fillingStationAgentsForSession.get( sessionId )

        if ( threadMap ) {

            for ( CarAgent task : threadMap.values() ) {

                if ( task.status == SchedulerStatus.play || task.status == SchedulerStatus.pause ) {
                    task.setInterval( scaleValue )
                }
            }

        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }

        if ( fillingStationAgentMap ) {

            for ( EFillingStationAgent task : fillingStationAgentMap.values() ) {

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


        long startTimestamp = System.currentTimeMillis();

        log.error( "starting.." )

        Map<Long, EFillingStationAgent> fillingStationsMap = fillingStationAgentsForSession.get( sessionId )
        Map<Long, CarAgent> threadMap = carAgentsForSession.get( sessionId )

        int barrierSize = fillingStationsMap.size() + threadMap.size();

        // Barrier barrier = new ButterflyBarrier( barrierSize );
        CyclicBarrier barrier = new CyclicBarrier( barrierSize )

        int personalId = 0;
        if ( fillingStationsMap ) {
            /**
             * stepping through all simulationTasks  and say start!
             */
            for ( EFillingStationAgent task : fillingStationsMap.values() ) {
                task.setBarrier( barrier )
                task.personalId = personalId
                task.start()

                personalId++;
            }



            statusForSession.put( sessionId, SchedulerStatus.play )

            log.debug( "simulation started for session: ${sessionId}" )

        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }

        if ( threadMap ) {
            /**
             * stepping through all simulationTasks  and say start!
             */
            for ( CarAgent task : threadMap.values() ) {
                task.setBarrier( barrier )
                task.personalId = personalId
                task.start()

                personalId++;
            }

            statusForSession.put( sessionId, SchedulerStatus.play )

            log.error( "simulation started for session: ${sessionId}" )

        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }

        log.error( "checker.." )

        boolean allRoutesFinished = false;

        while ( !allRoutesFinished ) {

            // Thread.sleep( 3000 )

            for ( CarAgent agent : threadMap.values() ) {

                allRoutesFinished = (
                        agent.getCarStatus().equals( CarStatus.MISSION_ACCOMBLISHED )||agent.getCarStatus().equals( CarStatus.WAITING_EMPTY )
                )

                if ( allRoutesFinished == false ) {
                    break;
                }

            }


            if ( allRoutesFinished == true ) {
                // stop them all
                for ( Agent agent : threadMap.values() ) {
                    agent.cancel();
                }
                for ( Agent agent : fillingStationsMap.values() ) {
                    agent.cancel();
                }

                long simTimeMillis = ( System.currentTimeMillis() - startTimestamp )

                long simExpId = stopSimulation2( 2, sessionId, simTimeMillis )

                log.error( "saved sim results in experimentResult: ${simExpId}" )
                // def m = experimentStatsService.createStats( simExpId )

            }
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

        Map<Long, EFillingStationAgent> fillingStationMap = fillingStationAgentsForSession.get( sessionId )

        if ( fillingStationMap ) {

            for (  EFillingStationAgent task : fillingStationMap.values()  ) {
                task.pause()
            }

            statusForSession.put( sessionId, SchedulerStatus.pause )

            log.debug( "simulation paused for session: ${sessionId}" )

        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }

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


        Map<Long, EFillingStationAgent> fillingStationMap = fillingStationAgentsForSession.get( sessionId )

        if ( fillingStationMap ) {

            for ( EFillingStationAgent task : fillingStationMap.values()  ) {
                task.proceed()
            }

            statusForSession.put( sessionId, SchedulerStatus.play )

            log.debug( "simulation proceeded for session: ${sessionId}" )

        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }

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

    /**
     * should stop all running tasks and collect all results
     *
     * @param simulationId
     * @param sessionId
     * @return
     */
    def stopSimulation2( Long simulationId, String sessionId, long simTimeMillis ) {

        Map<Long, CarAgent> threadMap = carAgentsForSession.get( sessionId )
        Map<Long, EFillingStationAgent> fillingStationMap = fillingStationAgentsForSession.get( sessionId )

        List<CarAgentResult> carAgentResults = new ArrayList<CarAgentResult>()
        List<EFillingStationAgentResult> fillingResults = new ArrayList<EFillingStationAgentResult>()

        Long experimentRunResultId = null

        if ( threadMap ) {

            for (  CarAgent task : threadMap.values()  ) {

                carAgentResults.add( task.getCarAgentResult() )
                task.cancel()

            }

            statusForSession.put( sessionId, SchedulerStatus.stop )

            log.debug( "simulation stopped for session: ${sessionId} and try to save results" )

        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }

        if ( fillingStationMap ) {

            for (  EFillingStationAgent task : fillingStationMap.values()  ) {

                fillingResults.add( task.geteFillingStationAgentResult() )
                task.cancel()

            }

            statusForSession.put( sessionId, SchedulerStatus.stop )

            log.debug( "simulation stopped for session: ${sessionId} and try to save results" )


        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }

        Double relativeSearchLimit = null
        for ( CarAgentResult result : carAgentResults ) {
            if ( relativeSearchLimit == null ) {
                relativeSearchLimit = result.relativeSearchLimit
            } else {
                break;
            }
        }

        experimentRunResultId = experimentDataService.saveExperimentResult( carAgentResults, fillingResults, relativeSearchLimit, simTimeMillis )

        threadMap.clear()
        fillingStationMap.clear()

        threadMap = new HashMap<Long,CarAgent>()
        fillingStationMap = new HashMap<Long, EFillingStationAgent>()

        carAgentsForSession.remove( sessionId )
        fillingStationAgentsForSession.remove( sessionId )

        return experimentRunResultId
    }

    /**
     * should stop all running tasks and collect all results
     *
     * @param simulationId
     * @param sessionId
     * @return
     */
    def stopSimulation3( Long simulationId, String sessionId, long simTimeMillis ) {

        Map<Long, CarAgent> threadMap = carAgentsForSession.get( sessionId )
        Map<Long, EFillingStationAgent> fillingStationMap = fillingStationAgentsForSession.get( sessionId )

        List<CarAgentResult> carAgentResults = new ArrayList<CarAgentResult>()
        List<EFillingStationAgentResult> fillingResults = new ArrayList<EFillingStationAgentResult>()

        if ( threadMap ) {

            for (  CarAgent task : threadMap.values()  ) {

                //carAgentResults.add( task.getCarAgentResult() )
                task.cancel()

            }

            statusForSession.put( sessionId, SchedulerStatus.stop )

            log.debug( "simulation stopped for session: ${sessionId} and try to save results" )

        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }

        if ( fillingStationMap ) {

            for (  EFillingStationAgent task : fillingStationMap.values()  ) {

                //fillingResults.add( task.geteFillingStationAgentResult() )
                task.cancel()

            }

            statusForSession.put( sessionId, SchedulerStatus.stop )

            log.debug( "simulation stopped for session: ${sessionId} and try to save results" )


        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }

        threadMap.clear()
        fillingStationMap.clear()

        threadMap = new HashMap<Long,CarAgent>()
        fillingStationMap = new HashMap<Long, EFillingStationAgent>()

        carAgentsForSession.remove( sessionId )
        fillingStationAgentsForSession.remove( sessionId )

        return true
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
            hours = hours - hoursD

            def days = ( hours / 24 ) as BigInteger
            def daysD = days.remainder( 24g )

            def secDisplay = secD<10?"0${secD}":secD
            def minDisplay = minD<10?"0${minD}":minD
            def hoursDisplay = hoursD<10?"0${hoursD}":hoursD
            def daysDisplay = daysD<10?"0${daysD}":daysD

            def res = "${daysDisplay} days ${hoursDisplay}:${minDisplay}:${secDisplay}"

            return res

        } else {
            return "00:00:00"
        }


    }



    def collectInfo( String sessionId ) {

        def resultMap = [ : ]

        def l = []



        boolean allCarsFinished = true;


        // simulationRouteId -> CarAgent
        Map<Long, CarAgent> threadMap = carAgentsForSession.get( sessionId )

        for ( Map.Entry<Long,CarAgent> carAgentEntry : threadMap ) {

            def m = [ : ]
            CarAgent carAgent = carAgentEntry.value

            m.personalId = carAgent.personalId

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


            if ( carAgent.carStatus.toString().equals( CarStatus.MISSION_ACCOMBLISHED.toString() )||carAgent.carStatus.toString().equals( CarStatus.WAITING_EMPTY.toString() ) ) {

            } else {
                allCarsFinished = false
            }

            l << m
        }

        def p = []

        Map<Long, EFillingStationAgent> stationMap = fillingStationAgentsForSession.get( sessionId )

        for ( Map.Entry<Long, EFillingStationAgent> stationEntry : stationMap ) {

            def m = [ : ]

            EFillingStationAgent agent = stationEntry.value

            m.personalId = agent.personalId

            m.status = agent.fillingStationStatus.toString()

            p << m
        }

        resultMap.cars = l;
        resultMap.stations = p;

        String simFinished = "false"
        if ( allCarsFinished == true ) {
            simFinished = "finished"
        }
        resultMap.finished = simFinished

        return resultMap;
    }

    def getCarInfos2( long simulationRouteId, String sessionId ) {

        Map<Long, CarAgent> carAgentMap = carAgentsForSession.get( sessionId )

        if ( carAgentMap ) {

            CarAgent task = carAgentMap.get( new Long( simulationRouteId ) )
            return task.dto()

        } else {
            log.error( "no threads found for session ${sessionId}" )
        }

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

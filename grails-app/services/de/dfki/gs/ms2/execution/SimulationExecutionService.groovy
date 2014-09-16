package de.dfki.gs.ms2.execution

import de.dfki.gs.domain.simulation.Car
import de.dfki.gs.domain.simulation.CarType
import de.dfki.gs.domain.simulation.Configuration
import de.dfki.gs.domain.simulation.FillingStation
import de.dfki.gs.domain.simulation.FillingStationGroup
import de.dfki.gs.domain.simulation.FillingStationType
import de.dfki.gs.domain.simulation.Fleet
import de.dfki.gs.domain.simulation.Route
import de.dfki.gs.domain.simulation.TrackEdge
import de.dfki.gs.model.elements.Agent
import de.dfki.gs.model.elements.CarAgent
import de.dfki.gs.model.elements.EFillingStationAgent
import de.dfki.gs.model.elements.EnergyConsumptionModel
import de.dfki.gs.model.elements.FillingStationStatus
import de.dfki.gs.model.elements.ModelCar
import de.dfki.gs.model.elements.RoutingPlan
import de.dfki.gs.model.elements.results.CarAgentResult
import de.dfki.gs.model.elements.results.EFillingStationAgentResult
import de.dfki.gs.simulation.CarStatus
import de.dfki.gs.simulation.SchedulerStatus
import de.dfki.gs.simulation.SimulationThreadTask
import grails.async.Promise
import grails.transaction.Transactional

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.CyclicBarrier

import static grails.async.Promises.waitAll

@Transactional
class SimulationExecutionService {

    def grailsApplication
    def experimentDataService


    ConcurrentHashMap<String, Map<Long, SimulationThreadTask>> threadsForSession = new ConcurrentHashMap<String, Map<Long, SimulationThreadTask>>()
    ConcurrentHashMap<String, SchedulerStatus> statusForSession = new ConcurrentHashMap<String, SchedulerStatus>()

    ConcurrentHashMap<String, Map<Long, CarAgent>> carAgentsForSession = new ConcurrentHashMap<String, Map<Long, CarAgent>>()
    ConcurrentHashMap<String, Map<Long, EFillingStationAgent>> fillingStationAgentsForSession = new ConcurrentHashMap<String, Map<Long, EFillingStationAgent>>()


    /**
     * starts a simulation experiment
     *
     * @param sessionId
     */
    def runSimulation( String sessionId ) {


        long startTimestamp = System.currentTimeMillis();

        log.error( "starting.." )

        Map<Long, EFillingStationAgent> fillingStationsMap = fillingStationAgentsForSession.get( sessionId )
        Map<Long, CarAgent> carAgentsMap = carAgentsForSession.get( sessionId )

        int barrierSize = fillingStationsMap.size() + carAgentsMap.size();

        /**
         * defining a barrier which takes all agents ( to assure simulation time synchronization )
         */
        CyclicBarrier barrier = new CyclicBarrier( barrierSize )

        int personalId = 0;
        if ( fillingStationsMap ) {
            /**
             * stepping through all filligStation agents, setting the barrier  and say start!
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

        if ( carAgentsMap ) {
            /**
             * stepping through all car agents, setting the barrier  and say start!
             */
            for ( CarAgent task : carAgentsMap.values() ) {

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

        log.error( "init checker.." )

        boolean allRoutesFinished = false;

        while ( !allRoutesFinished ) {

            int countP = 0;
            for ( EFillingStationAgent fillingStationAgent : fillingStationsMap.values() ) {

                if ( fillingStationAgent.fillingStationStatus == FillingStationStatus.IN_USE ) {
                    countP++;
                }

            }

            //log.error( "${countP} Filling Stations in use paralell" )

            for ( CarAgent agent : carAgentsMap.values() ) {

                allRoutesFinished = (
                        agent.getCarStatus().equals( CarStatus.MISSION_ACCOMBLISHED )||agent.getCarStatus().equals( CarStatus.WAITING_EMPTY )
                )

                if ( allRoutesFinished == false ) {
                    break;
                }

            }


            if ( allRoutesFinished == true ) {

                // stop them all which calls the cancel method in agents for collecting and persisting results
                for ( Agent agent : carAgentsMap.values() ) {
                    agent.cancel();
                }
                for ( Agent agent : fillingStationsMap.values() ) {
                    agent.cancel();
                }

                long simTimeMillis = ( System.currentTimeMillis() - startTimestamp )

                long simExpId = stopSimulation( 2, sessionId, simTimeMillis )

                log.error( "saved sim results in experimentResult: ${simExpId}" )

            }
        }


    }

    def panicStop( Long configurationId, String sessionId ) {

        Map<Long, CarAgent> carAgentMap = carAgentsForSession.get( sessionId )
        Map<Long, EFillingStationAgent> fillingStationMap = fillingStationAgentsForSession.get( sessionId )

        if ( carAgentMap ) {

            for (  CarAgent task : carAgentMap.values()  ) {

                task.cancel()

            }

            statusForSession.put( sessionId, SchedulerStatus.stop )

            log.debug( "simulation stopped for session: ${sessionId} and try to save results" )

        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }

        if ( fillingStationMap ) {

            for (  EFillingStationAgent task : fillingStationMap.values()  ) {

                task.cancel()

            }

            statusForSession.put( sessionId, SchedulerStatus.stop )

            log.debug( "simulation stopped for session: ${sessionId} and try to save results" )


        } else {
            log.error( "no threads found for session: ${sessionId}" )
        }

        carAgentMap.clear()
        fillingStationMap.clear()

        carAgentMap = new HashMap<Long,CarAgent>()
        fillingStationMap = new HashMap<Long, EFillingStationAgent>()

        carAgentsForSession.remove( sessionId )
        fillingStationAgentsForSession.remove( sessionId )

    }

    /**
     * should stop all running tasks and collect all results
     *
     * @param simulationId
     * @param sessionId
     * @return
     */
    def stopSimulation( Long configurationId, String sessionId, long simTimeMillis ) {

        Map<Long, CarAgent> carAgentMap = carAgentsForSession.get( sessionId )
        Map<Long, EFillingStationAgent> fillingStationMap = fillingStationAgentsForSession.get( sessionId )

        List<CarAgentResult> carAgentResults = new ArrayList<CarAgentResult>()
        List<EFillingStationAgentResult> fillingResults = new ArrayList<EFillingStationAgentResult>()

        Long experimentRunResultId = null

        /**
         * collecting results for car agents
         */
        if ( carAgentMap ) {

            for (  CarAgent task : carAgentMap.values()  ) {

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

        /**
         * TODO: only for debug printing, can be removed ..
         */
        for ( CarAgent carAgent : carAgentMap.values() ) {

            if ( carAgent.carStatus == CarStatus.MISSION_ACCOMBLISHED ) {

                log.error( "car ${carAgent.personalId} (${carAgent.modelCar.carName}) reached target " )

            } else {

                log.error( "car ${carAgent.personalId} (${carAgent.modelCar.carName}) FAILED" )

            }

        }


        experimentRunResultId = experimentDataService.saveExperimentResult(
                configurationId,
                carAgentResults,
                fillingResults,
                simTimeMillis
        )

        /**
         * giving the garbage collector the chance to clear out used space
         */
        carAgentMap.clear()
        fillingStationMap.clear()

        carAgentMap = new HashMap<Long,CarAgent>()
        fillingStationMap = new HashMap<Long, EFillingStationAgent>()

        carAgentsForSession.remove( sessionId )
        fillingStationAgentsForSession.remove( sessionId )

        return experimentRunResultId
    }

    /**
     * initialize a simulation experiment
     *
     * @param simulationId
     * @param initMap
     * @param sessionId
     * @param relativeSearchLimit
     * @return
     * @throws Exception
     */
    def init( long configurationId, initMap, String sessionId, Double relativeSearchLimit ) throws Exception {

        log.debug( "try to init simulation framework with simulation ${configurationId} for session: ${sessionId}" )


        /**
         * take the desired simulation configuration from db
         */
        Configuration configuration = Configuration.get( configurationId )


        /**
         * getting all fillingStations from the groups
         */
        List<FillingStation> fillingStations = new ArrayList<FillingStation>()
        configuration.fillingStationGroups.each { FillingStationGroup group ->

            group = FillingStationGroup.get( group.id )
            group.fillingStations.each { FillingStation station ->

                fillingStations.add( FillingStation.get( station.id ) )

            }

        }

        Map<Long, EFillingStationAgent> fillingStationMap = new HashMap<Long, EFillingStationAgent>()

        long mmm = System.currentTimeMillis();
        for ( FillingStation station : fillingStations ) {

            // TODO: getting the type is possibly much easier -> rethink
            FillingStationType type = FillingStationType.get( station.fillingStationType.id )
            fillingStationMap.put( station.id,
                        EFillingStationAgent.createFillingStationAgentFromFillingStation( station, type ) )

        }
        log.error( "filling station agents created in ${(System.currentTimeMillis()-mmm)} ms" )

        // initialize fillingStationSyncronizer:
        FillingStationAgentSyncronizer syncronizer = new FillingStationAgentSyncronizer(
                fillingStationMap
        )




        // here we store all car agents
        HashMap<Long, CarAgent> carAgentMap = new HashMap<Long, CarAgent>()

        StringBuilder sb = new StringBuilder()

        // TODO: async: @link ( "http://grails.org/doc/2.3.0.M1/guide/async.html#asyncGorm" )

        long m1 = System.currentTimeMillis()


        List<Promise> proms = new ArrayList<Promise>()

        List<Long> routeIds = new ArrayList<Long>()
        List<Route> allRoutes = new ArrayList<Route>()
        List<Car> allCars = new ArrayList<Car>()

        configuration.fleets.each { Fleet fleet ->
            fleet = Fleet.get( fleet.id )
            fleet.cars.each { Car car ->

                car = Car.get( car.id )
                allCars.add( car )

                Route route = Route.get( car.route.id )
                allRoutes.add( route )

                routeIds.add( car.route.id )
            }
        }

        Map<Long,List<TrackEdge>> simRouteMap = new HashMap<Long,List<TrackEdge>>()

        // split into portions of 90, which can be handled by 90 db connections
        def portion = 90;

        def idCounter = 0;
        def toMax = routeIds.size() - 1;
        def localTo = idCounter + ( portion - 1 );

        def hua = []

        while ( hua.size() != routeIds.size() ) {

            if ( localTo > toMax ) {
                localTo = toMax
            }

            log.error( "from ${idCounter} to ${localTo}" )

            routeIds[ idCounter..localTo ].each { Long l ->
                def promise = Route.async.task {

                    log.error( "fetching for ${l}" )

                    def tes = TrackEdge.withCriteria {
                        eq( "routeId", l )
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
            List<TrackEdge> l = simRouteMap.get( te.routeId )

            if ( l != null ) {

                l.add( te )

            } else {

                List<TrackEdge> newL = new ArrayList<TrackEdge>()
                newL.add( te )
                simRouteMap.put( te.routeId, newL )

            }

        }

        log.error( "filled track map in ${(System.currentTimeMillis()-m1)} ms" )
        m1 = System.currentTimeMillis()


        /**
         * distribute all routes to an interval of 0..(60*60*4) -> 4h
         */
        long intervalStep =  Math.floor( ( 60 * 60 * 4 ) / routeIds.size() )
        long runningStartTime = 0;

        for ( Car car : allCars ) {

            if ( car ) {

                List<TrackEdge> edges = simRouteMap.get( car.route.id )

                RoutingPlan routingPlan = RoutingPlan.createRoutingPlan( edges );


                CarType carType = car.carType

                // TODO: recheck factory method for creating model car!!!
                ModelCar modelCar = ModelCar.createModelCar( new EnergyConsumptionModel(), carType, 1, relativeSearchLimit );

                // TODO: recheck factory method for creating car Agent!!!
                CarAgent carAgent = CarAgent.createCarAgentWithFillingStations(
                        routingPlan,
                        modelCar,
                        configurationId,
                        35,
                        runningStartTime,
                        (double) car.route.edges.sum { TrackEdge edge -> edge.km },
                        car.route.id,
                        syncronizer
                )

                carAgentMap.put( car.route.id, carAgent )

                runningStartTime += intervalStep
            }

        }
        log.error( "loaded agents : ${(System.currentTimeMillis()-m1)} ms" )

        log.debug( "added ${carAgentMap.size()} car agents to simulation experiment: ${sb.toString()}" )


        carAgentsForSession.put( sessionId, carAgentMap )
        fillingStationAgentsForSession.put( sessionId, fillingStationMap )


        statusForSession.put( sessionId, SchedulerStatus.init )

        log.debug( "simulation framework is initiated for session: ${sessionId}" )

    }


    SchedulerStatus getSchedulerStatus( String sessionId ) {

        SchedulerStatus status = statusForSession.get( sessionId )

        if ( status ) {
            return status
        } else {
            return SchedulerStatus.isNull
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

    def getTime( String sessionId ) {

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

}

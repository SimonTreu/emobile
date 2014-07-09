package de.dfki.gs.controller

import de.dfki.gs.controller.commands.CreateSimulationCommand
import de.dfki.gs.controller.commands.ScaleCommand
import de.dfki.gs.controller.commands.SelectSimulationCommand
import de.dfki.gs.controller.commands.SimulationCommand
import de.dfki.gs.domain.users.Person
import de.dfki.gs.domain.simulation.Simulation
import de.dfki.gs.service.simulation.AsyncSimulationFrameworkService
import de.dfki.gs.simulation.SchedulerStatus
import de.dfki.gs.utils.ResponseConstants
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.web.util.WebUtils

class SimulationController {

    def simulationCollectDataService
    def simulationThreadFrameworkService
    def simulationDataService
    def experimentStatsService
    def generateStatsPictureService
    def springSecurityService
    def routeService

    AsyncSimulationFrameworkService asyncSimulationFrameworkService

    def init() {

        log.debug( "sessionId: ${request.requestedSessionId}" )

        SelectSimulationCommand cmd = new SelectSimulationCommand();
        bindData( cmd, params )
        Person loggedInPerson = (Person) springSecurityService.currentUser
        if (loggedInPerson!=null) {
            if ( cmd.selectedSimulationId != null && !cmd.validate() ) {
                log.error( "failed to get simulation by id: ${cmd.selectedSimulationId} -- ${cmd.errors}" )
            }

            def m = [ : ]

            if ( cmd.selectedSimulationId ) {
                log.debug( "selected simulation: ${cmd.selectedSimulationId} -- try to fetch data from db" )

                Simulation simulation = Simulation.read( cmd.selectedSimulationId )
                m.selectedSimulationId = simulation.id
                m.selectedSimulation = simulation

                // m.simulationRoutes = SimulationRoute.findAllBySimulation( simulation )
                m.simulationRoutes = simulation.simulationRoutes

                log.debug( "filled model for simulation with ${simulation.simulationRoutes.size()} simulationRoutes" )
            }

            m.name = "Simulations"
            m.availableSimulations = simulationCollectDataService.collectSimulations()

            m.welcome = [
                    'givenName' : loggedInPerson.givenName,
                    'familyName' : loggedInPerson.familyName
            ]
            m.carTypeCars = simulationDataService.collectCarTypes()
            m.electricStations = simulationDataService.collectElectricStations()
            log.debug( "model: ${m}" )

            render( view: 'index', model: m )
        } else {
            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
        }

    }

    def open () {
        def m = [ : ]
        SelectSimulationCommand cmd = new SelectSimulationCommand();
        bindData( cmd, params )
        Person loggedInPerson = (Person) springSecurityService.currentUser
        if (loggedInPerson != null) {
            if ( cmd.selectedSimulationId != null && !cmd.validate() ) {
                log.error( "failed to get simulation by id: ${cmd.selectedSimulationId} -- ${cmd.errors}" )
            }

            if ( cmd.selectedSimulationId ) {
                log.debug( "selected simulation: ${cmd.selectedSimulationId} -- try to fetch data from db" )

                Simulation simulation = Simulation.read( cmd.selectedSimulationId )
                m.selectedSimulationId = simulation.id
                m.selectedSimulation = simulation

                // m.simulationRoutes = SimulationRoute.findAllBySimulation( simulation )
                m.simulationRoutes = simulation.simulationRoutes
                log.debug( "filled model for simulation with ${simulation.simulationRoutes.size()} simulationRoutes" )
            }

            m.name = "Simulations"
            m.simulationRoutesSize = simulationCollectDataService.collectSimulations()
            //m.simulationGasolineStationSize = simulationCollectDataService.collectSimulations().gasolineStations.size()
            //m.availableSimulations = simulationCollectDataService.collectSimulations()


            log.error( "loggedInPerson: ${loggedInPerson}" )

                m.welcome = [
                        'givenName' : loggedInPerson.givenName,
                        'familyName': loggedInPerson.familyName
                ]

            render( view: 'open', model: m )
        } else {
            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
        }

    }


    def load() {
        def m = [ : ]

        SelectSimulationCommand cmd = new SelectSimulationCommand();
        bindData( cmd, params )
        Person loggedInPerson = (Person) springSecurityService.currentUser
        if (loggedInPerson!=null) {
            if ( cmd.selectedSimulationId != null && !cmd.validate() ) {
                log.error( "failed to get simulation by id: ${cmd.selectedSimulationId} -- ${cmd.errors}" )
            }

            if ( cmd.selectedSimulationId ) {
                log.debug( "selected simulation: ${cmd.selectedSimulationId} -- try to fetch data from db" )

                Simulation simulation = Simulation.read( cmd.selectedSimulationId )
                m.selectedSimulationId = simulation.id
                m.selectedSimulation = simulation

                // m.simulationRoutes = SimulationRoute.findAllBySimulation( simulation )
                m.simulationRoutes = simulation.simulationRoutes

                log.debug( "filled model for simulation with ${simulation.simulationRoutes.size()} simulationRoutes" )
            }

            m.name = "Simulations"
            m.availableSimulations = simulationCollectDataService.collectSimulations()
            log.error( "availableSimulations: ${simulationCollectDataService.collectSimulations()}" )
            m.welcome = [
                    'givenName' : loggedInPerson.givenName,
                    'familyName' : loggedInPerson.familyName
            ]

            render( view: 'load', model: m )
        } else {
            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
        }
    }

    def create() {

        log.debug( "params: ${params}" )

        CreateSimulationCommand cmd = new CreateSimulationCommand();
        bindData( cmd, params )

        log.debug( "cmd: ${cmd.properties}" )

        if ( !cmd.validate() ) {
            log.error( "failed to validate simulation name: ${cmd.errors}" )
        } else {

            Simulation simulation = new Simulation( name: cmd.simulationName );
            if ( !simulation.save( flush: true ) ) {
                log.error( "failed to save simulation: ${simulation.errors}" )
            }

            if ( cmd.createRandomRoutes && cmd.createRandomRoutes > 0 ) {

                routeService.createRandomRoutes( cmd.createRandomRoutes, simulation.id )

            }

            if ( cmd.createRandomStations && cmd.createRandomStations > 0 ) {

                routeService.createRandomStations( cmd.createRandomStations, simulation.id )

            }

        }

        def m = [ : ]
        m.name = "Simulations"
        m.availableSimulations = simulationCollectDataService.collectSimulations()

        render( view: 'index', model: m )
    }


    def startSimulation() {

        SimulationCommand cmd = new SimulationCommand();
        bindData( cmd, params )

        def sessionId = WebUtils.retrieveGrailsWebRequest().session.id

        if ( cmd.simulationId != null && !cmd.validate() ) {
            log.error( "failed to get simulation by id: ${cmd.simulationId} -- ${cmd.errors}" )
        } else {

            // check, if start means "proceed" or "start"

            if ( simulationThreadFrameworkService.getSchedulerStatus( sessionId ) == SchedulerStatus.pause ) {

                simulationThreadFrameworkService.proceedSimulation( sessionId )
                log.debug( "proceeded simulation" )

            } else if ( simulationThreadFrameworkService.getSchedulerStatus( sessionId ) == SchedulerStatus.init ) {

                simulationThreadFrameworkService.runSimulation2( sessionId )
                log.debug( "started simulation" )

                /*
                asyncSimulationFrameworkService.runSimulation()
                        .onComplete {
                    println "started simulation"
                }
                */

            }



        }

    }

    def stopSimulation() {

        log.debug( "params: ${params}" )

        def sessionId = WebUtils.retrieveGrailsWebRequest().session.id

        SimulationCommand cmd = new SimulationCommand();
        bindData( cmd, params )

        if ( cmd.simulationId != null && !cmd.validate() ) {
            log.error( "failed to get simulation by id: ${cmd.simulationId} -- ${cmd.errors}" )
        } else {

            Long experimentRunResultId = simulationThreadFrameworkService.stopSimulation2( cmd.simulationId, sessionId, 0 )

            def m = experimentStatsService.createStats( experimentRunResultId )

            File fillingStationUsageFile = generateStatsPictureService.createDataChartFileForFillingStationUsage( m );
            File timeFile = generateStatsPictureService.createDataChartFileForTime( m );
            File distanceFile = generateStatsPictureService.createDataChartFileForDistance( m );


            redirect( controller: 'stats', action: 'showStats', params: [
                    timeFileUUID : timeFile.getName(),
                    distanceFileUUID : distanceFile.getName(),
                    fillingFileUUID : fillingStationUsageFile?.getName(),
                    carsCount : m.carTypes.get( 0 )?.count,
                    countTargetReached : m.carTypes.get( 0 )?.countTargetReached,
                    simulationTime : m.fillingTypes.get( 0 )?.timeLivingList?.get( 0 )
            ] )
        }

    }

    def pauseSimulation() {

        def sessionId = WebUtils.retrieveGrailsWebRequest().session.id

        SimulationCommand cmd = new SimulationCommand();
        bindData( cmd, params )

        if ( cmd.simulationId != null && !cmd.validate() ) {
            log.error( "failed to get simulation by id: ${cmd.simulationId} -- ${cmd.errors}" )
        } else {


            simulationThreadFrameworkService.pauseSimulation( sessionId )
            log.debug( "paused simulation" )
            /*
            asyncSimulationFrameworkService.pauseSimulation()
                    .onComplete {
                println "paused simulation"
            }
            */

        }

    }

    def proceedSimulation() {

        def sessionId = WebUtils.retrieveGrailsWebRequest().session.id

        SimulationCommand cmd = new SimulationCommand();
        bindData( cmd, params )

        if ( cmd.simulationId != null && !cmd.validate() ) {
            log.error( "failed to get simulation by id: ${cmd.simulationId} -- ${cmd.errors}" )
        } else {

            simulationThreadFrameworkService.proceedSimulation( sessionId )
            log.debug( "proceeded simulation" )
            /*
            asyncSimulationFrameworkService.proceedSimulation()
                    .onComplete {
                println "proceeded simulation"
            }
            */

        }

    }

    def viewSimulation() {

        log.debug( "params sim: ${params}" )

        def sessionId = WebUtils.retrieveGrailsWebRequest().session.id

        SimulationCommand cmd = new SimulationCommand()
        bindData( cmd, params )

        if ( cmd.simulationId != null && !cmd.validate() ) {
            log.error( "failed to get simulation by id: ${cmd.simulationId} -- ${cmd.errors}" )
        } else {


            def m = simulationCollectDataService.collectSimulationModelForRendering( cmd.simulationId, true, true )

            // simulationThreadFrameworkService.init( cmd.simulationId, null, sessionId )
            Double relativeSearchLimit = 0.20
            simulationThreadFrameworkService.init2( cmd.simulationId, null, sessionId, relativeSearchLimit )


            render view: 'showPlaySimulation', model: m

        }

    }

    def getInfoForRoute() {

        def sessionId = WebUtils.retrieveGrailsWebRequest().session.id

        def json = request.JSON.data

        def data = [ : ]

        response.addHeader( "Content-Type", "application/json" );

        if ( !json.simulationRouteId ) {

            response.status = ResponseConstants.RESPONSE_STATUS_BAD_REQUEST
            render "${([ error: 'no route id provided' ] as JSON).toString()}"
            return
        }

        if ( simulationThreadFrameworkService.getSchedulerStatus( sessionId ) == SchedulerStatus.play ||
                simulationThreadFrameworkService.getSchedulerStatus( sessionId ) == SchedulerStatus.pause ) {

            long simulationRouteId = json.simulationRouteId


            data = simulationThreadFrameworkService.getCarInfos( simulationRouteId, sessionId )
            data.featureId = "${simulationRouteId}"

            response.status = ResponseConstants.RESPONSE_STATUS_OK
        } else {

            data = [ error: "scheduler status is ${simulationThreadFrameworkService.getSchedulerStatus( sessionId )}" ]
            response.status = ResponseConstants.RESPONSE_STATUS_BAD_REQUEST

        }

        render "${(data as JSON).toString()}"
    }

    def getInfo() {

        def sessionId = WebUtils.retrieveGrailsWebRequest().session.id

        def json = request.JSON.data

        def data = [ : ]

        def info = []
        // LoadSimulationRouteCommand cmd = new LoadSimulationRouteCommand();
        // bindData( cmd, params )

        if ( simulationThreadFrameworkService.getSchedulerStatus( sessionId ) == SchedulerStatus.play ||
                simulationThreadFrameworkService.getSchedulerStatus( sessionId ) == SchedulerStatus.pause ) {

            json.each{

                long simulationRouteId = it.simulationRouteId
                def route = simulationThreadFrameworkService.getCarInfos2( simulationRouteId, sessionId )

                String featureId = it.featureId

                route.featureId = featureId


                info << route
            }

            data.info = info
            data.time = simulationThreadFrameworkService.getTime( sessionId )

            response.status = ResponseConstants.RESPONSE_STATUS_OK
        } else {
            def messages = []
            messages << "error, scheduler status is ${simulationThreadFrameworkService.getSchedulerStatus( sessionId )}"
            data.messages = messages
            response.status = ResponseConstants.RESPONSE_STATUS_BAD_REQUEST
        }

        response.addHeader( "Content-Type", "application/json" );

        render "${(data as JSON).toString()}"

    }

    def scaleSimulation() {

        def sessionId = WebUtils.retrieveGrailsWebRequest().session.id

        def json = request.JSON.data

        ScaleCommand cmd = new ScaleCommand()
        bindData( cmd, json )

        def data = [ : ]
        response.addHeader( "Content-Type", "application/json" );
        response.status = ResponseConstants.RESPONSE_STATUS_OK


        if ( !cmd.validate() ) {
            log.error( "failed ot get scale value: ${cmd.errors}" )
            data.message = "failed to get scale value: ${cmd.errors}"

        } else {

            def interval = Math.floor( 1000 / cmd.scaleValue ).intValue();

            if ( simulationThreadFrameworkService.getSchedulerStatus( sessionId ) == SchedulerStatus.play ||
                    simulationThreadFrameworkService.getSchedulerStatus( sessionId ) == SchedulerStatus.pause ) {

                // log.error( "try to scale with ${interval}" )
                simulationThreadFrameworkService.setScale( interval, sessionId )

            }

            data.message = "done"
        }

        render "${(data as JSON).toString()}"
    }
}

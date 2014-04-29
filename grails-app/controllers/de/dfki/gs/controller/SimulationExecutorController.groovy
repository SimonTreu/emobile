package de.dfki.gs.controller

import de.dfki.gs.controller.commands.SimulationCommand
import de.dfki.gs.controller.commands.SimulationExcecutorCommand
import de.dfki.gs.domain.Simulation
import de.dfki.gs.domain.SimulationRoute
import de.dfki.gs.simulation.SchedulerStatus
import de.dfki.gs.utils.ResponseConstants
import grails.converters.JSON
import org.codehaus.groovy.grails.core.io.ResourceLocator
import org.codehaus.groovy.grails.web.util.WebUtils
import org.springframework.core.io.Resource

class SimulationExecutorController {

    def simulationThreadFrameworkService
    def experimentStatsService
    def generateStatsPictureService
    ResourceLocator grailsResourceLocator



    def executeExperiment() {

        def m = [ : ]

        def sessionId = WebUtils.retrieveGrailsWebRequest().session.id

        m.sessionId = sessionId

        SimulationExcecutorCommand cmd = new SimulationExcecutorCommand()
        bindData( cmd, params )

        if ( cmd.validate() && !cmd.hasErrors() ) {

            Simulation simulation = Simulation.get( cmd.simulationId )


            log.debug( "sessionId: ${sessionId}" )

            m.simulationId = cmd.simulationId
            m.routeCount = SimulationRoute.countBySimulation( simulation )

            Double relativeSearchLimit = ( cmd.relativeSearchLimit / 100 )
            simulationThreadFrameworkService.init2( cmd.simulationId, null, sessionId, relativeSearchLimit )


            if ( simulationThreadFrameworkService.getSchedulerStatus( sessionId ) == SchedulerStatus.pause ) {

                // simulationThreadFrameworkService.proceedSimulation2( sessionId )
                // log.debug( "proceeded simulation" )

            } else if ( simulationThreadFrameworkService.getSchedulerStatus( sessionId ) == SchedulerStatus.init ) {

                // simulationThreadFrameworkService.runSimulation2( sessionId )
                log.debug( "started simulation" )

            }

            log.error( "hua1" )

        }

        render view: 'playSimulation', model: m
    }

    def proceedExperiment() {
        def sessionId = WebUtils.retrieveGrailsWebRequest().session.id

        SimulationCommand cmd = new SimulationCommand();
        bindData( cmd, params )

        if ( cmd.simulationId != null && !cmd.validate() ) {
            log.error( "failed to get simulation by id: ${cmd.simulationId} -- ${cmd.errors}" )
        } else {

            if ( simulationThreadFrameworkService.getSchedulerStatus( sessionId ).equals( SchedulerStatus.init ) ) {
                simulationThreadFrameworkService.runSimulation2( sessionId )
            }
            else if ( simulationThreadFrameworkService.getSchedulerStatus( sessionId ).equals( SchedulerStatus.pause ) ) {
                simulationThreadFrameworkService.proceedSimulation2( sessionId )
            }


            log.error( "started or proceeded simulation" )

        }

    }

    def pauseExperiment() {
        def sessionId = WebUtils.retrieveGrailsWebRequest().session.id

        SimulationCommand cmd = new SimulationCommand();
        bindData( cmd, params )

        if ( cmd.simulationId != null && !cmd.validate() ) {
            log.error( "failed to get simulation by id: ${cmd.simulationId} -- ${cmd.errors}" )
        } else {

            simulationThreadFrameworkService.pauseSimulation2( sessionId )
            log.error( "paused simulation" )

        }
    }

    def stopExperiment() {

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
                    simulationTime : m.fillingTypes.get( 0 )?.timeLivingList?.get( 0 ),
                    experimentResultId : experimentRunResultId
            ] )
        }

    }


    def getInfo() {

        def sessionId = WebUtils.retrieveGrailsWebRequest().session.id

        //String sessionId = params.sessionId

        def data = [ : ]

        if ( simulationThreadFrameworkService.getSchedulerStatus( sessionId ) == SchedulerStatus.play ||
                simulationThreadFrameworkService.getSchedulerStatus( sessionId ) == SchedulerStatus.pause ) {

            def info = simulationThreadFrameworkService.collectInfo( sessionId )

            def theTime = simulationThreadFrameworkService.getTime2( sessionId );
            info.currentTime = theTime

            data.info = info

            data.time = theTime

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


}

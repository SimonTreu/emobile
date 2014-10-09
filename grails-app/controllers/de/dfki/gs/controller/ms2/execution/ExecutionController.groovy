package de.dfki.gs.controller.ms2.execution


import de.dfki.gs.controller.ms2.execution.commands.ExperimentExecutionCommandObject
import de.dfki.gs.controller.ms2.execution.commands.ExperimentPanicStopCommand
import de.dfki.gs.controller.ms2.execution.commands.ExperimentProceedCommandObject
import de.dfki.gs.domain.simulation.Configuration
import de.dfki.gs.domain.simulation.FillingStation
import de.dfki.gs.domain.simulation.FillingStationGroup
import de.dfki.gs.domain.simulation.Fleet
import de.dfki.gs.simulation.SchedulerStatus
import de.dfki.gs.utils.ResponseConstants
import grails.converters.JSON
import org.codehaus.groovy.grails.web.util.WebUtils

class ExecutionController {

    def simulationExecutionService
    def statisticService

    def executeExperiment() {

        def m = [ : ]

        def sessionId = WebUtils.retrieveGrailsWebRequest().session.id



        ExperimentExecutionCommandObject cmd = new ExperimentExecutionCommandObject()
        bindData( cmd, params )

        if ( cmd.validate() && !cmd.hasErrors() ) {

            Configuration configuration = Configuration.get( cmd.configurationId )

            log.debug( "sessionId: ${sessionId}" )

            int routeCount = 0
            configuration.fleets.each { Fleet fleet ->
                fleet = Fleet.get( fleet.id )
                routeCount += fleet.cars.size()
            }
            int stationCount = 0

            configuration.fillingStationGroups.each { FillingStationGroup fillingStation ->
                    fillingStation = FillingStationGroup.get( fillingStation.id )
                    stationCount += fillingStation.fillingStations.size()
            }

            m.configurationId = cmd.configurationId
            m.sessionId = sessionId
            m.routeCount = routeCount
            m.stationCount = stationCount

            Double relativeSearchLimit = ( cmd.relativeSearchLimit / 100 )
            Long experimentRunResultId = simulationExecutionService.init( cmd.configurationId, null, sessionId, relativeSearchLimit )

            m.experimentRunResultId = experimentRunResultId

            log.error( "hua1" )

        }

        render view: 'playSimulation', model: m
    }

    /**
     * only takes the start of experiment from client side
     * never gets back to somewhere!!
     *
     * a button is needed which is activated, when experiment is finished!!
     *
     * @return NOTHING!
     */
    def proceedExperiment() {
        def sessionId = WebUtils.retrieveGrailsWebRequest().session.id

        def m = [ : ]

        ExperimentProceedCommandObject cmd = new ExperimentProceedCommandObject();
        bindData( cmd, params )

        cmd.relativeSearchLimit = 50

        Long simExpResultId = null

        def stats = [ : ]

        if ( cmd.configurationId != null && !cmd.validate() ) {
            log.error( "failed to get simulation by id: ${cmd.configurationId} -- ${cmd.errors}" )
        } else {

            simExpResultId = simulationExecutionService.runSimulation( sessionId, cmd.configurationId, cmd.experimentRunResultId )

            // all statistics from experiment
            //stats = statisticService.generateStatisticMapForExperiment( simExpResultId )

            log.error( "started or proceeded simulation" )

        }

        m.stats = stats

        // FIXME: DON'T DO THAT!! just on javaScript side. no chance to get back
        // TODO: solution: print button ( 'stats' ) which requests state from simulation process
        // redirect( controller: 'statistics', action: 'showStats', params: [ simulationExperimentResultId : simExpResultId ] )
    }

    def getInfo() {

        def sessionId = WebUtils.retrieveGrailsWebRequest().session.id

        //String sessionId = params.sessionId

        def data = [ : ]

        if ( simulationExecutionService.getSchedulerStatus( sessionId ) == SchedulerStatus.play ||
                simulationExecutionService.getSchedulerStatus( sessionId ) == SchedulerStatus.pause ) {

            def info = simulationExecutionService.collectInfo( sessionId )

            def theTime = simulationExecutionService.getTime( sessionId );
            info.currentTime = theTime

            data.info = info

            data.time = theTime

            data.experimentRunResultId = simulationExecutionService.getExperimentRunResultId( sessionId )

            response.status = ResponseConstants.RESPONSE_STATUS_OK
        } else {

            def messages = []
            messages << "scheduler status is ${simulationExecutionService.getSchedulerStatus( sessionId )}"
            data.messages = messages

            data.finished = "finished"

            response.status = ResponseConstants.RESPONSE_STATUS_OK
        }

        response.addHeader( "Content-Type", "application/json" );

        render "${(data as JSON).toString()}"

    }

    def stopExperiment() {

        log.debug( "params: ${params}" )

        def sessionId = WebUtils.retrieveGrailsWebRequest().session.id

        ExperimentPanicStopCommand cmd = new ExperimentPanicStopCommand();
        bindData( cmd, params )

        if ( cmd.configurationId != null && !cmd.validate() ) {
            log.error( "failed to get configuration by id: ${cmd.configurationId} -- ${cmd.errors}" )
        } else {

            simulationExecutionService.panicStop( cmd.configurationId, sessionId )

        }

        // go where we came from
        redirect( controller: 'configuration', action: 'index', params: [ configurationStubId: cmd.configurationId ] )

    }

}

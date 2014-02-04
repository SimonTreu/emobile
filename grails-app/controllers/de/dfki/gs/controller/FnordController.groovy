package de.dfki.gs.controller

import de.dfki.gs.controller.commands.LoadSimulationRouteCommand
import de.dfki.gs.utils.ResponseConstants
import grails.converters.JSON

class FnordController {

    def simulationCollectDataService


    def index() {



    }

    def loadSimulationConfiguration() {

        def json = request.JSON.data
        log.error( "params: ${params} json: ${json}" )

        // TODO: Commadn object validation
        long simulationId = json.simulationId

        // load simulation data
        def data = simulationCollectDataService.collectSimulationModelForRendering( simulationId, false )
        def bounds = [ : ]

        // load Berlin bounds
        bounds.top    = 52.6064  // top
        bounds.bottom = 52.4271  // bottom
        bounds.left   = 13.2643  // left
        bounds.right  = 13.5135  // right
        data.bounds = bounds


        response.status = ResponseConstants.RESPONSE_STATUS_OK
        response.addHeader( "Content-Type", "application/json" );

        render ( data as JSON ).toString()

    }

    def getDisplayBounds() {

        def json = request.JSON.data
        log.error( "params: ${params} json: ${json}" )

        def data = [ : ]
        def bounds = [ : ]
        bounds.top    = 52.6064  // top
        bounds.bottom = 52.4271  // bottom
        bounds.left   = 13.2643  // left
        bounds.right  = 13.5135  // right

        data.bounds = bounds

        response.status = ResponseConstants.RESPONSE_STATUS_OK
        response.addHeader( "Content-Type", "application/json" );

        render ( data as JSON ).toString()
    }


    def getCarRoute() {

        def json = request.JSON.data
        log.error( "params: ${params} json: ${json}" )

        LoadSimulationRouteCommand cmd = new LoadSimulationRouteCommand();
        bindData( cmd, json )
        def data = [ : ]

        if ( !cmd.validate() ) {
            log.error( "failed to get simulationRoute for id: ${cmd.simulationRouteId} -- errors: ${cmd.errors}" )
            data.errors = "failed to get simulationRoute for id: ${cmd.simulationRouteId}"

            response.status = ResponseConstants.RESPONSE_STATUS_BAD_REQUEST
        } else {

            data = simulationCollectDataService.collectWaypointsForSimulationRoute( cmd.simulationRouteId )

            def attributes = [ : ]
            attributes.route_type = 3
            attributes.id = 34
            attributes.head_sign = "Hua Town Street"
            data.attributes = attributes

            response.status = ResponseConstants.RESPONSE_STATUS_OK
        }

        response.addHeader( "Content-Type", "application/json" );

        render ( data as JSON ).toString()
    }

    def testAction() {

        log.error( "params: ${params}" )

        String callback = params.callback

        log.error( "callback: ${callback}" )

        def data = []


        def jsonData = (data as JSON).toString()
        def resp = callback + "(" + jsonData + ")"

        response.status = ResponseConstants.RESPONSE_STATUS_OK
        response.addHeader( "Content-Type", "text/html" );

        render resp


    }

}

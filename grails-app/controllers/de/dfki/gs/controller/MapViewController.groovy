package de.dfki.gs.controller

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Point
import de.dfki.gs.controller.commands.CheckDateCommand
import de.dfki.gs.controller.commands.SaveGasolineInfoCommand
import de.dfki.gs.controller.commands.SaveTrackInfoCommand
import de.dfki.gs.controller.commands.ShowGasolineInfoCommand
import de.dfki.gs.controller.commands.ShowTrackInfoCommand
import de.dfki.gs.controller.commands.SimulationCommand
import de.dfki.gs.controller.commands.StartAndDestinationsCommandObject
import de.dfki.gs.domain.CarType
import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.Simulation
import de.dfki.gs.domain.SimulationRoute
import de.dfki.gs.domain.Track
import de.dfki.gs.domain.TrackEdge
import de.dfki.gs.utils.LatLonPoint
import de.dfki.gs.utils.ResponseConstants
import grails.converters.JSON
import groovy.sql.Sql
import org.geotools.graph.path.Path
import org.geotools.graph.structure.Edge
import org.geotools.graph.structure.basic.BasicEdge
import org.geotools.graph.structure.basic.BasicNode
import org.opengis.feature.simple.SimpleFeature

class MapViewController {

    def routeService
    def realStationsStatsService
    def simulationCollectDataService
    def grailsApplication

    def googleMaps() {
        render view: 'googleMaps'
    }

    def openLayers() {
        render view: 'openLayers', model: [ 'id' : 3, 'name' : 'glenn' ]
    }

    def showPlaySimulation() {
        log.debug( "params openlayers: ${params}" )
        log.debug( "sessionId: ${request.requestedSessionId}" )


        // params.simulationId can be null
        try {
            Long simulationId = Long.parseLong( params.simulationId as String )

            SimulationCommand cmd = new SimulationCommand()
            bindData( cmd, params )

            if ( !cmd.validate() ) {
                log.error( "failed to get simulation for provided simulationId: ${cmd.simulationId} -- ${cmd.errors}" )
            }

            // def m =  simulationCollectDataService.collectSimulationModelForRendering( cmd.simulationId, true, true )



            redirect( controller: 'simulation', action: 'viewSimulation', params: [ simulationId : cmd.simulationId ] )
        } catch ( Exception e ) {
            log.error( "cannot cast param to Long: ${params.simulationId}", e )
        }

    }

    /**
     *
     *
     * @return
     */
    def openLayersWithAction() {
        log.debug( "params openlayers: ${params}" )
        log.debug( "sessionId: ${request.requestedSessionId}" )


        // params.simulationId can be null
        try {
            Long simulationId = Long.parseLong( params.simulationId as String )

            SimulationCommand cmd = new SimulationCommand()
            bindData( cmd, params )

            if ( !cmd.validate() ) {
                log.error( "failed to get simulation for provided simulationId: ${cmd.simulationId} -- ${cmd.errors}" )
            }

            def m =  simulationCollectDataService.collectSimulationModelForRendering( simulationId )

            render view: 'showRoutes', model: m
        } catch ( Exception e ) {
            log.error( "cannot parse string to long ${params.simulationId}", e  )
        }

    }

    def showRouters() {
        log.debug( "params openlayers: ${params}" )
        log.debug( "sessionId: ${request.requestedSessionId}" )
    }

    def openLayersMapsStatistik() {
        log.debug( "params openlayers: ${params}" )
        log.debug( "sessionId: ${request.requestedSessionId}" )


        // params.simulationId can be null
        try {
            Long simulationId = Long.parseLong( params.simulationId as String )

            SimulationCommand cmd = new SimulationCommand()
            bindData( cmd, params )

            if ( !cmd.validate() ) {
                log.error( "failed to get simulation for provided simulationId: ${cmd.simulationId} -- ${cmd.errors}" )
            }

            def m =  simulationCollectDataService.collectSimulationModelForRendering( simulationId )

            render view: 'showRoutes', model: m
        } catch ( Exception e ) {
            log.error( "cannot parse string to long ${params.simulationId}", e  )
        }

    }

    def index() {
        def gasoline = GasolineStation.list()
        [gasoline:gasoline]
    }

    def completeTask() {
        println "hua"
    }

    def listUsages() {

        log.error( "date params: ${params}" )

        CheckDateCommand cmd = new CheckDateCommand()
        bindData( cmd, params )


        GregorianCalendar oneWeekBeforeCal = new GregorianCalendar()
        oneWeekBeforeCal.add( Calendar.WEEK_OF_YEAR, -1 )
        Date fromDate = oneWeekBeforeCal.getTime()

        Date toDate = new Date()

        def m = [:]

        if ( cmd.validate() && !cmd.hasErrors() ) {
            // create Date objects...
            // fromDate = ...
            // GregorianCalendar...
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.set(Calendar.MINUTE, cmd.fromDate_minute);
            calendar.set(Calendar.HOUR, cmd.fromDate_hour);
            calendar.set(Calendar.DAY_OF_MONTH, cmd.fromDate_day);
            calendar.set(Calendar.MONTH, cmd.fromDate_month - 1);
            calendar.set(Calendar.YEAR, cmd.fromDate_year);

            fromDate = calendar.getTime()

            calendar.set(Calendar.MINUTE, cmd.toDate_minute);
            calendar.set(Calendar.HOUR, cmd.toDate_hour);
            calendar.set(Calendar.DAY_OF_MONTH, cmd.toDate_day );
            calendar.set(Calendar.MONTH, cmd.toDate_month - 1);
            calendar.set(Calendar.YEAR, cmd.toDate_year);

            toDate = calendar.getTime()

        }

        m = realStationsStatsService.getUsages( fromDate, toDate );
        m.fromDate = fromDate
        m.toDate = toDate

        render( view: 'openLayersMapsStatistik', model: m )
    }

    def showCoords() {
        println "coordsdd: ${params}"
    }

    def showInfo() {
        println "params: ${params}"
        render( es.lightBox(
                iframe: true,
                //width: 932,
                width: 800,
                height: 300,
                closeHref: "javascript:parent.hideLightBox()",
                controller: 'fileUpload',
                action: "index",
                linkParams: params
        ))
    }

    /**
     * either calculates and returns a route from start via destinations to last destination
     * or calculates a valid gasoline stop point found near to the point clicked
     *
     * @return
     */
    def calculateRoute() {

        log.error( "calculateRoute request: ${request.JSON.data}" )

        StartAndDestinationsCommandObject cmd = new StartAndDestinationsCommandObject();

        // what we get
        def json = request.JSON.data

        // what we return
        def data = [:]

        data.currentLat  = json.currentLat;
        data.currentLon  = json.currentLon;
        data.currentZoom = json.currentZoom;

        // could be either a gasoline station or a route start point
        cmd.startPoint = new LatLonPoint( json.startPoint.y, json.startPoint.x )

        // in any case we have a simulation id! if not, it should fail!
        cmd.simulationId = json.simulationId



        if ( json.type == "lineRoute" ) {

            def destinations = []

            json.destinationPoints.each {

                destinations << new LatLonPoint( it.y, it.x )

            }

            cmd.destinationPoints = destinations

            if ( !cmd.validate() ) {
                log.error( "failed: ${cmd.errors}" )
                return
            }


            def points = [];
            points << cmd.startPoint;
            cmd.destinationPoints.each { points << it }
            Long simulationRouteId = routeService.calculatePathFromCoordinates( cmd.simulationId, points )

            SimulationRoute simulationRoute = SimulationRoute.get( simulationRouteId )
            def route = [];
            data.routes = [];

            simulationRoute.edges.each { TrackEdge edge ->

                edge = TrackEdge.get( edge.id )

                route << [
                        fromX : edge.fromLon,
                        fromY : edge.fromLat,
                        toX   : edge.toLon,
                        toY   : edge.toLat
                ]

            }

            data.type = "lineRoute"
            data.routes << route;

            data.showTrackInfoLink = g.createLink( controller: 'mapView', action: 'showTrackInfo', params: [ simulationRouteId: simulationRoute.id ] )

        } else if ( json.type == "gasolinePoint"  ) {

            Coordinate nearestPoint = routeService.getNearestValidPoint( new Coordinate( cmd.startPoint.x, cmd.startPoint.y ) );
            data.type = "gasolinePoint"
            data.gasolinePoint = [ x: nearestPoint.x, y: nearestPoint.y ]

            GasolineStation gas = routeService.saveGasoline( nearestPoint, GasolineStationType.AC_22_2KW.toString() )
            if ( gas ) {
                data.gasolineId = gas.id;
            }


            data.showGasolineInfoLink = g.createLink( controller: 'mapView', action: 'showGasolineInfo', params: [ gasolineId: data.gasolineId ] )

            Long simId = json.simulationId;
            Simulation simulation = Simulation.get( simId );

            if ( simulation ) {

                GasolineStation gasolineStation = GasolineStation.get( data.gasolineId )
                gasolineStation.simulation = simulation
                if ( !gasolineStation.save( flush: true ) ) {
                    log.error( "failed to save gasoline station: ${gasolineStation.errors}" )
                }

                simulation.addToGasolineStations( gasolineStation )
                if ( !simulation.save( flush: true ) ) {
                    log.error( "failed to save simulation: ${simulation.errors}" )
                }

                data.simulationId = simulation.id
            }
        }

        response.status = ResponseConstants.RESPONSE_STATUS_OK
        response.addHeader( "Content-Type", "application/json" );

        render "${(data as JSON).toString()}"

    }

    def retrieveTypeForGasoline() {

        // what we get
        def json = request.JSON.data

        // what we return
        def data = [:]

        GasolineStation gasolineStation = GasolineStation.get( json.gasolineId )
        if ( gasolineStation ) {
            data.gasolineType = gasolineStation.type
        }

        response.status = ResponseConstants.RESPONSE_STATUS_OK
        response.addHeader( "Content-Type", "application/json" );

        render "${(data as JSON).toString()}"

    }

    def showGasolineInfo() {

        ShowGasolineInfoCommand cmd = new ShowGasolineInfoCommand()
        bindData( cmd, params )

        if ( !cmd.validate() ) {
            log.error( "failed to get gasoline station by id: ${cmd.gasolineId} -- errors: ${cmd.errors}" )
        }

        GasolineStation gasolineStation = GasolineStation.get( cmd.gasolineId )

        def m = [ : ]
        m.gasolineId = cmd.gasolineId
        m.gasolineTypes = GasolineStationType.values()*.toString()

        if ( gasolineStation.simulation ) m.selectedSimulationId = gasolineStation.simulation.id

        if ( gasolineStation.type ) m.selectedType = gasolineStation.type
        if ( gasolineStation.name ) m.name = gasolineStation.name

        render( view: '_showGasolineInfo', model: m )
    }

    def showTrackInfo() {

        ShowTrackInfoCommand cmd = new ShowTrackInfoCommand();
        bindData( cmd, params )

        if ( !cmd.validate() ) {
            log.error( "failed to get simulationRouteId: ${cmd.errors}" )

        }

        SimulationRoute simulationRoute = SimulationRoute.get( cmd.simulationRouteId )

        def m = [ : ]
        m.simulationRouteId = cmd.simulationRouteId
        m.carTypes = CarType.findAll()
        m.gasolineStations = GasolineStation.findAll()
        m.availableSimulations = Simulation.findAll()


        if ( simulationRoute ) {

            m.selectedCarType = simulationRoute.carType
            m.initialPersons = simulationRoute.initialPersons

            Simulation simulation = simulationRoute.simulation
            m.selectedSimulationId = simulation.id

        }

        render( view: '_showTrackInfo', model: m )
    }

    def deleteGasolineStation() {
        ShowGasolineInfoCommand cmd = new ShowGasolineInfoCommand();
        bindData( cmd, params )

        if ( !cmd.validate() ) {
            log.error( "failed to validate: ${cmd.errors}" )
        } else {

            GasolineStation gasolineStation = GasolineStation.get( cmd.gasolineId )

            Simulation simulation = gasolineStation.simulation
            if ( simulation ) {
                simulation.removeFromGasolineStations( gasolineStation )

                if ( !simulation.save( flush: true ) ) {
                    log.error( "failed to save simulation: ${simulation.errors}" )
                }

            }

            gasolineStation.delete( flush: true )

        }

        response.status = ResponseConstants.RESPONSE_STATUS_OK

        response.addHeader( "Content-Type", "application/json" );
        render "${([ message: 'gasoline station deleted' ] as JSON).toString()}"
    }

    def deleteTrack() {

        ShowTrackInfoCommand cmd = new ShowTrackInfoCommand();
        bindData( cmd, params )

        if ( !cmd.validate() ) {
            log.error( "failed to validate: ${cmd.errors}" )
        } else {

            Track track = Track.get( cmd.trackId )
            SimulationRoute simulationRoute = SimulationRoute.findByTrack( track )

            Simulation simulation = simulationRoute.simulation
            if ( simulation ) {
                simulation.removeFromSimulationRoutes( simulationRoute )
                if ( !simulation.save( flush: true ) ) {
                    log.error( "failed to remove simulation route from simulation: ${simulation.errors}" )
                }
            }


            if ( simulationRoute ) {
                simulationRoute.delete( flush: true )
            }
            track.delete( flush: true )

        }

        response.addHeader( "Content-Type", "application/json" );
        response.status = ResponseConstants.RESPONSE_STATUS_OK
        render "${([ message: 'track deleted' ] as JSON).toString()}"
    }

    def saveGasolineInfo() {

        log.debug( "params: ${params}" )

        SaveGasolineInfoCommand cmd = new SaveGasolineInfoCommand()
        bindData( cmd, params )

        if ( !cmd.validate() ) {
            log.error( "failed to validate: ${cmd.errors}" )
        }

        GasolineStation gasolineStation = GasolineStation.get( cmd.gasolineId )
        if ( cmd.selectedSimulationId != null && cmd.selectedSimulationId ==~ /\d+/ ) {

            Simulation simulation = Simulation.get( Long.parseLong( cmd.selectedSimulationId ) )

            gasolineStation.simulation = simulation
        }
        gasolineStation.name = cmd.gasolineName
        gasolineStation.type = cmd.gasolineType

        if ( !gasolineStation.save( flush: true ) ) {
            log.error( "failed to save gasoline station: ${gasolineStation.errors}" )
        }

        if ( cmd.selectedSimulationId != null && cmd.selectedSimulationId ==~ /\d+/ ) {
            Simulation simulation = Simulation.get( Long.parseLong( cmd.selectedSimulationId ) )
            simulation.addToGasolineStations( gasolineStation )
            if ( !simulation.save( flush: true ) ) {
                log.error( "failed to save simulation: ${simulation.errors}" )
            }
        }

        response.addHeader( "Content-Type", "application/json" );
        response.status = ResponseConstants.RESPONSE_STATUS_OK
        render "${([ message: 'station saved' ] as JSON).toString()}"
    }

    def saveTrackInfo() {

        log.debug( "saveTrackInfos: params: ${params}" )

        SaveTrackInfoCommand cmd = new SaveTrackInfoCommand();
        bindData( cmd, params )

        if ( !cmd.validate() ) {
            log.error( "validating trackinfo failed: ${cmd.errors}" )
        }

        SimulationRoute simulationRoute = SimulationRoute.get( cmd.simulationRouteId )

        if ( !simulationRoute ) {

            log.error( "there is no simulationRoute for id: ${cmd.simulationRouteId}" )

            // simulationRoute = new SimulationRoute()

        } else {

            simulationRoute.carType = CarType.get( cmd.carType )
            simulationRoute.initialEnergy = cmd.initialEnergy
            simulationRoute.initialPersons = cmd.initialPersons


            // simulationRoute.maxEnergy = grailsApplication.config.energyConfig.maxEnergy

            if ( !simulationRoute.save( flush: true ) ) {
                log.error( "failed to save simulation route: ${simulationRoute.errors}" )
            } else {
                log.debug( "simulationRoute: ${simulationRoute.id} saved!" )
            }


            response.addHeader( "Content-Type", "application/json" );
            response.status = ResponseConstants.RESPONSE_STATUS_OK
            render "${([ message: 'track saved' ] as JSON).toString()}"
        }
    }

}

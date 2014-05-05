package de.dfki.gs.controller

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Point
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
import org.geotools.graph.path.Path
import org.geotools.graph.structure.Edge
import org.geotools.graph.structure.basic.BasicEdge
import org.geotools.graph.structure.basic.BasicNode
import org.opengis.feature.simple.SimpleFeature

class MapViewController {

    def routeService
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

    def index() {
    }

    def completeTask() {
        println "hua"
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
        cmd.startPoint = new LatLonPoint( json.startPoint.x, json.startPoint.y )

        // in any case we have a simulation id! if not, it should fail!
        cmd.simulationId = json.simulationId



        if ( json.type == "lineRoute" ) {

            def destinations = []

            json.destinationPoints.each {

                destinations << new LatLonPoint( it.x, it.y )

            }

            cmd.destinationPoints = destinations

            if ( !cmd.validate() ) {
                log.error( "failed: ${cmd.errors}" )
                return
            }

            Simulation simulation = Simulation.get( cmd.simulationId )

            SimulationRoute simulationRoute = new SimulationRoute(
                    simulation: simulation,
                    initialPersons: 1,
                    carType: CarType.get( 1 )
            )

            if ( !simulationRoute.save( flush: true ) ) {
                log.error( "failed to save simulationRoute: ${simulationRoute.errors}" )
            }

            /*
            if ( !simulationRoute.save( flush: true ) ) {
                log.error( "failed to save simulation route: ${simulationRoute.errors}" )
            } else {

                log.error( "saved simulationRoute ${simulationRoute.id} for simulation ${simulation.id}" )

            }
            */
            simulation.addToSimulationRoutes( simulationRoute )
            if ( !simulation.save( flush: false ) ) {
                log.error( "failed to save simulation: ${simulation.errors}" )
            } else {

                log.debug( "saved simulationRoute ${simulationRoute.id} for simulation ${simulation.id}" )

            }


            def points = [];
            points << cmd.startPoint;
            cmd.destinationPoints.each { points << it }

            def pairs = points.collate( 2, 1, false );

            data.routes = [];
            data.type = "lineRoute"

            ArrayList<List<BasicEdge>> multiTargetRoute = new ArrayList<List<BasicEdge>>()

            List<BasicEdge> edgesToRender = new ArrayList<BasicEdge>()

            pairs.each {

                Coordinate currentStart  = new Coordinate( it[0].y, it[0].x );
                Coordinate currentTarget = new Coordinate( it[1].y, it[1].x );

                List<BasicEdge> pathEdges = routeService.calculatePath( currentStart, currentTarget );

                // repair all edges' direction
                pathEdges = routeService.repairEdges( pathEdges )

                multiTargetRoute.add( pathEdges )

                edgesToRender.addAll( pathEdges )

            }

            Point currentStartPoint
            Point currentTargetPoint

            def route = [];

            for ( BasicEdge edge : edgesToRender ) {

                currentStartPoint  = (Point) ((BasicNode) edge.nodeA).getObject();
                currentTargetPoint = (Point) ((BasicNode) edge.nodeB).getObject();

                route << [
                        fromX : currentStartPoint.x,
                        fromY : currentStartPoint.y,
                        toX : currentTargetPoint.x,
                        toY : currentTargetPoint.y
                ]

            }


            data.routes << route;

            simulationRoute = routeService.persistRoute( simulationRoute, multiTargetRoute );

            data.showTrackInfoLink = g.createLink( controller: 'mapView', action: 'showTrackInfo', params: [ trackId: data.trackId ] )


        } else if ( json.type == "gasolinePoint"  ) {

            Coordinate nearestPoint = routeService.getNearestValidPoint( new Coordinate( cmd.startPoint.x, cmd.startPoint.y ) );
            data.type = "gasolinePoint"
            data.gasolinePoint = [ x: nearestPoint.x, y: nearestPoint.y ]

            GasolineStation gas = routeService.saveGasoline( nearestPoint )
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
        m.availableSimulations = Simulation.findAll()

        if ( gasolineStation.simulation ) m.selectedSimulationId = gasolineStation.simulation.id
        if ( gasolineStation.type ) m.selectedType = gasolineStation.type
        if ( gasolineStation.name ) m.name = gasolineStation.name

        render( view: '_showGasolineInfo', model: m )
    }

    def showTrackInfo() {

        ShowTrackInfoCommand cmd = new ShowTrackInfoCommand();
        bindData( cmd, params )

        if ( !cmd.validate() ) {
            log.error( "failed to get trackId: ${cmd.errors}" )

        }

        Track track = Track.get( cmd.trackId )
        SimulationRoute simulationRoute = SimulationRoute.findByTrack( track );

        def m = [ : ]
        m.trackId = cmd.trackId
        m.carTypes = CarType.values()*.toString()
        m.availableSimulations = Simulation.findAll()

        // to chose the battery pack
        m.maxEnergies = grailsApplication.config.energyConfig.batteries.values().sort()


        def maxEnergy = grailsApplication.config.energyConfig.maxEnergy


        if ( simulationRoute ) {
            m.selectedCarType = simulationRoute.carType
            m.selectedEnergyDrain = simulationRoute.energyDrain
            m.initialEnergy = simulationRoute.initialEnergy
            m.initialPersons = simulationRoute.initialPersons

            m.selectedMaxEnergy = simulationRoute.maxEnergy
            maxEnergy = simulationRoute.maxEnergy

            Simulation simulation = simulationRoute.simulation
            m.selectedSimulationId = simulation.id
        } else {
            m.initialEnergy  = grailsApplication.config.energyConfig.initialEnergy
            m.initialPersons = grailsApplication.config.energyConfig.initialPersons

            m.selectedEnergyDrain = grailsApplication.config.energyConfig.batteryDrain

            m.selectedMaxEnergy = grailsApplication.config.energyConfig.maxEnergy
        }

        // def energies = ( 0..maxEnergy ).findAll { it%10 == 0 }
        def energies = ( 0..maxEnergy )
        m.energies = energies

        m.energyDrains = ( 20..100 )*.div( 2 )

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

        Track track = Track.get( cmd.trackId )
        SimulationRoute simulationRoute = SimulationRoute.findByTrack( track )

        if ( !simulationRoute ) {

            log.error( "there is no simulationRoute for Track: ${track.id}" )

            // simulationRoute = new SimulationRoute()

        } else {
            log.debug( "simulationRoute: ${simulationRoute.id} found for track: ${track.id}" )

            simulationRoute.carType = cmd.carType
            simulationRoute.initialEnergy = cmd.initialEnergy
            simulationRoute.initialPersons = cmd.initialPersons
            simulationRoute.track = track
            simulationRoute.maxEnergy = cmd.selectedMaxEnergy
            simulationRoute.energyDrain = cmd.selectedEnergyDrain

            // simulationRoute.maxEnergy = grailsApplication.config.energyConfig.maxEnergy

            if ( !simulationRoute.save( flush: true ) ) {
                log.error( "failed to save simulation route: ${simulationRoute.errors}" )
            } else {
                log.debug( "simulationRoute: ${simulationRoute.id} saved!" )
            }

            if ( cmd.selectedSimulationId ) {
                Simulation sim = Simulation.get( cmd.selectedSimulationId )
                sim.addToSimulationRoutes( simulationRoute )

                if ( !sim.save( flush: true ) ) {
                    log.error( "failed to save simulation: ${sim.errors}" )
                }
            }

            response.addHeader( "Content-Type", "application/json" );
            response.status = ResponseConstants.RESPONSE_STATUS_OK
            render "${([ message: 'track saved' ] as JSON).toString()}"
        }
    }

}

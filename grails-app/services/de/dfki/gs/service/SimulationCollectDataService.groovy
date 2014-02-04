package de.dfki.gs.service

import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.domain.Simulation
import de.dfki.gs.domain.SimulationRoute
import de.dfki.gs.domain.Track
import de.dfki.gs.domain.TrackEdge
import de.dfki.gs.domain.TrackEdgeType

class SimulationCollectDataService {

    def collectSimulations() {

        log.debug( "try to list all available simulations..." )

        List<Simulation> simulations = Simulation.findAll()

        log.debug( "found ${simulations.size()} simulations" )

        return simulations
    }

    def collectWaypointsForSimulationRoute( Long simulationRouteId ) {

        def track = [ : ]

        def waypoints = []


        SimulationRoute simulationRoute = SimulationRoute.get( simulationRouteId )

        track.carType = simulationRoute.carType

        simulationRoute.track.edges.each { TrackEdge trackEdge ->

            def point = [ : ]
            point.lat = trackEdge.fromLat
            point.lon = trackEdge.fromLon

            waypoints << point

        }

        track.waypoints = waypoints

        return track
    }

    /**
     * collects all relevant objects and infos for simulation
     *
     * @param simulationId
     * @return a map with all relevant infos, if simulationId is null or invalid an empty map is returned
     */
    def collectSimulationModelForRendering( Long simulationId, boolean trackAppended = true, boolean carInfos = false ) {

        def millis = System.currentTimeMillis()

        def m = [ : ]

        Simulation simulation = Simulation.read( simulationId )

        if ( simulation ) {
            m.simulationId = simulationId

            log.debug( "collect data for simulation id: ${m.simulationId}" )

            def routes = []
            def gasolineStations = []

            // def persistedGasStations = GasolineStation.findAllBySimulation( simulation );


            log.debug( "collect data for ${simulation.gasolineStations.size()} gasoline stations" )

            simulation.gasolineStations.each { GasolineStation gasolineStation ->

                def gasModel = [ : ]
                gasModel.gasolineId = gasolineStation.id
                gasModel.gasolineType = gasolineStation.type
                gasModel.fromX = gasolineStation.lat
                gasModel.fromY = gasolineStation.lon
                gasModel.name = gasolineStation.name

                gasolineStations << gasModel
            }
            m.gasolineStations = gasolineStations

            // def persistedRouteSimulation = SimulationRoute.findAllBySimulation( simulation )
            def persistedRouteSimulation = simulation.simulationRoutes

            log.debug( "collect data for ${persistedRouteSimulation.size()} simulation routes" )

            millis = System.currentTimeMillis()

            def counter = 0;


            long collectMillis = 0

            for ( SimulationRoute simulationRoute : persistedRouteSimulation ) {

                collectMillis = System.currentTimeMillis()

                def simRouteModel = [ : ]

                simRouteModel.id = simulationRoute.id
                simRouteModel.trackId = simulationRoute.track.id
                simRouteModel.carType = simulationRoute.carType.toString()
                simRouteModel.initialEnergy = simulationRoute.initialEnergy
                simRouteModel.initialPersons = simulationRoute.initialPersons

                if ( carInfos ) {

                    simRouteModel.batteryLevel = Math.round( (simulationRoute.initialEnergy / simulationRoute.maxEnergy) * 100 * 100 ) /100
                    simRouteModel.currentEnergyUsed = 0
                    simRouteModel.currentPrice = 0

                }

                log.debug( "trivia information for simulation route ${simulationRoute.id} took ${(System.currentTimeMillis()-collectMillis)} ms" )

                collectMillis = System.currentTimeMillis()

                List<TrackEdge> trackEdgeList = simulationRoute.track.edges
                List<TrackEdge> persistedEdgeList = new ArrayList<TrackEdge>()

                if ( trackAppended ) {
                    log.debug( "collecting track edges from db" )
                    long coEMillis = System.currentTimeMillis()

                    persistedEdgeList = TrackEdge.findAllBySimulationRouteId( simulationRoute.id )


                    // persistedEdgeList = TrackEdge.findAllByIdInList( trackEdgeList*.id )

                    /*
                    for ( TrackEdge ee : trackEdgeList ) {
                        persistedEdgeList.add( TrackEdge.read( ee.id ) )
                    }
                    */

                    log.debug( "..collecting ${persistedEdgeList.size()} trackEdges took ${(System.currentTimeMillis()-coEMillis)} ms" )
                }

                if ( trackAppended ) {
                    simRouteModel.edges = persistedEdgeList
                }
                log.debug( "append easy tracks for simulation route ${simulationRoute.id} took ${(System.currentTimeMillis()-collectMillis)} ms" )

                def route = [];
                def vias = [];

                collectMillis = System.currentTimeMillis()
                if ( trackAppended ) {

                    for ( TrackEdge edge : persistedEdgeList ) {

                        if ( edge ) {

                            if ( edge.type.toString() == TrackEdgeType.via_target.toString() ) {
                                vias << [
                                        fromX : edge.fromLon,
                                        fromY : edge.fromLat
                                ]
                            }

                            route << [
                                    fromX : edge.fromLat,
                                    fromY : edge.fromLon,
                                    toX : edge.toLat,
                                    toY : edge.toLon
                            ]

                        }

                    }

                    simRouteModel.vias = vias
                    simRouteModel.route = route
                }
                log.debug( "full track infos for simulation route ${simulationRoute.id} took ${(System.currentTimeMillis()-collectMillis)} ms" )

                if ( counter%1 == 0 ) {
                    log.debug( "added ${++counter} simulation routes yet.." )
                }

                routes << simRouteModel;

            }



            log.debug( "collection data for simulation routes took ${(System.currentTimeMillis() - millis)} ms" )

            m.routes = routes
        }

        return m
    }

    def serviceMethod() {

    }
}

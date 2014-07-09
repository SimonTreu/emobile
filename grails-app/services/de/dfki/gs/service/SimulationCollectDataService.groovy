package de.dfki.gs.service

import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.domain.simulation.Simulation
import de.dfki.gs.domain.simulation.SimulationRoute
import de.dfki.gs.domain.simulation.TrackEdge
import de.dfki.gs.domain.utils.TrackEdgeType
import grails.async.Promise

import static grails.async.Promises.waitAll

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

            def persistedRouteSimulation = simulation.simulationRoutes

            def counter = 0;

            /**
             * collect all trackEdges:
             */
            List<Promise> proms = new ArrayList<Promise>()

            List<Long> ids = simulation.simulationRoutes*.id

            log.error( "ids: ${ids}" )

            ids.each { Long l ->
                def promise = SimulationRoute.async.task {

                    log.error( "fetching for ${l}" )

                    def tes = TrackEdge.withCriteria {
                        eq( "simulationRouteId", l)
                    }

                }

                proms.add( promise )
            }

            def hua = waitAll( proms )
            def simRouteMap = [ : ]
            hua.flatten().each { TrackEdge te ->

                def l = simRouteMap.get( te.simulationRouteId )

                if ( l ) {
                    l << te
                } else {
                    def newL = []
                    newL << te
                    simRouteMap.put( te.simulationRouteId, newL )
                }

            }



            for ( SimulationRoute simulationRoute : persistedRouteSimulation ) {

                def simRouteModel = [ : ]

                simRouteModel.id = simulationRoute.id
                simRouteModel.simulationRouteId = simulationRoute.id
                simRouteModel.carType = simulationRoute.carType.toString()
                simRouteModel.initialEnergy = simulationRoute.initialEnergy
                simRouteModel.initialPersons = simulationRoute.initialPersons

                if ( carInfos ) {


                    // simRouteModel.batteryLevel = Math.round( (simulationRoute.initialEnergy / simulationRoute.maxEnergy) * 100 * 100 ) /100
                    simRouteModel.batteryLevel = 100
                    simRouteModel.currentEnergyUsed = 0
                    simRouteModel.currentPrice = 0

                }



                List<TrackEdge> persistedEdgeList = new ArrayList<TrackEdge>( (List<TrackEdge>) simRouteMap.get( simulationRoute.id ) )

                if ( trackAppended ) {
                    simRouteModel.edges = persistedEdgeList
                }


                def route = [];
                def vias = [];

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


                if ( counter%1 == 0 ) {
                    log.debug( "added ${++counter} simulation routes yet.." )
                }

                routes << simRouteModel;

            }

            m.routes = routes
        }

        return m
    }

    def serviceMethod() {

    }
}

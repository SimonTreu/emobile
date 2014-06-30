package de.dfki.gs.service

import de.dfki.gs.domain.CarType
import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.Simulation
import de.dfki.gs.domain.SimulationRoute
import de.dfki.gs.domain.TrackEdge
import de.dfki.gs.domain.stats.ExperimentRunResult
import de.dfki.gs.domain.stats.PersistedCarAgentResult
import de.dfki.gs.simulation.CarStatus
import grails.transaction.Transactional

@Transactional
class SimulationDataService {

    def routeService



    def addNCarAndRoutesWithFixedKmToSimulation( long routeCount, Long simulationId, double fixedKm, long carTypeId ) {

        Simulation simulation = Simulation.get( simulationId )
        CarType carType = CarType.get( carTypeId )

        long millis = System.currentTimeMillis()

        if ( simulation && carType ) {

            def countBefore = SimulationRoute.countBySimulationAndCarType( simulation, carType );
            def expected = countBefore + routeCount

            def countAfter = countBefore;
            while ( countAfter < expected ) {

                // routeService.createRandomRoutes( expected - countAfter, simulationId, targetsCount, carTypeId )

                routeService.createRandomFixedDistanceRoutes( expected - countAfter, simulationId, fixedKm, carTypeId )

                countAfter = SimulationRoute.countBySimulationAndCarType( simulation, carType );
            }

        }

        log.error( "found ${routeCount} routes in ${(System.currentTimeMillis()-millis)} ms" )

    }

    /**
     * adds count new cars with routes (with targetsCount targets) and carType to simulation with simulationId
     *
     *
     * @param simulationId
     * @param carTypeId
     * @param count
     */
    def addNCarAndRoutesToSimulation( long simulationId, long carTypeId, long count, long targetsCount ) {

        Simulation simulation = Simulation.get( simulationId )
        CarType carType = CarType.get( carTypeId )

        if ( simulation && carType ) {

            def countBefore = SimulationRoute.countBySimulationAndCarType( simulation, carType );
            def expected = countBefore + count

            def countAfter = countBefore;
            while ( countAfter < expected ) {

                routeService.createRandomRoutes( expected - countAfter, simulationId, targetsCount, carTypeId )

                countAfter = SimulationRoute.countBySimulationAndCarType( simulation, carType );
            }

        }

    }

    /**
     * adds count fillingstations to simulation
     *
     * @param simulationId
     * @param fillingType
     * @param count
     */
    def addNFillingStationInstancesToSimulation( long simulationId, String fillingType, long count ) {

        Simulation simulation = Simulation.get( simulationId )

        if ( simulation ) {

            def countBefore = GasolineStation.countBySimulationAndType( simulation, fillingType );
            def expected = countBefore + count
            def countAfter = countBefore

            while ( countAfter < expected ) {

                routeService.createRandomStations( expected - countAfter, simulationId, fillingType )

                countAfter = GasolineStation.countBySimulationAndType( simulation, fillingType )
            }

        }

    }


    def deleteAllFillingStationsOfSimulation( long simulationId, String fillingType ) {

        Simulation simulation = Simulation.get( simulationId )

        if ( simulation ) {

            List<GasolineStation> gasolineStationsToDelete = GasolineStation.findAllBySimulationAndType( simulation, fillingType );

            for ( GasolineStation gasolineStation : gasolineStationsToDelete ) {
                simulation.removeFromGasolineStations( gasolineStation )
            }

            if ( !simulation.save( flush: true ) ) {
                log.error( "failed to save simulation - ${simulation.errors}" )
            }
        }

    }

    def collectResults( long simulationId ) {

        def resultList = []

        List<ExperimentRunResult> results = ExperimentRunResult.findAllBySimulationId( simulationId )

        results.each { ExperimentRunResult result ->

            def local = [:]
            local.id = result.id

            local.successRate = Math.round( ( ( result.persistedCarAgentResults.count { PersistedCarAgentResult carAgentResult ->

                carAgentResult.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )

            } ) / result.persistedCarAgentResults.size() ) * 100 )

            local.fillingStations = result.persistedFillingStationResults.size()

            local.behaviour = ( Math.round( result.relativeSearchLimit * 100 ) )

            String gasolineType = result.persistedFillingStationResults.get(0)?.gasolineStationType

            local.optionValue = "${ Math.round( local.successRate )}% - ${local.fillingStations} FS ( ${gasolineType} ) - ${local.behaviour}%"

            resultList << local
        }

        // results.each { resultList << it.id }

        return resultList
    }

    def collectModelForEditSimulation( long simulationId ) {

        def m = [ : ]

        Simulation simulation = Simulation.get( simulationId )

        m.simulationName = simulation.name
        m.simulationId = simulation.id

        /**
         * collecting SimulationRoutes with carType -> count
         */
        def carTypes = CarType.findAll();
        def carTypesCars = [ : ]
        carTypes.each { CarType carType ->

            def carsPerCarType = SimulationRoute.countBySimulationAndCarType( simulation, carType )

            Double sumOfKm = SimulationRoute.findAllBySimulationAndCarType( simulation, carType ).sum { SimulationRoute route ->
                route.plannedDistance
            }
            /*
            double kms = 0;
            List<SimulationRoute> simulationRoutes = SimulationRoute.findAllBySimulationAndCarType( simulation, carType )
            for ( SimulationRoute simulationRoute : simulationRoutes ) {

                List<TrackEdge> edges = simulationRoute.track.edges
                for ( TrackEdge edge : edges ) {
                    kms+=edge.km
                }

            }

            def meanDistance = kms / carsPerCarType
            */
            def pp = [ : ]
            pp.count = carsPerCarType
            if ( carsPerCarType != null && carsPerCarType > 0 ) {
                pp.meanDistance = ( Math.round(  ( sumOfKm / carsPerCarType ) ) )
            } else {
                pp.meanDistance = 0
            }


            carTypesCars.put( carType, pp )
        }

        m.carTypeCars = carTypesCars

        /**
         * collecting routes count
         */
        m.routesCount = SimulationRoute.countBySimulation( simulation )

        /**
         * collecting gasoline stations
         */
        def gasolineStationTypes = GasolineStationType.values()
        def gasolineStationTypesStations = [ : ]
        gasolineStationTypes.each { GasolineStationType type ->
            def gasolineStationsPerType = GasolineStation.countBySimulationAndType( simulation, type.toString() )
            gasolineStationTypesStations.put( type, gasolineStationsPerType )
        }
        m.gasolineStationTypesStations = gasolineStationTypesStations

        return m
    }

}

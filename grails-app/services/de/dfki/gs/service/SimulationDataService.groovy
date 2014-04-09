package de.dfki.gs.service

import de.dfki.gs.domain.CarType
import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.Simulation
import de.dfki.gs.domain.SimulationRoute
import grails.transaction.Transactional

@Transactional
class SimulationDataService {

    def routeService



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
            carTypesCars.put( carType, carsPerCarType )
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

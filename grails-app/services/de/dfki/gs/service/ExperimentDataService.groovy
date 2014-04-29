package de.dfki.gs.service

import de.dfki.gs.domain.CarType
import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.Simulation
import de.dfki.gs.domain.stats.CarTypeCount
import de.dfki.gs.domain.stats.ExperimentRunResult
import de.dfki.gs.domain.stats.FillingStationTypeCount
import de.dfki.gs.domain.stats.PersistedCarAgentResult
import de.dfki.gs.domain.stats.PersistedFillingStationResult
import de.dfki.gs.model.elements.results.CarAgentResult
import de.dfki.gs.model.elements.results.EFillingStationAgentResult
import grails.transaction.Transactional

@Transactional
class ExperimentDataService {


    public long saveExperimentResult(
                    List<CarAgentResult> carAgentResults,
                    List<EFillingStationAgentResult> fillingAgentResults,
                    Double relativeSearchLimit,
                    long simTimeMillis ) {

        Long simulationId;
        int targetCount = 0;

        ExperimentRunResult experimentRunResult = new ExperimentRunResult(
                targetCount: targetCount,
                relativeSearchLimit: relativeSearchLimit,
                simTimeMillis: simTimeMillis
        )

        Map<CarType,Integer> carTypeCountMap = new HashMap<CarType,Integer>()

        for ( CarAgentResult carAgentResult : carAgentResults ) {

            if ( simulationId == null ) {
                simulationId = carAgentResult.simulationId;
                experimentRunResult.simulationId = simulationId
            }

            Integer count = carTypeCountMap.get( carAgentResult.carType )
            if ( count == null ) {
                carTypeCountMap.put( carAgentResult.carType, 1 );
            } else {
                carTypeCountMap.put( carAgentResult.carType, ( count + 1 ) );
            }


            PersistedCarAgentResult persistedCarAgentResult = new PersistedCarAgentResult(
                    energyConsumed:         carAgentResult.energyConsumed,
                    carType:                carAgentResult.carType,
                    carStatus:              carAgentResult.carAgentStatus,
                    plannedDistance:        carAgentResult.plannedDistance,
                    realDistance:           carAgentResult.realDistance,
                    targets:                carAgentResult.targets,
                    timeForPlannedDistance: carAgentResult.timeForPlannedDistance,
                    timeForRealDistance:    carAgentResult.timeForRealDistance,
                    timeForLoading:         carAgentResult.timeForLoading,
                    timeForDetour:          carAgentResult.timeForDetour,
                    energyLoaded:           carAgentResult.energyLoaded,
                    fillingStationsVisited: carAgentResult.fillingStationsVisited

            )

            if ( !persistedCarAgentResult.save( flush: true ) ) {

                log.error( "failed to save persistedCarAgentResult - ${persistedCarAgentResult.errors}" )

            } else {

                experimentRunResult.addToPersistedCarAgentResults( persistedCarAgentResult );

            }

        }

        for ( EFillingStationAgentResult result : fillingAgentResults ) {


            PersistedFillingStationResult persistedFillingStationResult = new PersistedFillingStationResult(
                    timeInUse: result.timeInUse,
                    timeLiving: result.simulationTime,
                    gasolineStationType: result.gasolineStationType
            )

            if ( !persistedFillingStationResult.save( flush: true ) ) {

                log.error( "failed to save persistedFillingStationResult - ${persistedFillingStationResult.errors}" )

            } else {

                experimentRunResult.addToPersistedFillingStationResults( persistedFillingStationResult )

            }

        }

        for ( Map.Entry<CarType,Integer> item : carTypeCountMap ) {

            CarTypeCount carTypeCount = new CarTypeCount( carType: item.key, countValue: item.value );

            if ( !carTypeCount.save( flush: true ) ) {
                log.error( "failed to save carTypeCount - ${carTypeCount.errors}" )
            } else {
                experimentRunResult.addToCarTypeCounts( carTypeCount );
            }

        }

        if ( simulationId != null ) {
            Simulation simulation = Simulation.get( simulationId );

            List<String> gasolineTypes = GasolineStationType.values()*.toString();

            for ( String gasolineType : gasolineTypes ) {

                int cc = GasolineStation.countBySimulationAndType( simulation, gasolineType );

                if ( cc != null && cc > 0 ) {
                    FillingStationTypeCount fillingStationTypeCount = new FillingStationTypeCount(
                            gasolineStationType: gasolineType,
                            countValue: cc
                    )

                    if ( !fillingStationTypeCount.save( flush: true ) ) {
                        log.error( "failed to save fillingStationTypeCount - ${fillingStationTypeCount.errors}" )
                    } else {
                        experimentRunResult.addToFillingStationTypeCounts( fillingStationTypeCount )
                    }

                }

            }
        }

        if ( !experimentRunResult.save( flush: true ) ) {
            log.error( "failed to save experimentResults - ${experimentRunResult.errors}" )
        }

        return experimentRunResult.id;
    }

}

package de.dfki.gs.ms2.data


import de.dfki.gs.domain.stats.ExperimentRunResult
import de.dfki.gs.domain.stats.PersistedCarAgentResult
import de.dfki.gs.domain.stats.PersistedFillingStationResult
import de.dfki.gs.model.elements.results.CarAgentResult
import de.dfki.gs.model.elements.results.EFillingStationAgentResult
import grails.transaction.Transactional

@Transactional
class ExperimentDataService {

    /**
     * for a given configuration there was a special experiment identified by configurationId
     * in carAgentResults all results for each car is saved
     * same same for fillingStations
     * simTimeMillis is the needed time to process the simulation in ms
     *
     * @param configurationId
     * @param carAgentResults
     * @param fillingAgentResults
     * @param simTimeMillis
     * @return the id of the persisted experiment result
     */
    public long saveExperimentResult(
            Long configurationId,
            List<CarAgentResult> carAgentResults,
            List<EFillingStationAgentResult> fillingAgentResults,
            long simTimeMillis ) {


        // stub object for results to be persisted
        ExperimentRunResult experimentRunResult = new ExperimentRunResult(
                simTimeMillis: simTimeMillis,
                configurationId: configurationId
        )


        for ( CarAgentResult carAgentResult : carAgentResults ) {

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

        if ( !experimentRunResult.save( flush: true ) ) {
            log.error( "failed to save experimentResults - ${experimentRunResult.errors}" )
        }

        return experimentRunResult.id;
    }


}

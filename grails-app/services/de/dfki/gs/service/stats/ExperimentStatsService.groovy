package de.dfki.gs.service.stats

import de.dfki.gs.domain.stats.CarTypeCount
import de.dfki.gs.domain.stats.ExperimentRunResult
import de.dfki.gs.domain.stats.FillingStationTypeCount
import de.dfki.gs.domain.stats.PersistedCarAgentResult
import de.dfki.gs.simulation.CarStatus
import grails.transaction.Transactional

@Transactional
class ExperimentStatsService {


    public ExperimentRunResult getResultForSimulationAndTargetAndFillingStationTypeCount(
            long simulationId,
            int targetCount,
            String fillingStationType,
            int fillingStationCount
    ) {

        Long experimentResultId = null;

        List<ExperimentRunResult> resultList = ExperimentRunResult.findAllBySimulationIdAndTargetCount( simulationId, targetCount );

        for ( ExperimentRunResult runResult : resultList ) {

            List<FillingStationTypeCount> fillingStationTypeCounts = runResult.fillingStationTypeCounts
            for ( FillingStationTypeCount fillingStationTypeCount : fillingStationTypeCounts ) {

                if ( fillingStationTypeCount.countValue == fillingStationCount
                            && fillingStationTypeCount.gasolineStationType.equals( fillingStationType ) ) {

                    experimentResultId = runResult.id
                }

            }


        }

        return getResultForExperiment( experimentResultId )
    }

    public ExperimentRunResult getResultForExperiment( Long experimentResultId ) {

        if ( experimentResultId == null ) {
            return null;
        }

        return ExperimentRunResult.get( experimentResultId )
    }

    def createStats( long experimentRunResultId ) {

        def m = [ : ]

        ExperimentRunResult experimentRunResult = ExperimentRunResult.get( experimentRunResultId )

        def carTypes = []

        if ( experimentRunResult ) {

            m.countTargetReached = experimentRunResult.persistedCarAgentResults.count { PersistedCarAgentResult result ->
                result.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
            }

            experimentRunResult.carTypeCounts.each { CarTypeCount carTypeCount ->

                def carType = [ : ]

                carType.carType = carTypeCount.carType.name
                carType.count = carTypeCount.countValue

                carType.countTargetReached = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult result ->
                    result.carType.name.equals( carTypeCount.carType.name )
                } . count { it.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() ) }

                carType.kmDrivenList = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult result ->
                    result.carType.name.equals( carTypeCount.carType.name )
                } *.realDistance

                carType.plannedDistanceList = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult result ->
                    result.carType.name.equals( carTypeCount.carType.name )
                } *.plannedDistance

                carType.realTimeUsedList = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult result ->
                    result.carType.name.equals( carTypeCount.carType.name )
                } *.timeForRealDistance

                carType.plannedTimeUsedList = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult result ->
                    result.carType.name.equals( carTypeCount.carType.name )
                } *.timeForPlannedDistance

                carType.timeForLoadingList = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult result ->
                    result.carType.name.equals( carTypeCount.carType.name )
                } *.timeForLoading

                carTypes << carType
            }
            m.carTypes = carTypes

        }


        return m;
    }





}

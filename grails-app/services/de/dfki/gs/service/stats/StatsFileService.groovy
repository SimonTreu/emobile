package de.dfki.gs.service.stats

import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.stats.ExperimentRunResult
import de.dfki.gs.domain.stats.PersistedCarAgentResult
import de.dfki.gs.domain.stats.PersistedFillingStationResult
import de.dfki.gs.simulation.CarStatus
import grails.transaction.Transactional

@Transactional
class StatsFileService {

    File getCsvFile( Long simulationId ) {

        List<ExperimentRunResult> experimentRunResultList = ExperimentRunResult.findAllBySimulationId( simulationId )

        StringBuilder sb = new StringBuilder()

        experimentRunResultList.each { ExperimentRunResult experimentRunResult ->

            int fillingStationCount = experimentRunResult.persistedFillingStationResults.size()

            PersistedFillingStationResult persistedFillingStationResult = PersistedFillingStationResult.get( experimentRunResult.persistedFillingStationResults.get( 0 ).id )
            String powerType = persistedFillingStationResult.gasolineStationType

            double power = 0;
            switch ( powerType ) {
                case GasolineStationType.AC_2_3KW.toString() :
                    power = 2.3
                    break
                case GasolineStationType.AC_3_7KW.toString() :
                    power = 3.7
                    break
                case GasolineStationType.AC_7_4KW.toString() :
                    power = 7.4
                    break
                case GasolineStationType.AC_11_1KW.toString() :
                    power = 11.1
                    break
                case GasolineStationType.AC_22_2KW.toString() :
                    power = 22.2
                    break
                case GasolineStationType.AC_43KW.toString() :
                    power = 43
                    break
                case GasolineStationType.DC_49_8KW.toString() :
                    power = 49.8
                    break
            }

            // to have readable percent
            double behaviour = experimentRunResult.relativeSearchLimit * 100


            experimentRunResult.persistedCarAgentResults.each { PersistedCarAgentResult persistedCarAgentResult ->

                PersistedCarAgentResult result = PersistedCarAgentResult.get( persistedCarAgentResult.id )
                int success = 1
                if ( !result.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() ) ) {
                    success = 0;
                }

                sb.append( fillingStationCount ).append( ";" ).append( power ).append( ";" ).append( behaviour ).append( ";" ).append( success ).append( "\n" )

            }

        }

        UUID uuid = UUID.randomUUID()
        final File file = new File( "/tmp/csv-stats-${uuid}.csv" );

        file.withWriter{ it << sb.toString() }

        return file
    }
}

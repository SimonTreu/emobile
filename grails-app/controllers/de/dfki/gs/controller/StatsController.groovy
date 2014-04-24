package de.dfki.gs.controller

import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.stats.CarTypeCount
import de.dfki.gs.domain.stats.ExperimentRunResult
import de.dfki.gs.domain.stats.PersistedCarAgentResult
import de.dfki.gs.domain.stats.PersistedFillingStationResult
import de.dfki.gs.model.elements.results.CarAgentResult
import de.dfki.gs.simulation.CarStatus
import org.apache.commons.io.FileUtils

class StatsController {


    def showStats() {

        String timeStatsPictureName = "/tmp/${params.timeFileUUID}"
        String distanceStatsPictureName = "/tmp/${params.distanceFileUUID}"
        String fillingStatsPictureName = "/tmp/${params.fillingFileUUID}"

        String experimentResultIdString = params.experimentResultId;

        Long experimentResultId = null
        try {
            experimentResultId = Long.parseLong( experimentResultIdString )
        } catch ( NumberFormatException nfe ) {
            log.error( "coudn't read id from ${experimentResultIdString}", nfe )
        }


        def m = [
                    timeFileUUID : timeStatsPictureName,
                    distanceFileUUID : distanceStatsPictureName,
                    fillingStationFileUUID : fillingStatsPictureName
        ]

        if ( experimentResultId != null ) {

            ExperimentRunResult experimentRunResult = ExperimentRunResult.get( experimentResultId )

            m.carAgentsCount = experimentRunResult.persistedCarAgentResults.size()
            m.fillingStationCount = experimentRunResult.persistedFillingStationResults.size()

            def carTypes = [:]
            def carCountTotal = 0;
            def carTargetReachedTotal = 0;

            experimentRunResult.carTypeCounts.each { CarTypeCount carTypeCount ->

                def ccc = [:]
                ccc.count = carTypeCount.countValue

                carCountTotal += carTypeCount.countValue

                def tRTotal = experimentRunResult.persistedCarAgentResults.count { PersistedCarAgentResult res ->
                    res.carType.toString().equals( carTypeCount.carType.toString() ) && res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                }
                carTargetReachedTotal += tRTotal
                ccc.targetReached = tRTotal

                carTypes.put( carTypeCount.carType.name, ccc )

            }
            def cATtotal = [:]
            cATtotal.count = carCountTotal
            cATtotal.targetReached = carTargetReachedTotal

            carTypes.put( "all", cATtotal )

            m.carTypes = carTypes

            def fillingStations = [:]

            def usageTotal = 0
            def countTotal = 0
            GasolineStationType.values().each { GasolineStationType type ->

                def ccc = [:]

                int cc = experimentRunResult.persistedFillingStationResults.count { PersistedFillingStationResult res ->
                    res.gasolineStationType.equals( type.toString() )
                }
                countTotal += cc
                ccc.count = cc;

                def usage = 0;
                int cU = 0

                experimentRunResult.persistedFillingStationResults.each { PersistedFillingStationResult res ->
                    if ( res.gasolineStationType.equals( type.toString() ) ) {
                        usage += res.timeInUse

                        if ( res.timeInUse > 0 ) {
                            cU++;
                        }

                    }
                }

                usage =  ( usage / 3600 )
                usageTotal += usage

                ccc.usage = Math.round( usage * 100 ) / 100

                ccc.countUsed = cU

                if ( cc > 0 ) {
                    fillingStations.put( type.toString(), ccc )
                }


            }

            def stTotal = [:]
            stTotal.count = countTotal
            stTotal.usage = Math.round( usageTotal * 100 ) / 100
            fillingStations.put( "all",  stTotal )


            m.fillingStations = fillingStations
        }

        int carsCount = params.carsCount as int
        int countTargetReached = params.countTargetReached as int
        double simulationTime = params.simulationTime as double

        double percentageReached = ( countTargetReached / carsCount ) * 100

        m.carsCount = carsCount
        m.countTargetReached = countTargetReached
        m.percentageReached = percentageReached

        m.simulationTime = Math.round( ( simulationTime / 3600 ) *100 ) /100

        render view: 'stats', model: m
    }

    def showStatsFile() {

        String statsPicturePath = params.fileUUID

        File file = new File( statsPicturePath )

        byte[] imageBytes = file.bytes

        response.contentType = 'image/png'
        response.contentLength = imageBytes.size()

        OutputStream out = response.outputStream
        out.write( imageBytes )

        out.close()

        FileUtils.deleteQuietly( file )

    }


}

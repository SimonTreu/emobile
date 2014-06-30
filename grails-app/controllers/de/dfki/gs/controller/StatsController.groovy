package de.dfki.gs.controller

import de.dfki.gs.domain.CarType
import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.Simulation
import de.dfki.gs.domain.stats.CarTypeCount
import de.dfki.gs.domain.stats.ExperimentRunResult
import de.dfki.gs.domain.stats.PersistedCarAgentResult
import de.dfki.gs.domain.stats.PersistedFillingStationResult
import de.dfki.gs.model.elements.results.CarAgentResult
import de.dfki.gs.simulation.CarStatus
import de.dfki.gs.utils.TimeCalculator
import org.apache.commons.io.FileUtils
import org.geotools.coverage.processing.operation.Exp

class StatsController {

    def experimentStatsService
    def generateStatsPictureService
    def statsFileService


    def createCsv() {

        Long simulationId = Long.parseLong( params.simulationId )

        File csvFile = statsFileService.getCsvFile( simulationId )

        // TODO: test!
        response.setHeader "Content-disposition", "attachment; filename=${simulationId}-results.csv"
        response.contentType = 'text/csv'
        response.outputStream << csvFile.text
        response.outputStream.flush()

        FileUtils.deleteQuietly( csvFile )
    }

    def detourTimePicture() {
        log.error( "params: ${params}" )

        Long simulationId = Long.parseLong( params.simulationId )

        Long relativeSearchLimit = Long.parseLong( params.relativeSearchLimit )

        boolean withLoading = params.withLoading

        def m = experimentStatsService.createStatsForTimeClassAndRelativeSearchLimit( simulationId, relativeSearchLimit as Double, withLoading )

        File detourFile = generateStatsPictureService.createDataChartFileForDetourTimeDetails( m, Math.round( relativeSearchLimit ) as Integer, withLoading );

        byte[] imageBytes = detourFile.bytes

        response.contentType = 'image/png'
        response.contentLength = imageBytes.size()

        OutputStream out = response.outputStream
        out.write( imageBytes )

        out.close()

        FileUtils.deleteQuietly( detourFile )
    }

    def detourPicture() {

        log.error( "params: ${params}" )

        Long simulationId = Long.parseLong( params.simulationId )

        Long relativeSearchLimit = Long.parseLong( params.relativeSearchLimit )



        def m = experimentStatsService.createStatsForDistanceClassAndRelativeSearchLimit( simulationId, relativeSearchLimit as Double )

        File detourFile = generateStatsPictureService.createDataChartFileForDetourDetails( m, Math.round( relativeSearchLimit ) as Integer );

        byte[] imageBytes = detourFile.bytes

        response.contentType = 'image/png'
        response.contentLength = imageBytes.size()

        OutputStream out = response.outputStream
        out.write( imageBytes )

        out.close()

        FileUtils.deleteQuietly( detourFile )
    }

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

        double relativeSearchLimit = 0;


        def plannedDistanceClasses = [
                '  0 - 99'  : 0..99,
                '100 - 199' : 100..199,
                '200 - 299' : 200..299,
                '300 - 399' : 300..399,
                '400 - 499' : 400..499,
                '    > 500' : 500..1000
        ]


        if ( experimentResultId != null ) {

            ExperimentRunResult experimentRunResult = ExperimentRunResult.get( experimentResultId )

            List<ExperimentRunResult> allResults = ExperimentRunResult.findAllBySimulationId( experimentRunResult.simulationId )
            def relativeSearchLimits = []
            allResults.each { ExperimentRunResult experimentRunResult1 ->

                int rel = Math.round( experimentRunResult1.relativeSearchLimit * 100 ) as Integer
                if ( !relativeSearchLimits.contains( rel ) ) {
                    relativeSearchLimits.add( rel )
                }

            }

            m.relativeSearchLimits = relativeSearchLimits


            m.simulationId = experimentRunResult.simulationId

            m.experimentResultId = experimentResultId

            m.relativeSearchLimit = experimentRunResult.relativeSearchLimit
            m.simTimeMillis = experimentRunResult.simTimeMillis

            m.carAgentsCount = experimentRunResult.persistedCarAgentResults.size()
            m.fillingStationCount = experimentRunResult.persistedFillingStationResults.size()

            def carTypes = [:]
            def carCountTotal = 0;
            def carTargetReachedTotal = 0;

            // for each carType:
            experimentRunResult.carTypeCounts.each { CarTypeCount carTypeCount ->

                def ccc = [:]
                def details = []

                ccc.count = carTypeCount.countValue

                carCountTotal += carTypeCount.countValue

                CarType carTypeIterator = CarType.get( carTypeCount.carType.id )

                def tRTotal = experimentRunResult.persistedCarAgentResults.count { PersistedCarAgentResult res ->
                    res.carType.id == carTypeIterator.id && res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                }
                carTargetReachedTotal += tRTotal
                ccc.targetReached = tRTotal

                // detail stats:
                // all distance groups:
                plannedDistanceClasses.each {

                    def detail = [:]

                    detail.plannedDistanceClass = it.key
                    detail.count = experimentRunResult.persistedCarAgentResults.count { PersistedCarAgentResult res ->
                        res.carType.toString().equals( carTypeCount.carType.toString() ) &&
                                ( Math.round( res.plannedDistance ) as Integer ) in it.value
                    }
                    detail.targetReached = experimentRunResult.persistedCarAgentResults.count { PersistedCarAgentResult res ->
                        res.carType.toString().equals( carTypeCount.carType.toString() ) &&
                                ( Math.round( res.plannedDistance ) as Integer ) in it.value &&
                                res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                    }

                    detail.successRate = ( detail.count > 0 )? Math.round( ( ( detail.targetReached as Integer ) / ( detail.count as Integer ) ) * 100 ):'n.a.'

                    // in hours
                    Long timeForLoadingSum = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult res ->
                        res.carType.toString().equals( carTypeCount.carType.toString() ) &&
                                ( Math.round( res.plannedDistance ) as Integer ) in it.value &&
                                res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                    }.sum { PersistedCarAgentResult pca -> pca.timeForLoading } as Long
                    detail.meanTimeForLoading = (detail.targetReached > 0)? (
                            TimeCalculator.readableTime( ( timeForLoadingSum / ( detail.targetReached as Integer ) ) , false )
                    ) : 'n.a.'



                    // in km
                    /*
                    Double realDistanceSum1 = experimentRunResult.persistedCarAgentResults.sum { PersistedCarAgentResult res ->
                        (res.carType.toString().equals( carTypeCount.carType.toString() ) &&
                                ( Math.round( res.plannedDistance ) as Integer ) in it.value &&
                                res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() ))?res.realDistance:0

                    }
                    */

                    /*
                    Double realDistanceSum =
                            ( experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult res ->
                                res.carType.toString().equals( carTypeCount.carType.toString() ) &&
                                        ( Math.round( res.plannedDistance ) as Integer ) in it.value &&
                                        res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                            }.sum { PersistedCarAgentResult pca -> pca.realDistance } ) as Double
                    */

                    Double realDistanceSum = experimentRunResult.persistedCarAgentResults.sum { PersistedCarAgentResult res ->

                        ( res.carType.toString().equals( carTypeCount.carType.toString() ) &&
                                ( Math.round( res.plannedDistance ) as Integer ) in it.value &&
                                res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                        )?res.realDistance:0d

                    }

                    detail.meanRealDistance = (detail.targetReached > 0)? Math.round( realDistanceSum / ( detail.targetReached as Integer ) ) : 'n.a.'


                    /*
                    Double diffRealPlannedDistanceSum =
                            ( experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult res ->

                                res.carType.toString().equals( carTypeCount.carType.toString() ) &&
                                        ( Math.round( res.plannedDistance ) as Integer ) in it.value &&
                                        res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )

                            }.sum { PersistedCarAgentResult pca -> ( Math.max( pca.realDistance - pca.plannedDistance, 0 ) ) } ) as Double
                    */

                    Double diffRealPlannedDistanceSum = experimentRunResult.persistedCarAgentResults.sum { PersistedCarAgentResult res ->

                        ( res.carType.toString().equals( carTypeCount.carType.toString() ) &&
                                ( Math.round( res.plannedDistance ) as Integer ) in it.value &&
                                res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() ) )?( res.realDistance - res.plannedDistance ):0

                    }

                    detail.meanDiffRealPlannedDistance = ( detail.targetReached > 0 )? ( diffRealPlannedDistanceSum / ( detail.targetReached as Integer ) ) : 'n.a.'


                    // in hours
                    /*
                    Long meanDiffRealPlannedTimeSum = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult res ->
                        res.carType.toString().equals( carTypeCount.carType.toString() ) &&
                                ( Math.round( res.plannedDistance ) as Integer ) in it.value &&
                                res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                    }.sum { PersistedCarAgentResult pca -> ( Math.abs( pca.timeForRealDistance - pca.timeForPlannedDistance ) ) } as Long
                    */
                    Long meanDiffRealPlannedTimeSum = experimentRunResult.persistedCarAgentResults.sum { PersistedCarAgentResult res ->

                        ( res.carType.toString().equals( carTypeCount.carType.toString() ) &&
                                ( Math.round( res.plannedDistance ) as Integer ) in it.value &&
                                res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() ) )?( res.timeForRealDistance - res.timeForPlannedDistance ):0

                    }


                    detail.meanDiffRealPllanedTime = (detail.targetReached > 0)? (
                            TimeCalculator.readableTime( ( meanDiffRealPlannedTimeSum / ( detail.targetReached as Integer ) ), false )
                    ) : 'n.a.'


                    Long meanPlannedTimeSum = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult res ->
                        res.carType.toString().equals( carTypeCount.carType.toString() ) &&
                                ( Math.round( res.plannedDistance ) as Integer ) in it.value &&
                                res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                    }.sum { PersistedCarAgentResult pca -> ( pca.timeForPlannedDistance ) } as Long

                    detail.meanPlannedTime = (detail.targetReached > 0)? (
                            TimeCalculator.readableTime( ( meanPlannedTimeSum / ( detail.targetReached as Integer ) ), false )
                    ) : 'n.a.'

                    Long meanRealTimeSum = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult res ->
                        res.carType.toString().equals( carTypeCount.carType.toString() ) &&
                                ( Math.round( res.plannedDistance ) as Integer ) in it.value &&
                                res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                    }.sum { PersistedCarAgentResult pca -> ( pca.timeForRealDistance ) } as Long

                    detail.meanRealTime = (detail.targetReached > 0)? (
                            TimeCalculator.readableTime( ( meanRealTimeSum / ( detail.targetReached as Integer ) ), false )
                    ) : 'n.a.'


                    details << detail
                }



                ccc.details = details

                carTypes.put( carTypeCount.carType.name, ccc )

            }
            def cATtotal = [:]
            cATtotal.count = carCountTotal
            cATtotal.targetReached = carTargetReachedTotal

            // details:
            // detail stats:
            // all distance groups:
            def details = []
            plannedDistanceClasses.each {

                def detail = [:]

                detail.plannedDistanceClass = it.key
                detail.count = experimentRunResult.persistedCarAgentResults.count { PersistedCarAgentResult res ->
                    ( Math.round( res.plannedDistance ) as Integer ) in it.value
                }
                detail.targetReached = experimentRunResult.persistedCarAgentResults.count { PersistedCarAgentResult res ->
                    ( Math.round( res.plannedDistance ) as Integer ) in it.value &&
                            res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                }

                detail.successRate = ( detail.count > 0 )? Math.round( ( ( detail.targetReached as Integer ) / ( detail.count as Integer ) ) * 100 ):'n.a.'


                Long timeForLoadingSum = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult res ->
                    ( Math.round( res.plannedDistance ) as Integer ) in it.value &&
                            res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                }.sum { PersistedCarAgentResult pca -> pca.timeForLoading } as Long
                detail.meanTimeForLoading = (detail.targetReached > 0)?
                        TimeCalculator.readableTime( timeForLoadingSum / ( detail.targetReached as Long ) , false ) : 'n.a.'

                // in km
                Double realDistanceSum = ( experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult res ->
                    ( Math.round( res.plannedDistance ) as Integer ) in it.value &&
                            res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                }.sum { PersistedCarAgentResult pca -> pca.realDistance } ) as Double
                detail.meanRealDistance = (detail.targetReached > 0)? Math.round( realDistanceSum / ( detail.targetReached as Integer ) ) : 'n.a.'

                Double diffRealPlannedDistanceSum =
                        ( experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult res ->
                            ( Math.round( res.plannedDistance ) as Integer ) in it.value &&
                                    res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )

                        }.sum { PersistedCarAgentResult pca -> ( Math.abs( pca.realDistance - pca.plannedDistance ) ) } ) as Double
                detail.meanDiffRealPlannedDistance = ( detail.targetReached > 0 )? ( diffRealPlannedDistanceSum / ( detail.targetReached as Integer ) ) : 'n.a.'


                // in hours
                Long meanDiffRealPlannedTimeSum = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult res ->
                    ( Math.round( res.plannedDistance ) as Integer ) in it.value &&
                            res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                }.sum { PersistedCarAgentResult pca -> ( pca.timeForRealDistance - pca.timeForPlannedDistance ) } as Long
                detail.meanDiffRealPllanedTime = (detail.targetReached > 0)? (
                        TimeCalculator.readableTime( ( meanDiffRealPlannedTimeSum / ( detail.targetReached as Integer ) ), false )
                ) : 'n.a.'

                Long meanPlannedTimeSum = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult res ->
                    ( Math.round( res.plannedDistance ) as Integer ) in it.value &&
                            res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                }.sum { PersistedCarAgentResult pca -> ( pca.timeForPlannedDistance ) } as Long

                detail.meanPlannedTime = (detail.targetReached > 0)? (
                        TimeCalculator.readableTime( ( meanPlannedTimeSum / ( detail.targetReached as Integer ) ), false )
                ) : 'n.a.'

                Long meanRealTimeSum = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult res ->
                    ( Math.round( res.plannedDistance ) as Integer ) in it.value &&
                            res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                }.sum { PersistedCarAgentResult pca -> ( pca.timeForRealDistance ) } as Long

                detail.meanRealTime = (detail.targetReached > 0)? (
                        TimeCalculator.readableTime( ( meanRealTimeSum / ( detail.targetReached as Integer ) ), false )
                ) : 'n.a.'

                details << detail
            }

            cATtotal.details = details

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

    def showDetailsPicture() {

        log.error( "params: ${params}" )

        String timeDistanceOption = ( params.time != null )?"time":"distance"

        def m = experimentStatsService.createStatsForDetailPicture( params.experimentResultId as Long, params.carType )

        // File file = generateStatsPictureService
        File distanceFile = generateStatsPictureService.createDataChartFileForDistanceDetails( m, timeDistanceOption  );

        byte[] imageBytes = distanceFile.bytes

        response.contentType = 'image/png'
        response.contentLength = imageBytes.size()

        OutputStream out = response.outputStream
        out.write( imageBytes )

        out.close()

        FileUtils.deleteQuietly( distanceFile )

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

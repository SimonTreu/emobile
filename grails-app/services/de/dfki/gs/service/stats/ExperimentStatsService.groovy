package de.dfki.gs.service.stats

import de.dfki.gs.domain.CarType
import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.stats.CarTypeCount
import de.dfki.gs.domain.stats.ExperimentRunResult
import de.dfki.gs.domain.stats.FillingStationTypeCount
import de.dfki.gs.domain.stats.PersistedCarAgentResult
import de.dfki.gs.domain.stats.PersistedFillingStationResult
import de.dfki.gs.model.stats.SimulationConfiguration
import de.dfki.gs.simulation.CarStatus
import de.dfki.gs.utils.TimeCalculator
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

    /**
     * creates the statistics of one simulation experiment
     *
     * only cars, which arrived their goal are included!
     *
     * @param experimentRunResultId
     * @return
     */
    def createStats( long experimentRunResultId ) {

        def m = [ : ]

        ExperimentRunResult experimentRunResult = ExperimentRunResult.get( experimentRunResultId )

        def carTypes = []

        def fillingTypes = []

        if ( experimentRunResult ) {


            def allFillingTypes = [ : ]
            allFillingTypes.type = "all"
            allFillingTypes.timeInUseList = experimentRunResult.persistedFillingStationResults*.timeInUse
            allFillingTypes.timeLivingList = experimentRunResult.persistedFillingStationResults*.timeLiving

            fillingTypes << allFillingTypes


            m.countTargetReached = experimentRunResult.persistedCarAgentResults.count { PersistedCarAgentResult result ->
                result.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
            }

            def allTypes = [ : ]
            allTypes.carType = "all"
            allTypes.count = experimentRunResult.carTypeCounts.sum { it.countValue }
            allTypes.countTargetReached = experimentRunResult.persistedCarAgentResults.count {
                it.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
            }

            // allTypes.kmDrivenList = experimentRunResult.persistedCarAgentResults*.realDistance
            allTypes.kmDrivenList = experimentRunResult.persistedCarAgentResults.findAll {
                it.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
            }*.realDistance

            // allTypes.plannedDistanceList = experimentRunResult.persistedCarAgentResults*.plannedDistance
            allTypes.plannedDistanceList = experimentRunResult.persistedCarAgentResults.findAll {
                it.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
            }*.plannedDistance

            // allTypes.realTimeUsedList = experimentRunResult.persistedCarAgentResults*.timeForRealDistance
            allTypes.realTimeUsedList = experimentRunResult.persistedCarAgentResults.findAll {
                it.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
            }*.timeForRealDistance


            // allTypes.plannedTimeUsedList = experimentRunResult.persistedCarAgentResults*.timeForPlannedDistance
            allTypes.plannedTimeUsedList = experimentRunResult.persistedCarAgentResults.findAll {
                it.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
            }*.timeForPlannedDistance

            // allTypes.timeForLoadingList = experimentRunResult.persistedCarAgentResults*.timeForLoading
            allTypes.timeForLoadingList = experimentRunResult.persistedCarAgentResults.findAll {
                it.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
            }*.timeForLoading

            carTypes << allTypes;

            experimentRunResult.carTypeCounts.each { CarTypeCount carTypeCount ->

                def carType = [ : ]

                carType.carType = carTypeCount.carType.name
                carType.count = carTypeCount.countValue

                carType.countTargetReached = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult result ->
                    result.carType.name.equals( carTypeCount.carType.name )
                } . count { it.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() ) }

                carType.kmDrivenList = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult result ->
                    result.carType.name.equals( carTypeCount.carType.name ) && result.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                } *.realDistance

                carType.plannedDistanceList = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult result ->
                    result.carType.name.equals( carTypeCount.carType.name ) && result.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                } *.plannedDistance

                carType.realTimeUsedList = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult result ->
                    result.carType.name.equals( carTypeCount.carType.name ) && result.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                } *.timeForRealDistance

                carType.plannedTimeUsedList = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult result ->
                    result.carType.name.equals( carTypeCount.carType.name ) && result.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                } *.timeForPlannedDistance

                carType.timeForLoadingList = experimentRunResult.persistedCarAgentResults.findAll { PersistedCarAgentResult result ->
                    result.carType.name.equals( carTypeCount.carType.name ) && result.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                } *.timeForLoading

                carTypes << carType
            }
            m.carTypes = carTypes

            m.fillingTypes = fillingTypes

        }


        return m;
    }

    def getSuccessRate( ExperimentRunResult result ) {

        int count = 0;
        int succeeded = 0;
        result.persistedCarAgentResults.each { PersistedCarAgentResult carAgentResult ->

            PersistedCarAgentResult persistedCarAgentResult = PersistedCarAgentResult.get( carAgentResult.id )

            if ( persistedCarAgentResult.carStatus.toString().equals( CarStatus.MISSION_ACCOMBLISHED.toString() ) ) {
                succeeded++;
            }
            count++

        }

        return ( succeeded / count )
    }


    def createStatsForTimeClassAndRelativeSearchLimit( Long simulationId, double relativeSearchLimit, boolean withLoading ) {

        def plannedDistanceClasses = [
                '  0 - 99'  : 0..99,
                '100 - 199' : 100..199,
                '200 - 299' : 200..299,
                '300 - 399' : 300..399,
                '400 - 499' : 400..499,
                '    > 500' : 500..1000
        ]

        def m = [ : ]
        List<ExperimentRunResult> experimentRunResults = ExperimentRunResult.findAllBySimulationId( simulationId )

        def allDifferentGasolineStationCounts = []

        log.error( "hua" )

        // select the best by successRates with same configuration
        Map<SimulationConfiguration,ExperimentRunResult> mostSuccessfulExperiments = new HashMap<SimulationConfiguration,ExperimentRunResult>()
        experimentRunResults.each { ExperimentRunResult experimentRunResult ->

            PersistedFillingStationResult fillingStationResult = PersistedFillingStationResult.get( experimentRunResult.persistedFillingStationResults.get( 0 ).id )

            if ( !allDifferentGasolineStationCounts.contains( experimentRunResult.persistedFillingStationResults.size() ) ) {
                allDifferentGasolineStationCounts.add( experimentRunResult.persistedFillingStationResults.size() )
            }

            SimulationConfiguration simulationConfiguration = new SimulationConfiguration(
                    relativeSearchLimit: experimentRunResult.relativeSearchLimit,
                    gasolineStationCount: experimentRunResult.persistedFillingStationResults.size(),
                    gasolineStationType: fillingStationResult.gasolineStationType
            )

            ExperimentRunResult mapExperimentResult = mostSuccessfulExperiments.get( simulationConfiguration )

            if ( mapExperimentResult != null ) {
                // compare
                double mapSuccessRate = getSuccessRate( mapExperimentResult )
                double dbSuccessRate = getSuccessRate( experimentRunResult )

                if ( dbSuccessRate > mapSuccessRate ) {
                    // put the db thing into map

                    mostSuccessfulExperiments.put( simulationConfiguration, experimentRunResult )
                }


            } else {

                mostSuccessfulExperiments.put( simulationConfiguration, experimentRunResult )

            }

        }

        def typeList = GasolineStationType.values()*.toString()

        def distanceClasses = []

        plannedDistanceClasses.each { distClassEntry ->

            def distanceClassMap = [ : ]
            distanceClassMap.distanceClass = distClassEntry.key
            def types = []

            typeList.each { String typeName ->

                def typeMap = [ : ]
                typeMap.type = typeName

                def counts = []

                allDifferentGasolineStationCounts.each { Integer counting ->

                    def countMap = [ : ]
                    countMap.count = counting

                    def valueList = []

                    // log.error( "filter: ${distClassEntry.key} -- ${typeName} -- ${counting}" )

                    def relevants = []
                    mostSuccessfulExperiments.each { Map.Entry<SimulationConfiguration,ExperimentRunResult> experimentRunResultMapEntry ->

                        if ( Math.round( experimentRunResultMapEntry.key.relativeSearchLimit * 100 ) == Math.round( relativeSearchLimit ) &&
                                experimentRunResultMapEntry.value.persistedCarAgentResults.size() > 0 ) {

                            relevants.add( experimentRunResultMapEntry.value )

                        }

                    }

                    relevants.each { ExperimentRunResult currentExperimentRunResult ->

                        PersistedFillingStationResult persistedFillingStationResult = PersistedFillingStationResult.get( currentExperimentRunResult.persistedFillingStationResults.get( 0 ).id )

                        currentExperimentRunResult.persistedCarAgentResults.each { PersistedCarAgentResult persistedCarAgentResult ->

                            PersistedCarAgentResult pcar = PersistedCarAgentResult.get( persistedCarAgentResult.id )


                            if ( ( Math.round( pcar.plannedDistance ) as Integer ) in distClassEntry.value &&
                                    persistedFillingStationResult.gasolineStationType.equals( typeName ) &&
                                    currentExperimentRunResult.persistedFillingStationResults.size() == counting &&
                                    persistedCarAgentResult.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString()  ))  {

                                long planned = pcar.timeForPlannedDistance
                                long real = pcar.timeForRealDistance
                                long diff = real - planned

                                if ( !withLoading ) {
                                    diff = diff - pcar.timeForLoading
                                }

                                double relativeDetour = diff / planned

                                // in percent
                                valueList.add( relativeDetour * 100 )

                            }

                        }

                    }


                    countMap.valueList = valueList

                    counts.add( countMap )
                }
                typeMap.counts = counts

                types.add( typeMap )

            }
            distanceClassMap.types = types

            distanceClasses.add( distanceClassMap )
        }





        m.distanceClasses = distanceClasses

        return m

    }

    def createStatsForDistanceClassAndRelativeSearchLimit( Long simulationId, double relativeSearchLimit ) {

        def plannedDistanceClasses = [
                '  0 - 99'  : 0..99,
                '100 - 199' : 100..199,
                '200 - 299' : 200..299,
                '300 - 399' : 300..399,
                '400 - 499' : 400..499,
                '    > 500' : 500..1000
        ]

        def m = [ : ]
        List<ExperimentRunResult> experimentRunResults = ExperimentRunResult.findAllBySimulationId( simulationId )

        def allDifferentGasolineStationCounts = []

        log.error( "hua" )

        // select the best by successRates with same configuration
        Map<SimulationConfiguration,ExperimentRunResult> mostSuccessfulExperiments = new HashMap<SimulationConfiguration,ExperimentRunResult>()
        experimentRunResults.each { ExperimentRunResult experimentRunResult ->

            PersistedFillingStationResult fillingStationResult = PersistedFillingStationResult.get( experimentRunResult.persistedFillingStationResults.get( 0 ).id )

            if ( !allDifferentGasolineStationCounts.contains( experimentRunResult.persistedFillingStationResults.size() ) ) {
                allDifferentGasolineStationCounts.add( experimentRunResult.persistedFillingStationResults.size() )
            }

            SimulationConfiguration simulationConfiguration = new SimulationConfiguration(
                    relativeSearchLimit: experimentRunResult.relativeSearchLimit,
                    gasolineStationCount: experimentRunResult.persistedFillingStationResults.size(),
                    gasolineStationType: fillingStationResult.gasolineStationType
            )

            ExperimentRunResult mapExperimentResult = mostSuccessfulExperiments.get( simulationConfiguration )

            if ( mapExperimentResult != null ) {
                // compare
                double mapSuccessRate = getSuccessRate( mapExperimentResult )
                double dbSuccessRate = getSuccessRate( experimentRunResult )

                if ( dbSuccessRate > mapSuccessRate ) {
                    // put the db thing into map

                    mostSuccessfulExperiments.put( simulationConfiguration, experimentRunResult )
                }


            } else {

                mostSuccessfulExperiments.put( simulationConfiguration, experimentRunResult )

            }

        }

        def typeList = GasolineStationType.values()*.toString()

        def distanceClasses = []

        plannedDistanceClasses.each { distClassEntry ->

            def distanceClassMap = [ : ]
            distanceClassMap.distanceClass = distClassEntry.key
            def types = []

            typeList.each { String typeName ->

                def typeMap = [ : ]
                typeMap.type = typeName

                def counts = []

                allDifferentGasolineStationCounts.each { Integer counting ->

                    def countMap = [ : ]
                    countMap.count = counting

                    def valueList = []

                    // log.error( "filter: ${distClassEntry.key} -- ${typeName} -- ${counting}" )

                    def relevants = []
                    mostSuccessfulExperiments.each { Map.Entry<SimulationConfiguration,ExperimentRunResult> experimentRunResultMapEntry ->

                        if ( Math.round( experimentRunResultMapEntry.key.relativeSearchLimit * 100 ) == Math.round( relativeSearchLimit ) &&
                                experimentRunResultMapEntry.value.persistedCarAgentResults.size() > 0 ) {

                            relevants.add( experimentRunResultMapEntry.value )

                        }

                    }

                    relevants.each { ExperimentRunResult currentExperimentRunResult ->

                        PersistedFillingStationResult persistedFillingStationResult = PersistedFillingStationResult.get( currentExperimentRunResult.persistedFillingStationResults.get( 0 ).id )

                        currentExperimentRunResult.persistedCarAgentResults.each { PersistedCarAgentResult persistedCarAgentResult ->

                            PersistedCarAgentResult pcar = PersistedCarAgentResult.get( persistedCarAgentResult.id )


                            if ( ( Math.round( pcar.plannedDistance ) as Integer ) in distClassEntry.value &&
                                   persistedFillingStationResult.gasolineStationType.equals( typeName ) &&
                                    currentExperimentRunResult.persistedFillingStationResults.size() == counting &&
                                    persistedCarAgentResult.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString()  ))  {

                                double planned = pcar.plannedDistance
                                double real = pcar.realDistance
                                double diff = real - planned
                                double relativeDetour = diff / planned

                                // in percent
                                valueList.add( relativeDetour * 100 )

                            }

                        }

                    }


                    countMap.valueList = valueList

                    counts.add( countMap )
                }
                typeMap.counts = counts

                types.add( typeMap )

            }
            distanceClassMap.types = types

            distanceClasses.add( distanceClassMap )
        }





        m.distanceClasses = distanceClasses

        return m
    }


    def createStatsForDetailPicture( Long experimentResultId, String carType ) {

        def m = [ : ]

        // fetching Experiment Run Result
        ExperimentRunResult experimentRunResult = ExperimentRunResult.get( experimentResultId )


        /**
         * setting some general properties to display in picture title
         *
         * a) relative search limit
         * b) count of filling-stations
         * c) type of filling stations
         */
        m.relativeSearchLimit = experimentRunResult.relativeSearchLimit * 100
        m.fillingStations = experimentRunResult.persistedFillingStationResults.size()
        PersistedFillingStationResult fillingStationResult = PersistedFillingStationResult.get( experimentRunResult.persistedFillingStationResults.get( 0 ).id )
        m.fillingStationType = fillingStationResult.gasolineStationType

        /**
         * d) to calculate success rate in percent
         */
        int totalCount = 0;
        int succeeded = 0;

        /**
         * collecting all interesting persisted car agent results, filtered by
         *
         * a) type of car ( "Ampera",... or "all" ) and
         */
        def persistedCarAgentResults = [];
        experimentRunResult.persistedCarAgentResults.each { PersistedCarAgentResult res ->

            PersistedCarAgentResult result = PersistedCarAgentResult.get( res.id )
            CarType ct = CarType.get( result.carType.id )

            totalCount++
            if ( result.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() ) ) {

                succeeded++;

            }

            if ( carType.equals( "all" ) ) {

                persistedCarAgentResults << PersistedCarAgentResult.get( res.id )

            } else if ( ct.name.equals( carType ) ) {

                persistedCarAgentResults << PersistedCarAgentResult.get( res.id )

            }

        }

        // setting success rate for picture title
        m.successRate = Math.round( succeeded / totalCount ) * 100

        /**
         * to split the list of persistedCarAgentResults into planned-distance-classes
         */
        def plannedDistanceClasses = [
                '  0 - 99'  : 0..99,
                '100 - 199' : 100..199,
                '200 - 299' : 200..299,
                '300 - 399' : 300..399,
                '400 - 499' : 400..499,
                '    > 500' : 500..1000
        ]

        m.carType = carType;

        def details = []

        /**
         * split list of persistedCarAgentResults by planned-distance-classes
         *
         */
        if ( experimentRunResult != null ) {

            plannedDistanceClasses.each {

                def detail = [ : ]

                detail.plannedDistanceClass = it.key

                // all cars which are in distance class
                detail.count = persistedCarAgentResults.count { PersistedCarAgentResult res ->

                    ( Math.round( res.plannedDistance ) as Integer ) in it.value

                }

                // how many of them reached their target?
                detail.targetReached = persistedCarAgentResults.count { PersistedCarAgentResult res ->

                    ( Math.round( res.plannedDistance ) as Integer ) in it.value &&
                            res.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() )
                }

                detail.successRate = ( detail.count > 0 )? Math.round( ( ( detail.targetReached as Integer ) / ( detail.count as Integer ) ) * 100 ):0


                def realDistances = []
                def plannedDistanceList = []
                def diffDistanceList = []

                def timeForLoadingList = []
                def diffRealPlannedTimeList = []
                def plannedTimeList = []
                def realTimeList = []


                persistedCarAgentResults.each { PersistedCarAgentResult pcar ->

                    /**
                     * we only take those results which reached their target and are in
                     * current distance class
                     */
                    if ( pcar.carStatus.toString().equals( CarStatus.MISSION_ACCOMBLISHED.toString() ) &&
                            ( Math.round( pcar.plannedDistance ) as Integer ) in it.value ) {

                        realDistances.add( pcar.realDistance )
                        plannedDistanceList.add( pcar.plannedDistance )
                        diffDistanceList.add( ( pcar.realDistance - pcar.plannedDistance ) )

                        timeForLoadingList.add( pcar.timeForLoading )
                        diffRealPlannedTimeList.add( ( pcar.timeForRealDistance - pcar.timeForPlannedDistance ) )
                        plannedTimeList.add( pcar.timeForPlannedDistance )
                        realTimeList.add( pcar.timeForRealDistance )

                    }

                }

                /**
                 * some error loggings
                 */
                log.error( "${it.key} : real distance max : ${realDistances.max()} ( ${ realDistances.size() > 0 ? (persistedCarAgentResults.find { PersistedCarAgentResult res -> res.realDistance >= ( realDistances.max() - 1 ) }):' ' } ) " )
                log.error( "${it.key} : real distance min : ${realDistances.min()} ( ${ realDistances.size() > 0 ? (persistedCarAgentResults.find { PersistedCarAgentResult res -> res.realDistance <= ( realDistances.min() + 1 ) }):' ' } ) " )

                log.error( "${it.key} : planned distance max : ${plannedDistanceList.max()} ( ${ plannedDistanceList.size() > 0 ? (persistedCarAgentResults.find { PersistedCarAgentResult res -> res.plannedDistance >= ( plannedDistanceList.max() - 1 ) }):' ' } ) " )
                log.error( "${it.key} : planned distance min : ${plannedDistanceList.min()} ( ${ plannedDistanceList.size() > 0 ? (persistedCarAgentResults.find { PersistedCarAgentResult res -> res.plannedDistance <= ( plannedDistanceList.min() + 1 ) }):' ' } )  " )

                log.error( "${it.key} : diff distance max : ${diffDistanceList.max()} ( ${ diffDistanceList.size() > 0 ? (persistedCarAgentResults.find { PersistedCarAgentResult res ->  ( res.realDistance - res.plannedDistance ) >= ( diffDistanceList.max() - 1 ) }):' ' } )  " )
                log.error( "${it.key} : diff distance min : ${diffDistanceList.min()} ( ${ diffDistanceList.size() > 0 ? (persistedCarAgentResults.find { PersistedCarAgentResult res ->  ( res.realDistance - res.plannedDistance ) >= ( diffDistanceList.min() + 1 ) }):' ' } ) " )

                log.error( "${it.key} : time for loading max : ${timeForLoadingList.size()>0?timeForLoadingList.max() / (60*60):' '} ( ${ timeForLoadingList.size() > 0 ? (persistedCarAgentResults.find { PersistedCarAgentResult res -> res.timeForLoading >= ( timeForLoadingList.max() - 1 ) }):' ' } )  " )
                log.error( "${it.key} : time for loading min : ${timeForLoadingList.size()>0?timeForLoadingList.min() / (60*60):' '} ( ${ timeForLoadingList.size() > 0 ? (persistedCarAgentResults.find { PersistedCarAgentResult res -> res.timeForLoading >= ( timeForLoadingList.min() + 1 ) }):' ' } )  " )

                log.error( "${it.key} : diff time max : ${diffRealPlannedTimeList.size()>0?diffRealPlannedTimeList.max() / (60*60):' '} ( ${ diffRealPlannedTimeList.size() > 0 ? (persistedCarAgentResults.find { PersistedCarAgentResult res ->  ( res.timeForRealDistance - res.timeForPlannedDistance ) >= ( diffRealPlannedTimeList.max() - 1 ) }):' ' } ) " )
                log.error( "${it.key} : diff time min : ${diffRealPlannedTimeList.size()>0?diffRealPlannedTimeList.min() / (60*60):' '} ( ${ diffRealPlannedTimeList.size() > 0 ? (persistedCarAgentResults.find { PersistedCarAgentResult res ->  ( res.timeForRealDistance - res.timeForPlannedDistance ) >= ( diffRealPlannedTimeList.min() + 1 ) }):' ' } ) " )

                log.error( "${it.key} : planned time max : ${plannedTimeList.size()>0?plannedTimeList.max() / (60*60):' '} ( ${ plannedTimeList.size() > 0 ? (persistedCarAgentResults.find { PersistedCarAgentResult res -> res.timeForPlannedDistance >= ( plannedTimeList.max() - 1 ) }):' ' } ) " )
                log.error( "${it.key} : planned time min : ${plannedTimeList.size()>0?plannedTimeList.min() / (60*60):' '} ( ${ plannedTimeList.size() > 0 ? (persistedCarAgentResults.find { PersistedCarAgentResult res -> res.timeForPlannedDistance >= ( plannedTimeList.min() + 1 ) }):' ' } ) " )

                log.error( "${it.key} : real time max : ${realTimeList.size()>0?realTimeList.max() / (60*60) :' ' } ( ${ realTimeList.size() > 0 ? (persistedCarAgentResults.find { PersistedCarAgentResult res -> res.timeForRealDistance >= ( realTimeList.max() - 1 ) }):' ' } ) " )
                log.error( "${it.key} : real time min : ${realTimeList.size()>0?realTimeList.min() / (60*60) :' ' } ( ${ realTimeList.size() > 0 ? (persistedCarAgentResults.find { PersistedCarAgentResult res -> res.timeForRealDistance >= ( realTimeList.min() + 1 ) }):' ' } ) " )
                log.error( "------------------------" )




                detail.plannedDistanceList = plannedDistanceList
                detail.timeForLoadingList = timeForLoadingList
                detail.realDistances = realDistances
                detail.diffRealPlannedTimeList = diffRealPlannedTimeList
                detail.plannedTimeList = plannedTimeList
                detail.realTimeList = realTimeList
                detail.diffDistanceList = diffDistanceList

                details << detail
            }

            m.details = details

        }


        return m
    }



}

package de.dfki.gs.ms2.stats

import de.dfki.gs.domain.simulation.Configuration
import de.dfki.gs.domain.simulation.Fleet
import de.dfki.gs.domain.stats.ExperimentRunResult
import de.dfki.gs.domain.stats.PersistedCarAgentResult
import de.dfki.gs.domain.stats.PersistedFillingStationResult
import de.dfki.gs.domain.utils.Distribution
import de.dfki.gs.ms2.stats.utils.ResultView
import de.dfki.gs.simulation.CarStatus
import de.dfki.gs.stats.StatsCalculator
import grails.transaction.Transactional
import org.apache.commons.math3.distribution.MultivariateNormalDistribution
import org.apache.commons.math3.distribution.NormalDistribution
import org.apache.commons.math3.random.RandomDataGenerator

@Transactional
class StatisticService {


    def generateStatisticMapForExperiment( Long experimentResultId ) {

        def m = [ : ]

        ExperimentRunResult result = ExperimentRunResult.get( experimentResultId )
        Configuration configuration = Configuration.get( result.configurationId )

        if ( result == null ) {
            m.errors = "no experiment result data found for id: ${experimentResultId}"
            return m
        }

        // fill carAgentResults with full qualified objects
        List<PersistedCarAgentResult> carAgents = result.persistedCarAgentResults
        carAgents.each { PersistedCarAgentResult carAgentResult ->
            carAgentResult = PersistedCarAgentResult.get( carAgentResult.id )
        }

        // fill fillingStationResults with full qualified objects
        List<PersistedFillingStationResult> fillingStations = result.persistedFillingStationResults
        fillingStations.each { PersistedFillingStationResult fillingStationResult ->
            fillingStationResult = PersistedFillingStationResult.get( fillingStationResult.id )
        }

        def cars = [ : ]

        def successFullCars = []
        def failedCars = []
        for ( PersistedCarAgentResult carAgent : carAgents ) {

            // each car
            if ( carAgent.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() ) ) {
                successFullCars << carAgent
            }
            if ( carAgent.carStatus.equals( CarStatus.WAITING_EMPTY.toString() ) ) {
                failedCars << carAgent
            }


        }
        cars.'success' = successFullCars
        cars.'failed'  = failedCars

        m.'cars' = cars


        def fleets = []

        // split up carAgents results in a map fleet id -> list of cars
        def fleetCarsMap = [ : ]
        carAgents.each { PersistedCarAgentResult pcar ->

            List<PersistedCarAgentResult> fleetList = (List<PersistedCarAgentResult>) fleetCarsMap.get( pcar.fleetId )

            if ( fleetList == null ) {

                fleetList = new ArrayList<PersistedCarAgentResult>()
                fleetCarsMap.put( pcar.fleetId, fleetList )

            }

            fleetList.add( pcar )

        }

        for ( Fleet fleet : configuration.fleets ) {
            fleet = Fleet.get( fleet.id )

            def fleetMap = fleet.dto()
            fleetMap.carResults = fleetCarsMap.get( fleet.id )

            // stats per fleet
            def stats = calculateStatsForGroupOfCars( fleetCarsMap.get( fleet.id ) )

            fleetMap.stats = stats

            fleets << fleetMap

        }

        m.fleets = fleets

        return m
    }


    def detailsStatsForGroupOfCars( List<PersistedCarAgentResult> persistedCarAgentResults ) {

        def details = [ : ]

        def plannedTime = [ : ]
        def plannedDistance = [ : ]
        def realDistance = [ : ]
        def realTime = [ : ]
        def loadingTime = [ : ]
        def energyLoaded = [ : ]
        def energyDemanded = [ : ]
        def realDrivingTime = [ : ]

        plannedTime.mean        = StatsCalculator.meanPlannedTime( persistedCarAgentResults*.timeForPlannedDistance )
        plannedTime.valuez      = persistedCarAgentResults*.timeForPlannedDistance
        details.plannedTime     = plannedTime

        plannedDistance.mean    = StatsCalculator.meanPlannedDistance( persistedCarAgentResults*.plannedDistance )
        plannedDistance.valuez  = persistedCarAgentResults*.plannedDistance
        details.plannedDistance = plannedDistance

        realDistance.mean       = StatsCalculator.meanRealDistance( persistedCarAgentResults*.realDistance )
        realDistance.valuez     = persistedCarAgentResults*.realDistance
        details.realDistance    = realDistance

        realTime.mean           = StatsCalculator.meanRealTime( persistedCarAgentResults*.timeForRealDistance )
        realTime.valuez         = persistedCarAgentResults*.timeForRealDistance
        details.realTime        = realTime

        loadingTime.mean        = StatsCalculator.meanRealLoadingTime( persistedCarAgentResults*.timeForLoading )
        loadingTime.valuez      = persistedCarAgentResults*.timeForLoading
        details.loadingTime     = loadingTime

        energyLoaded.mean       = StatsCalculator.meanEnergyLoaded( persistedCarAgentResults*.energyLoaded )
        energyLoaded.valuez     = persistedCarAgentResults*.energyLoaded
        details.ernergyLoaded   = energyLoaded

        energyDemanded.mean     = StatsCalculator.meanEnergyDemanded( persistedCarAgentResults*.energyConsumed )
        energyDemanded.valuez   = persistedCarAgentResults*.energyConsumed
        details.energyDemanded  = energyDemanded

        List<Float> drivingTimes = new ArrayList<Float>()
        for ( PersistedCarAgentResult result : persistedCarAgentResults ) {

            drivingTimes.add( ( result.timeForRealDistance - result.timeForLoading ) )

        }

        realDrivingTime.mean    = StatsCalculator.meanRealDrivingTime( drivingTimes )
        realDrivingTime.valuez  = drivingTimes
        details.realDrivingTime = realDrivingTime

        return details
    }

    def calculateStatsForGroupOfCars( List<PersistedCarAgentResult> carResults ) {

        def stats = [ : ]

        def allCars = [ : ]
        def successfulCars = [ : ]
        def failedCars = [ : ]

        List<PersistedCarAgentResult> failedResults = new ArrayList<PersistedCarAgentResult>()
        List<PersistedCarAgentResult> successfulResults = new ArrayList<PersistedCarAgentResult>()
        carResults.each { PersistedCarAgentResult pcar ->
            if ( pcar.carStatus.equals( CarStatus.MISSION_ACCOMBLISHED.toString() ) ) {
                successfulResults << pcar
            } else if ( pcar.carStatus.equals( CarStatus.WAITING_EMPTY.toString() ) ) {
                failedResults << pcar
            }
        }


        stats.allCars = detailsStatsForGroupOfCars( carResults )
        stats.failedCars = detailsStatsForGroupOfCars( failedResults )
        stats.succeededCars = detailsStatsForGroupOfCars( successfulResults )



        return stats
    }



    def generateRandomGaussianDistributedList( Integer count, Double mean, Double sigma ) {

        int seed = 123;

        List<Double> randomList = new ArrayList<Double>( count )

        RandomDataGenerator randomData = new RandomDataGenerator();
        randomData.reSeed( seed );

        for ( int i = 0; i < count; i++ ) {

            randomList.add( randomData.nextGaussian( mean, sigma ) )

        }

        return randomList
    }

    def generateRandomUniformDistributedList( Integer count, Integer fromKm, Integer toKm ) {

        int seed = 123;

        List<Double> randomList = new ArrayList<Double>( count )

        RandomDataGenerator randomData = new RandomDataGenerator();
        randomData.reSeed( seed );

        for ( int i = 0; i < count; i++ ) {

            randomList.add( ( randomData.nextLong( fromKm, toKm )).doubleValue() )

        }

        return randomList

    }

    def generateRandomGaussianVectors( int count, double meanLat, double meanLon, double sigmaLat, double sigmaLon ) {

        NormalDistribution distributionLat = new NormalDistribution( meanLat, sigmaLat );
        NormalDistribution distributionLon = new NormalDistribution( meanLon, sigmaLon );

        long seed = 123;
        distributionLat.reseedRandomGenerator( seed );
        distributionLon.reseedRandomGenerator( seed + seed );

        double [][] randomVectors = new double [2][count];

        for ( int i = 0; i < count; i++ ) {

            double sampleLat = distributionLat.sample()
            double sampleLon = distributionLon.sample()

            randomVectors[ 0 ][ i ] = sampleLat
            randomVectors[ 1 ][ i ] = sampleLon

        }

        return randomVectors
    }

    def generateMultivariateGaussianVectors( int count, double[] meanVector, double[][] covarianceMatrix ) {

        MultivariateNormalDistribution distribution = new MultivariateNormalDistribution( meanVector, covarianceMatrix );
        long seed = 123
        distribution.reseedRandomGenerator( seed );

        double [][] randomVectors = new double [3][count];

        for ( int i = 0; i < count; i++ ) {

            double [] sample = distribution.sample()

            for ( int j = 0; j < 3; j++ ) {

                randomVectors[ j ][ i ] = sample[ j ]

            }

        }

        return randomVectors
    }

    def generateRandomListFromDistribution( Integer count, Integer fromKm, Integer toKm, Distribution distribution ) {


        List<Double> randomList = new ArrayList<Double>( count )

        int seed = 123;


        switch ( distribution ) {

            case Distribution.EQUAL_DISTRIBUTION:

                RandomDataGenerator randomData = new RandomDataGenerator();
                randomData.reSeed( seed );

                for ( int i = 0; i < count; i++ ) {

                    randomList.add( randomData.nextLong( fromKm, toKm ) )

                }

                break;
            case Distribution.NORMAL_DISTRIBUTION:

                NormalDistribution normalDistribution = new NormalDistribution( (toKm-fromKm), 20 )
                normalDistribution.reseedRandomGenerator( seed )

                for ( int i = 0; i < count; i++ ) {
                    randomList.add( normalDistribution.sample() + fromKm )
                }



                // randomList = generateRandomUniformDistributedList( count, fromKm, toKm )
                break;

        }

        return randomList
    }


}

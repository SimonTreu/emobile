package de.dfki.gs.ms2.stats

import de.dfki.gs.domain.utils.Distribution
import grails.transaction.Transactional
import org.apache.commons.math3.distribution.MultivariateNormalDistribution
import org.apache.commons.math3.distribution.NormalDistribution
import org.apache.commons.math3.random.RandomDataGenerator

@Transactional
class StatisticService {


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

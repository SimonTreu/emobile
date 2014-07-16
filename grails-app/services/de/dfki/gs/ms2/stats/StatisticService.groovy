package de.dfki.gs.ms2.stats

import de.dfki.gs.domain.utils.Distribution
import grails.transaction.Transactional
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

                randomList = generateRandomUniformDistributedList( count, fromKm, toKm )
                break;

        }

        return randomList
    }


}

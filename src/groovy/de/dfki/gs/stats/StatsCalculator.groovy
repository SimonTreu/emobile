package de.dfki.gs.stats

/**
 * Created by glenn on 10.04.14.
 */
class StatsCalculator {


    public static float meanTimeInUse( List<Long> times ) {
        return meanL( times )
    }

    public static float meanTimeLiving( List<Long> times ) {
        return meanL( times )
    }
    public static float meanFailedToRoute( List<Long> times ) {
        return meanL( times )
    }


    public static float meanPlannedTime( List<Float> plannedTimes ) {
        return mean( plannedTimes )
    }

    public static float meanPlannedDistance( List<Float> plannedDistances ) {
        return mean( plannedDistances )
    }

    public static float meanRealTime( List<Float> realTimes ) {
        return mean( realTimes )
    }

    public static float meanRealDistance( List<Float> plannedTimes ) {
        return mean( plannedTimes )
    }

    public static float meanRealDrivingTime( List<Float> plannedTimes ) {
        return mean( plannedTimes )
    }

    public static float meanRealLoadingTime( List<Float> plannedTimes ) {
        return mean( plannedTimes )
    }

    public static float meanEnergyLoaded( List<Float> energyLoads ) {
        return mean( energyLoads )
    }

    public static float meanEnergyDemanded( List<Float> energyDemandeds ) {
        return mean( energyDemandeds )
    }

    private static float meanL( List<Long> longs ) {

        if ( longs.size() == 0 ) {
            return 0
        }

        float result = 0
        for ( Long f : longs ) {
            result += f
        }

        result = result / longs.size()

        return result
    }

    private static float mean( List<Float> floats ) {

        if ( floats.size() == 0 ) {
            return 0
        }

        float result = 0
        for ( Float f : floats ) {
            result += f
        }

        result = result / floats.size()

        return result
    }

}

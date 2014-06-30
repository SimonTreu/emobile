package de.dfki.gs.model.stats

/**
 * Created by glenn on 06.06.14.
 */
class SimulationConfiguration {

    double relativeSearchLimit
    String gasolineStationType
    int gasolineStationCount


    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof SimulationConfiguration)) return false

        SimulationConfiguration that = (SimulationConfiguration) o

        if (gasolineStationCount != that.gasolineStationCount) return false
        if (Double.compare(that.relativeSearchLimit, relativeSearchLimit) != 0) return false
        if (gasolineStationType != that.gasolineStationType) return false

        return true
    }

    int hashCode() {
        int result
        long temp
        temp = relativeSearchLimit != +0.0d ? Double.doubleToLongBits(relativeSearchLimit) : 0L
        result = (int) (temp ^ (temp >>> 32))
        result = 31 * result + (gasolineStationType != null ? gasolineStationType.hashCode() : 0)
        result = 31 * result + gasolineStationCount
        return result
    }
}
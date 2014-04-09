package de.dfki.gs.model.elements.results

/**
 * Created by glenn on 01.04.14.
 */
class CarAgentResult {


    long estimatedTimeForRouteWithoutCharging

    long totalTimeUsed

    double energyConsumed




    long getEstimatedTimeForRouteWithoutCharging() {
        return estimatedTimeForRouteWithoutCharging
    }

    void setEstimatedTimeForRouteWithoutCharging(long estimatedTimeForRouteWithoutCharging) {
        this.estimatedTimeForRouteWithoutCharging = estimatedTimeForRouteWithoutCharging
    }

    long getTotalTimeUsed() {
        return totalTimeUsed
    }

    void setTotalTimeUsed(long totalTimeUsed) {
        this.totalTimeUsed = totalTimeUsed
    }

    double getEnergyConsumed() {
        return energyConsumed
    }

    void setEnergyConsumed(double energyConsumed) {
        this.energyConsumed = energyConsumed
    }
}

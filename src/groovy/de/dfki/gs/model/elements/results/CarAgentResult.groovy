package de.dfki.gs.model.elements.results

import de.dfki.gs.domain.CarType

/**
 * Created by glenn on 01.04.14.
 */
class CarAgentResult {


    double energyConsumed


    CarType carType

    String carAgentStatus

    double plannedDistance
    double realDistance

    int targets

    long timeForPlannedDistance
    long timeForRealDistance
    long timeForLoading

    long timeForDetour
    double energyLoaded

    int fillingStationsVisited

    long simulationId

    double relativeSearchLimit



}

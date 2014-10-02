package de.dfki.gs.model.elements.results

import de.dfki.gs.domain.simulation.CarType

/**
 * Created by glenn on 01.04.14.
 */
class CarAgentResult {


    float energyConsumed


    CarType carType

    String carAgentStatus

    float plannedDistance
    float realDistance

    int targets

    long timeForPlannedDistance
    long timeForRealDistance
    long timeForLoading

    long timeForDetour
    float energyLoaded

    int fillingStationsVisited

    long configurationId

    float relativeSearchLimit

    long fleetId



}

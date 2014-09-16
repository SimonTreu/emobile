package de.dfki.gs.domain.stats

import de.dfki.gs.domain.simulation.CarType
import de.dfki.gs.simulation.CarStatus

class PersistedCarAgentResult {


    Float     energyConsumed
    CarType   carType
    String    carStatus
    Float     plannedDistance
    Float     realDistance
    Integer   targets

    Long      timeForPlannedDistance
    Long      timeForRealDistance
    Long      timeForLoading
    Long      timeForDetour

    Float     energyLoaded

    Integer   fillingStationsVisited

    float     relativeSearchLimit


    static constraints = {
    }
}

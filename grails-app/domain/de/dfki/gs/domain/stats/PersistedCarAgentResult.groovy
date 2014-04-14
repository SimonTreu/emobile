package de.dfki.gs.domain.stats

import de.dfki.gs.domain.CarType
import de.dfki.gs.simulation.CarStatus

class PersistedCarAgentResult {


    double energyConsumed

    CarType carType
    String carStatus

    double plannedDistance
    double realDistance

    int targets

    long timeForPlannedDistance
    long timeForRealDistance
    long timeForLoading


    long timeForDetour
    double energyLoaded

    int fillingStationsVisited



    static constraints = {
        carStatus ( nullable: false, blank: false, inList: CarStatus.values()*.toString() )
    }
}

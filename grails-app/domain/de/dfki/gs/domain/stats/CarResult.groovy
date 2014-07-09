package de.dfki.gs.domain.stats

import de.dfki.gs.domain.simulation.CarType
import de.dfki.gs.simulation.CarStatus

class CarResult {


    double energyConsumed

    CarType carType

    long carId

    String carStatus

    /**
     * dependant of the predefined route, it only the sum of all tracks..
     */
    double plannedDistance

    /**
     * the real driven Distance
     */
    double realDistance

    /**
     * count of the via targets
     */
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


    static hasMany = [
    ]

    static mapping = {
    }
}

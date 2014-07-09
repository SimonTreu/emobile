package de.dfki.gs.domain.stats

import de.dfki.gs.domain.simulation.FillingStationType

class FillingStationResult {


    FillingStationType fillingStationType

    long fillingStationId

    long timeLiving

    /**
     *
     */
    long timeInUse

    /**
     * count of cars which used fillingStation
     */
    int countOfVisitingCars


    static constraints = {

    }
}

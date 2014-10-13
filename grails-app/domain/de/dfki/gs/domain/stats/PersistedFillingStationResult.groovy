package de.dfki.gs.domain.stats

import de.dfki.gs.domain.simulation.FillingStationType

class PersistedFillingStationResult {

    // String gasolineStationType

    FillingStationType fillingStationType

    Long groupId

    long timeLiving
    long timeInUse

    int failedToRouteCount


    static constraints = {
        fillingStationType( nullable: false )
    }
}

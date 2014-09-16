package de.dfki.gs.domain.stats

import de.dfki.gs.domain.simulation.FillingStationType

class PersistedFillingStationResult {

    // String gasolineStationType

    FillingStationType fillingStationType

    long timeLiving
    long timeInUse


    static constraints = {
        fillingStationType( nullable: false )
    }
}

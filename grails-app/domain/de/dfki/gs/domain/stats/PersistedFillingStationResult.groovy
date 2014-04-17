package de.dfki.gs.domain.stats

import de.dfki.gs.domain.GasolineStationType

class PersistedFillingStationResult {

    String gasolineStationType

    long timeLiving
    long timeInUse


    static constraints = {

        gasolineStationType( nullable: false, blank: false, inList: GasolineStationType.values()*.toString() )

    }
}

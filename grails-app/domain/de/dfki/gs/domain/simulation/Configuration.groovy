package de.dfki.gs.domain.simulation

/**
 * this class is to configure simulation experiment
 * different fleets, fillingstation, aso.
 *
 */
class Configuration {

    /**
     * all executed experiments.. for the stats
     */
    Set<Experiment> experiments

    /**
     * possibility to have different fleets, and to plug ready-steady-fleets into Configuration
     */
    Set<Fleet> fleets
    /**
     * same same for fillingstations
     */
    Set<FillingStationGroup> fillingStationGroups


    static hasMany = [
            experiments : Experiment,
            fleets : Fleet,
            fillingStationGroups : FillingStationGroup
    ]

    static constraints = {
    }

    static mapping = {
    }
}

package de.dfki.gs.domain.simulation

import de.dfki.gs.domain.users.Company

/**
 * this class is to configure simulation experiment
 * different fleets, fillingstation, aso.
 *
 */
class Configuration {

    Company company

    /**
     * all executed experiments.. for the stats
     */
    Set<Experiment> experiments = []

    /**
     * possibility to have different fleets, and to plug ready-steady-fleets into Configuration
     */
    Set<Fleet> fleets = []

    /**
     * same same for fillingstations
     */
    Set<FillingStationGroup> fillingStationGroups = []

    Boolean stub

    static hasMany = [
            experiments : Experiment,
            fleets : Fleet,
            fillingStationGroups : FillingStationGroup
    ]

    static constraints = {
    }

    static mapping = {

        stub type: 'yes_no'

    }
}

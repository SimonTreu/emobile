package de.dfki.gs.domain.simulation

import de.dfki.gs.domain.users.Company

class FillingStationGroup {

    Set<FillingStation> fillingStations

    // to title the group
    String name

    Company company

    Boolean stub

    static hasMany = [
            fillingStations : FillingStation
    ]

    static constraints = {
    }

    static mapping = {

        stub type: 'yes_no'

    }
}

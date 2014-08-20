package de.dfki.gs.domain.simulation

import de.dfki.gs.domain.users.Company
import de.dfki.gs.domain.utils.GroupStatus

class FillingStationGroup {

    Set<FillingStation> fillingStations

    // to title the group
    String name

    Company company
    GroupStatus groupStatus
    Boolean stub
    Boolean groupsConfigured

    Date dateCreated
    Date lastUpdated


    static hasMany = [
            fillingStations : FillingStation
    ]

    static constraints = {
    }

    static mapping = {

        stub type: 'yes_no'
        groupsConfigured type: 'yes_no'

    }
}

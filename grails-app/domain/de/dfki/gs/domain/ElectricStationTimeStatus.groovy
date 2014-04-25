package de.dfki.gs.domain

import de.dfki.gs.model.elements.FillingStationStatus

/**
 * Created by anbu02 on 16.04.14.
 */
class ElectricStationTimeStatus {

    GasolineStation station

    // FillingStationStatus
    String fillingStationStatus

    Date dateCreated
    Date lastUpdated

    static constraints = {

        fillingStationStatus( nullable: false, blank: false, inList: FillingStationStatus.values()*.toString() )

    }

    static mapping = {
        autoTimestamp true
    }

}

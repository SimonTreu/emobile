package de.dfki.gs.domain

import de.dfki.gs.domain.CarSharingOwners
import de.dfki.gs.domain.simulation.Simulation

class CarSharingTimeStatus {

    /**
     * Berlin: lat: 52, lon: 13
     */

    CarSharingCars carSharing

    Double lat
    Double lon

    String address

    //Car personal number
    String fuel
    Integer mileage

    Date dateCreated
    Date lastUpdated

    static constraints = {
        lat ( nullable: false, blank: false )
        lon ( nullable: false, blank: false )
        address( nullable: false , blank: false )
        fuel ( nullable: false, blank: false )
        mileage ( nullable: true, blank: true )
    }

    static mapping = {
        autoTimestamp true
    }
}

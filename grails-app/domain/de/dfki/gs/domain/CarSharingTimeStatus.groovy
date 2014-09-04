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
    Integer fuel
    Integer mileage

    Date dateCreated
    Date lastUpdated

    static constraints = {
        lat ( nullable: true, blank: true )
        lon ( nullable: true, blank: true )
        address( nullable: true , blank: true )
        fuel ( nullable: true, blank: true )
        mileage ( nullable: true, blank: true )
    }

    static mapping = {
        autoTimestamp true
    }
}

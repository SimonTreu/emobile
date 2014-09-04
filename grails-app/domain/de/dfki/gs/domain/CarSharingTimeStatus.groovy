package de.dfki.gs.domain

import de.dfki.gs.domain.CarSharingOwners
import de.dfki.gs.domain.simulation.Simulation

class CarSharingTimeStatus {

    /**
     * Berlin: lat: 52, lon: 13
     */

    CarSharingTimeStatus carSharing

    Double lat
    Double lon

    String address

    //Car personal number
    String vin
    String name

    Integer fuel
    Integer mileage
    String ownerName

    Date dateCreated
    Date lastUpdated

    static constraints = {
        lat ( nullable: false, blank: false )
        lon ( nullable: false, blank: false )
        vin ( nullable: false, blank: true )
        name( nullable: false, blank: false )
        address( nullable: true , blank: true )
        ownerName( nullable: false, blank: false,  inList: CarSharingOwners.values()*.toString() )
        fuel ( nullable: false, blank: true )
        mileage ( nullable: true, blank: true )
    }

    static mapping = {
        autoTimestamp true
    }
}

package de.dfki.gs.domain

import de.dfki.gs.domain.CarSharingOwners
import de.dfki.gs.domain.simulation.Simulation

class CarSharingCars {

    /**
     * Berlin: lat: 52, lon: 13
     */

    //Car personal number
    String vin
    String name
    String ownerName

    static constraints = {

        vin ( nullable: true, blank: true )
        name( nullable: true, blank: true )
        ownerName( nullable: false, blank: false,  inList: CarSharingOwners.values()*.toString() )

    }


}

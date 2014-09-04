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

        vin ( nullable: false, blank: true )
        name( nullable: false, blank: false )
        ownerName( nullable: true, blank: true,  inList: CarSharingOwners.values()*.toString() )

    }


}

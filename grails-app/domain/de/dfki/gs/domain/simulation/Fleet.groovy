package de.dfki.gs.domain.simulation

import de.dfki.gs.domain.users.Company

class Fleet {

    /**
     * to title the fleet
     */
    String name

    Set<Car> cars

    Company company


    static hasMany = [
            cars : Car
    ]

    static constraints = {
    }

    static mapping = {
    }
}

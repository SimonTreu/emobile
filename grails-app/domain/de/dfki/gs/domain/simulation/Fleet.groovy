package de.dfki.gs.domain.simulation

import de.dfki.gs.domain.users.Company

class Fleet {

    /**
     * to title the fleet
     */
    String name

    Set<Car> cars = []

    Company company


    Boolean stub


    static hasMany = [
            cars : Car
    ]

    static constraints = {
        name nullable: true
    }

    static mapping = {

        stub type: 'yes_no'

    }
}

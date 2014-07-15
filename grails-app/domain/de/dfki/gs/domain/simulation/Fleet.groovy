package de.dfki.gs.domain.simulation

import de.dfki.gs.domain.users.Company
import de.dfki.gs.domain.utils.Distribution

class Fleet {

    /**
     * to title the fleet
     */
    String name

    Set<Car> cars = []

    Company company

    Date dateCreated
    Date lastUpdated


    Boolean stub

    Boolean routesConfigured

    Distribution distribution


    static hasMany = [
            cars : Car
    ]

    static constraints = {
        name nullable: true
    }

    static mapping = {

        stub type: 'yes_no'
        routesConfigured type: 'yes_no'

    }
}

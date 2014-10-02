package de.dfki.gs.domain.simulation

import de.dfki.gs.domain.users.Company
import de.dfki.gs.domain.utils.Distribution
import de.dfki.gs.domain.utils.FleetStatus

class Fleet {

    /**
     * to title the fleet
     */
    String name

    Set<Car> cars = []

    Company company

    Integer plannedFromKm
    Integer plannedToKm

    Date dateCreated
    Date lastUpdated


    Boolean stub

    Boolean routesConfigured

    FleetStatus fleetStatus

    Distribution distribution


    static hasMany = [
            cars : Car
    ]

    static constraints = {
        name nullable: true
        plannedFromKm nullable: true
        plannedToKm nullable: true
    }

    static mapping = {

        stub type: 'yes_no'
        routesConfigured type: 'yes_no'

    }

    transient Map dto( qualified = false ) {

        def ret = [ : ]

        ret.id            = id
        ret.name          = name
        ret.carsCount     = cars.size()
        ret.distribution  = distribution.toString()

        ret.plannedFromKm = plannedFromKm
        ret.plannedToKm   = plannedToKm

        // placeholder for cars
        ret.cars = []

        return ret
    }
}

package de.dfki.gs.domain.simulation

/**
 * this class models a car
 *
 */
class Car {

    CarType carType

    String name

    Route route

    Boolean routesConfigured

    Long fleetId


    static constraints = {
        name ( nullable: true, blank: true )
        route nullable: true
        routesConfigured nullable: false
        fleetId nullable: false
    }

    static mapping = {

        routesConfigured type: 'yes_no'

    }

}

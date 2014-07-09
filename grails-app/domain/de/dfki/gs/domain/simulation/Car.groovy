package de.dfki.gs.domain.simulation

/**
 * this class models a car
 *
 */
class Car {

    CarType carType

    String name

    Route route



    static constraints = {
        name ( nullable: true, blank: true )

    }

}

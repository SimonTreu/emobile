package de.dfki.gs.domain

/**
 * this class models a car
 *
 */
class Car {

    CarType carType

    String name

    static constraints = {
        name ( nullable: true, blank: true )

    }

}

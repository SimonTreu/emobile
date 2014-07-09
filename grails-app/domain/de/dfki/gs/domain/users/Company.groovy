package de.dfki.gs.domain.users

import de.dfki.gs.domain.users.Person

/**
 * this class represents all relevantis of a company
 */
class Company {

    String name

    /**
     * Main address
     */
    String street
    String number
    String zipCode
    String telephoneNumber

    /**
     * Main Person to talk with
     */
    Person chief
    Set<Person> persons = []


    static hasMany = [
            persons : Person
    ]


    static constraints = {

        chief nullable: true

    }
}

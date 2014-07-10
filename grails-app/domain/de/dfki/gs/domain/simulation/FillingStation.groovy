package de.dfki.gs.domain.simulation

class FillingStation {


    String name

    /**
     * Berlin: lat: 52, lon: 13
     */
    float lat
    float lon

    String streetName
    String houseNumber

    FillingStationType fillingStationType


    static hasMany = [
    ]

    static constraints = {
        streetName ( nullable: true, blank: true )
        houseNumber nullable: true
    }

    static mapping = {
    }
}

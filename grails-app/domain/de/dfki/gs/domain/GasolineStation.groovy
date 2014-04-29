package de.dfki.gs.domain

class GasolineStation {

    String name
    String type

    // filling portion per second!
    Double fillingPortion

    /**
     * Berlin: lat: 52, lon: 13
     */
    Double lat
    Double lon

    Simulation simulation

    /**
     * collecting real stations via web requests
     */
    Long ownerId
    String ownerName

    String streetName
    String houseNumber

    /**
     * indicates how long gasoline station is in use with a car
     * this field never gets persisted, see transients
     *
     */
    Date usedUntil

    /**
     * proposed: a schedule for gasoline station, in which the car can reserve a loading time span
     */

    static transients = [ 'usedUntil' ]

    static constraints = {

        name ( nullable: true, blank: false )
        type( nullable: false, blank: false, inList: GasolineStationType.values()*.toString() )

        simulation ( nullable: true )

    }
}

package de.dfki.gs.domain

class GasolineStation {

    String name
    String type

    /**
     * Berlin: lat: 52, lon: 13
     */
    Double lat
    Double lon

    Simulation simulation


    static constraints = {

        name ( nullable: true, blank: false )
        type( nullable: false, blank: false, inList: GasolineStationType.values()*.toString() )

        simulation ( nullable: true )

    }
}

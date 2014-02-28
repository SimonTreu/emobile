package de.dfki.gs.domain

/**
 * this class models a car
 *
 */
class Car {

    /**
     * leaf, or tessla or sth useless
     */
    String name

    /**
     * energy drain for the specific battery
     */
    Double energyDrain

    /**
     * "Ernergie-Verbrauch" in kWh/100km
     *
     */
    Double energyConsumption

    /**
     * maximum battery size
     */
    Double maxEnergyLoad

    /**
     *
     */
    Double currentErnergyLoad


    List<GasolineStation> allowedGasolationStations

    static transients = [ 'currentErnergyLoad' ]


    static constraints = {



    }
}

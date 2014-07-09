package de.dfki.gs.domain.simulation

import de.dfki.gs.domain.users.Company

class CarType {

    Company company

    /**
     * leaf, or tessla or sth useless
     */
    String name

    /**
     * energy drain for the specific battery
     */
    // Double energyDrain

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
    // Double currentErnergyLoad


    List<FillingStationType> allowedFillingStationTypes = []

    static transients = [ 'currentErnergyLoad' ]


    static constraints = {



    }
}

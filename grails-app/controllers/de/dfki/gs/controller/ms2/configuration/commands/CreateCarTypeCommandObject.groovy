package de.dfki.gs.controller.ms2.configuration.commands

import grails.validation.Validateable

/**
 * Created by glenn on 08.07.14.
 */
@Validateable
class CreateCarTypeCommandObject {

    String carName
    Double energyDemand
    Double maxEnergyCapacity

    static constraints = {

        carName nullable: false, blank: false
        energyDemand nullable: false
        maxEnergyCapacity nullable: false

    }

}

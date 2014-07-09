package de.dfki.gs.controller.ms2.configuration.commands

import de.dfki.gs.domain.simulation.FillingStationType
import grails.validation.Validateable

/**
 * Created by glenn on 09.07.14.
 */
@Validateable
class CreateFillingStationTypeCommandObject {

    String fillingStationTypeName
    String power

    static constraints = {

        fillingStationTypeName nullable: false, blank: false
        power nullable: false, blank: false, matches: "\\d+(\\.\\d+)?"

    }

}

package de.dfki.gs.controller.ms2.configuration.commands

import de.dfki.gs.domain.simulation.FillingStationType
import grails.validation.Validateable

/**
 * Created by glenn on 09.07.14.
 */
@Validateable
class UpdateFillingStationTypeCommandObject {

    String fillingStationTypeName
    String power
    Long fillingStationTypeId

    static constraints = {

        fillingStationTypeId nullable: false, validator: { val,obj ->

            FillingStationType fillingStationType = FillingStationType.get( val )

            if ( fillingStationType == null ) {
                return 'configuration.fillingStationType.not.exist'
            }

        }
        fillingStationTypeName nullable: false, blank: false
        power nullable: false, blank: false, matches: "\\d+(\\.\\d+)?"

    }


}

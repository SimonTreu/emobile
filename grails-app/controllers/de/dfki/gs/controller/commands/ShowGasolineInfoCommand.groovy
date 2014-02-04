package de.dfki.gs.controller.commands

import de.dfki.gs.domain.GasolineStation
import grails.validation.Validateable

/**
 * @author: glenn
 * @since: 18.10.13
 */
@Validateable
class ShowGasolineInfoCommand {

    Long gasolineId

    static constraints = {

        gasolineId( nullable: false, validator: { val, obj ->
            if ( !GasolineStation.get( val ) ) return false

            return true;
        })


    }

}

package de.dfki.gs.controller.ms2.configuration.commands

import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.domain.simulation.Configuration
import grails.validation.Validateable
import de.dfki.gs.domain.simulation.FillingStationType


/**
 * @author: glenn
 * @since: 18.10.13
 */
@Validateable
class ShowInfoStationsCommandObject {
    Long configurationStubId
    Long gasolineId

    static constraints = {

        gasolineId( nullable: false, validator: { val, obj ->
            if ( !GasolineStation.get( val ) ) return false

            return true;
        })

        configurationStubId nullable: false, validator: { val, obj ->

            Configuration stub = Configuration.get(val)

            if (stub == null) {
                return 'configuration.stub.not.exist'
            }


        }


    }

}

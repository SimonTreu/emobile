package de.dfki.gs.controller.ms2.configuration.commands

import de.dfki.gs.domain.simulation.Configuration
import de.dfki.gs.domain.simulation.Fleet
import grails.validation.Validateable

/**
 * Created by glenn on 11.07.14.
 */
@Validateable
class CreateRouteSelectorCommandObject {

    Long configurationStubId
    Long fleetId

    static constraints = {

        configurationStubId nullable: false, validator: { val,obj ->

            Configuration stub = Configuration.get( val )

            if ( stub == null ) {
                return 'configuration.stub.not.exist'
            }

        }

        fleetId nullable: false, validator: { val,obj ->

            Fleet fleet = Fleet.get( val )

            if ( fleet == null ) {

                return 'configuration.fleet.not.exist'
            }

        }

    }

}

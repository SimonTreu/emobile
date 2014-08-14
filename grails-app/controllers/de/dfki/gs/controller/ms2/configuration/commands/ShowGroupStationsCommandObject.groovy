package de.dfki.gs.controller.ms2.configuration.commands

import de.dfki.gs.domain.simulation.Configuration
import grails.validation.Validateable

/**
 * Created by anbu02 on 07.08.14.
 */

@Validateable
class ShowGroupStationsCommandObject {

    Long configurationStubId

    static constraints = {
        configurationStubId nullable: false, validator: { val, obj ->

            Configuration stub = Configuration.get(val)

            if (stub == null) {
                return 'configuration.stub.not.exist'
            }


        }
    }

}
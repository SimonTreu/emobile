package de.dfki.gs.controller.ms2.configuration.commands

import de.dfki.gs.domain.simulation.Configuration
import grails.validation.Validateable

/**
 * Created by glenn on 09.07.14.
 */
@Validateable
class EditConfigurationStubCommandObject {

    Long configurationStubId

    static constraints = {

        configurationStubId nullable: true, validator: { val,obj ->

            if ( val != null ) {

                Configuration stub = Configuration.get( val )

                if ( stub == null ) {
                    return 'configuration.stub.not.exist'
                }

            }

        }

    }

}

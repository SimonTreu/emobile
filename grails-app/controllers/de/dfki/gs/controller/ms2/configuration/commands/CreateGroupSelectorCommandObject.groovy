package de.dfki.gs.controller.ms2.configuration.commands

import de.dfki.gs.domain.simulation.Configuration
import de.dfki.gs.domain.simulation.FillingStationGroup
import grails.validation.Validateable

/**
 * Created by anbu02 on 08.08.14.
 */

@Validateable
class CreateGroupSelectorCommandObject {

    Long configurationStubId
    Long groupId

    static constraints = {

        configurationStubId nullable: false, validator: { val,obj ->

            Configuration stub = Configuration.get( val )

            if ( stub == null ) {
                return 'configuration.stub.not.exist'
            }

        }

        groupId nullable: false, validator: { val,obj ->

            FillingStationGroup fillingStations = FillingStationGroup.get( val )

            if ( fillingStations == null ) {

                return 'configuration.group.not.exist'
            }

        }

    }
}

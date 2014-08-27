package de.dfki.gs.controller.ms2.configuration.commands

import de.dfki.gs.domain.simulation.Configuration
import de.dfki.gs.domain.simulation.FillingStationGroup
import de.dfki.gs.domain.utils.Distribution
import grails.validation.Validateable

/**
 * Created by glenn on 27.08.14.
 */
@Validateable
class DistributionForGroupCommandObject {

    Long configurationStubId
    Long groupId

    Distribution selectedDist


    static constraints = {

        configurationStubId nullable: false, validator: { val,obj ->

            Configuration configuration = Configuration.get( val )
            if ( configuration == null ) {
                return 'configuration.stub.not.exist'
            }

        }

        groupId nullable: false, validator: { val,obj ->

            FillingStationGroup group = FillingStationGroup.get( val )

            if ( group == null ) {
                return 'configuration.group.not.exist'
            }

        }

        selectedDist nullable: false, inList: Distribution.values() as List

    }

}

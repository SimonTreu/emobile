package de.dfki.gs.controller.ms2.configuration.commands

import de.dfki.gs.domain.simulation.Configuration
import de.dfki.gs.domain.simulation.Fleet
import de.dfki.gs.domain.utils.Distribution
import grails.validation.Validateable

/**
 * Created by glenn on 14.07.14.
 */
@Validateable
class DistributionForFleetCommandObject {


    Long configurationStubId
    Long fleetId
    Distribution selectedDist

    Integer fromKm
    Integer toKm

    static constraints = {

        configurationStubId nullable: false, validator: { val,obj ->

            Configuration configuration = Configuration.get( val )
            if ( configuration == null ) {
                return 'configuration.stub.not.exist'
            }

        }

        fleetId nullable: false, validator: { val,obj ->

            Fleet fleet = Fleet.get( val )

            if ( fleet == null ) {
                return 'configuration.fleet.not.exist'
            }

        }

        selectedDist nullable: false, inList: Distribution.values() as List

        fromKm nullable: false
        toKm nullable: false, validator: { val, obj ->

            if ( val <= obj.fromKm ) {
                return 'configuration.fleet.distribution.tokm.too.small'
            }

        }

    }

}

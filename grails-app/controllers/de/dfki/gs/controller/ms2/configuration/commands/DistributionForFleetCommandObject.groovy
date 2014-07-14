package de.dfki.gs.controller.ms2.configuration.commands

import de.dfki.gs.domain.simulation.Fleet
import de.dfki.gs.domain.utils.Distribution
import grails.validation.Validateable

/**
 * Created by glenn on 14.07.14.
 */
@Validateable
class DistributionForFleetCommandObject {

    Long fleetId
    Distribution selectedDist

    static constraints = {

        fleetId nullable: false, validator: { val,obj ->

            Fleet fleet = Fleet.get( val )

            if ( fleet == null ) {
                return 'configuration.fleet.not.exist'
            }

        }

        selectedDist nullable: false, inList: Distribution*.name()

    }

}

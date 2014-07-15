package de.dfki.gs.controller.ms2.configuration.commands

import de.dfki.gs.domain.simulation.CarType
import de.dfki.gs.domain.simulation.Configuration
import de.dfki.gs.domain.simulation.Fleet
import grails.validation.Validateable

/**
 * Created by glenn on 09.07.14.
 */
@Validateable
class AddCarsToFleedCommandObject {

    Long configurationStubId

    Integer carCount
    List<Integer> carCountList

    Long carTypeId
    List<Long> carTypeSelect

    Long fleetStubId
    String nameForFleet

    static constraints = {

        nameForFleet nullable: false, blank: false

        configurationStubId nullable: false, validator: { val,obj ->


            Configuration stub = Configuration.get( val )

            if ( stub == null ) {
                return 'configuration.stub.not.exist'
            }



        }
        carCount nullable: true
        carCountList nullable: true

        carTypeId nullable: true
        carTypeSelect nullable: true

        fleetStubId nullable: false, validator: { val,obj ->

            Fleet fleet = Fleet.get( val )
            if ( fleet == null ) {

                return 'configuration.fleet.not.exist'

            }

        }

    }

}

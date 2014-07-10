package de.dfki.gs.controller.ms2.configuration.commands

import de.dfki.gs.domain.simulation.Configuration
import de.dfki.gs.domain.simulation.FillingStationGroup
import de.dfki.gs.domain.simulation.FillingStationType
import grails.validation.Validateable

/**
 * Created by glenn on 09.07.14.
 */
@Validateable
class AddFillingStationsToGroupCommandObject {

    Long configurationStubId
    Integer stationCount
    List<Integer> stationCountList
    Long stationTypeId
    Long groupStubId

    static constraints = {

        configurationStubId nullable: false, validator: { val,obj ->


            Configuration stub = Configuration.get( val )

            if ( stub == null ) {
                return 'configuration.stub.not.exist'
            }



        }
        stationCount nullable: true
        stationCountList nullable: true
        stationTypeId nullable: false, validator: { val,obj ->

            FillingStationType fillingStationType = FillingStationType.get( val )

            if ( fillingStationType == null ) {

                return 'configuration.stationtype.not.exist'

            }

        }
        groupStubId nullable: false, validator: { val,obj ->

            FillingStationGroup group = FillingStationGroup.get( val )
            if ( group == null ) {

                return 'configuration.group.not.exist'

            }

        }

    }

}

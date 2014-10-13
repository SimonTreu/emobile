package de.dfki.gs.controller.ms2.stats.commands

import de.dfki.gs.domain.simulation.FillingStationGroup
import de.dfki.gs.domain.simulation.FillingStationType
import de.dfki.gs.domain.stats.ExperimentRunResult
import grails.validation.Validateable

/**
 * Created by glenn on 13.10.14.
 */
@Validateable
class ShowStationStatsCommandObject {

    String stationTypeName
    String groupName
    Long experimentRunResultId

    static constraints = {

        stationTypeName( nullable: false, blank: false, validator: { val,obj ->

            if ( !val.equals( "All Stations" ) ) {

                FillingStationType stationType = FillingStationType.findByName( val )

                if ( stationType == null ) {
                    return 'fail'
                }

            }

        } )


        groupName( nullable: false, blanl: false, validator: { val,obj ->

            if ( !val.equals( "All Filling Stations of Experiment" ) ) {

                FillingStationGroup group = FillingStationGroup.findByName( val )

                if ( group == null ) {

                    return 'fail'

                }

            }

        } )

        experimentRunResultId( nullable: false, validator: { val,obj ->

            ExperimentRunResult result = ExperimentRunResult.get( val )

            if ( result == null ) {
                // TODO: check entry
                return 'de.dfki.gs.controller.commands.SimulationCommand.not.exist'
            }

        } )


    }

}

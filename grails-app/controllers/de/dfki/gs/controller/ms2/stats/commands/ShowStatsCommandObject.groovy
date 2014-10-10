package de.dfki.gs.controller.ms2.stats.commands

import de.dfki.gs.domain.simulation.CarType
import de.dfki.gs.domain.simulation.Fleet
import de.dfki.gs.domain.stats.ExperimentRunResult
import grails.validation.Validateable

/**
 * Created by glenn on 10.10.14.
 */
@Validateable
class ShowStatsCommandObject {

    String carTypeName
    String fleetName
    Long experimentRunResultId

    static constraints = {

        carTypeName( nullable: false, blank: false, validator: { val,obj ->

            if ( !val.equals( "All Cars" ) ) {

                CarType carType = CarType.findByName( val )

                if ( carType == null ) {
                    return 'fail'
                }

            }

        } )


        fleetName( nullable: false, blanl: false, validator: { val,obj ->

            if ( !val.equals( "All cars of Experiment" ) ) {

                Fleet fleet = Fleet.findByName( val )

                if ( fleet == null ) {

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

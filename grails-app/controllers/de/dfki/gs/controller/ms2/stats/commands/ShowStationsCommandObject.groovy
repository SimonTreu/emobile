package de.dfki.gs.controller.ms2.stats.commands

import de.dfki.gs.domain.stats.ExperimentRunResult
import grails.validation.Validateable

/**
 * Created by glenn on 14.10.14.
 */
@Validateable
class ShowStationsCommandObject {

    Long experimentRunResultId

    static constraints = {

        experimentRunResultId( nullable: false, validator: { val,obj ->

            ExperimentRunResult result = ExperimentRunResult.get( val )

            if ( result == null ) {
                // TODO: check entry
                return 'de.dfki.gs.controller.commands.SimulationCommand.not.exist'
            }

        } )


    }

}

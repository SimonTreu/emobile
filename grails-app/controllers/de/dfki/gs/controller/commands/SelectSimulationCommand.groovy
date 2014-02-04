package de.dfki.gs.controller.commands

import de.dfki.gs.domain.Simulation
import grails.validation.Validateable

/**
 * @author: glenn
 * @since: 17.10.13
 */
@Validateable
class SelectSimulationCommand {

    Long selectedSimulationId

    static constraints = {
        selectedSimulationId ( nullable: false
        /*
                , validator: { val,obj ->
            if ( !Simulation.read( val ) ) return 'de.dfki.gs.controller.commands.SelectSimulationCommand.not.exist'
        }
        */
        )
    }

}

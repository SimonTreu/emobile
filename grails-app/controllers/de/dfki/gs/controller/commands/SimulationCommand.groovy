package de.dfki.gs.controller.commands

import de.dfki.gs.domain.simulation.Simulation
import grails.validation.Validateable

/**
 * @author: glenn
 * @since: 17.10.13
 */
@Validateable
class SimulationCommand {

    Long simulationId

    static constraints = {

        simulationId( nullable: true, validator: { val,obj ->
            if ( val && !Simulation.get( val ) ) return 'de.dfki.gs.controller.commands.SimulationCommand.not.exist'
        } )

    }

}

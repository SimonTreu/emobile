package de.dfki.gs.controller.commands

import de.dfki.gs.domain.simulation.Simulation
import grails.validation.Validateable

/**
 * Created by glenn on 28.04.14.
 */
@Validateable
class SimulationExcecutorCommand {

    Long simulationId
    Integer relativeSearchLimit

    static constraints = {

        simulationId( nullable: true, validator: { val,obj ->
            if ( val && !Simulation.get( val ) ) return 'de.dfki.gs.controller.commands.SimulationCommand.not.exist'
        } )

        relativeSearchLimit( nullable: false, min: 0, max: 100 )

    }

}

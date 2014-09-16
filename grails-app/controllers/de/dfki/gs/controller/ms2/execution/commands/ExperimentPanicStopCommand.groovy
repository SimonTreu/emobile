package de.dfki.gs.controller.ms2.execution.commands

import de.dfki.gs.domain.simulation.Configuration
import grails.validation.Validateable

/**
 * Created by glenn on 09.09.14.
 */
@Validateable
class ExperimentPanicStopCommand {

    Long configurationId

    static constraints = {

        configurationId( nullable: true, validator: { val,obj ->
            if ( val && !Configuration.get( val ) ) return 'de.dfki.gs.controller.commands.SimulationCommand.not.exist'
        } )

    }

}

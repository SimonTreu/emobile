package de.dfki.gs.controller.commands

import grails.validation.Validateable

/**
 * Created by glenn on 07.04.14.
 */
@Validateable
class CreateSimulationOnlyCommand  {

    String simulationName

    static constraints = {

        simulationName( nullable: false, blank: false, unique: true, validator: { val,obj ->
            if ( val && val.length() < 3 ) return 'de.dfki.gs.controller.commands.CreateSimulationCommand.name.toShort'

            if ( val && val.length() > 50 ) return 'de.dfki.gs.controller.commands.CreateSimulationCommand.name.toLong'
        } )

    }

}

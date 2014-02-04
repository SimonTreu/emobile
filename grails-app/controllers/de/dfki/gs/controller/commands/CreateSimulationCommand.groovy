package de.dfki.gs.controller.commands

import grails.validation.Validateable

/**
 * @author: glenn
 * @since: 17.10.13
 */
@Validateable
class CreateSimulationCommand {

    String simulationName
    Long createRandomRoutes
    Long createRandomStations

    static constraints = {

        simulationName( nullable: false, blank: false, unique: true, validator: { val,obj ->
            if ( val && val.length() < 3 ) return 'de.dfki.gs.controller.commands.CreateSimulationCommand.name.toShort'

            if ( val && val.length() > 50 ) return 'de.dfki.gs.controller.commands.CreateSimulationCommand.name.toLong'
        } )

    }

}

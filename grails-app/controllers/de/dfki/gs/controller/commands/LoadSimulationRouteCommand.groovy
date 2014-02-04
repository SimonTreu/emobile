package de.dfki.gs.controller.commands

import de.dfki.gs.domain.SimulationRoute
import grails.validation.Validateable

/**
 * @author: glenn
 * @since: 22.10.13
 */
@Validateable
class LoadSimulationRouteCommand {

    long simulationRouteId

    static constraints = {
        simulationRouteId ( nullable: false, validator: { val,obj ->
            if ( !SimulationRoute.get( val ) ) return 'de.dfki.gs.controller.commands.LoadSimulationRouteCommand.not.exist'
        } )
    }

}

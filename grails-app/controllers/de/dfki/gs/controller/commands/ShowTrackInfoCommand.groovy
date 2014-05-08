package de.dfki.gs.controller.commands

import de.dfki.gs.domain.SimulationRoute
import de.dfki.gs.domain.Track
import grails.validation.Validateable

/**
 * @author: glenn
 * @since: 16.10.13
 */
@Validateable
class ShowTrackInfoCommand {

    Long simulationRouteId

    static constraints = {

        simulationRouteId( nullable: false, validator: { val, obj ->
            if ( !SimulationRoute.get( val ) ) return false

            return true;
        })


    }

}

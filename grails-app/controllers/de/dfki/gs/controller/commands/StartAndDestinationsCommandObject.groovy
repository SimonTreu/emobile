package de.dfki.gs.controller.commands

import de.dfki.gs.domain.Simulation
import de.dfki.gs.utils.LatLonPoint
import grails.validation.Validateable

/**
 * @author: glenn
 * @since: 11.10.13
 */
@Validateable
class StartAndDestinationsCommandObject implements Serializable {

    Long simulationId

    List<LatLonPoint> destinationPoints = []
    LatLonPoint startPoint

    static constraints = {

        simulationId( nullable: false, validator: { val, obj ->
            if ( !Simulation.get( val ) ) {
                return 'simulation not exist'
            }
        } )

        destinationPoints( nullable: false )

        startPoint( nullable: false )

    }

}

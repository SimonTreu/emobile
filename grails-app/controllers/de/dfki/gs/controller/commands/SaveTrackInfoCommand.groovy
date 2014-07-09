package de.dfki.gs.controller.commands

import de.dfki.gs.domain.simulation.CarType
import de.dfki.gs.domain.simulation.Simulation
import de.dfki.gs.domain.simulation.SimulationRoute
import de.dfki.gs.domain.simulation.Track
import grails.validation.Validateable

/**
 * @author: glenn
 * @since: 17.10.13
 */
@Validateable
class SaveTrackInfoCommand {

    Long simulationRouteId

    Long carType
    Integer initialPersons
    Double initialEnergy


    static constraints = {

        simulationRouteId( nullable: false, validator: { val, obj ->
            if ( !SimulationRoute.get( val ) ) return false

            return true;
        })

        carType( nullable: false, validator: { val,obj ->
            if ( !CarType.get( val ) ) {
                return false
            }

            return true
        } )

        initialPersons( nullable: true )

        initialEnergy( nullable: true )

    }



}

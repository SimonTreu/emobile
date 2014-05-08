package de.dfki.gs.controller.commands

import de.dfki.gs.domain.CarType
import de.dfki.gs.domain.Simulation
import de.dfki.gs.domain.SimulationRoute
import de.dfki.gs.domain.Track
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

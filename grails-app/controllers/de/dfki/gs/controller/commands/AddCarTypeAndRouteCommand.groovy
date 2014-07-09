package de.dfki.gs.controller.commands

import de.dfki.gs.domain.simulation.CarType
import de.dfki.gs.domain.simulation.Simulation
import grails.validation.Validateable

/**
 * Created by glenn on 07.04.14.
 */
@Validateable
class AddCarTypeAndRouteCommand {

    long simulationId
    long carTypeId
    long count
    // long targets
    double linearDistance

    static constraints = {
        simulationId ( nullable: false, validator: { val,obj  ->
            if ( Simulation.get( val ) == null ) {
                return "fail"
            }
        } )

        carTypeId( nullable: false, validator: { val,obj ->
            if ( CarType.get( val ) == null ) {
                return "fail"
            }
        } )

        count ( nullable: false, blank: false, matches: '\\d+' )

        // targets ( nullable: false, blank: false, matches: '\\d+' )
        linearDistance( nullable: false, blank: false, matches: '\\d+' )
    }

}

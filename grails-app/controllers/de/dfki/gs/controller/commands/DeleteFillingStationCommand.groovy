package de.dfki.gs.controller.commands

import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.Simulation
import grails.validation.Validateable

/**
 * Created by glenn on 10.04.14.
 */
@Validateable
class DeleteFillingStationCommand {

    long simulationId
    String fillingType

    static constraints = {

        simulationId ( nullable: false, validator: { val,obj  ->
            if ( Simulation.get( val ) == null ) {
                return "fail"
            }
        } )

        fillingType( nullable: false, inList: GasolineStationType.values()*.toString() )

    }

}

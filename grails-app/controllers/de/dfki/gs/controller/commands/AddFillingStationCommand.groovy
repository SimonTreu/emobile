package de.dfki.gs.controller.commands

import de.dfki.gs.domain.simulation.CarType
import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.simulation.Simulation
import grails.validation.Validateable

/**
 * Created by glenn on 08.04.14.
 */
@Validateable
class AddFillingStationCommand {

    long simulationId
    long count
    String fillingType

    static constraints = {

        simulationId ( nullable: false, validator: { val,obj  ->
            if ( Simulation.get( val ) == null ) {
                return "fail"
            }
        } )

        fillingType( nullable: false, inList: GasolineStationType.values()*.toString() )

        count ( nullable: false, blank: false, matches: '\\d+' )

    }

}

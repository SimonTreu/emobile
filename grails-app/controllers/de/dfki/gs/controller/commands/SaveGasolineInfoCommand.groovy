package de.dfki.gs.controller.commands

import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.Simulation
import grails.validation.Validateable

/**
 * @author: glenn
 * @since: 18.10.13
 */
@Validateable
class SaveGasolineInfoCommand {

    Long gasolineId
    String gasolineType
    String gasolineName
    String selectedSimulationId

    static constraints = {

        gasolineId( nullable: false, validator: { val, obj ->
            if ( !GasolineStation.get( val ) ) return false

            return true;
        })

        gasolineType( nullable: false, inList: GasolineStationType.values()*.toString() )


        selectedSimulationId( nullable: true, validator: { val,obj ->
            if ( val && val ==~ /\d+/ && !Simulation.get( Long.parseLong( val ) ) ) return 'de.dfki.gs.controller.commands.SaveGasolineInfoCommand.not.exist'
        } )

        gasolineName( nullable: true, validator: { val,obj ->
            if ( val && val.length() > 50 ) return 'de.dfki.gs.controller.commands.SaveGasolineInfoCommand.name.tooLong'
        } )

    }



}

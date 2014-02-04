package de.dfki.gs.controller.commands

import de.dfki.gs.domain.CarType
import de.dfki.gs.domain.Simulation
import de.dfki.gs.domain.Track
import grails.validation.Validateable

/**
 * @author: glenn
 * @since: 17.10.13
 */
@Validateable
class SaveTrackInfoCommand {

    Long trackId
    String carType
    Integer initialPersons
    Double initialEnergy
    Long selectedSimulationId
    Double selectedMaxEnergy
    Double selectedEnergyDrain

    static constraints = {

        trackId( nullable: false, validator: { val, obj ->
            if ( !Track.get( val ) ) return false

            return true;
        })

        carType( nullable: false, inList: CarType.values()*.toString() )

        initialPersons( nullable: false, min: 1 )

        initialEnergy( nullable: false, min: 0d )

        selectedSimulationId( nullable: true, validator: { val,obj ->
            if ( val && !Simulation.get( val ) ) return 'de.dfki.gs.controller.commands.SaveTrackInfoCommand.not.exist'
        } )

        selectedMaxEnergy( nullable: false )

        selectedEnergyDrain( nullable: false )
    }



}

package de.dfki.gs.controller.commands

import de.dfki.gs.domain.Track
import grails.validation.Validateable

/**
 * @author: glenn
 * @since: 16.10.13
 */
@Validateable
class ShowTrackInfoCommand {

    Long trackId

    static constraints = {

        trackId( nullable: false, validator: { val, obj ->
            if ( !Track.get( val ) ) return false

            return true;
        })


    }

}

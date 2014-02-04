package de.dfki.gs.controller.commands

import grails.validation.Validateable

/**
 * @author: glenn
 * @since: 31.10.13
 */
@Validateable
class ScaleCommand {

    Double scaleValue

    static constraints = {
        scaleValue( nullable: false )

    }


}

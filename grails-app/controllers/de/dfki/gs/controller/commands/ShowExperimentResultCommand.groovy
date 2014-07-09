package de.dfki.gs.controller.commands


import grails.validation.Validateable

/**
 * Created by glenn on 23.04.14.
 */
@Validateable
class ShowExperimentResultCommand {

    Long resultId

    static constraints = {

        resultId( nullable: false, validator: { val,obj ->
            if ( !ExperimentRunResult.get( val ) ) {
                return false
            }
        } )

    }


}

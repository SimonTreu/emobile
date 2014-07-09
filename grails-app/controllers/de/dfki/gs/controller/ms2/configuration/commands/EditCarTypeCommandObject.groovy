package de.dfki.gs.controller.ms2.configuration.commands

import de.dfki.gs.domain.simulation.CarType
import grails.validation.Validateable

/**
 * Created by glenn on 09.07.14.
 */
@Validateable
class EditCarTypeCommandObject {

    Long carTypeId

    static constraints = {

        carTypeId nullable: false, validator: { val, obj ->

            CarType carType = CarType.get( val )
            if ( carType == null ) {
                return 'cartype.not.exist'
            }

        }

    }

}

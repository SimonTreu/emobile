package de.dfki.gs.controller.commands

import de.dfki.gs.domain.users.Person
import grails.validation.Validateable

/**
 * Created by glenn on 01.07.14.
 */
@Validateable
class AllowPersonCommandObject {

    Long personId

    static constraints = {

        personId ( nullable: false, validator: { val,obj ->

            Person p = Person.get( val )
            if ( p == null ) {
                return "no.such.person"
            }

            if ( p.accountLocked == false ) {
                return "already.allowed.to.login"
            }

        } )

    }

}

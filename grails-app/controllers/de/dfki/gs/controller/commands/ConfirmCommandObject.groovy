package de.dfki.gs.controller.commands

import de.dfki.gs.domain.Person
import grails.validation.Validateable

/**
 * Created by glenn on 01.07.14.
 */
@Validateable
class ConfirmCommandObject {

    String username
    String confirmationCode

    static constraints = {

        username blank: false, validator: { val,obj ->

            Person p = Person.findByUsername( val )

            if ( p == null ) {
                return "no.matching.user"
            }

        }

        confirmationCode blank: false, validator: { val,obj ->

            Person p = Person.findByConfirmationCode( val )
            if ( p == null ) {
                return "no.matching.user"
            }

            if ( !p.username.equals( obj.username ) ) {
                return "no.matching.confirmation.code"
            }

            if ( ! (p.enabled == false && p.accountLocked == true ) ) {
                return "already.signed.in"
            }

        }

    }

}

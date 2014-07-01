package de.dfki.gs.controller.commands

import de.dfki.gs.domain.Person
import grails.validation.Validateable

/**
 * Created by glenn on 01.07.14.
 */
@Validateable
class SigninCommandObject {

    String signinUserName
    String signinGivenName
    String signinFamilyName

    static constraints = {

        signinUserName ( nullable: false, email: true, validator: { val,obj ->

            Person p = Person.findByUsername( val )
            if ( p != null ) {
                return 'de.dfki.gs.person.exist.already'
            }

        } )
        signinGivenName ( nullable: false, blank: false )
        signinFamilyName ( nullable: false, blank: false  )

    }

}

package de.dfki.gs.controller.commands

import de.dfki.gs.domain.users.Person
import grails.validation.Validateable
import de.dfki.gs.domain.users.Company
/**
 * Created by glenn on 01.07.14.
 */
@Validateable
class SigninCommandObject {

    String signinUserName
    String signinGivenName
    String signinFamilyName
    Long companyId

    String password
    String confirm

    static constraints = {

        signinUserName ( nullable: false, email: true, validator: { val,obj ->

            Person p = Person.findByUsername( val )
            if ( p != null ) {
                return 'de.dfki.gs.person.exist.already'
            }

        } )
        companyId ( nullable: false, blank: false, validator: { val,obj ->
            Company company = Company.get(val)

            if ( company == null  ) {
                return 'de.dfki.gs.person.company.not.match'
            }
        })
        signinGivenName ( nullable: false, blank: false )
        signinFamilyName ( nullable: false, blank: false  )
        password ( nullable: false, blank: false )
        confirm ( nullable: false, blank: false, validator: { val,obj ->
            if ( !val.equals( obj.password ) ) {
                return 'de.dfki.gs.person.password.not.match'
            }
        } )

    }

}

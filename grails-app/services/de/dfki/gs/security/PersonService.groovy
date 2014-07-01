package de.dfki.gs.security

import de.dfki.gs.domain.Person
import grails.transaction.Transactional

@Transactional
class PersonService {

    def mailService


    def createSigninPerson( String givenName, String familyName, String emailAddress, String password ) {

        def m = [:]


        Person p = Person.findByUsername( emailAddress )
        if ( p != null ) {
            log.error( "person with email ${emailAddress} already exists" )

            // ende der geschichte!
            m.message = "person with email ${emailAddress} already exists"
            return m
        }

        UUID uuid = UUID.randomUUID()

        p = new Person(
                familyName: familyName,
                givenName: givenName,
                username: emailAddress,
                password: password,
                accountLocked: true,
                enabled: false,
                confirmationCode: uuid.toString()
        )

        if ( !p.save( flush: true ) ) {

            log.error( "error saving new signin user with email: ${emailAddress} -- errors: ${p.errors}" )

            m.message = "error saving new signin user with email: ${emailAddress} -- errors: ${p.errors}"
            return m
        }

        // otherwise inform admins with email to activate and enable user

        m.message = "signin request saved with email address ${emailAddress}"
        m.person = p


        return m
    }

    def serviceMethod() {

    }
}

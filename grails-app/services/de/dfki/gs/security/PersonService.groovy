package de.dfki.gs.security

import de.dfki.gs.bootstrap.BootstrapHelper
import de.dfki.gs.domain.Person
import de.dfki.gs.domain.PersonRole
import de.dfki.gs.domain.Role
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

    def allowPersonToLogin( Person p ) {

        p.accountLocked = false

        if ( !p.save( flush: true ) ) {
            log.error( "failed to save person: ${p.errors}" )
        }

    }

    def confirmPersonAsUser( Person p ) {

        p.enabled = true

        Role role = findOrCreateRole( "ROLE_USER" )

        if (!PersonRole.create(p, role, true)) log.error("failed to add Persion[ $p ] to Role[ $role ] -- errors: ${p?.errors}")


        if ( !p.save( flush: true ) ) {
            log.error( "failed to save person: ${p.errors}" )
        }


    }

    Role findOrCreateRole( String roleName, failOnError = true ) {

        def role = Role.findByAuthority(roleName)
        if (!role) {
            role = new Role(authority: roleName)
            if (!role.save(flush: true, failOnError: true)) log.error("failed to save ROLE[ $roleName ] -- errors: ${role.errors}")
        }

        return role
    }
}

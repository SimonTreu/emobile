package de.dfki.gs.bootstrap

import de.dfki.gs.domain.Person
import de.dfki.gs.domain.PersonRole
import de.dfki.gs.domain.RequestMap
import de.dfki.gs.domain.Role
import org.apache.commons.logging.LogFactory

/**
 * Created by glenn on 30.06.14.
 */
class BootstrapHelper {

    private static def log = LogFactory.getLog(BootstrapHelper.class)


    Role findOrCreateRole( String roleName, failOnError = true ) {

        def role = Role.findByAuthority(roleName)
        if (!role) {
            role = new Role(authority: roleName)
            if (!role.save(flush: true, failOnError: true)) log.error("failed to save ROLE[ $roleName ] -- errors: ${role.errors}")
        }

        return role
    }


    Person findOrCreatePersonInRole(String userName, String givenName, String familyName, Role role, boolean failOnError = true, boolean flush = true ) {

        log.debug( "findOrCreatePersonInRole: ${userName}")

        def p = Person.findByUsername( userName )

        if (!p) {
            p = createPerson( userName, givenName, familyName, failOnError, flush )
        }
        if (!PersonRole.create(p, role, true)) log.error("failed to add Persion[ $p ] to Role[ $role ] -- errors: ${p?.errors}")

        return p
    }

    Person createPerson( String userName, String givenName, String familyName, boolean failOnError = true, boolean flush = true ) {


            def found = Person.findByUsername( userName )
            if( found != null ) return found

            def username4Pass = userName.split('@')[0]
            def newPassword = "hua!$username4Pass"

            UUID uuid = UUID.randomUUID()

            def p = new Person(
                    username: userName,
                    givenName: givenName,
                    familyName: familyName,
                    password: newPassword,
                    confirmationCode: uuid.toString()
            )

            if (!p.save(flush: flush, failOnError: failOnError)) log.error("failed to save Person[ $p ] -- errors: ${p?.errors}" )

            return p

    }

    RequestMap findOrCreateRequestmap(String url, String configAttribute, failOnError = true) {
        def p = RequestMap.findByUrl(url)
        if (!p) {
            p = new RequestMap(url: url, configAttribute: configAttribute)
            if (!p.save(flush: true, failOnError: failOnError)) log.error("failed to save Requestmap[ $p ] -- errors: ${p.errors}")
        }
        return p
    }


}

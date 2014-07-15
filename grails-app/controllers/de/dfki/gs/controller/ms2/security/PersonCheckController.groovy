package de.dfki.gs.controller.ms2.security

import de.dfki.gs.domain.users.Person
import grails.plugin.springsecurity.SpringSecurityUtils

class PersonCheckController {

    def springSecurityService

    def checkPerson() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {

            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        def m = [:]

        m.givenName = person.givenName
        m.familyName = person.familyName


        render template: '/templates/security/currentUser', model: m
    }
}

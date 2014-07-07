package de.dfki.gs.controller

import de.dfki.gs.domain.Person
import grails.plugin.springsecurity.SpringSecurityUtils

class ExtrasController {

    def simulationDataService
    def springSecurityService
    def index() {
        def m = [ : ]
        m.carTypeCars = simulationDataService.collectCarTypes()
        Person loggedInPerson = (Person) springSecurityService.currentUser
        if (loggedInPerson!=null) {
            m.welcome = [
                    'givenName' : loggedInPerson.givenName,
                    'familyName': loggedInPerson.familyName
            ]

            render(view: 'index', model: m)
        } else {
            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
        }
    }

    def station () {
        def m = [:]
        m.electricStations = simulationDataService.collectElectricStations()
        Person loggedInPerson = (Person) springSecurityService.currentUser
        if (loggedInPerson!=null) {
            m.welcome = [
                    'givenName' : loggedInPerson.givenName,
                    'familyName': loggedInPerson.familyName
            ]

            render(view: 'newStation', model: m)
        }else {
            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
        }
    }
}

package de.dfki.gs.controller

import de.dfki.gs.domain.Person

class ExtrasController {

    def simulationDataService
    def springSecurityService
    def index() {
        def m = [ : ]
        m.carTypeCars = simulationDataService.collectCarTypes()
        Person loggedInPerson = (Person) springSecurityService.currentUser
        m.welcome = [
                'givenName' : loggedInPerson.givenName,
                'familyName' : loggedInPerson.familyName
        ]

        render( view: 'index', model: m )
    }

    def station () {
        def m = [ : ]
        m.electricStations = simulationDataService.collectElectricStations()
        Person loggedInPerson = (Person) springSecurityService.currentUser
        m.welcome = [
                'givenName' : loggedInPerson.givenName,
                'familyName' : loggedInPerson.familyName
        ]

        render( view: 'newStation', model: m )
    }
}

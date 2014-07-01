package de.dfki.gs.controller

import de.dfki.gs.domain.Person

class FrontController {

    def springSecurityService


    def index() {
        redirect( action: init() )
    }

    def init() {

        Person loggedInPerson = (Person) springSecurityService.currentUser

        log.error( "currentUser: ${loggedInPerson}" )

        def model = [:]
        model.name = "xa"
        model.info = ""
        model.welcome = [
                'givenName' : loggedInPerson.givenName,
                'familyName' : loggedInPerson.familyName
        ]


        render( view: 'index',  model: model )
    }


}

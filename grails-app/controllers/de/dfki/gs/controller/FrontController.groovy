package de.dfki.gs.controller

class FrontController {

    def index() {
        redirect( action: init() )
    }

    def init() {

        def model = [:]
        model.name = "Hua!"
        model.info = "voodoo"

        render( view: 'index', model: model )
    }

}

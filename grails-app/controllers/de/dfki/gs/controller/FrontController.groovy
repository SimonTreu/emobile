package de.dfki.gs.controller

class FrontController {

    def index() {
        redirect( action: init() )
    }

    def init() {



        def model = [:]
        model.name = ""
        model.info = ""

        render( view: 'index', model: model )
    }

}

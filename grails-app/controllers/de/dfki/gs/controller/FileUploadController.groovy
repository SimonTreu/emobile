package de.dfki.gs.controller

class FileUploadController {

    def index() {}

    def testing() {

        println "params: ${params}"

        def m = [ : ]
        m.name = "Hua!!!!"

        render( view: 'testing', model: m )
    }
}

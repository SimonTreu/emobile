package de.dfki.gs.controller

class ExtrasController {

    def simulationDataService

    def index() {
        def m = [ : ]
        m.carTypeCars = simulationDataService.collectCarTypes()

        render( view: 'index', model: m )
    }

    def station () {
        def m = [ : ]
        m.electricStations = simulationDataService.collectElectricStations()

        render( view: 'newStation', model: m )
    }
}

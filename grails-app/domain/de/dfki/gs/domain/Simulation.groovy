package de.dfki.gs.domain

class Simulation {

    String name
    Set<SimulationRoute> simulationRoutes
    Set<GasolineStation> gasolineStations


    static hasMany = [
            simulationRoutes: SimulationRoute,
            gasolineStations : GasolineStation
    ]

    static constraints = {
    }

    static mapping = {
        simulationRoutes lazy: false
        gasolineStations lazy: false
        cache usage: 'read-write', include: 'non-lazy'
    }

}

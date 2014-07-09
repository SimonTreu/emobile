package de.dfki.gs.domain.simulation

class Track {


    List edges
    SimulationRoute simulationRoute

    static hasMany = [
            edges: TrackEdge
    ]


    static constraints = {
        simulationRoute nullable: true
    }

    static belongsTo = [ simulationRoute : SimulationRoute ]

    static mapping = {
        edges lazy: true
    }
}

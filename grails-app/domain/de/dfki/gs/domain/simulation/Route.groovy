package de.dfki.gs.domain.simulation

class Route {


    List edges

    static hasMany = [
            edges: TrackEdge
    ]


    static constraints = {
    }

    static mapping = {
    }
}

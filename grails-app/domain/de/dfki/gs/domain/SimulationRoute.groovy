package de.dfki.gs.domain

class SimulationRoute {

    // Track track;

    CarType carType

    Integer initialPersons;
    Double initialEnergy;

    Double maxEnergy

    Simulation simulation

    Double energyDrain

    Double plannedDistance


    List edges

    static hasMany = [
            edges: TrackEdge
    ]


    static constraints = {
        // track( nullable: true )
        initialPersons( nullable: false, min: 1 )
        initialEnergy( nullable: true )
        maxEnergy( nullable: true )
        simulation( nullable: true )
        energyDrain( nullable: true )
        plannedDistance( nullable: true )
    }

    static mapping = {
        edges lazy: true
    }

    transient def fetchComplete()  {

        def fetchedEdges = []

        withTransaction {

            edges.each { fetchedEdges << TrackEdge.get( it.id ) }

        }


        log.error( "fetched ${fetchedEdges.size()} edges for ${id}" )
        return fetchedEdges
    }

}

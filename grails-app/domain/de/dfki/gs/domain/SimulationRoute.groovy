package de.dfki.gs.domain

class SimulationRoute {

    Track track;

    CarType carType

    Integer initialPersons;
    Double initialEnergy;

    Double maxEnergy

    Simulation simulation

    Double energyDrain



    static constraints = {
        track( nullable: true )
        initialPersons( nullable: false, min: 1 )
        initialEnergy( nullable: true )
        maxEnergy( nullable: true )
        simulation( nullable: true )
        energyDrain( nullable: true )
    }

    static mapping = {
        track lazy: false
    }

}

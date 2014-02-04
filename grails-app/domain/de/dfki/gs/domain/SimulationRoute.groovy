package de.dfki.gs.domain

class SimulationRoute {

    Track track;
    String carType;
    Integer initialPersons;
    Double initialEnergy;

    Double maxEnergy

    Simulation simulation

    Double energyDrain



    static constraints = {
        carType( nullable: false, blank: false, inList: CarType.values()*.toString() )
        track( nullable: true )
        initialPersons( nullable: false, min: 1 )
        initialEnergy( nullable: false, min: 0d )
        maxEnergy( nullable: false, min: 0d )
        simulation( nullable: true )
        energyDrain( nullable: false, min: 0.1d )
    }

    static mapping = {
        track lazy: false
    }

}

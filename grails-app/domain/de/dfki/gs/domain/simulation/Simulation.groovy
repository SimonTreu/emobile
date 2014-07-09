package de.dfki.gs.domain.simulation

import de.dfki.gs.domain.users.Company

class Simulation {

    String name

    /**
     * space for long description, if needed
     */
    String description

    /**
     * short abstract, what is it about, if needed
     */
    String abstractText

    /**
     * for the possibility to have different configs for one simulation object
     */
    Set<Configuration> configurations

    /**
     * the company of the person, who creates this simulation
     */
    Company company


    static hasMany = [
            configurations : Configuration
    ]

    static constraints = {
    }

    static mapping = {
    }

}

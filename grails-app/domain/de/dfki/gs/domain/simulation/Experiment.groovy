package de.dfki.gs.domain.simulation

import de.dfki.gs.domain.stats.ExperimentResult

/**
 * each configuration can be executed many times, for each of execution there is an experiment object
 *
 * TODO: later here can be used a scheduler.. starting time
 */
class Experiment {

    /**
     * the statistical part is covered here
     */
    ExperimentResult experimentResult


    long executionTimeMillis


    static hasMany = [
    ]

    static constraints = {
    }

    static mapping = {
    }


}

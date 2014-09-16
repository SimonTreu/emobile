package de.dfki.gs.domain.stats

/**
 *
 */
class ExperimentRunResult {

    long configurationId

    long simTimeMillis


    List<PersistedCarAgentResult> persistedCarAgentResults

    List<PersistedFillingStationResult> persistedFillingStationResults


    static hasMany = [
            persistedCarAgentResults : PersistedCarAgentResult,
            persistedFillingStationResults : PersistedFillingStationResult
    ]






    static constraints = {
    }
}

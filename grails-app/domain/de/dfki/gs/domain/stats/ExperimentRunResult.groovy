package de.dfki.gs.domain.stats

/**
 *
 */
class ExperimentRunResult {


    int targetCount

    long simulationId


    List<PersistedCarAgentResult> persistedCarAgentResults


    /**
     * counts of filling stations
     */
    List<FillingStationTypeCount> fillingStationTypeCounts

    /**
     * counts of cars
     */
    List<CarTypeCount> carTypeCounts


    static hasMany = [
            fillingStationTypeCounts : FillingStationTypeCount,
            carTypeCounts : CarTypeCount,
            persistedCarAgentResults : PersistedCarAgentResult
    ]






    static constraints = {
    }
}

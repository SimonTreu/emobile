package de.dfki.gs.domain.stats

class ExperimentResult {


    Set<CarResult> carResults
    Set<FillingStationResult> fillingStationResults

    static hasMany = [
            carResults : CarResult,
            fillingStationResults : FillingStationResult
    ]

    static constraints = {
    }

    static mapping = {
    }
}

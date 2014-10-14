package de.dfki.gs.model.elements.results

import de.dfki.gs.domain.simulation.FillingStationType

/**
 * Created by glenn on 01.04.14.
 */
class EFillingStationAgentResult {

    long timeInUse;
    long simulationTime;
    long groupId
    int failedToRouteCount

    float lat
    float lon

    FillingStationType fillingStationType

}

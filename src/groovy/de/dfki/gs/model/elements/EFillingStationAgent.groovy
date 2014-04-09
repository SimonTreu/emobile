package de.dfki.gs.model.elements

import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.model.elements.results.EFillingStationAgentResult

/**
 * Created by glenn on 01.04.14.
 *
 * this class models a filling station in the model
 *
 * it contains all relevant attributes like: ( type, connector-type, filling speed, geographically position ) -> GasolineStationType
 * also it sets up the waiting queue for service requesting cars
 *
 * it also contains the usage result
 *
 */
class EFillingStationAgent {

    private GasolineStationType gasolineStationType

    private EFillingStationAgentResult eFillingStationAgentResult




    private EFillingStationAgent() {}

    public static EFillingStationAgent createFillingStationAgent( GasolineStationType type ) {

        EFillingStationAgent agent = new EFillingStationAgent();
        agent.eFillingStationAgentResult = new EFillingStationAgentResult();
        agent.gasolineStationType = type;

        return agent;
    }



}

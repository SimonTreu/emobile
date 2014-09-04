package de.dfki.gs.model.elements

import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.simulation.FillingStation
import de.dfki.gs.domain.simulation.FillingStationType
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
class EFillingStationAgent extends Agent {

    private String gasolineStationType

    private EFillingStationAgentResult eFillingStationAgentResult

    private FillingStationStatus fillingStationStatus;

    // in kW per second
    Double fillingPortion

    private long fillingStationId

    private long reservedByAgentId


    private float lon
    private float lat

    private int failedToRouteCount

    long timeInUse = 0;

    private long stationId

    private EFillingStationAgent() {}

    public static EFillingStationAgent createFillingStationAgentFromFillingStation(
                    FillingStation fillingStation,
                    FillingStationType stationType ) {


        EFillingStationAgent agent = new EFillingStationAgent();


        agent.stationId = fillingStation.id
        agent.eFillingStationAgentResult = new EFillingStationAgentResult( gasolineStationType: stationType.power );

        agent.gasolineStationType = stationType.power;
        agent.fillingStationStatus = FillingStationStatus.FREE;

        agent.lat = fillingStation.lat
        agent.lon = fillingStation.lon

        double fillingPortion = 0.0001
        // calculate filling Portion
        agent.fillingPortion = stationType.fillingPortion

        agent.failedToRouteCount = 0

        return agent;
    }

    public static EFillingStationAgent createFillingStationAgent( GasolineStation station ) {

        EFillingStationAgent agent = new EFillingStationAgent();

        agent.eFillingStationAgentResult = new EFillingStationAgentResult( gasolineStationType: station.type );
        agent.gasolineStationType = station.type;
        agent.fillingStationStatus = FillingStationStatus.FREE;

        double fillingPortion = 0.0001
        // calculate filling Portion
        switch ( station.type ) {
            case GasolineStationType.AC_2_3KW.toString() :
                fillingPortion = 2.3 / ( 60*60 )
                break;
            case GasolineStationType.AC_3_7KW.toString() :
                fillingPortion = 3.7 / ( 60*60 )
                break;
            case GasolineStationType.AC_7_4KW.toString() :
                fillingPortion = 7.4 / ( 60*60 )
                break;
            case GasolineStationType.AC_11_1KW.toString() :
                fillingPortion = 11.1 / ( 60*60 )
                break;
            case GasolineStationType.AC_22_2KW.toString() :
                fillingPortion = 22.2 / ( 60*60 )
                break;
            case GasolineStationType.AC_43KW.toString() :
                fillingPortion = 43 / ( 60*60 )
                break;
            case GasolineStationType.DC_49_8KW.toString() :
                fillingPortion = 49.8 / ( 60*60 )
                break;

        }

        agent.fillingPortion = fillingPortion
        agent.reservedByAgentId = -1

        return agent;
    }



    @Override
    def step(long currentTime) {

        if ( fillingStationStatus.equals( FillingStationStatus.IN_USE ) ) {
            timeInUse++;
        }

    }

    @Override
    def finish() {

        eFillingStationAgentResult.timeInUse = timeInUse
        eFillingStationAgentResult.simulationTime = currentTime

    }

    synchronized FillingStationStatus getFillingStationStatus() {
        return fillingStationStatus
    }

    synchronized int getFailedToRouteCount() {

        return failedToRouteCount

    }

    void updateFailedToRouteCount( long byAgentId ) {

        synchronized ( getClass() ) {
            this.failedToRouteCount++
        }

    }

    void setFillingStationStatus( FillingStationStatus fillingStationStatus, long reservedByAgentId ) {

        synchronized ( getClass() ) {
            this.fillingStationStatus = fillingStationStatus
            this.reservedByAgentId = reservedByAgentId

            log.debug( "fs ${personalId} set to ${fillingStationStatus}  from car ${reservedByAgentId}" )
        }

    }

    EFillingStationAgentResult geteFillingStationAgentResult() {
        return eFillingStationAgentResult
    }

    long getReservedByAgentId() {
        return reservedByAgentId
    }

    float getLon() {
        return lon
    }

    float getLat() {
        return lat
    }

    long getStationId() {
        return stationId
    }
}

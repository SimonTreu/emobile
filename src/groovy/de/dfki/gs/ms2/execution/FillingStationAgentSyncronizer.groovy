package de.dfki.gs.ms2.execution

import de.dfki.gs.model.elements.EFillingStationAgent
import de.dfki.gs.model.elements.FillingStationStatus
import de.dfki.gs.utils.Calculater
import org.apache.commons.logging.LogFactory

import java.util.concurrent.ConcurrentMap

/**
 * this class holds a map and a list of all related fillingStationAgents and fillingStations
 * and provide requesting features and sync features
 */
class FillingStationAgentSyncronizer {


    private static def log = LogFactory.getLog( FillingStationAgentSyncronizer.class )


    /**
     * real filling stations ( fillingStation.id -> fillingStationAgent )
     */
    ConcurrentMap<Long, EFillingStationAgent> fillingStationAgentsMap;

    Map<Long,EFillingStationAgent> unroutableStations

    public FillingStationAgentSyncronizer(
                ConcurrentMap<Long,EFillingStationAgent> fillingStationAgentsMap
    ) {

        this.fillingStationAgentsMap = fillingStationAgentsMap
        unroutableStations = new HashMap<Long,EFillingStationAgent>()

    }

    /**
     * given a currentPosition of a carAgent this method returns a reference to
     * a FillingStationAgent which is the closest one within a radius and
     * fillingStationAgent is FREE
     *
     * if there is this fillingStationAgent, it is set to IN_USE and that reference
     * is returned
     * otherwise the return value is null
     *
     * @param currentLon
     * @param currentLat
     * @param radius
     * @return
     */
    public EFillingStationAgent reserveFreeFillingStationAgentInRadius(
                                    long reservedForAgentId,
                                    float currentLon,
                                    float currentLat,
                                    float radius ) {


        EFillingStationAgent lastFreeAgent = null
        float minDistance = Float.MAX_VALUE

        for ( EFillingStationAgent fsAgent : fillingStationAgentsMap.values() ) {

            int failedToRouteCount = fsAgent.getFailedToRouteCount()

            if ( failedToRouteCount <= 1 ) {

                // in [ km ]
                float currentDistance = Calculater.haversine( currentLat, currentLon, fsAgent.lon, fsAgent.lat )




                if ( currentDistance <= radius && currentDistance <= minDistance
                        && fsAgent.getFillingStationStatus() == FillingStationStatus.FREE
                ) {

                    // set new minDistance
                    minDistance = currentDistance

                    // set last found agent to FREE again!
                    if ( lastFreeAgent != null ) {
                        log.error( "car ${reservedForAgentId} releases FS ${fsAgent.personalId} for distance reasons" )
                        lastFreeAgent.setFillingStationStatus( FillingStationStatus.FREE, -1 )
                    }

                    // reserve !
                    fsAgent.setFillingStationStatus( FillingStationStatus.IN_USE, reservedForAgentId )
                    lastFreeAgent = fsAgent

                    log.error( "Reserved ${fsAgent.personalId} for ${reservedForAgentId} -- dist: ${minDistance}" )

                }

            } else {

                log.error( "${failedToRouteCount} times failed to route to ${fsAgent.personalId} -- skipped" )
                if ( fsAgent.getFillingStationStatus() == FillingStationStatus.FREE ) {

                    fsAgent.finish()
                    unroutableStations.put( fsAgent.stationId, fsAgent )
                    fillingStationAgentsMap.remove( fsAgent.getStationId() )

                }

            }


        }

        return lastFreeAgent
    }

    public void updateFailedToRouteCount( EFillingStationAgent agent, long personalId ) {

        agent.updateFailedToRouteCount( personalId )

    }

    public void setFillingStationToFree( EFillingStationAgent agent ) {

        agent.setFillingStationStatus( FillingStationStatus.FREE, -1 )

    }

    Map<Long, EFillingStationAgent> getUnroutableStations() {
        return unroutableStations
    }
}

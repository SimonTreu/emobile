package de.dfki.gs.ms2.execution

import de.dfki.gs.model.elements.EFillingStationAgent
import de.dfki.gs.model.elements.FillingStationStatus
import de.dfki.gs.utils.Calculater
import org.apache.commons.logging.LogFactory

import java.util.concurrent.ConcurrentHashMap
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
    Map<Long, EFillingStationAgent> fillingStationAgentsMap;

    ConcurrentHashMap<Long,EFillingStationAgent> unroutableStations

    ConcurrentHashMap<Long,List<Long>> unroutableStationByCars

    public FillingStationAgentSyncronizer(
                Map<Long,EFillingStationAgent> fillingStationAgentsMap
    ) {

        this.fillingStationAgentsMap = fillingStationAgentsMap
        unroutableStations = new ConcurrentHashMap<Long,EFillingStationAgent>()
        unroutableStationByCars = new ConcurrentHashMap<Long,List<Long>>()

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
                                    float radius,
                                    List<Long> excludedStationIds ) {


        EFillingStationAgent lastFreeAgent = null
        float minDistance = Float.MAX_VALUE

        // TODO: CHECK BREAKPOINT
        for ( EFillingStationAgent fsAgent : fillingStationAgentsMap.values() ) {

            int failedToRouteCount = fsAgent.getFailedToRouteCount()

            if ( failedToRouteCount <= 1 && !excludedStationIds.contains( fsAgent.stationId ) ) {

                // in [ km ]
                float currentDistance = Calculater.haversine( currentLat, currentLon, fsAgent.lon, fsAgent.lat )




                if ( currentDistance <= radius && currentDistance <= minDistance
                        && fsAgent.fillingStationStatus.equals( FillingStationStatus.FREE )
                ) {

                    // set new minDistance
                    minDistance = currentDistance

                    // set last found agent to FREE again!
                    if ( lastFreeAgent != null ) {
                        log.debug( "car ${reservedForAgentId} releases FS ${fsAgent.personalId} for distance reasons" )
                        lastFreeAgent.setFillingStationStatus( FillingStationStatus.FREE, -1 )
                    }

                    // reserve !
                    fsAgent.setFillingStationStatus( FillingStationStatus.IN_USE, reservedForAgentId )
                    lastFreeAgent = fsAgent

                    log.debug( "Reserved fillingStation: ${fsAgent.personalId} for car: ${reservedForAgentId} -- dist: ${minDistance}" )

                }

            } else {

                // log.error( "${failedToRouteCount} times failed to route to ${fsAgent.personalId} -- skipped" )

                List<Long> failedCars = unroutableStationByCars.get( fsAgent.stationId )

                if ( failedCars == null ) {
                    unroutableStationByCars.put( fsAgent.stationId, new ArrayList<Long>() )
                }

                if ( !unroutableStationByCars.get( fsAgent.stationId ).contains( reservedForAgentId ) ) {
                    unroutableStationByCars.get( fsAgent.stationId ).add( reservedForAgentId )
                }

                if ( fsAgent.geteFillingStationAgentResult() == FillingStationStatus.FREE
                        && unroutableStationByCars.get( fsAgent ).size() > 10 ) {

                    fsAgent.finish()
                    unroutableStations.put( fsAgent.stationId, fsAgent )
                    fillingStationAgentsMap.remove( fsAgent.getStationId() )

                    log.error( "FillingStations ${fsAgent.stationId} unroutable for 10 cars -> removed from sim" )
                }

            }


        }
        // log.error( unroutableStationByCars )
        return lastFreeAgent
    }

    public static void updateFailedToRouteCount( EFillingStationAgent agent, long personalId ) {

        agent.updateFailedToRouteCount( personalId )

    }

    public static void setFillingStationToFree( EFillingStationAgent agent ) {

        agent.setFillingStationStatus( FillingStationStatus.FREE, -1 )

    }

    Map<Long, EFillingStationAgent> getUnroutableStations() {
        return unroutableStations
    }
}

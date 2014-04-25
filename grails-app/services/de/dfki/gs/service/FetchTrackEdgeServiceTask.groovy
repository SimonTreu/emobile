package de.dfki.gs.service

import de.dfki.gs.domain.SimulationRoute
import de.dfki.gs.domain.TrackEdge

/**
 * Created by glenn on 19.04.14.
 */
class FetchTrackEdgeServiceTask implements Runnable {

    long simulationRouteId
    List<TrackEdge> trackEdgeList;

    FetchTrackEdgeServiceTask(long simulationRouteId) {
        this.simulationRouteId = simulationRouteId
        trackEdgeList = new ArrayList<TrackEdge>()
    }

    @Override
    void run() {

        SimulationRoute.withTransaction {

            SimulationRoute simulationRoute = SimulationRoute.get( simulationRouteId )


            for ( TrackEdge edge : simulationRoute.edges ) {
                trackEdgeList.add( TrackEdge.get( edge.id ) );
            }

        }

    }

    long getSimulationRouteId() {
        return simulationRouteId
    }

    List<TrackEdge> getTrackEdgeList() {
        return trackEdgeList
    }
}

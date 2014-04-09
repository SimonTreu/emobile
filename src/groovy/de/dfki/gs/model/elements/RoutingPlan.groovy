package de.dfki.gs.model.elements

import com.sun.tools.doclets.formats.html.resources.standard
import de.dfki.gs.domain.TrackEdge
import de.dfki.gs.utils.LatLonPoint

/**
 *
 * contains routes and targets
 *
 *
 * Created by glenn on 01.04.14.
 */
class RoutingPlan {

    private List<TrackEdge> trackEdges

    private LatLonPoint startPoint

    private LatLonPoint endPoint

    private RoutingPlan() {}

    public static RoutingPlan createRoutingPlan( List<TrackEdge> trackEdges ) {

        RoutingPlan plan = new RoutingPlan();
        // grails loads lazely trackedges, so we fetch them here
        List<TrackEdge> fetchedTrackEdges = new ArrayList<TrackEdge>()

        if ( trackEdges.size() > 0 ) {

            plan.startPoint = new LatLonPoint( trackEdges.get( 0 ).fromLat, trackEdges.get( 0 ).fromLon );
            plan.endPoint = new LatLonPoint( trackEdges.get( trackEdges.size() - 1 ).toLat, trackEdges.get( trackEdges.size() - 1 ).toLon );

            for ( TrackEdge trackEdge : trackEdges ) {
                fetchedTrackEdges.add( TrackEdge.get( trackEdge.id ) )
            }

        }

        plan.trackEdges = fetchedTrackEdges;

        return plan;
    }

    List<TrackEdge> getTrackEdges() {
        return trackEdges
    }

    LatLonPoint getStartPoint() {
        return startPoint
    }

    LatLonPoint getEndPoint() {
        return endPoint
    }
}

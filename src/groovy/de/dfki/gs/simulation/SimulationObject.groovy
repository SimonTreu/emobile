package de.dfki.gs.simulation

import de.dfki.gs.domain.TrackEdge
import de.dfki.gs.utils.LatLonPoint

/**
 * @author: glenn
 * @since: 23.10.13
 */
class SimulationObject {

    double currentEnergy

    double maxEnergy

    double initialEnergy


    double searchLimit

    double emptyLimit


    long startTime

    String carType

    List<TrackEdge> edges

    // LatLonPoint currentPosition

    TrackEdge currentEdge

    int currentEdgeIndex

    boolean finished

}

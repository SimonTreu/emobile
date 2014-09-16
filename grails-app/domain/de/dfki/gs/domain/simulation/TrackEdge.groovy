package de.dfki.gs.domain.simulation

import de.dfki.gs.domain.utils.TrackEdgeType

class TrackEdge {

    /**
     * maybe a dirty hack, at least an unproper design
     * but this is to collect trackedges faster by it's simulationId
     */
    Long routeId;

    Long gisId;

    /**
     * Berlin: lat: 52, lon: 13
     */
    float fromLat
    float fromLon

    float toLat
    float toLon

    Integer kmh
    float cost
    float km

    String type

    String streetName

    static constraints = {

        type( nullable: false, blank: false, inList: TrackEdgeType.values()*.toString() )

        streetName ( nullable: true, blank: true )

    }

    // static belongsTo = [ track : Track ]
}

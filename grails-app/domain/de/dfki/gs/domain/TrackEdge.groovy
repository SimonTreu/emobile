package de.dfki.gs.domain

class TrackEdge {

    /**
     * maybe a dirty hack, at least an unproper design
     * but this is to collect trackedges faster by it's simulationId
     */
    Long simulationRouteId;

    Long gisId;

    /**
     * Berlin: lat: 52, lon: 13
     */
    Double fromLat
    Double fromLon

    Double toLat
    Double toLon

    Integer kmh
    Double cost
    Double km

    String type

    String streetName

    static constraints = {

        type( nullable: false, blank: false, inList: TrackEdgeType.values()*.toString() )

        streetName ( nullable: true, blank: true )

    }

    // static belongsTo = [ track : Track ]
}

<div id="openModal" class="modalDialogMap">

<div id="map" style="background-color: #eee; width:100%; height:100%; position: absolute; left:0%; top:0% padding-top:1px" class="olMap"></div>

<script type="text/javascript">

    // global variables
    var map, vectors, routesLayer, lonlat, zoom, markers;

    var startIconSize = new OpenLayers.Size( 40, 40 );
    var targetIconSize = new OpenLayers.Size( 20, 20 );
    // var offset = new OpenLayers.Pixel( -(size.x/2) , -(size.y/2));
    var startIcon =  new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'start.png' )}" , startIconSize );
    var targetIcon = new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'target.png' )}" , targetIconSize );
    var viaIcon = new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'via.png' )}" , targetIconSize );

    var gasolineIconSize = new OpenLayers.Size( 30, 30 );
    var gasolineNormalIcon = new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'gasoline-normal.png' )}" , gasolineIconSize );
    var gasolineFastIcon = new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'gasoline-fast.png' )}" , gasolineIconSize );
    var gasolineMiddleIcon = new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'gasoline-middle.png' )}" , gasolineIconSize );
    var gasolineSlowIcon = new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'gasoline-slow.png' )}" , gasolineIconSize );

    var p1 = new OpenLayers.Projection( "EPSG:4326" );
    var pMerc = new OpenLayers.Projection( "EPSG:900913" );
    lonlat = new OpenLayers.LonLat( 13.38, 52.52 );
    zoom = 11;
    map = new OpenLayers.Map( "map", {
        controls: [
            new OpenLayers.Control.KeyboardDefaults(),
            new OpenLayers.Control.Navigation(),
            new OpenLayers.Control.LayerSwitcher(),
            new OpenLayers.Control.PanZoomBar(),
            new OpenLayers.Control.MousePosition()
        ],
        maxExtent:
                new OpenLayers.Bounds( -20037508.34, -20037508.34,
                        20037508.34,  20037508.34 ),
        numZoomLevels: 18,
        maxResolution: 156543,
        units: 'm',
        projection: pMerc,
        displayProjection: p1
    } );

    routesLayer = new OpenLayers.Layer.Vector( "Route Vectors", {
        styleMap: new OpenLayers.StyleMap({'default':{
            strokeColor: "red",  // TODO: chose a good color
            strokeOpacity: 0.6,
            strokeWidth: 6
        }}) } );

    markers = new OpenLayers.Layer.Markers( "Markers", {
        strategies: [
            new OpenLayers.Strategy.Fixed(),
            new OpenLayers.Strategy.Cluster()
        ]
    } );

    var mapnik_layer = new OpenLayers.Layer.OSM.Mapnik( "Mapnik" );
    var mapgoogle_layer = new OpenLayers.Layer.Google( "Google Streets");

    vectors = new OpenLayers.Layer.Vector("Vector Layer", {
        styleMap: new OpenLayers.StyleMap({'default':{
            strokeColor: "#FF11FF",  // TODO: chose a good color
            strokeOpacity: 0.6,
            strokeWidth: 6,
            fillColor: "#FF5500",
            fillOpacity: 0.5,
            pointRadius: 6,
            pointerEvents: "visiblePainted",
            label : "Start",
            fontSize: "10px",
            fontFamily: "Courier New, monospace",
            fontWeight: "bold",
            labelOutlineColor: "white",
            labelOutlineWidth: 5
        }}),
        eventListeners: {
            'featureadded' : function( evt ) {
                var feature = evt.feature;
                var data = {
                    feature: feature,
                    map: map,
                    vectors: vectors,
                    markers: markers,
                    routesLayer: routesLayer,
                    calculateRouteLink: '${g.createLink( controller: 'mapView', action: 'calculateRoute' )}',
                    showGasolineInfoLink: '${g.createLink( controller: 'mapView', action: 'showGasolineInfo', params: [ gasolineId: gasolineId ] )}',
                    showTrackInfoLink: '${g.createLink( controller: 'mapView', action: 'showTrackInfo', params: [ simulationRouteId: simulationRouteId ] )}'
                };
                serialize( data );
            }
        }
    });

    map.addLayers( [ mapnik_layer, mapgoogle_layer, vectors, routesLayer, markers ] );


    map.addControl(new OpenLayers.Control.MousePosition());
    map.addControl(new OpenLayers.Control.EditingToolbar( vectors ) );
    var options = {
        hover: true,
        click: true
    };

    // DRAW Routes to 'routes'
    // and all markers to markers!
    var fleetDat = new Object();
    <g:each var="fleet" in="${fleets}" >
        <g:each var="car" in="${fleet.cars}" >
            var segments = new Array();
            <g:each var="seg" in="${car.route}">
                var segm = new Object();
                segm.fromX = ${seg.fromLon};
                segm.fromY = ${seg.fromLat};
                segm.toX = ${seg.toLon};
                segm.toY = ${seg.toLat};
                segments.push( segm );
            </g:each>
            fleetDat.route = segments;
            fleetDat.routesLayer = routesLayer;
            fleetDat.markers = markers;
            drawRoute( fleetDat );
        </g:each>
    </g:each>


    var gasDat = new Object();
    <g:each var="gas" in="${gasolineStations}" >

    gasDat.fromX = ${gas.fromY};
    gasDat.fromY = ${gas.fromX};
    gasDat.gasolineId = ${gas.gasolineId};
    gasDat.gasolineType = "${gas.gasolineType}";
    gasDat.showGasolineInfoLink = '${g.createLink( controller: 'mapView', action: 'showGasolineInfo', params: [ gasolineId: gas.gasolineId ] )}';
    drawGasolineStation( gasDat );
    </g:each>


    lonlat.transform( p1, pMerc );
    map.setCenter( lonlat, zoom );



    /*function deleteTrackInfos( trackId ) {

        // doesn't work
        // var kk = routes.features.getFeaturesByAttribute( "trackId", trackId );

        var routeFeatSize = routesLayer.features.length;
        var routeToDelete = new Array();

        for ( var k = 0; k < routeFeatSize; k++ ) {
            if ( routesLayer.features[ k ].simulationRouteId == id ) {
                routeToDelete.push( routesLayer.features[ k ] );
            }
        }

        routesLayer.removeFeatures( routeToDelete );


        var markersFeatSize = markers.markers.length;
        var markersToDelete = new Array();

        for ( var k = 0; k < markersFeatSize; k++ ) {
            if ( markers.markers[ k ].simulationRouteId == id ) {
                markersToDelete.push( markers.markers[ k ] );
            }
        }

        for ( var s = 0; s < markersToDelete.length; s++ ) {
            markers.removeMarker( markersToDelete[ s ] );
        }

        document.getElementsByTagName("body")[0].removeChild(document.
                getElementById("trackInfo"));
        document.getElementsByTagName("body")[0].removeChild(document.
                getElementById("lightBox"));
    }


    function deleteGasolineStation( gasolineId ) {

        var markersFeatSize = markers.markers.length;
        var markersToDelete = new Array();

        for ( var k = 0; k < markersFeatSize; k++ ) {
            if ( markers.markers[ k ].gasolineId == gasolineId ) {
                markersToDelete.push( markers.markers[ k ] );
            }
        }

        for ( var s = 0; s < markersToDelete.length; s++ ) {
            markers.removeMarker( markersToDelete[ s ] );
        }

        document.getElementsByTagName("body")[0].removeChild(document.
                getElementById("trackInfo"));
        document.getElementsByTagName("body")[0].removeChild(document.
                getElementById("lightBoxGasoline"));

    }*/


</script>
<a class="close" title="Close" href=""></a>
</div>
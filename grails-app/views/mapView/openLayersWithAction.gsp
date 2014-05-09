<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 25.09.13
  Time: 14:26
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
<title></title>

<link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">

<g:javascript src="ol/OpenLayers.js" />

<%-- <g:javascript src="firebug.js" /> --%>

<script type="text/javascript" src="http://openstreetmap.org/openlayers/OpenStreetMap.js"></script>

<g:javascript library="jquery" />
<g:javascript src="application.js" />


<script type="text/javascript">

    function serialize( feature ) {

        var geom = feature.geometry;
        var jData;
        var dests = new Array();

        var currentLon  = map.center.lon;
        var currentLat  = map.center.lat;
        var currentZoom = map.zoom;

        if ( geom.CLASS_NAME == "OpenLayers.Geometry.LineString" ) {
            // there are some lines..
            var coords = geom.components;
            if ( coords != null && coords.length > 1 ) {

                var startPoint = coords[ 0 ];
                startPoint.transform(
                        new OpenLayers.Projection( "EPSG:900913"), // transform from Spherical Mercator Projection
                        new OpenLayers.Projection( "EPSG:4326" ) // to WGS 1984
                );

                jData = {
                    type: 'lineRoute',
                    currentLon: currentLon,
                    currentLat: currentLat,
                    currentZoom: currentZoom,
                    startPoint: {
                        x: startPoint.x,
                        y: startPoint.y
                    },
                    destinationPoints : dests
                };
                var size = coords.length;
                for ( var k = 1 ; k < size; k++ ) {
                    var destPoint = coords[ k ];
                    destPoint.transform(
                            new OpenLayers.Projection( "EPSG:900913"), // transform from Spherical Mercator Projection
                            new OpenLayers.Projection( "EPSG:4326" ) // to WGS 1984
                    );

                    dests.push( { x: destPoint.x, y: destPoint.y } );
                }
            }
        } else if ( geom.CLASS_NAME == "OpenLayers.Geometry.Point" ) {
            // gasoline station
            var gasolinePoint = new OpenLayers.LonLat( geom.x, geom.y );
            gasolinePoint.transform(
                    new OpenLayers.Projection( "EPSG:900913"), // transform from Spherical Mercator Projection
                    new OpenLayers.Projection( "EPSG:4326" ) // to WGS 1984
            );

            jData = {
                type: 'gasolinePoint',
                currentLon: currentLon,
                currentLat: currentLat,
                currentZoom: currentZoom,
                startPoint: {
                    x: gasolinePoint.lon,
                    y: gasolinePoint.lat
                }
            }
        }


        jQuery.ajax({
            url: "${g.createLink( controller: 'mapView', action: 'calculateRoute' )}",
            type: "POST",
            data: JSON.stringify( { data: jData } ) ,

            success: function( dat ) {

                if ( dat.type == "gasolinePoint" ) {

                    drawGasolinePoint( dat );

                } else if ( dat.type == "lineRoute" ) {

                    drawLineRoute( dat );

                }

                map.setCenter( new OpenLayers.LonLat( dat.currentLon, dat.currentLat ), dat.currentZoom );
                // vectors.destroyFeatures( [ feature ] );

            },

            contentType: "application/json; charset=utf-8",
            dataType: "json"
        });
        vectors.destroyFeatures( [ feature ] );


    }

    function drawGasolinePoint( data ) {

        var lonlatGasoline = new OpenLayers.LonLat( data.gasolinePoint.x, data.gasolinePoint.y );
        lonlatGasoline.transform(
                new OpenLayers.Projection("EPSG:4326"),
                new OpenLayers.Projection("EPSG:900913")
        );
        var gasolineMarker = new OpenLayers.Marker(
                lonlatGasoline,
                gasolineSlowIcon.clone()
        );

        gasolineMarker.gasolineId = data.gasolineId;

        gasolineMarker.events.register(
                'mousedown', gasolineMarker, function( evt ) {

                    showGasolineInfos( 'display', gasolineMarker.gasolineId );

                    OpenLayers.Event.stop( evt );
                }
        );

        gasolineMarker.setOpacity( 0.9 );
        markers.addMarker( gasolineMarker );

    }

    function drawLineRoute( data ) {

        var size = data.routes.length;

        // for all routes
        for ( var i = 0; i < size; i++ ) {

            // create markers set here and append markers object to highlighted lines
            // 0.route, 0.element -> startMarker
            // all other routes lat element -> targetMarker
            var route = data.routes[ i ];
            var segmentSize = route.length;
            for ( var j = 0; j < segmentSize; j++ ) {

                var routeSegment = route[ j ];


                if ( i == 0 && j == 0 ) {
                    // first route: startMarker
                    var lonlatStart = new OpenLayers.LonLat( routeSegment.fromX, routeSegment.fromY );
                    lonlatStart.transform(
                            new OpenLayers.Projection("EPSG:4326"),
                            new OpenLayers.Projection("EPSG:900913")
                    );
                    var startMarker = new OpenLayers.Marker(
                            lonlatStart,
                            startIcon.clone()
                    );

                    startMarker.trackId = data.trackId;

                    startMarker.events.register(
                            'mousedown', startMarker, function( evt ) {

                                showTrackInfos( 'display', startMarker.trackId );

                                OpenLayers.Event.stop( evt );
                            }
                    );
                    startMarker.setOpacity( 0.7 );
                    markers.addMarker( startMarker );
                }

                if ( i != 0 && i != size-1 && j == 0 ) {

                    // route: viaMarker
                    var lonlatVia = new OpenLayers.LonLat( routeSegment.fromX, routeSegment.fromY );
                    lonlatVia.transform(
                            new OpenLayers.Projection("EPSG:4326"),
                            new OpenLayers.Projection("EPSG:900913")
                    );
                    var viaMarker = new OpenLayers.Marker(
                            lonlatVia,
                            viaIcon.clone()
                    );

                    viaMarker.trackId = data.trackId;

                    viaMarker.setOpacity( 0.7 );
                    markers.addMarker( viaMarker );

                }

                if ( i == size-1 && j == segmentSize-1 ) {
                    // set target flag
                    var lonlatTarget = new OpenLayers.LonLat( routeSegment.fromX, routeSegment.fromY );
                    lonlatTarget.transform(
                            new OpenLayers.Projection("EPSG:4326"),
                            new OpenLayers.Projection("EPSG:900913")
                    );
                    var targetMarker = new OpenLayers.Marker(
                            lonlatTarget,
                            targetIcon.clone()
                    );
                    targetMarker.trackId = data.trackId;
                    targetMarker.setOpacity( 0.7 );
                    markers.addMarker( targetMarker );
                }

                addHiglightedLines( data.trackId,
                        routes,
                        routeSegment.fromX,
                        routeSegment.fromY,
                        routeSegment.toX,
                        routeSegment.toY
                )
            }

        }

    }


    // preload images
    (function() {
        var roots = ["draw_point", "draw_line", "draw_polygon", "pan"];
        var onImages = [];
        var offImages = [];
        for(var i=0; i<roots.length; ++i) {
            onImages[i] = new Image();
            onImages[i].src = "../theme/default/img/" + roots[i] + "_on.png";
            offImages[i] = new Image();
            offImages[i].src = "../theme/default/img/" + roots[i] + "_on.png";
        }
    })();

    function showGasolineInfos( mode, gasolineId ) {
        // 'display', gasolineMarker.gasolineId
        if ( mode == 'display' ) {

            if( document.getElementById("trackInfo") === null ) {
                div = document.createElement("div");
                div.setAttribute('id', 'trackInfo');
                div.setAttribute('className', 'overlayBG');
                div.setAttribute('class', 'overlayBG');
                document.getElementsByTagName("body")[0].appendChild(div);
            }
            if( document.getElementById("lightBoxGasoline") === null ) {
                div = document.createElement("div");
                div.setAttribute('id', 'lightBoxGasoline');

                var link = "${g.createLink( controller: 'mapView', action: 'showGasolineInfo', params: [ gasolineId: gasolineId ] )}";

                jQuery.ajax({
                    url: link + gasolineId,
                    type: "POST",
                    success: function( data ) {
                        div.innerHTML = data;
                        document.getElementsByTagName("body")[0].appendChild(div);
                    }
                });
            }

        } else {
            // trackInfo is the overlay !!
            document.getElementsByTagName("body")[0].removeChild(document.
                    getElementById("trackInfo"));
            document.getElementsByTagName("body")[0].removeChild(document.
                    getElementById("lightBoxGasoline"));
        }

    }

    function showTrackInfos( mode, trackId ) {

        if( mode == 'display' ) {
            if( document.getElementById("trackInfo") === null ) {
                div = document.createElement("div");
                div.setAttribute('id', 'trackInfo');
                div.setAttribute('className', 'overlayBG');
                div.setAttribute('class', 'overlayBG');
                document.getElementsByTagName("body")[0].appendChild(div);
            }
            if( document.getElementById("lightBox") === null ) {
                div = document.createElement("div");
                div.setAttribute('id', 'lightBox');

                var link = "${g.createLink( controller: 'mapView', action: 'showTrackInfo', params: [ trackId: trackId ] )}";

                jQuery.ajax({
                    url: link + trackId,
                    type: "POST",
                    success: function( data ) {
                        div.innerHTML = data;
                        document.getElementsByTagName("body")[0].appendChild(div);
                    }
                });

            }


        } else {
            document.getElementsByTagName("body")[0].removeChild(document.
                    getElementById("trackInfo"));
            document.getElementsByTagName("body")[0].removeChild(document.
                    getElementById("lightBox"));

        }
    }



</script>

</head>
<body>

<g:render template="/layouts/topbar" />

<div id="map" class="olMap"></div>

<es:mapDiv/>


<r:layoutResources></r:layoutResources>
</body>
</html>
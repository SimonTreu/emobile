<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 17.10.13
  Time: 17:02
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

    <%--
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
                        simulationId: ${simulationId},
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
                    simulationId: ${simulationId},
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
                    vectors.destroyFeatures( [ feature ] );

                },

                contentType: "application/json; charset=utf-8",
                dataType: "json"
            });


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

                    if ( i == size-1 && j == segmentSize-1 ) {
                        // set target flag
                        var lonlatTarget = new OpenLayers.LonLat( routeSegment.toX, routeSegment.toY );
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

        function drawGasolineStation( dat ) {

            var gasolineType = dat.gasolineType;
            var newIcon;
            if ( gasolineType == "slow" ) {
                newIcon = gasolineSlowIcon.clone()
            } else if ( gasolineType == "middle" ) {
                newIcon = gasolineMiddleIcon.clone()
            } else if ( gasolineType == "fast" ) {
                newIcon = gasolineFastIcon.clone()
            }

            var lonlatGasoline = new OpenLayers.LonLat( dat.fromX, dat.fromY );

            lonlatGasoline.transform(
                new OpenLayers.Projection("EPSG:4326"),
                new OpenLayers.Projection("EPSG:900913")
            );

            var gasolineMarker = new OpenLayers.Marker(
                    lonlatGasoline,
                    newIcon
            );

            gasolineMarker.gasolineId = dat.gasolineId;

            gasolineMarker.events.register(
                    'mousedown', gasolineMarker, function( evt ) {

                        showGasolineInfos( 'display', gasolineMarker.gasolineId );

                        OpenLayers.Event.stop( evt );
                    }
            );

            gasolineMarker.setOpacity( 0.9 );
            markers.addMarker( gasolineMarker );

        }

        function drawRoute( data ) {
            // create markers set here and append markers object to highlighted lines
            // 0.route, 0.element -> startMarker
            // all other routes lat element -> targetMarker
            var route = data.route

            var segmentSize = route.length;
            for ( var j = 0; j < segmentSize; j++ ) {

                var routeSegment = route[ j ];


                if ( j == 0 ) {
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

                if ( j == segmentSize-1 ) {
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

    </script>
    --%>
</head>
<body>

<g:render template="/layouts/topbar" />

<div id="content">

    <div id="map" style="background-color: #eee; width:90%; height:90%; position: absolute; left:5%; top:5%" class="olMap"></div>



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
                fontSize: "12px",
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
                        simulationId: ${simulationId},
                        calculateRouteLink: '${g.createLink( controller: 'mapView', action: 'calculateRoute' )}',
                        showGasolineInfoLink: '${g.createLink( controller: 'mapView', action: 'showGasolineInfo', params: [ gasolineId: gasolineId ] )}',
                        showTrackInfoLink: '${g.createLink( controller: 'mapView', action: 'showTrackInfo', params: [ trackId: trackId ] )}'
                    };
                    serialize( data );
                }
            }
        });

        map.addLayers( [ mapnik_layer, vectors, routesLayer, markers ] );


        map.addControl(new OpenLayers.Control.MousePosition());
        map.addControl(new OpenLayers.Control.EditingToolbar( vectors ) );
        var options = {
            hover: true,
            click: true
        };

        // now add all feuature vecotrs to 'routes'
        // and all markers to markers!
        var routeDat = new Object();
        <g:each var="route" in="${routes}" >

            console.log( ${route.id} );

            routeDat.trackId = ${route.trackId};

            var segments = new Array();

            var vias = new Array();
            <g:each var="via" in="${route.vias}">

                var vv = new Object();
                vv.fromX = ${via.fromX};
                vv.fromY = ${via.fromY};
                vias.push( vv );
            </g:each>

            routeDat.vias = vias;

            <g:each var="seg" in="${route.route}">
                var segm = new Object();
                segm.fromX = ${seg.fromY};
                segm.fromY = ${seg.fromX};
                segm.toX = ${seg.toY};
                segm.toY = ${seg.toX};
                segments.push( segm );
            </g:each>

            routeDat.route = segments;

            var showTrackInfoLink = '${g.createLink( controller: 'mapView', action: 'showTrackInfo', params: [ trackId: route.trackId ] )}';
            routeDat.showTrackInfoLink = showTrackInfoLink;
            routeDat.routesLayer = routesLayer;
            routeDat.markers = markers;
            drawRoute( routeDat );

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



        function deleteTrackInfos( trackId ) {

            // doesn't work
            // var kk = routes.features.getFeaturesByAttribute( "trackId", trackId );

            var routeFeatSize = routesLayer.features.length;
            var routeToDelete = new Array();

            for ( var k = 0; k < routeFeatSize; k++ ) {
                if ( routesLayer.features[ k ].trackId == trackId ) {
                    routeToDelete.push( routesLayer.features[ k ] );
                }
            }

            routesLayer.removeFeatures( routeToDelete );


            var markersFeatSize = markers.markers.length;
            var markersToDelete = new Array();

            for ( var k = 0; k < markersFeatSize; k++ ) {
                if ( markers.markers[ k ].trackId == trackId ) {
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

        }


    </script>
</div>

<g:render template="/layouts/footer" />


<r:layoutResources></r:layoutResources>


</body>
</html>
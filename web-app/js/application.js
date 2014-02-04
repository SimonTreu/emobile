if (typeof jQuery !== 'undefined') {
	(function($) {
		$('#spinner').ajaxStart(function() {
			$(this).fadeIn();
		}).ajaxStop(function() {
			$(this).fadeOut();
		});
	})(jQuery);
}

function addHiglightedLines( trackId, layer, fromLon, fromLat, toLon, toLat ) {

    var startPoint = new OpenLayers.Geometry.Point( fromLon, fromLat );
    var endPoint = new OpenLayers.Geometry.Point( toLon, toLat );

    var vector;

    if ( trackId == 0 ) {
        var style = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
        style.strokeWidth = 2;
        style.strokeColor = "#00FF00";

        vector = new OpenLayers.Feature.Vector(
            ( new OpenLayers.Geometry.LineString( [startPoint, endPoint] ).transform( new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913") ) ),
            null,
            style
        );
    } else {
        vector = new OpenLayers.Feature.Vector(
            ( new OpenLayers.Geometry.LineString( [startPoint, endPoint] ).transform( new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913") ) )
        );

    }




    vector.trackId = trackId;

    layer.addFeatures(
        [ vector ]
    );

}

function saveGasolineInfos( gasolineId, link, markers ) {

    var jData = { gasolineId: gasolineId }


    jQuery.ajax({
        url: link,
        type: "POST",
        data: JSON.stringify( { data: jData } ) ,

        success: function( dat ) {

            var gasolineType = dat.gasolineType;
            var newIcon;
            if ( gasolineType == "slow" ) {
                newIcon = gasolineSlowIcon.clone()
            } else if ( gasolineType == "middle" ) {
                newIcon = gasolineMiddleIcon.clone()
            } else if ( gasolineType == "fast" ) {
                newIcon = gasolineFastIcon.clone()
            }

            // find the marker with gasolineId and choose a new icon by gasolineType
            var markersFeatSize = markers.markers.length;
            for ( var k = 0; k < markersFeatSize; k++ ) {
                if ( markers.markers[ k ].gasolineId == gasolineId ) {

                    // markers.markers[ k ].

                    var lonlatGasoline = markers.markers[ k ].lonlat;
                    /*
                     lonlatGasoline.transform(
                     new OpenLayers.Projection("EPSG:4326"),
                     new OpenLayers.Projection("EPSG:900913")
                     );
                     */
                    var gasolineMarker = new OpenLayers.Marker(
                        lonlatGasoline,
                        newIcon
                    );

                    gasolineMarker.gasolineId = gasolineId;

                    gasolineMarker.events.register(
                        'mousedown', gasolineMarker, function( evt ) {

                            showGasolineInfos( 'display', gasolineMarker.gasolineId, link );

                            OpenLayers.Event.stop( evt );
                        }
                    );

                    markers.removeMarker( markers.markers[ k ] );

                    gasolineMarker.setOpacity( 0.9 );
                    markers.addMarker( gasolineMarker );

                }
            }

        },

        contentType: "application/json; charset=utf-8",
        dataType: "json"
    });


    document.getElementsByTagName("body")[0].removeChild(document.
        getElementById("trackInfo"));
    document.getElementsByTagName("body")[0].removeChild(document.
        getElementById("lightBoxGasoline"));
}


function saveTrackInfos(  ) {

    /*
     initialEnergy, initialPersons, selectedCarType, trackId
    var link = "${g.createLink( controller: 'mapView', action: 'showTrackInfo', params: [ trackId: trackId, initialEnergy: initialEnergy, initialPersons: initialPersons, carType: selectedCarType ] )}";

    jQuery.ajax({
        url: link,
        type: "POST"
    });
    */


    document.getElementsByTagName("body")[0].removeChild(document.
        getElementById("trackInfo"));
    document.getElementsByTagName("body")[0].removeChild(document.
        getElementById("lightBox"));
}





/**
 *
 * @param data
 */
function serialize( data ) {

    var feature      = data[ 'feature' ];
    var map          = data[ 'map' ];
    var markers      = data[ 'markers' ];
    var vectors      = data[ 'vectors' ];
    var routesLayer  = data[ 'routesLayer' ];
    // can be null, if called initially:
    var simulationId = data[ 'simulationId' ];
    var calculateRouteLink = data[ 'calculateRouteLink' ];
    var showGasolineInfoLink = data[ 'showGasolineInfoLink' ];
    var showTrackInfoLink = data[ 'showTrackInfoLink' ];

    var geom = feature[ 'geometry' ];

    var jData;
    var dests = new Array();

    var currentLon  = map[ 'center' ].lon;
    var currentLat  = map[ 'center' ].lat;
    var currentZoom = map[ 'zoom' ];

    if ( geom[ 'CLASS_NAME' ] == "OpenLayers.Geometry.LineString" ) {
        // there are some lines..
        var coords = geom[ 'components' ];
        if ( coords != null && coords.length > 1 ) {

            var startPoint = coords[ 0 ];
            startPoint.transform(
                new OpenLayers.Projection( "EPSG:900913"), // transform from Spherical Mercator Projection
                new OpenLayers.Projection( "EPSG:4326" ) // to WGS 1984
            );

            jData = {
                type: 'lineRoute',
                simulationId: simulationId,
                currentLon:   currentLon,
                currentLat:   currentLat,
                currentZoom:  currentZoom,
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
    } else if ( geom[ 'CLASS_NAME' ] == "OpenLayers.Geometry.Point" ) {
        // gasoline station
        var gasolinePoint = new OpenLayers.LonLat( geom.x, geom.y );
        gasolinePoint.transform(
            new OpenLayers.Projection( "EPSG:900913"), // transform from Spherical Mercator Projection
            new OpenLayers.Projection( "EPSG:4326" ) // to WGS 1984
        );

        jData = {
            type:           'gasolinePoint',
            simulationId:   simulationId,
            currentLon:     currentLon,
            currentLat:     currentLat,
            currentZoom:    currentZoom,
            startPoint: {
                x: gasolinePoint.lon,
                y: gasolinePoint.lat
            }
        }
    }


    jQuery.ajax({
        url: calculateRouteLink,
        type: "POST",
        data: JSON.stringify( { data: jData } ) ,

        success: function( dat ) {

            if ( dat[ 'type' ] == "gasolinePoint" ) {

                dat.markers = markers;
                drawGasolinePoint( dat );

            } else if ( dat[ 'type' ] == "lineRoute" ) {

                dat.markers = markers;
                dat.routesLayer = routesLayer;

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

    var size = data[ "routes" ].length;
    var markers = data[ 'markers' ];
    var routes = data[ 'routes' ];
    var showTrackInfoLink = data[ 'showTrackInfoLink' ];

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

                        showTrackInfos( 'display', startMarker.trackId, showTrackInfoLink );

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

            addHiglightedLines(
                data.trackId,
                routesLayer,
                routeSegment.fromX,
                routeSegment.fromY,
                routeSegment.toX,
                routeSegment.toY
            )
        }

    }

}

function drawGasolinePoint( data ) {

    var lonlatGasoline = new OpenLayers.LonLat( data.gasolinePoint.x, data.gasolinePoint.y );
    var markers = data[ 'markers' ];
    var showGasolineInfoLink = data[ 'showGasolineInfoLink' ];

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

            showGasolineInfos( 'display', gasolineMarker.gasolineId, showGasolineInfoLink );

            OpenLayers.Event.stop( evt );
        }
    );

    gasolineMarker.setOpacity( 0.9 );
    markers.addMarker( gasolineMarker );

}

function drawGasolineStation( dat ) {

    var gasolineType = dat[ 'gasolineType' ];
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

    var showGasolineInfoLink = dat[ 'showGasolineInfoLink' ];

    gasolineMarker.gasolineId = dat.gasolineId;

    gasolineMarker.events.register(
        'mousedown', gasolineMarker, function( evt ) {

            showGasolineInfos( 'display', gasolineMarker.gasolineId, showGasolineInfoLink );

            OpenLayers.Event.stop( evt );
        }
    );

    gasolineMarker.setOpacity( 0.9 );
    markers.addMarker( gasolineMarker );

}

function showGasolineInfos( mode, gasolineId, showGasolineInfoLink ) {
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

            var link = showGasolineInfoLink;


            jQuery.ajax({
                url: link,
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

function showTrackInfos( mode, trackId, showTrackInfoLink ) {

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

            var link = showTrackInfoLink;

            jQuery.ajax({
                url: link,
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

function changeBatteryFill() {


        var $selectPack = $("#packSelector");
        var $selectE = $("#eSelector");


            // determine value selected
            var selectPackValue = $selectPack.val();  /* use .text() if you do not assign values to each select option */

            // determine available options based off hashmap
            var select2OptionsHtml = "";
            for( var i = 0; i <= selectPackValue; i++ ) {

                if ( i == selectPackValue ) {
                    select2OptionsHtml += '<option selected="selected" value="' + i + '">' + i + '</option>'
                } else {
                    select2OptionsHtml += '<option value="' + i + '">' + i + '</option>'
                }



                // select2OptionsHtml += "<option>" + i + "</option>";

            }

            // insert new options into dom
            $selectE.html(select2OptionsHtml);


}

function drawRoute( data ) {
    // create markers set here and append markers object to highlighted lines
    // 0.route, 0.element -> startMarker
    // all other routes lat element -> targetMarker
    var route = data[ 'route' ];
    var markers = data[ 'markers' ];
    var routesLayer = data[ 'routesLayer' ];

    var vias = data[ 'vias' ];

    if ( vias != null ) {
        for ( var j = 0; j < vias.length; j++ ) {

            var via = vias[ j ];

            var lonlatVia = new OpenLayers.LonLat( via.fromX, via.fromY );
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
    }



    var showTrackInfoLink = data[ 'showTrackInfoLink' ];

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

                    showTrackInfos( 'display', startMarker.trackId, showTrackInfoLink );

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
            routesLayer,
            routeSegment.fromX,
            routeSegment.fromY,
            routeSegment.toX,
            routeSegment.toY
        )
    }
}


function drawExtraRoute( data ) {


    var routeToEnergy = data[ 'routeToEnergy' ];
    var routeBackToTarget = data[ 'routeBackToTarget' ];

    var routesLayer = data[ 'routesLayer' ];


    var segmentSize = routeToEnergy.length;
    for ( var j = 0; j < segmentSize; j++ ) {

        var routeSegment = routeToEnergy[ j ];

        addHiglightedLines( 0,
            routesLayer,
            routeSegment.fromX,
            routeSegment.fromY,
            routeSegment.toX,
            routeSegment.toY
        )
    }

    var segmentSize = routeBackToTarget.length;
    for ( var j = 0; j < segmentSize; j++ ) {

        var routeSegment = routeBackToTarget[ j ];

        addHiglightedLines( 0,
            routesLayer,
            routeSegment.fromX,
            routeSegment.fromY,
            routeSegment.toX,
            routeSegment.toY
        )
    }
}


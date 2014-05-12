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
<link rel="stylesheet" href="${resource(dir: 'css/i2maps', file: 'jquery-ui-aristo.css')}" type="text/css">
<link rel="stylesheet" href="${resource(dir: 'css/i2maps', file: 'ui.panel.css')}" type="text/css">
 <script type='text/javascript' src="${resource(dir: 'js', file: 'menu_jquery.js')}"></script>
 <link rel='stylesheet' href="${resource(dir: 'css', file: 'menu.css')}" type='text/css' />
    <%--
    <g:javascript src="jq/jquery-1.9.1.js" />

    <g:javascript src="/jq/jquery-ui.js" />
    --%>

    <%-- <r:require module="jqueryui" /> --%>



<g:javascript library="jquery" />


<g:javascript src="ol/OpenLayers.js" />
<%-- <g:javascript src="i2maps/ui.panel.min.js" /> --%>


<%-- <g:javascript src="firebug.js" /> --%>

<script type="text/javascript" src="http://openstreetmap.org/openlayers/OpenStreetMap.js"></script>
<script type="text/javascript" src="http://ol3js.org/en/master/examples/google-map.js"></script>
    <script src="http://maps.google.com/maps/api/js?v=3&amp;sensor=false"></script>

<g:javascript src="application.js" />

<g:javascript src="ol/OpenLayers.js" />

<%-- <g:javascript src="firebug.js" /> --%>

    <script type="text/javascript" src="http://openstreetmap.org/openlayers/OpenStreetMap.js"></script>

    <g:javascript library="jquery" />
    <g:javascript src="application.js" />

<r:layoutResources/>
</head>
<body>

<g:render template="/layouts/topbar" />


<div id="content">

    <div>


        <button id="button_play_pause" onClick="toggle_button_clicked()"><b>Play</b></button>

        <br/>

        <button id="button_stopp" type="submit" onclick="location.href='${createLink( controller: 'simulation', action: 'stopSimulation', params: [ simulationId: simulationId ] ) }'"><i class="icon icon-warning-sign"></i>Stop

        </button>


        <%--
        <g:formRemote name="runsim" action="stopSimulation" on404="alert('not found!')"
                      url="[  controller: 'simulation', action: 'stopSimulation']" >

            <g:hiddenField name="simulationId" value="${simulationId}" />
            <button id="button_stopp" type="submit"><i class="icon icon-warning-sign"></i>Stop</button> </p>

        </g:formRemote>
        --%>

        <%--
        <button id="button_stopp" onClick="button_stop()"><b>Stop</b></button>
        --%>

        <div id="map" style="background-color: #eee; width:94%; height:94%; position: absolute; left:5%; top:2% padding-top:1px" class="olMap"></div>
        <%--<div id="map" style="background-color: #eee; width:90%; height:90%; position: absolute; left:5%; top:5%" class="map"></div>--%>

<script type="text/javascript">


    // global variables
    var map, vectors, routesLayer, lonlat, zoom, markers, simulationLayer;
    var g_playing = false;
    var active;
    var simulationId;

    var simulationRoutes = new Array();

    var currentFeatureId;


    var startIconSize = new OpenLayers.Size( 30, 30 );
    var targetIconSize = new OpenLayers.Size( 20, 20 );
    // var offset = new OpenLayers.Pixel( -(size.x/2) , -(size.y/2));
    var startIcon =  new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'start.png' )}" , startIconSize );
    var targetIcon = new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'target.png' )}" , targetIconSize );
    var viaIcon = new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'via.png' )}" , targetIconSize );

    var gasolineIconSize = new OpenLayers.Size( 30, 30 );
    var carIconSize = new OpenLayers.Size( 10,10 );
    var gasolineNormalIcon = new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'gasoline-normal.png' )}" , gasolineIconSize );
    var gasolineFastIcon = new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'gasoline-fast.png' )}" , gasolineIconSize );
    var gasolineMiddleIcon = new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'gasoline-middle.png' )}" , gasolineIconSize );
    var gasolineSlowIcon = new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'gasoline-slow.png' )}" , gasolineIconSize );

    var carIcon = new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'car.png' )}", carIconSize );
    carIcon.setOpacity( 0.3 );

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
            strokeOpacity: 0.9,
            strokeWidth: 2
        }}) } );

    markers = new OpenLayers.Layer.Markers( "Markers", {
        strategies: [
            new OpenLayers.Strategy.Fixed(),
            new OpenLayers.Strategy.Cluster()
        ]
    } );


    simulationLayer = new OpenLayers.Layer.Vector("Simple Geometry", {
        styleMap: new OpenLayers.StyleMap({'default':{
            strokeColor: "#00FF00",
            strokeOpacity: 1,
            strokeWidth: 3,
            fillColor: "#FF5500",
            fillOpacity: 0.5,
            pointRadius: 2,
            pointerEvents: "visiblePainted",
// label with \n linebreaks
            //label : "\$\{speed\} km/h\n\$\{kmDriven\} km \n\$\{batteryLevel\} %\n\$\{currentEnergyUsed\} kW\n\$\{currentPrice\} EUR",
            fontColor: "${favColor}",
            fontSize: "10px",
            fontFamily: "Courier New, monospace",
            fontWeight: "bold",
            labelAlign: "${align}",
            labelXOffset: "${xOffset}",
            labelYOffset: "${yOffset}",
            labelOutlineColor: "white",
            labelOutlineWidth: 1
        }}),
        renderers: OpenLayers.Layer.Vector.prototype.renderers
    });

    simulationLayer.events.on( {
        'featureclick' : function( feature ) {

            currentFeatureId = feature.feature[ 'carFeatureId' ];

        }
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

    map.addLayers( [ mapnik_layer, mapgoogle_layer, vectors, routesLayer, markers, simulationLayer ] );


    map.addControl(new OpenLayers.Control.MousePosition());
    map.addControl(new OpenLayers.Control.EditingToolbar( vectors ) );
    var options = {
        hover: true,
        click: true
    };



    // now add all feuature vecotrs to 'routes'
    // and all markers to markers!

    var routeDat = new Object();


    var selectFeature = new OpenLayers.Control.SelectFeature( simulationLayer );
    map.addControl(selectFeature);
    selectFeature.activate();

    /**
     * the request interval in ms
     */
    var interval = 1000;

    simulationId = ${simulationId};
    <g:each var="route" in="${routes}" >

        var startPoint = new OpenLayers.Geometry.Point(
                ${route.route[ 0 ].fromY},
                ${route.route[ 0 ].fromX}
        );

        startPoint.transform(
                new OpenLayers.Projection("EPSG:4326"),
                new OpenLayers.Projection("EPSG:900913")
        );

        var pointFeature = new OpenLayers.Feature.Vector( startPoint );
        pointFeature.attributes = {
            speed: "0",
            kmDriven: "0",
            favColor: 'red',
            align: "cm",
            batteryLevel: "${route.batteryLevel}",
            currentEnergyUsed: "${route.currentEnergyUsed}",
            currentPrice: "${route.currentPrice}"
        };

        /*
        var pointFeature = new OpenLayers.Feature.Vector( startPoint, null, {
            externalGraphic: "${g.resource( dir: 'images', file: 'car.png' )}",
            graphicWidth: 32,
            graphicHeight: 32,
            fillOpacity: 1,
            kmDriven: 0,
            speed: 0
        } );
        */

        pointFeature.carFeatureId = ${route.id};
        pointFeature.fid = '${route.id}';

        simulationLayer.addFeatures( [ pointFeature ] );

        var simulationRoute = {
            featureId: pointFeature.fid,
            simulationRouteId: ${route.id}
            // layer: simulationLayer,
            // carFeature : pointFeature,
            // infoLink: '${ g.createLink( controller: 'simulation', action: 'getInfo', params: [ simulationRouteId: route.id ] ) }'
        };
        simulationRoutes.push( simulationRoute );


        routeDat.trackId = ${route.simulationRouteId};

        var vias = new Array();
        <g:each var="via" in="${route.vias}">

            var vv = new Object();
            vv.fromX = ${via.fromX};
            vv.fromY = ${via.fromY};
            vias.push( vv );
        </g:each>

        routeDat.vias = vias;

        var segments = new Array();

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

    function showInfoPane( featureId ) {

        // get infos for one simulationRoutes
        jQuery.ajax({
            url: '${ g.createLink( controller: 'simulation', action: 'getInfoForRoute' ) }',
            data: JSON.stringify( { data: { simulationRouteId : featureId } } ),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            type: "POST",
            success: function( data ) {

                    var lon = data[ 'lon' ];
                    var lat = data[ 'lat' ];
                    var currentPrice = data[ 'currentPrice' ];
                    var batteryLevel = data[ 'batteryLevel' ];
                    var currentEnergyUsed = data[ 'currentEnergyUsed' ];

                    var speed = data[ 'speed' ];
                    var kmDriven = data[ 'kmDriven' ];

                    var street = data[ 'street' ];

                    // put infos into pane
                    $( '#speedInfo').html( speed );
                    $( '#kmInfo').html( kmDriven );
                    $( '#priceInfo' ).html( currentPrice );
                    $( '#streetInfo' ).html( street );
                    $( '#fillInfo' ).html( batteryLevel );

                    var batString = batteryLevel + "%";
                    $( '#progress-bar' ).css("width", batString );

                    if ( batteryLevel <= 30 ) {
                        var factor = 1 - ( batteryLevel / 30 );
                        $( '#progress-bar-red' ).css("opacity", factor );
                    } else {
                        $( '#progress-bar-red' ).css("opacity", 0 );
                    }

            },
            error: function( data ) {

                // console.log( data );

            }
        });

    }

    function moveCar( simRoutes ) {

        // get infos for all simulationRoutes
        jQuery.ajax({
            url: '${ g.createLink( controller: 'simulation', action: 'getInfo' ) }',
            data: JSON.stringify( { data: simRoutes } ),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            type: "POST",
            success: function( data ) {

                var info = data[ 'info' ];
                var time = data[ 'time' ];

                $( '#time' ).html( time );

                // for each feature update position
                // find by carFeatureId in list..
                for ( var i = 0; i < info.length; i++ ) {

                    var simRoute = info[ i ];

                    var routeToBackTarget = simRoute[ 'routeBackToTarget' ];
                    var routeToEnergy = simRoute[ 'routeToEnergy' ];
                    if ( routeToEnergy != null && routeToBackTarget != null ) {
                        var routeDat = new Object();

                        routeDat.routesLayer = routesLayer;

                        var segmentsToEnergy = new Array();

                        for ( var i = 0; i < routeToEnergy.length; i++ ) {
                            var segm = new Object();
                            segm.fromX = routeToEnergy[ i ].fromX;
                            segm.fromY = routeToEnergy[ i ].fromY;
                            segm.toX = routeToEnergy[ i ].toX;
                            segm.toY = routeToEnergy[ i ].toY;
                            segmentsToEnergy.push( segm );
                        }

                        routeDat.routeToEnergy = segmentsToEnergy;

                        var segmentsBackToTarget = new Array();

                        for ( var i = 0; i < routeToBackTarget.length; i++ ) {
                            var segm = new Object();
                            segm.fromX = routeToBackTarget[ i ].fromX;
                            segm.fromY = routeToBackTarget[ i ].fromY;
                            segm.toX = routeToBackTarget[ i ].toX;
                            segm.toY = routeToBackTarget[ i ].toY;
                            segmentsBackToTarget.push( segm );
                        }

                        routeDat.routeBackToTarget = segmentsBackToTarget;

                        drawExtraRoute( routeDat );
                    }

                    var feat = simulationLayer.getFeatureByFid( simRoute[ 'featureId' ] );
                    var lon = simRoute[ 'lon' ];
                    var lat = simRoute[ 'lat' ];
                    var currentPrice = simRoute[ 'currentPrice' ];
                    var batteryLevel = simRoute[ 'batteryLevel' ];
                    var currentEnergyUsed = simRoute[ 'currentEnergyUsed' ];

                    var speed = simRoute[ 'speed' ];
                    var kmDriven = simRoute[ 'kmDriven' ];

                    if ( lat != null && lon != null ) {

                        var ll = new OpenLayers.LonLat( lon, lat );

                        ll.transform(
                                new OpenLayers.Projection("EPSG:4326"),
                                new OpenLayers.Projection("EPSG:900913")
                        );

                        feat.attributes = {
                            speed: speed,
                            kmDriven: kmDriven,
                            currentPrice : currentPrice,
                            batteryLevel: batteryLevel,
                            currentEnergyUsed: currentEnergyUsed
                        };

                        feat.move( ll );
                        feat.layer.drawFeature( feat );
                    }

                }
                simulationLayer.redraw();

            },
            error: function( data ) {

                console.log( data );

            }
        });

    }

    function play( simulationRoutes ) {
        var button = document.getElementById('button_play_pause');
        button.innerHTML = "<b>Pause</b>";
        g_playing = true;

        // action to the controller:
        jQuery.ajax({
            url: '${g.createLink( controller: 'simulation', action: 'startSimulation' )}',
            type: "POST",
            success: function( data ) {

            }
        });

        active = window.setInterval( function() {
                    moveCar( simulationRoutes );
                    showInfoPane( currentFeatureId );
                }, interval
        );

    }


    function button_stop() {

        var button = document.getElementById('button_play_pause');
        button.setAttribute( "hidden", "true" );

        window.clearInterval( active );
        // call controller simulation stop:




        jQuery.ajax({
            url: '${g.createLink( controller: 'simulation', action: 'stopSimulation', params: [ simulationId: simulationId ] )}',
            type: "POST",
            success: function( data ) {

                g_playing = false;

                var button = document.getElementById('button_play_pause');
                button.innerHTML = "<b>Play</b>";
            }
        });

    }

    function pause() {

        window.clearInterval( active );

        // call controller simulation PAUSE:
        jQuery.ajax({
            url: '${g.createLink( controller: 'simulation', action: 'pauseSimulation' )}',
            type: "POST",
            success: function( data ) {

            }
        });

        g_playing = !g_playing;
        var button = document.getElementById('button_play_pause');
        button.innerHTML = "<b>Play</b>";
    }

    function toggle_button_clicked() {
        if(g_playing)
        {
            pause();
        }

        else
        {
            play( simulationRoutes );
        }
    }

    /*
    $(function() {
        $( "#scale-slider" ).slider({
            orientation: "vertical",
            range: "min",
            min: 0,
            max: 100,
            value: 60,
            slide: function( event, ui ) {
                $( "#amount" ).val( ui.value );
            }
        });
        $( "#amount" ).val( $( "#scale-slider" ).slider( "value" ) );

    });
    */

   function scale() {

       var scaleValue = $('#scale-slider').val();
       var scaleString = "time scale 1:" + scaleValue;

       $('#scale-value').text( scaleString );

       jQuery.ajax({
           url: '${g.createLink( controller: 'simulation', action: 'scaleSimulation' )}',
           type: "POST",
           data: JSON.stringify( { data: { scaleValue : scaleValue } } ),
           contentType: "application/json; charset=utf-8",
           dataType: "json",
           success: function( data ) {

               console.log( "scaling: " + data )

           }
       });

   }



</script>
        </div>
    </div>


<div class="slide-control-group" >
    <p id="scale-value" style="font-size: 13px">time scale 1:50</p>
    <input id="scale-slider" name="r" type="range" min="1" max="500" value="50" onchange="scale()" style="width: 280px"/>
</div>


    <div draggable="true" id="info_box" class="panel ui-helper-reset ui-widget ui-panel ui-draggable"
         style="position: fixed; right:1%; top:50%; height: 500px; width: 280px; margin-left: 5px; opacity: 0.65;">

        <h3 class="ui-helper-reset ui-widget-header ui-panel-header ui-corner-top">
            <span class="ui-icon-triangle-1-s ui-icon">
                <span>

                </span>
            </span>
            <span class="ui-panel-rightbox">

            </span>
            <div class="ui-panel-title">
                <span class="ui-panel-title-text">Info
                </span>
            </div>
        </h3>

        <div class="ui-helper-reset ui-widget-content ui-panel-content ui-corner-bottom" style="position: absolute;">
            <div class="ui-panel-content-text">
                <div style="height: 100%;" id="carInformation">

                    <table class="lightboxselectiontable" style="font-size: 13px;" >

                        <tr>
                            <td>
                                speed
                            </td>
                            <td id="speedInfo">

                            </td>
                            <td>
                                km/h
                            </td>
                        </tr>
                        <tr>
                            <td>
                                distance traveled
                            </td>
                            <td id="kmInfo">

                            </td>
                            <td>
                                km
                            </td>
                        </tr>
                        <tr>
                            <td>
                                costs
                            </td>
                            <td id="priceInfo">

                            </td>
                            <td>
                                Euro
                            </td>
                        </tr>
                        <tr>
                            <td>
                                time
                            </td>
                            <td id="time">

                            </td>
                            <td>
                                hh:mm:ss
                            </td>
                        </tr>
                        <tr>
                            <td>
                                fill level
                            </td>
                            <td id="fillInfo">

                            </td>
                            <td>
                                %
                            </td>
                        </tr>

                    </table>

                    <table class="lightboxselectiontable" style="font-size: 13px;" >
                        <tr>
                            <td id="battery">
                                <div id="batteryContainer" style="width:230px; height:10px; border:1px solid black;">
                                    <div id="progress-bar"
                                        style="width:0%; /*change this width */
                                                background-color: greenyellow;
                                                height:10px;">
                                        <div id="progress-bar-red"
                                             style="width:100%;
                                             background-color: red;
                                             opacity: 0;  /* 1 is red , 0 is green */
                                             height:10px;">
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>

                    <table class="lightboxselectiontable" style="font-size: 13px;" >
                        <tr>
                            <td>street</td>
                            <td id="streetInfo"></td>
                        </tr>
                    </table>


                </div>
            </div>
        </div>
    </div>




<g:render template="/layouts/footer" />


<r:layoutResources></r:layoutResources>


</body>
</html>
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

    <g:javascript library="jquery" />

    <g:javascript src="ol/OpenLayers.js" />

    <script type="text/javascript" src="http://openstreetmap.org/openlayers/OpenStreetMap.js"></script>

    <script type="text/javascript" >


        OpenLayers.Control.Click = OpenLayers.Class(OpenLayers.Control, {
            defaultHandlerOptions: {
                'single': true,
                'double': false,
                'pixelTolerance': 0,
                'stopSingle': false,
                'stopDouble': false
            },
            initialize: function(options) {
                this.handlerOptions = OpenLayers.Util.extend(
                        {}, this.defaultHandlerOptions
                );
                OpenLayers.Control.prototype.initialize.apply(
                        this, arguments
                );
                this.handler = new OpenLayers.Handler.Click(
                        this, {
                            'click': this.trigger
                        }, this.handlerOptions
                );
            },
            trigger: function(e) {

                var lonlat = map.getLonLatFromPixel( e.xy );
                var size = new OpenLayers.Size(21,25);
                var offset = new OpenLayers.Pixel(-(size.w/2), -size.h);
                var icon = new OpenLayers.Icon('http://localhost:8080/emobilesim/js/ol/img/marker-blue.png', size, offset);
                markers.addMarker(new OpenLayers.Marker( lonlat, icon ));


                <%-- transforms coords into good format --%>
                lonlat.transform(new OpenLayers.Projection("EPSG:900913"), new OpenLayers.Projection("EPSG:4326"));

                jQuery.ajax({
                    url: "/emobilesim/mapView/showCoords",
                    type: "POST",
                    data: { 'lat' : lonlat.lat, 'lon' : lonlat.lon }
                });

            }
        });


        var map = new OpenLayers.Map;
        var markers;

        function init() {
            var p1 = new OpenLayers.Projection( "EPSG:4326" );
            var pMerc = new OpenLayers.Projection( "EPSG:900913" );
            var lonlat = new OpenLayers.LonLat( 13.38, 52.52 );

            var zoom = 13;

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


            var mapnik_layer = new OpenLayers.Layer.OSM.Mapnik( "Mapnik" );
            // var tah_layer = new OpenLayers.Layer.OSM.Osmarender( "Tiles@Home" );

            markers = new OpenLayers.Layer.Markers( "Markers" );

            map.addLayer( markers );
            map.addLayer( mapnik_layer );

            <%--
            map.addLayers( [ mapnik_layer, markers ] );
            --%>

            lonlat.transform( p1, pMerc );
            map.setCenter( lonlat, zoom );

            var click = new OpenLayers.Control.Click();
            map.addControl( click );
            click.activate();

        }

    </script>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">

</head>
<body onload="init()">

<g:render template="/layouts/topbar" />

<div id="map" style="width: 100%; height: 95%; position: absolute; padding-bottom: 100px; "></div>

<r:layoutResources></r:layoutResources>

<%--
    <g:render template="/layouts/footer" />
--%>
</body>
</html>
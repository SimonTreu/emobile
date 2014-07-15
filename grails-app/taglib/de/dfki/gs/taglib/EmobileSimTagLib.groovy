package de.dfki.gs.taglib

import de.dfki.gs.domain.users.Person
import grails.plugin.springsecurity.SpringSecurityUtils

class EmobileSimTagLib {

    static namespace = "es"
    def springSecurityService

    def inputLabeled = { attrs, body ->
        def m = [ : ];
        m.css = attrs.css;
        m.id = attrs.id;
        m.label = attrs.label.replaceAll( " ", "&#160;" );
        m.name = attrs.name;
        m.onchange = attrs.onchange
        m.onkeyup = attrs.onkeyup
        m.tabindex = attrs.tabindex?attrs.tabindex:1;
        m.type = attrs.type ? attrs.type : 'text'
        m.value = body();
        out << g.render( template: '/templates/form.input.labeled', model: m )
    }

    def htmlOutput = { attrs, body ->
        out << attrs.value
    }

    def center = { attrs, body ->
        def m = [ html: body() ]
        out << g.render( template: '/mapView/center', model: m )
    }

    def lightBox = { attrs, body ->
        log.debug( "attrs: ${attrs}" )
        log.debug( "body: ${body}" )

        def m = [ : ]
        m.closeHref = attrs.closeHref;
        m.css = attrs.css ? " ${attrs.css}" : "";
        m.width = attrs.width ? attrs.width : 300;
        m.height = attrs.height ? attrs.height : 300;
        m.iframe = attrs.iframe;
        if( m.iframe && m.iframe ==~ /(?i)true/ ){
            def crtl = attrs.controller ? attrs.controller : 'fnord'
            m.src = g.createLink( controller: crtl, action: attrs.action, absolute: true, id: attrs.id, params:attrs.linkParams )
            m.css4fixes = 'js-lightBox';
            m.isRemote = true;
        }else{
            m.css4fixes = 'noJs-lightBox';
            m.html = g.render( template: attrs.action )
        }

        log.debug( g.render( template: '/mapView/box.lightBox', model: m ) );

        out << g.render( template: '/mapView/box.lightBox', model: m )
    }
    def lightBoxCloseBtn = { attrs, body ->
        def m = [ : ]
        m.href = attrs.href;
        out << g.render( template: '/mapView/box.lightBox.close', model: m )
    }


    def welcomeLoggedInUser = {

        Person person = (Person) springSecurityService.currentUser

        String givenName = ""
        String familyName = ""

        if ( person != null ) {

            // response.sendRedirect( "${SpringSecurityUtils.securityConfig.logout.filterProcessesUrl}" )
            //return

            givenName = person.givenName
            familyName = person.familyName
        }

        out << "${givenName} ${familyName}"

    }

    def mapDiv = { attrs, body ->

        /*
          initial lonlat:
            lat: 6894699.8003227
            lon: 1489454.7866067

	        initial zoom : 13
        */

        out <<  """
                    <script type="text/javascript">
                        var map, vectors, routes, lonlat, zoom, markers;

                        var startIconSize = new OpenLayers.Size( 40, 40 );
                        var targetIconSize = new OpenLayers.Size( 20, 20 );

                        var gasolineIconSize = new OpenLayers.Size( 30, 30 );

                        // var offset = new OpenLayers.Pixel( -(size.x/2) , -(size.y/2));
                        var startIcon =  new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'start.png' )}" , startIconSize );
                        var targetIcon = new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'target.png' )}" , targetIconSize );
                        var viaIcon = new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'via.png' )}" , targetIconSize );

                        var gasolineNormalIcon = new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'gasoline-normal.png' )}" , gasolineIconSize );
                        var gasolineFastIcon = new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'gasoline-fast.png' )}" , gasolineIconSize );
                        var gasolineMiddleIcon = new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'gasoline-middle.png' )}" , gasolineIconSize );
                        var gasolineSlowIcon = new OpenLayers.Icon( "${g.resource( dir: '/images', file: 'gasoline-slow.png' )}" , gasolineIconSize );


                        var p1 = new OpenLayers.Projection( "EPSG:4326" );
                        var pMerc = new OpenLayers.Projection( "EPSG:900913" );
                        lonlat = new OpenLayers.LonLat( 13.38, 52.52 );
                        zoom = 13;
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

                        routes = new OpenLayers.Layer.Vector( "Route Vectors", {
                            styleMap: new OpenLayers.StyleMap({'default':{
                                strokeColor: "red",  // TODO: chose a good color
                                strokeOpacity: 0.6,
                                strokeWidth: 6
                            }}) } );

                        markers = new OpenLayers.Layer.Markers( "Markers", {
                            eventListeners: {
                                'featureselected' : function( evt ) {

                                    alert("hua");

                                }
                            }
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
                                    serialize( feature );
                                }
                            }
                        });

                        map.addLayers( [ mapnik_layer, vectors, routes, markers ] );


                        map.addControl(new OpenLayers.Control.MousePosition());
                        map.addControl(new OpenLayers.Control.EditingToolbar( vectors ) );
                        var options = {
                            hover: true,
                            click: true
                        };

                        /*
                        var select = new OpenLayers.Control.SelectFeature( markers, options );
                        map.addControl( select );
                        select.activate();
                        */

                        /*
                        var select = new OpenLayers.Control.SelectFeature(vectors, options);
                        map.addControl(select);
                        select.activate();

                        var selectRoute = new OpenLayers.Control.SelectFeature( routes, options);
                        map.addControl(selectRoute);
                        selectRoute.activate();
                        */

                        lonlat.transform( p1, pMerc );
                        map.setCenter( lonlat, zoom );
                    </script>
                """
    }

}

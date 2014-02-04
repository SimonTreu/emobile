/**
 * Created with IntelliJ IDEA.
 * User: glenn
 * Date: 22.10.13
 * Time: 07:05
 * To change this template use File | Settings | File Templates.
 */

requireJS('http://localhost:8080/emobilesim2/js/i2maps/cloudmade.js');
requireJS('http://localhost:8080/emobilesim2/js/i2maps/canvas.js');
requireJS('http://localhost:8080/emobilesim2/js/i2maps/anagent.js');
i2maps.cache.enabled = false;
// Globals
var g_map;
var g_layer_mapnik;
var g_agent_layer;

// How should features be drawn on the maps? Red, Green, Blue, label=''?
var g_style_map = new OpenLayers.StyleMap
({'default':
{
    fillColor: "${color}",
    strokeColor: "#000000",
    strokeWidth: 0.5,
    pointRadius: 2,
    pointerEvents: "visiblePainted",
    fillOpacity: 0.4,
    label: "${id}",
    fontColor: "${color}",
    fontSize: "${fontSize}",
    fontFamily: "Courier New, monospace",
    fontWeight: "bold",
    labelXOffset: "0",
    labelYOffset: "${yOff}"
},
    'hover':
    {
        strokeWidth: 1,
        pointRadius: 6,
    },
    'select':
    {
        fillColor: "#0000ff",
        strokeColor: "#0000ff",
        strokeWidth: 2,
        pointRadius: 2,
    }
});


var g_playing = false;
var agents = [];

// Defaults from simple_crowd.py
var POT_EXTEND = 10
var POT_DECAY = 0.6 //0.027
var POT_STRENGTH = 300 //350


id = 0;
transparency_time = 10;
draw_vectors = true;
draw_traces = true;

function GenerateAgent(data, color)
{
    var agent = new anagent({color: "rgb(250,20,20)", attributes: data.attributes });
    // convert wayPoints
    var waypoints = new Array();
    var wp = data.waypoints;
    for ( var i = 0; i < wp.length - 1 ; i++ ) {

        var llat = wp[ i ].lat;
        var llon = wp[ i ].lon;

        var ll = new OpenLayers.LonLat( llon, llat );

        ll.transform(
            new OpenLayers.Projection( "EPSG:4326" ), // to WGS 1984
            new OpenLayers.Projection( "EPSG:900913") // transform from Spherical Mercator Projection
        );

        var waypoint = new Array();
        waypoint.push( ll.lon, ll.lat );

        waypoints.push( waypoint );
    }


    // agent.setWayPoints(data.waypoints);
    agent.setWayPoints( waypoints );
    agent.onmove = draw_trace;
    g_agent_layer.addFeatures(agent.f);
    agents.push(agent);
}

function update()
{
    // Update all agents.
    for(var i = 0; i < agents.length; i++)
    {
        agents[i].update();
    }
    if(g_playing) setTimeout("update();", update.interval);
}

function draw_trace(a, o, d)
{
    if(draw_traces)
    {
        pixel1 = surface_layer.geoToPixel(o.x,o.y);
        pixel = surface_layer.geoToPixel(d.x,d.y);
        ctx.strokeStyle = a.color;
        ctx.lineWidth = 0.5*(1 + (g_map.zoom - 8));
        ctx.beginPath();
        ctx.moveTo(pixel1[0], pixel1[1]);
        ctx.lineTo(pixel[0], pixel[1]);
        ctx.stroke();
    }
}

transparency_delta = 0.5;
function update_transparency()
{
    canvasData = ctx.getImageData(0, 0, surface_layer.canvas.width, surface_layer.canvas.height);
    for(var i=3; i < canvasData.data.length; i+=4)
    {
        if(canvasData.data[i] > 0)
        {
            canvasData.data[i] -= canvasData.data[i]/(50);
            canvasData.data[i-3] -= canvasData.data[i-3]/(10);
            canvasData.data[i-1] += canvasData.data[i-3]/(10);
        }
    }
    ctx.putImageData(canvasData, 0, 0);
    if(g_playing) setTimeout("update_transparency();", update_transparency.interval);
}


function button_clicked()
{
    if(g_playing)
    {
        stop();
    }

    else
    {
        play();
    }
}

function myRandom(abs_maximum)
{
    var rand = Math.random()*abs_maximum;

    if(Math.random() > 0.45)
        rand = 1 - rand;

    return rand;
}

function init()
{
    fetchTrip.interval = 3000;

    update_transparency.interval = 1000;
    update.interval = 1;
    setup_ui();
    trips = [];

    /**
     * with first query we load all simulation infos here!
     */
    i2maps.doQuery(
        "get_bounds",
        "loadSimulationConfiguration",
        { simulationId : 1 },
        "buses",
        function( data ){
            bb = data.bounds;

            b = new OpenLayers.Bounds(bb[ 'left' ], bb[ 'bottom' ], bb[ 'right' ], bb[ 'top' ]);

            b.transform(
                new OpenLayers.Projection( "EPSG:4326" ), // to WGS 1984
                new OpenLayers.Projection( "EPSG:900913") // transform from Spherical Mercator Projection
            );


            g_map.zoomToExtent( b );
            ctx = surface_layer.canvas.getContext('2d');
            ctx.globalAlpha = 0.2;
            //ctx.lineWidth = 3; // Not used, defined in draw traces
            ctx.lineCap = "square";

            var simulationRoutes = data[ 'routes' ];

            play( simulationRoutes );
        }
    );
}

function fetchTrip( simulationRouteId )
{

    if ( simulationRouteId != null ) {
        i2maps.doQuery(
            "get_trip",
            "getCarRoute",
            { simulationRouteId : simulationRouteId }, // empty parameters! ( here we can put some infos )
            "buses",
            function( data ){

                trips.push( data[ 'waypoints' ] );
                n = trips.length;
                GenerateAgent( data, "#ff0000");
            }
        );
        if(g_playing && trips.length < 1) setTimeout("fetchTrip();", fetchTrip.interval);
    }

}


function setup_ui()
{
    surface_layer = new Canvas.Layer("Traces");

    show_agent_info = function(e)
    {
        agent = e.feature.agent;
        route_types = ["Tram", "Subway", "Rail", "Bus", "Ferry", "Cable Car", "Gondola", "Funicular"];

        var txt = "<p><strong>ID</strong>: "+ agent.attributes.id + "<br/><br/>";
        txt += "<strong>Head Sign</strong>: "+ agent.attributes.head_sign + "<br/><br/>";
        txt += "<strong>Route Type</strong>: "+ route_types[agent.attributes.route_type] + "<br/><br/>";
        txt += "</p>";
        $("#attributes").html(txt);
    }

    g_agent_layer  = new OpenLayers.Layer.Vector("Vehicles", {styleMap: g_style_map});
    g_agent_layer.events.on({
        "featureselected": show_agent_info,
    });

    /*
    var cloudmade = new OpenLayers.Layer.CloudMade("Gray Open Street Map", {
        key: '31d1dfacd9ee46628ed4cbf5c0998bea',
        styleId: 6787
    });
    */


    var map = new i2maps.Map({
        id: 'map',
        mapDiv: 'map',
        baseLayers: [ "Open Street Map" ],
        layers: [surface_layer, g_agent_layer],
    });


    g_map = map.olMap;
    var highlightCtrl = new OpenLayers.Control.SelectFeature(g_agent_layer, {
        hover: true,
        highlightOnly: true,
        renderIntent: "hover",
        eventListeners: {
            featurehighlighted: show_agent_info,
        }
    });
    var selectCtrl = new OpenLayers.Control.SelectFeature(g_agent_layer,
        {clickout: true}
    );
    g_map.addControl(highlightCtrl);
    g_map.addControl(selectCtrl);
    highlightCtrl.activate();
    selectCtrl.activate();

    g_map.getControlsBy("CLASS_NAME", "OpenLayers.Control.LayerSwitcher")[0].maximizeControl();

    g_map.layers.map(function(l){if(l.isBaseLayer) l.setOpacity(0.9)})
    black_layer = g_map.getLayersByName("Open Street Map")[0].clone();
    black_layer.name = "Black Layer";
    black_layer.url = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVQIHWNgYGD4DwABBAEAHnOcQAAAAABJRU5ErkJggg==";
    // White background:
    //black_layer.url = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAMAAAAoyzS7AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAAZQTFRF////AAAAVcLTfgAAAAxJREFUeNpiYAAIMAAAAgABT21Z4QAAAABJRU5ErkJggg==";
    black_layer.setOpacity(1);
    g_map.addLayer(black_layer);


    $("#info_box").panel({
        draggable: true,
        collapsible: true,
        collapsed: false,
    });
    $("#attributes").html("<p>Click a marker to view it's information</p>");
    if(!$.browser.safari) $("#info_box").hide();
}

function play( simulationRoutes )
{
    var button = document.getElementById('button_play_pause');
    button.innerHTML = "<b>Pause</b>";
    g_playing = true;
    update();
    update_transparency();

    // for each track found in simulationRoute: fetchTrip
    for ( var i = 0; i < simulationRoutes.length; i++ ) {

        var simRoute = simulationRoutes[ i ];
        var simulationRouteId = simRoute[ 'id' ];

        fetchTrip( simulationRouteId );
    }

    // fetchTrip();
}

function stop()
{
    g_playing = !g_playing;
    var button = document.getElementById('button_play_pause');
    button.innerHTML = "<b>Play</b>";
}

i2maps.setupProject(function()
{
    init();
});
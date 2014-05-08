<%--
  Created by IntelliJ IDEA.
  User: anbu02
  Date: 07.05.14
  Time: 16:00
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Map created by GPS Visualizer</title>
    <base target="_top"></base>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="geo.position" content="52.4917555; 13.37085" />
    <meta name="ICBM" content="52.4917555, 13.37085" />
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
</head>
<body style="margin:0px;">
<script type="text/javascript">
    var google_api_key = ''; // Your project's Google Maps API key goes here (https://code.google.com/apis/console)
    document.writeln('<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?v=3&amp;libraries=geometry&amp;sensor=false&amp;'+((self.google_api_key&&document.location.toString().indexOf('file://')!=0)?'key='+google_api_key:'')+'"><'+'/script>');
</script>

<!--
			If you want to transplant this map into another Web page, by far the best method is to
			simply include it in a IFRAME tag (see http://www.gpsvisualizer.com/faq.html#google_html).
			But, if you must paste the code into another page, be sure to include all of these parts:
			   1. The "div" tags that contain the map and its widgets, below
			   2. Three sections of JavaScript code:
			      a. The Google code (maps.google.com or googleapis.com) code, above
			      b. "gv_options" and the code that calls a .js file on maps.gpsvisualizer.com
			      c. The "GV_Map" function, which contains all the geographic info for the map
		-->
<div style="margin-left:0px; margin-right:0px; margin-top:0px; margin-bottom:0px;">
    <div id="gmap_div" style="width:1800px; height:1030px; margin:0px; margin-right:12px; background-color:#F0F0F0; float:left; overflow:hidden;">
        <p align="center" style="font:10px Arial;">This map was created using <a target="_blank" href="http://www.gpsvisualizer.com/">GPS Visualizer</a>'s do-it-yourself geographic utilities.<br /><br />Please wait while the map data loads...</p>
    </div>

    <div id="gv_infobox" class="gv_infobox" style="font:11px Arial; border:solid #666666 1px; background:#ffffff; padding:4px; overflow:auto; display:none; ">
        <div id="gv_legend_header" style="padding-bottom:2px;"><b>Charge_Time in hours: (29.04-06.05)</b></div>
        <div class="gv_legend_item"><span style="color:#0000cc;">&#9608; 121.0</span></div>
        <div class="gv_legend_item"><span style="color:#002bcc;">&#9608; 114.7</span></div>
        <div class="gv_legend_item"><span style="color:#0057cc;">&#9608; 108.4</span></div>
        <div class="gv_legend_item"><span style="color:#0081cc;">&#9608; 102.1</span></div>
        <div class="gv_legend_item"><span style="color:#00accc;">&#9608; 95.7</span></div>
        <div class="gv_legend_item"><span style="color:#00ccc2;">&#9608; 89.4</span></div>
        <div class="gv_legend_item"><span style="color:#00cc96;">&#9608; 83.1</span></div>
        <div class="gv_legend_item"><span style="color:#00cc6b;">&#9608; 76.8</span></div>
        <div class="gv_legend_item"><span style="color:#00cc41;">&#9608; 70.5</span></div>
        <div class="gv_legend_item"><span style="color:#00cc16;">&#9608; 64.2</span></div>
        <div class="gv_legend_item"><span style="color:#16cc00;">&#9608; 57.8</span></div>
        <div class="gv_legend_item"><span style="color:#41cc00;">&#9608; 51.5</span></div>
        <div class="gv_legend_item"><span style="color:#6bcc00;">&#9608; 45.2</span></div>
        <div class="gv_legend_item"><span style="color:#96cc00;">&#9608; 38.9</span></div>
        <div class="gv_legend_item"><span style="color:#c2cc00;">&#9608; 32.6</span></div>
        <div class="gv_legend_item"><span style="color:#ccac00;">&#9608; 26.3</span></div>
        <div class="gv_legend_item"><span style="color:#cc8100;">&#9608; 19.9</span></div>
        <div class="gv_legend_item"><span style="color:#cc5700;">&#9608; 13.6</span></div>
        <div class="gv_legend_item"><span style="color:#cc2b00;">&#9608; 7.3</span></div>
        <div class="gv_legend_item"><span style="color:#cc0000;">&#9608; 1.0</span></div>
    </div>



    <div id="gv_marker_list" class="gv_marker_list" style="background:#ffffff; overflow:auto; display:none;"><!-- --></div>

    <div id="gv_clear_margins" style="height:0px; clear:both;"><!-- clear the "float" --></div>
</div>


<!-- begin GPS Visualizer setup script (must come after maps.google.com code) -->
<script type="text/javascript">
    /* Global variables used by the GPS Visualizer functions (20140506045006): */
    gv_options = {};

    // basic map parameters:
    gv_options.center = [52.4917555,13.37085];  // [latitude,longitude] - be sure to keep the square brackets
    gv_options.zoom = 12;  // higher number means closer view; can also be 'auto' for automatic zoom/center based on map elements
    gv_options.map_type = 'GV_STREET';  // popular map_type choices are 'GV_STREET', 'GV_SATELLITE', 'GV_HYBRID', 'GV_TERRAIN', 'GV_TOPO_US', 'GV_TOPO_WORLD', 'GV_OSM'
    gv_options.map_opacity = 1.00;  // number from 0 to 1
    gv_options.full_screen = true;  // true|false: should the map fill the entire page (or frame)?
    gv_options.width = 1800;  // width of the map, in pixels
    gv_options.height = 1030;  // height of the map, in pixels

    gv_options.map_div = 'gmap_div';  // the name of the HTML "div" tag containing the map itself; usually 'gmap_div'
    gv_options.doubleclick_zoom = true;  // true|false: zoom in when mouse is double-clicked?
    gv_options.doubleclick_center = true;  // true|false: re-center the map on the point that was double-clicked?
    gv_options.mousewheel_zoom = true; // true|false; or 'reverse' for down=in and up=out
    gv_options.autozoom_adjustment = 0;
    gv_options.centering_options = { 'open_info_window':true, 'partial_match':true, 'center_key':'center', 'default_zoom':null } // URL-based centering (e.g., ?center=name_of_marker&zoom=14)
    gv_options.tilt = false; // true|false: allow Google to show 45-degree tilted aerial imagery?
    gv_options.street_view = false; // true|false: allow Google Street View on the map
    gv_options.animated_zoom = false; // true|false: may or may not work properly

    // widgets on the map:
    gv_options.zoom_control = 'large'; // 'large'|'small'|'none'
    gv_options.recenter_button = true; // true|false: is there a 'double-click to recenter' option in the zoom control?
    gv_options.scale_control = true; // true|false
    gv_options.center_coordinates = true;  // true|false: show a "center coordinates" box and crosshair?
    gv_options.mouse_coordinates = false;  // true|false: show a "mouse coordinates" box?
    gv_options.crosshair_hidden = true;  // true|false: hide the crosshair initially?
    gv_options.map_opacity_control = true;  // true|false
    gv_options.map_type_control = {};  // widget to change the background map
    gv_options.map_type_control.style = 'menu';  // 'menu'|'none'
    gv_options.map_type_control.filter = false;  // true|false: when map loads, are irrelevant maps ignored?
    gv_options.map_type_control.excluded = [];  // comma-separated list of quoted map IDs that will never show in the list ('included' also works)
    gv_options.disable_google_pois = false;  // true|false: if you disable clickable POIs, you also lose the labels on parks, airports, etc.
    gv_options.measurement_tools = { visible:false, distance_color:'', area_color:'', position:[] };
    gv_options.infobox_options = {}; // options for a floating info box (id="gv_infobox"), which can contain anything
    gv_options.infobox_options.enabled = true;  // true|false: enable or disable the info box altogether
    gv_options.infobox_options.position = ['G_ANCHOR_BOTTOM_RIGHT',4,40];  // [Google anchor name, relative x, relative y]
    gv_options.infobox_options.draggable = true;  // true|false: can it be moved around the screen?
    gv_options.infobox_options.collapsible = true;  // true|false: can it be collapsed by double-clicking its top bar?


    // marker-related options:
    gv_options.default_marker = { color:'red',icon:'blankcircle',scale:1,opacity:0.7 }; // icon can be a URL, but be sure to also include size:[w,h] and optionally anchor:[x,y]
    gv_options.marker_tooltips = true; // do the names of the markers show up when you mouse-over them?
    gv_options.marker_shadows = true; // true|false: do the standard markers have "shadows" behind them?
    gv_options.marker_link_target = '_blank'; // the name of the window or frame into which markers' URLs will load
    gv_options.info_window_width = 0;  // in pixels, the width of the markers' pop-up info "bubbles" (can be overridden by 'window_width' in individual markers)
    gv_options.thumbnail_width = 0;  // in pixels, the width of the markers' thumbnails (can be overridden by 'thumbnail_width' in individual markers)
    gv_options.photo_size = [0,0];  // in pixels, the size of the photos in info windows (can be overridden by 'photo_width' or 'photo_size' in individual markers)
    gv_options.hide_labels = false;  // true|false: hide labels when map first loads?
    gv_options.labels_behind_markers = false; // true|false: are the labels behind other markers (true) or in front of them (false)?
    gv_options.label_offset = [0,0];  // [x,y]: shift all markers' labels (positive numbers are right and down)
    gv_options.label_centered = false;  // true|false: center labels with respect to their markers?  (label_left is also a valid option.)
    gv_options.driving_directions = false;  // put a small "driving directions" form in each marker's pop-up window? (override with dd:true or dd:false in a marker's options)
    gv_options.garmin_icon_set = 'gpsmap'; // 'gpsmap' are the small 16x16 icons; change it to '24x24' for larger icons
    gv_options.marker_list_options = {};  // options for a dynamically-created list of markers
    gv_options.marker_list_options.enabled = false;  // true|false: enable or disable the marker list altogether
    gv_options.marker_list_options.floating = true;  // is the list a floating box inside the map itself?
    gv_options.marker_list_options.position = ['RIGHT_BOTTOM',6,38];  // floating list only: position within map
    gv_options.marker_list_options.min_width = 160; // minimum width, in pixels, of the floating list
    gv_options.marker_list_options.max_width = 160;  // maximum width
    gv_options.marker_list_options.min_height = 0;  // minimum height, in pixels, of the floating list
    gv_options.marker_list_options.max_height = 475;  // maximum height
    gv_options.marker_list_options.draggable = true;  // true|false, floating list only: can it be moved around the screen?
    gv_options.marker_list_options.collapsible = true;  // true|false, floating list only: can it be collapsed by double-clicking its top bar?
    gv_options.marker_list_options.include_tickmarks = false;  // true|false: are distance/time tickmarks included in the list?
    gv_options.marker_list_options.include_trackpoints = false;  // true|false: are "trackpoint" markers included in the list?
    gv_options.marker_list_options.dividers = false;  // true|false: will a thin line be drawn between each item in the list?
    gv_options.marker_list_options.desc = false;  // true|false: will the markers' descriptions be shown below their names in the list?
    gv_options.marker_list_options.icons = true;  // true|false: should the markers' icons appear to the left of their names in the list?
    gv_options.marker_list_options.thumbnails = false;  // true|false: should markers' thumbnails be shown in the list?
    gv_options.marker_list_options.folders_collapsed = false;  // true|false: do folders in the list start out in a collapsed state?
    gv_options.marker_list_options.folders_hidden = false;  // true|false: do folders in the list start out in a hidden state?
    gv_options.marker_list_options.collapsed_folders = []; // an array of folder names
    gv_options.marker_list_options.hidden_folders = []; // an array of folder names
    gv_options.marker_list_options.count_folder_items = false;  // true|false: list the number of items in each folder?
    gv_options.marker_list_options.wrap_names = true;  // true|false: should marker's names be allowed to wrap onto more than one line?
    gv_options.marker_list_options.unnamed = '[unnamed]';  // what 'name' should be assigned to  unnamed markers in the list?
    gv_options.marker_list_options.colors = false;  // true|false: should the names/descs of the points in the list be colorized the same as their markers?
    gv_options.marker_list_options.default_color = '';  // default HTML color code for the names/descs in the list
    gv_options.marker_list_options.limit = 0;  // how many markers to show in the list; 0 for no limit
    gv_options.marker_list_options.center = false;  // true|false: does the map center upon a marker when you click its name in the list?
    gv_options.marker_list_options.zoom = false;  // true|false: does the map zoom to a certain level when you click on a marker's name in the list?
    gv_options.marker_list_options.zoom_level = 15;  // if 'zoom' is true, what level should the map zoom to?
    gv_options.marker_list_options.info_window = true;  // true|false: do info windows pop up when the markers' names are clicked in the list?
    gv_options.marker_list_options.url_links = false;  // true|false: do the names in the list become instant links to the markers' URLs?
    gv_options.marker_list_options.toggle = false;  // true|false: does a marker disappear if you click on its name in the list?
    gv_options.marker_list_options.help_tooltips = false;  // true|false: do "tooltips" appear on marker names that tell you what happens when you click?
    gv_options.marker_list_options.id = 'gv_marker_list';  // id of a DIV tag that holds the list
    gv_options.marker_list_options.header = ''; // HTML code; be sure to put backslashes in front of any single quotes, and don't include any line breaks
    gv_options.marker_list_options.footer = ''; // HTML code
    gv_options.marker_filter_options = {};  // options for removing waypoints that are out of the current view
    gv_options.marker_filter_options.enabled = false;  // true|false: should out-of-range markers be removed?
    gv_options.marker_filter_options.movement_threshold = 8;  // in pixels, how far the map has to move to trigger filtering
    gv_options.marker_filter_options.limit = 0;  // maximum number of markers to display on the map; 0 for no limit
    gv_options.marker_filter_options.update_list = true;  // true|false: should the marker list be updated with only the filtered markers?
    gv_options.marker_filter_options.sort_list_by_distance = false;  // true|false: should the marker list be sorted by distance from the center of the map?
    gv_options.marker_filter_options.min_zoom = 0;  // below this zoom level, don't show any markers at all
    gv_options.marker_filter_options.zoom_message = '';  // message to put in the marker list if the map is below the min_zoom threshold
    gv_options.synthesize_fields = {}; // for example: {label:'{name}'} would cause all markers' names to become visible labels


    // Load GPS Visualizer's Google Maps functions (this must be loaded AFTER gv_options are set):
    document.writeln('<script src="http://maps.gpsvisualizer.com/google_maps/functions3.js" type="text/javascript"><'+'/script>');
</script>

<style type="text/css">
/* Put any custom style definitions here (e.g., .gv_marker_info_window, .gv_marker_info_window_name, .gv_marker_list_item, .gv_tooltip, .gv_label, etc.) */
#gmap_div .gv_label {
    filter:alpha(opacity=80); -moz-opacity:0.8; opacity:0.8;
    background:#333333; border:1px solid black; padding:1px;
    font:9px Verdana,sans-serif !important; color:white; font-weight:normal;
}

</style>

<!-- end GPSV setup script and styles; begin map-drawing script (they must be separate) -->
<script type="text/javascript">
    function GV_Map() {

        GV_Setup_Map();



        GV_Draw_Marker({lat:52.5078320,lon:13.3491370,name:'St&#252;lerstra&#223;e 11',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5156500,lon:13.3900060,name:'Behrensstra&#223;e 28',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5050730,lon:13.3435690,name:'Burggrafenstra&#223;e 6',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5008360,lon:13.3168690,name:'Kurf&#252;rstendamm 190-192',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5130970,lon:13.3298440,name:'Stra&#223;e des 17. Juni 115',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5166640,lon:13.3094190,name:'Otto-Suhr-Allee 84',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5164530,lon:13.2611250,name:'Steubenplatz 2-4',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5366870,lon:13.2641510,name:'Nonnendammallee 101',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4751550,lon:13.3236670,name:'S&#252;dwestkorso 10',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5396030,lon:13.2057780,name:'Am Juliusturm 106',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5281960,lon:13.2286760,name:'Charlottenburger Chaussee 75',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5333310,lon:13.1968890,name:'Brunsb&#252;tteler Damm 4',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5579990,lon:13.2183120,name:'Hugo-Cassirer-Stra&#223;e 35',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5265340,lon:13.2167270,name:'Ruhlebener Str. 161',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5556670,lon:13.1983330,name:'Hohenzollernring 117',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5476280,lon:13.1835700,name:'Falkenseer Chaussee 259',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5381510,lon:13.2175050,name:'Zitadellenweg 34',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5336530,lon:13.1744420,name:'Brunsb&#252;tteler Damm 153',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5444440,lon:13.5197220,name:'Marzahner Str. 24',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4811830,lon:13.5264690,name:'Stolzenfelsstra&#223;e 1',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5056030,lon:13.5136150,name:'Einbecker Stra&#223;e 119',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5622220,lon:13.3244440,name:'Kurt-Schumacher-Damm 28',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4401070,lon:13.3870120,name:'Alt-Mariendorf 37',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5484580,lon:13.5011570,name:'Gro&#223;e-Leege-Str. 115',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4826640,lon:13.4322480,name:'Karl-Marx-Stra&#223;e 62',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4699570,lon:13.4434860,name:'Wipperstra&#223;e 1',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4406840,lon:13.4594270,name:'M&#246;wenweg 42',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4305580,lon:13.4557410,name:'Johannisthaler Chaussee 300',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5353820,lon:13.4968110,name:'Landsberger Allee 277',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4166090,lon:13.4968440,name:'Alt-Rudow 74',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5346580,lon:13.5105930,name:'Landsberger Allee 364',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4647860,lon:13.3671820,name:'Bessemerstr. 33',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4373050,lon:13.2618590,name:'Clayallee 328',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4639850,lon:13.3698570,name:'Alboinstra&#223;e 56',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5474620,lon:13.4683580,name:'Indira-Gandhi-Str. 106-109',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5700660,lon:13.5665450,name:'Havemannstra&#223;e 4',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5309950,lon:13.4416110,name:'Margarete-Sommer-Str. 2',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5128900,lon:13.6121440,name:'H&#246;nower Stra&#223;e 89',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5501480,lon:13.4335680,name:'Ostseestra&#223;e 23',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5111840,lon:13.4291850,name:'Holzmarktstra&#223;e 44',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4764100,lon:13.6009830,name:'Kastanienallee 1',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5101060,lon:13.3780460,name:'Leipziger Platz 19',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4646090,lon:13.3275090,name:'Schlo&#223;stra&#223;e 1',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5137710,lon:13.5542410,name:'Kulmseestra&#223;e 4',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4253690,lon:13.5861950,name:'Wendenschlo&#223;stra&#223;e 354-358',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5026020,lon:13.3384380,name:'N&#252;rnberger Str. 12',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5422220,lon:13.5430560,name:'Marzahner Promenade 8',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4133750,lon:13.4927290,name:'Gro&#223;-Ziethener Chaussee 33',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5109670,lon:13.5877930,name:'Planitzstra&#223;e 1',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5071540,lon:13.2753350,name:'Hammarskj&#246;ldplatz 1',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5214760,lon:13.5862260,name:'Teterower Ring 43',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5348480,lon:13.2018730,name:'Stabholzgarten 4',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5237190,lon:13.3805810,name:'Schumannstr. 6',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5023040,lon:13.3250700,name:'Kurf&#252;rstendamm 31',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4214290,lon:13.3584060,name:'Malteserstra&#223;e 136-138',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5287790,lon:13.3786570,name:'Luisenstrasse',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5147090,lon:13.4766140,name:'Frankfurter Allee 113',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4668220,lon:13.3305530,name:'Rheinstra&#223;e 46',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4689860,lon:13.3286690,name:'Handjerystra&#223;e 54',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4680030,lon:13.3284190,name:'Bundesallee 87',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4726000,lon:13.3351670,name:'Lauterstra&#223;e 12',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5199330,lon:13.3802310,name:'Luisenstr. 31',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5111730,lon:13.3895630,name:'Friedrichstra&#223;e 191',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5045650,lon:13.3405730,name:'N&#252;rnberger Stra&#223;e 5-7',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4570840,lon:13.5279640,name:'Wilhelminenhofstr. 75',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5274430,lon:13.3719170,name:'Invalidenstr. 51',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5190670,lon:13.4169670,name:'Grunerstra&#223;e 20',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5440010,lon:13.5440080,name:'Marzahner Promenade 1A',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5190670,lon:13.4169670,name:'Grunertstra&#223;e 20',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4859870,lon:13.4581420,name:'Kiefholzstr. 50',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4350950,lon:13.4349650,name:'Buckower Damm 100',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4160060,lon:13.1295560,name:'Golfweg 22',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4109400,lon:13.2624120,name:'Ladiusstra&#223;e 26',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5130740,lon:13.3218030,name:'Ernst-Reuter-Platz 9',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5725710,lon:13.3662790,name:'Emmentaler Stra&#223;e 122-130',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5610050,lon:13.2130330,name:'Rauchstra&#223;e 56',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4604400,lon:13.3234820,name:'Ahornstra&#223;e 32',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.4948390,lon:13.4525560,name:'Puschkinallee 52',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5384400,lon:13.4135580,name:'Sch&#246;nhauser Allee 36',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5498910,lon:13.3897530,name:'Badstra&#223;e 4',desc:'<br>Charging Time:0 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#7a7a7a',icon:'',scale:0.188});
        GV_Draw_Marker({lat:52.5229060,lon:13.3809910,name:'Reinhardtstra&#223;e 32',desc:'<br>Charging Time:1 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc0000',icon:'',scale:0.194});
        GV_Draw_Marker({lat:52.5053080,lon:13.3277390,name:'Fasanenstrasse 154',desc:'<br>Charging Time:1 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc0000',icon:'',scale:0.194});
        GV_Draw_Marker({lat:52.4967740,lon:13.3448620,name:'Motzstra&#223;e 43',desc:'<br>Charging Time:1 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc0000',icon:'',scale:0.194});
        GV_Draw_Marker({lat:52.5012250,lon:13.3437110,name:'Bayreuther Stra&#223;e 36',desc:'<br>Charging Time:1 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc0000',icon:'',scale:0.194});
        GV_Draw_Marker({lat:52.5076870,lon:13.3284020,name:'Fasanenstra&#223;e 85',desc:'<br>Charging Time:1 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#cc0000',icon:'',scale:0.194});
        GV_Draw_Marker({lat:52.5259560,lon:13.3706330,name:'Friedrich-List-Ufer',desc:'<br>Charging Time:1 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc0000',icon:'',scale:0.194});
        GV_Draw_Marker({lat:52.5234610,lon:13.4132230,name:'Am Alexanderplatz Alexanderstr. 2',desc:'<br>Charging Time:1 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#cc0000',icon:'',scale:0.194});
        GV_Draw_Marker({lat:52.5121000,lon:13.3092610,name:'Krumme Stra&#223;e 73',desc:'<br>Charging Time:2 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc0700',icon:'',scale:0.2});
        GV_Draw_Marker({lat:52.5068220,lon:13.3062860,name:'Kantstra&#223;e 111',desc:'<br>Charging Time:2 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc0700',icon:'',scale:0.2});
        GV_Draw_Marker({lat:52.5231670,lon:13.3626670,name:'Alt-Moabit 5',desc:'<br>Charging Time:3 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc0d00',icon:'',scale:0.206});
        GV_Draw_Marker({lat:52.5040970,lon:13.3530950,name:'L&#252;tzowplatz 11',desc:'<br>Charging Time:3 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc0d00',icon:'',scale:0.206});
        GV_Draw_Marker({lat:52.5187440,lon:13.4091360,name:'J&#252;denstrasse 1',desc:'<br>Charging Time:4 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc1400',icon:'',scale:0.212});
        GV_Draw_Marker({lat:52.4932310,lon:13.3152080,name:'Pommersche Stra&#223;e 11',desc:'<br>Charging Time:5 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc1c00',icon:'',scale:0.218});
        GV_Draw_Marker({lat:52.4730600,lon:13.4573240,name:'Sonnenallee 224',desc:'<br>Charging Time:5 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc1c00',icon:'',scale:0.218});
        GV_Draw_Marker({lat:52.5405540,lon:13.5742840,name:'Hasenholzer Allee 2',desc:'<br>Charging Time:5 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc1c00',icon:'',scale:0.218});
        GV_Draw_Marker({lat:52.5192310,lon:13.3904890,name:'Planckstr. 4',desc:'<br>Charging Time:6 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc2200',icon:'',scale:0.225});
        GV_Draw_Marker({lat:52.5104260,lon:13.4088980,name:'Alte Jakobstra&#223;e 76',desc:'<br>Charging Time:7 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc2900',icon:'',scale:0.231});
        GV_Draw_Marker({lat:52.5203330,lon:13.3456670,name:'Flensburger Stra&#223;e 1',desc:'<br>Charging Time:7 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc2900',icon:'',scale:0.231});
        GV_Draw_Marker({lat:52.5291250,lon:13.3958410,name:'Torstra&#223;e 165',desc:'<br>Charging Time:9 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc3600',icon:'',scale:0.243});
        GV_Draw_Marker({lat:52.4694520,lon:13.3712790,name:'Alboinstra&#223;e 5',desc:'<br>Charging Time:9 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc3600',icon:'',scale:0.243});
        GV_Draw_Marker({lat:52.5065390,lon:13.3223750,name:'Savignyplatz (Nord) 6',desc:'<br>Charging Time:10 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc3d00',icon:'',scale:0.249});
        GV_Draw_Marker({lat:52.4604910,lon:13.3245720,name:'Hubertusstra&#223;e 14',desc:'<br>Charging Time:10 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#cc3d00',icon:'',scale:0.249});
        GV_Draw_Marker({lat:52.5094670,lon:13.3895430,name:'Krausenstra&#223;e 72',desc:'<br>Charging Time:11 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc4500',icon:'',scale:0.256});
        GV_Draw_Marker({lat:52.4597260,lon:13.3847860,name:'Tempelhofer Damm 187',desc:'<br>Charging Time:12 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc4b00',icon:'',scale:0.262});
        GV_Draw_Marker({lat:52.5210680,lon:13.3211960,name:'Helmholtzstr. 41',desc:'<br>Charging Time:12 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#cc4b00',icon:'',scale:0.262});
        GV_Draw_Marker({lat:52.5104280,lon:13.3738440,name:'Bellevuestr. 3',desc:'<br>Charging Time:12 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc4b00',icon:'',scale:0.262});
        GV_Draw_Marker({lat:52.5016530,lon:13.3354670,name:'Augsburger Str. 25',desc:'<br>Charging Time:13 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc5200',icon:'',scale:0.268});
        GV_Draw_Marker({lat:52.4699590,lon:13.3397280,name:'Bahnhofstra&#223;e 4',desc:'<br>Charging Time:17 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc6e00',icon:'',scale:0.293});
        GV_Draw_Marker({lat:52.5050500,lon:13.2999970,name:'Windscheid Str. 20',desc:'<br>Charging Time:19 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc7b00',icon:'',scale:0.305});
        GV_Draw_Marker({lat:52.5343980,lon:13.3958240,name:'Anklamer Str. 8',desc:'<br>Charging Time:20 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc8100',icon:'',scale:0.311});
        GV_Draw_Marker({lat:52.4841250,lon:13.3857890,name:'Tempelhofer Damm 2',desc:'<br>Charging Time:21 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc8800',icon:'',scale:0.318});
        GV_Draw_Marker({lat:52.5304830,lon:13.3913500,name:'Gartenstra&#223;e 13',desc:'<br>Charging Time:22 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc8e00',icon:'',scale:0.324});
        GV_Draw_Marker({lat:52.4837500,lon:13.2966780,name:'Fritz-Wildung-Stra&#223;e 26',desc:'<br>Charging Time:23 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc9500',icon:'',scale:0.33});
        GV_Draw_Marker({lat:52.5085610,lon:13.2727860,name:'Theodor-Heuss-Platz 5',desc:'<br>Charging Time:23 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc9500',icon:'',scale:0.33});
        GV_Draw_Marker({lat:52.5116670,lon:13.4975000,name:'Frankfurter Allee 248',desc:'<br>Charging Time:23 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc9500',icon:'',scale:0.33});
        GV_Draw_Marker({lat:52.4523970,lon:13.4225200,name:'Gradestra&#223;e 100',desc:'<br>Charging Time:23 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc9500',icon:'',scale:0.33});
        GV_Draw_Marker({lat:52.4909500,lon:13.3305530,name:'G&#252;ntzelstr.14/Ecke Bundesallee 14',desc:'<br>Charging Time:24 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc9d00',icon:'',scale:0.336});
        GV_Draw_Marker({lat:52.5189320,lon:13.2957180,name:'Schlo&#223;stra&#223;e 1',desc:'<br>Charging Time:24 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc9d00',icon:'',scale:0.336});
        GV_Draw_Marker({lat:52.4885310,lon:13.3411600,name:'Grunewaldstra&#223;e 57',desc:'<br>Charging Time:24 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#cc9d00',icon:'',scale:0.336});
        GV_Draw_Marker({lat:52.5357750,lon:13.4050060,name:'Griebenowstra&#223;e 5',desc:'<br>Charging Time:26 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#ccaa00',icon:'',scale:0.349});
        GV_Draw_Marker({lat:52.5029770,lon:13.4810840,name:'Stadthausstra&#223;e',desc:'<br>Charging Time:28 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#ccb700',icon:'',scale:0.361});
        GV_Draw_Marker({lat:52.5311260,lon:13.4689790,name:'Landsberger Allee 174',desc:'<br>Charging Time:30 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#ccc600',icon:'',scale:0.373});
        GV_Draw_Marker({lat:52.4717200,lon:13.3280600,name:'Friedrich-Wilhelm-Platz 7',desc:'<br>Charging Time:32 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#c5cc00',icon:'',scale:0.386});
        GV_Draw_Marker({lat:52.5161390,lon:13.4799890,name:'Normannenstr. 1',desc:'<br>Charging Time:34 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#b8cc00',icon:'',scale:0.398});
        GV_Draw_Marker({lat:52.5082480,lon:13.3765040,name:'Gabriele-Tergit-Promenade 1',desc:'<br>Charging Time:35 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#b1cc00',icon:'',scale:0.404});
        GV_Draw_Marker({lat:52.4776490,lon:13.4273610,name:'Werbellinstra&#223;e 79',desc:'<br>Charging Time:35 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#b1cc00',icon:'',scale:0.404});
        GV_Draw_Marker({lat:52.5241670,lon:13.4213890,name:'Mollstra&#223;e 4',desc:'<br>Charging Time:36 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#a9cc00',icon:'',scale:0.411});
        GV_Draw_Marker({lat:52.5338420,lon:13.4031540,name:'Veteranenstra&#223;e 25',desc:'<br>Charging Time:38 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#9ccc00',icon:'',scale:0.423});
        GV_Draw_Marker({lat:52.5230560,lon:13.4661110,name:'Hermann-Blankenstein-Stra&#223;e 40',desc:'<br>Charging Time:41 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#89cc00',icon:'',scale:0.442});
        GV_Draw_Marker({lat:52.4822770,lon:13.3572830,name:'Torgauer Str. 12',desc:'<br>Charging Time:41 hours<br><br>  Charging Typ:AC_7_4KW <br><br> Charging owner: VATTENFALL',color:'#89cc00',icon:'',scale:0.442});
        GV_Draw_Marker({lat:52.4861990,lon:13.4239850,name:'Hermannstra&#223;e 1',desc:'<br>Charging Time:51 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#44cc00',icon:'',scale:0.504});
        GV_Draw_Marker({lat:52.4838890,lon:13.4436110,name:'Wildenbruchplatz 1',desc:'<br>Charging Time:51 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#44cc00',icon:'',scale:0.504});
        GV_Draw_Marker({lat:52.5178330,lon:13.4171670,name:'Voltairestra&#223;e 2',desc:'<br>Charging Time:53 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#37cc00',icon:'',scale:0.516});
        GV_Draw_Marker({lat:52.4777620,lon:13.4476410,name:'Treptower Stra&#223;e 97',desc:'<br>Charging Time:55 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#28cc00',icon:'',scale:0.528});
        GV_Draw_Marker({lat:52.5359310,lon:13.3688890,name:'Boyenstr. 23',desc:'<br>Charging Time:65 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#00cc1b',icon:'',scale:0.59});
        GV_Draw_Marker({lat:52.5157900,lon:13.4104600,name:'Neue J&#252;denstra&#223;e 1',desc:'<br>Charging Time:69 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#00cc37',icon:'',scale:0.615});
        GV_Draw_Marker({lat:52.5136610,lon:13.3227780,name:'Marchstra&#223;e 9',desc:'<br>Charging Time:72 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#00cc4a',icon:'',scale:0.634});
        GV_Draw_Marker({lat:52.4822690,lon:13.3572750,name:'Torgauer Stra&#223;e 12-15',desc:'<br>Charging Time:89 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#00ccbf',icon:'',scale:0.739});
        GV_Draw_Marker({lat:52.5055780,lon:13.3229780,name:'Savignyplatz (S&#252;d) 2',desc:'<br>Charging Time:111 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#0045cc',icon:'',scale:0.876});
        GV_Draw_Marker({lat:52.4994160,lon:13.3290860,name:'Schaperstra&#223;e 17',desc:'<br>Charging Time:112 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#003dcc',icon:'',scale:0.882});
        GV_Draw_Marker({lat:52.4816670,lon:13.3575000,name:'Torgauer Stra&#223;e 15',desc:'<br>Charging Time:121 hours<br><br>  Charging Typ:AC_22_2KW <br><br> Charging owner: RWE',color:'#0000cc',icon:'',scale:0.938});


        GV_Finish_Map();


    }
    GV_Map(); // execute the above code


</script>


</body>

</html>

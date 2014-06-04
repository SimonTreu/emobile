
<%@ page import="org.jfree.data.time.*; de.dfki.gs.domain.GasolineStation; de.dfki.gs.domain.CarType;  java.util.Date;" contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <style>
        #date {
            position:absolute;
            z-index:1;
            right:10px;
            top:5px;
        }

        #date1 {
            position:absolute;
            z-index:1;
            right:10px;
            top:5px;
        }
        .calendar {
            position:absolute;
            z-index:3;
            right:200px;
            top:50px;
        }
        .calendar1 {
            position:absolute;
            z-index:3;
            right:10px;
            top:50px;
        }
        #fromDate_value{
             z-index:3;
             position:absolute;
             right:270px;
             top:10px;
         }
        #fromDate-trigger{
            z-index:3;
            position:absolute;
            right:245px;
            top:15px;
        }

        #toDate_value{
            z-index:3;
            position:absolute;
            right:75px;
            top:10px;
        }
        #toDate-trigger{
            z-index:3;
            position:absolute;
            right:50px;
            top:15px;
        }
        #dateButton {
            position:absolute;
            z-index:4;
            right:5px;
            top:10px;
        }
        </style>

        <script type="javascript">
            function getColor(value){
                //value from 0 to 1
                var hue=((1-value)*120).toString(10);
                return ["hsl(",hue,",100%,50%)"].join("");
            }
            var len=10;
            for(var i=0; i<=len; i++){
                var value=i/len;
                var d=document.createElement('div');
                d.textContent="value="+value;
                d.style.backgroundColor=getColor(value);
                document.body.appendChild(d);
            }
         </script>
<title></title>
    <script type="text/javascript">
        var google_api_key = ''; // Your project's Google Maps API key goes here (https://code.google.com/apis/console)
        document.writeln('<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?v=3&amp;libraries=geometry&amp;sensor=false&amp;'+((self.google_api_key&&document.location.toString().indexOf('file://')!=0)?'key='+google_api_key:'')+'"><'+'/script>');
    </script>

    <g:javascript src="functions3.js" />

    <calendar:resources lang="en" theme="tiger"/>
    <link rel='stylesheet' href="${resource(dir: 'css', file: 'menu.css')}" type='text/css' />
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
    <script type='text/javascript' src="${resource(dir: 'js', file: 'menu_jquery.js')}"></script>
</head>

<body style="margin-left:0px; margin-right:0px; margin-top:0px; margin-bottom:0px;">

<g:render template="/layouts/topbar"/>

<g:form controller="mapView" action="listUsages">
    <calendar:datePicker id="fromDate" name="fromDate" showTime="true" dateFormat="%H:%M - %d.%m.%Y" years="${2014}" singleClick="true" defaultValue="${fromDate}" value="${fromDate_value}"/>
    <calendar:datePicker id="toDate" name="toDate" showTime="true" dateFormat="%H:%M - %d.%m.%Y" years="${2014}" defaultValue="${toDate}" value="${toDate}"/>
    <g:submitButton name="Go" id="dateButton"></g:submitButton>
</g:form>
<div id="map" class="olMap" style="z-index:1">

<div id="gmap_div"></div>

    <div id="gv_infobox" class="gv_infobox" style="font:11px Arial; border:solid #666666 1px; background:#ffffff; padding:4px; overflow:auto; display:none; float:right; ">
        <div id="gv_legend_header" style="padding-bottom:2px;"><b>Loading Time in %</b></div>
<g:each in="${legendGroups}" var="legendGroup">
    <g:if test="${((legendGroup.count > 0) && (legendGroup.name=='0-10') )}" >
        <div class="gv_legend_item"><span style="color: #00FF00;">&#9608; ${legendGroup.name}</span></div>
    </g:if>
    <g:if test="${((legendGroup.count > 0) && (legendGroup.name=='11-20') )}" >
        <div class="gv_legend_item"><span style="color: #47FF00;">&#9608; ${legendGroup.name}</span></div>
    </g:if>
    <g:if test="${((legendGroup.count > 0) && (legendGroup.name=='21-30') )}" >
        <div class="gv_legend_item"><span style="color: #B0FF00;">&#9608; ${legendGroup.name}</span></div>
    </g:if>
    <g:if test="${((legendGroup.count > 0) && (legendGroup.name=='31-40') )}" >
        <div class="gv_legend_item"><span style="color: #E5FF00;">&#9608; ${legendGroup.name}</span></div>
    </g:if>
    <g:if test="${((legendGroup.count > 0) && (legendGroup.name=='41-50') )}" >
        <div class="gv_legend_item"><span style="color: #FFE400;">&#9608; ${legendGroup.name}</span></div>
    </g:if>
    <g:if test="${((legendGroup.count > 0) && (legendGroup.name=='51-60') )}" >
        <div class="gv_legend_item"><span style="color: #FFAF00;">&#9608; ${legendGroup.name}</span></div>
    </g:if>
    <g:if test="${((legendGroup.count > 0) && (legendGroup.name=='61-70') )}" >
        <div class="gv_legend_item"><span style="color: #FF7B00;">&#9608; ${legendGroup.name}</span></div>
    </g:if>
    <g:if test="${((legendGroup.count > 0) && (legendGroup.name=='71-80') )}" >
        <div class="gv_legend_item"><span style="color: #FF4600;">&#9608; ${legendGroup.name}</span></div>
    </g:if>
    <g:if test="${((legendGroup.count > 0) && (legendGroup.name=='81-90') )}" >
        <div class="gv_legend_item"><span style="color: #FF1100;">&#9608; ${legendGroup.name}</span></div>
    </g:if>
    <g:if test="${((legendGroup.count > 0) && (legendGroup.name=='91-100') )}" >
        <div class="gv_legend_item"><span style="color: #FF0000;">&#9608; ${legendGroup.name}</span></div>
    </g:if>


</g:each>


 </div>


<div id="gv_marker_list" class="gv_marker_list" style="background:#ffffff; overflow:auto; display:none;"><!-- --></div>

<div id="gv_clear_margins" style="height:0px; clear:both;"><!-- clear the "float" --></div>
</div>


<!-- begin GPS Visualizer setup script (must come after maps.google.com code) -->
<script type="text/javascript">
 /* Global variables used by the GPS Visualizer functions (20140514011204): */
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
 gv_options.infobox_options.position = ['G_ANCHOR_BOTTOM_LEFT',3,50];  // [Google anchor name, relative x, relative y]
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

 <g:each in="${stationList}" var="station" >
     <g:if test ="${((station.scale>=0.0) && (station.scale<=0.1))}">
     GV_Draw_Marker({lat:${station.lat}, lon:${station.lon}, name: '${station.streetName + ' ' +  station.houseNumber} ' ,desc:'Charging Time: ${station.inUsePercentage} %<br><br>  Charging Typ:${station.type} <br><br> Charging owner: ${station.ownerName}',color:'#00FF00', icon:'',scale:${station.scale}});
     </g:if>
     <g:if test ="${((station.scale>=0.11) && (station.scale<=0.2))}">
     GV_Draw_Marker({lat:${station.lat}, lon:${station.lon}, name: '${station.streetName + ' ' +  station.houseNumber} ' ,desc:'Charging Time: ${station.inUsePercentage} %<br><br>  Charging Typ:${station.type} <br><br> Charging owner: ${station.ownerName}',color:'#47FF00', icon:'',scale:${station.scale}});
     </g:if>
    <g:if test ="${((station.scale>=0.21) && (station.scale<=0.3))}">
         GV_Draw_Marker({lat:${station.lat}, lon:${station.lon}, name: '${station.streetName + ' ' +  station.houseNumber} ' ,desc:'Charging Time: ${station.inUsePercentage} %<br><br>  Charging Typ:${station.type} <br><br> Charging owner: ${station.ownerName}',color:'#B0FF00', icon:'',scale:${station.scale}});
   </g:if>
    <g:if test ="${((station.scale>=0.31) && (station.scale<=0.4))}">
        GV_Draw_Marker({lat:${station.lat}, lon:${station.lon}, name: '${station.streetName + ' ' +  station.houseNumber} ' ,desc:'Charging Time: ${station.inUsePercentage} %<br><br>  Charging Typ:${station.type} <br><br> Charging owner: ${station.ownerName}',color:'#E5FF00', icon:'',scale:${station.scale}});
    </g:if>
    <g:if test ="${((station.scale>=0.41) && (station.scale<=0.5))}">
     GV_Draw_Marker({lat:${station.lat}, lon:${station.lon}, name: '${station.streetName + ' ' +  station.houseNumber} ' ,desc:'Charging Time: ${station.inUsePercentage} %<br><br>  Charging Typ:${station.type} <br><br> Charging owner: ${station.ownerName}',color:'#FFE400', icon:'',scale:${station.scale}});
    </g:if>
    <g:if test ="${((station.scale>=0.51) && (station.scale<=0.6))}">
     GV_Draw_Marker({lat:${station.lat}, lon:${station.lon}, name: '${station.streetName + ' ' +  station.houseNumber} ' ,desc:'Charging Time: ${station.inUsePercentage} %<br><br>  Charging Typ:${station.type} <br><br> Charging owner: ${station.ownerName}',color:'#FFAF00', icon:'',scale:${station.scale}});
    </g:if>
    <g:if test ="${((station.scale>=0.61) && (station.scale<=0.7))}">
     GV_Draw_Marker({lat:${station.lat}, lon:${station.lon}, name: '${station.streetName + ' ' +  station.houseNumber} ' ,desc:'Charging Time: ${station.inUsePercentage} %<br><br>  Charging Typ:${station.type} <br><br> Charging owner: ${station.ownerName}',color:'#FF7B00', icon:'',scale:${station.scale}});
    </g:if>
    <g:if test ="${((station.scale>=0.71) && (station.scale<=0.8))}">
     GV_Draw_Marker({lat:${station.lat}, lon:${station.lon}, name: '${station.streetName + ' ' +  station.houseNumber} ' ,desc:'Charging Time: ${station.inUsePercentage} %<br><br>  Charging Typ:${station.type} <br><br> Charging owner: ${station.ownerName}',color:'#FF4600', icon:'',scale:${station.scale}});
    </g:if>
    <g:if test ="${((station.scale>=0.81) && (station.scale<=0.9))}">
     GV_Draw_Marker({lat:${station.lat}, lon:${station.lon}, name: '${station.streetName + ' ' +  station.houseNumber} ' ,desc:'Charging Time: ${station.inUsePercentage} %<br><br>  Charging Typ:${station.type} <br><br> Charging owner: ${station.ownerName}',color:'#FF1100', icon:'',scale:${station.scale}});
    </g:if>
    <g:if test ="${((station.scale>=0.91) && (station.scale<=1.0))}">
     GV_Draw_Marker({lat:${station.lat}, lon:${station.lon}, name: '${station.streetName + ' ' +  station.houseNumber} ' ,desc:'Charging Time: ${station.inUsePercentage} %<br><br>  Charging Typ:${station.type} <br><br> Charging owner: ${station.ownerName}',color:'#FF0000', icon:'',scale:${station.scale}});
    </g:if>
     </g:each>
    GV_Finish_Map();

}

GV_Map(); // execute the above code

</script>

</body>
</html>
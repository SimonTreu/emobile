<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 25.09.13
  Time: 13:06
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>


    <title></title>
    <style>
    html, body, #map {
        height: 100%;
        margin: 0px;
        padding: 0px
    }
    </style>
    <script type="text/javascript"
            src="http://maps.googleapis.com/maps/api/js?key=AIzaSyAVgC48E2RvrDZoQnQeaRCowxshL67fAcM&sensor=true">
    </script>

    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false"></script>
    <script type="text/javascript" >
        function initialize() {
            var mapOptions = {
                center: new google.maps.LatLng(52.5234051,	13.4113999),
                zoom: 13,
                mapTypeId: google.maps.MapTypeId.ROADMAP
            };

            var map = new google.maps.Map(document.getElementById("map"),
                    mapOptions);
            var trafficLayer = new google.maps.TrafficLayer();
            trafficLayer.setMap(map);

        }
        google.maps.event.addDomListener(window, 'load', initialize);
    </script>

    <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
</head>
<body onload="initialize()">

<g:render template="/layouts/topbar" />

<br/>
<div>
    <p>HUA!</p>
</div>

<div id="content">
    <div id="map" style="height: 80%; width: 90%; padding: 30px 30px; ">
    </div>
</div>




<g:render template="/layouts/footer" />
</body>
</html>
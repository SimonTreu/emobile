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
    <script type="text/javascript"
            src="http://maps.googleapis.com/maps/api/js?key=AIzaSyAVgC48E2RvrDZoQnQeaRCowxshL67fAcM&sensor=true">
    </script>

    <script type="text/javascript" >
        function initialize() {
            var mapOptions = {
                center: new google.maps.LatLng(-34.397, 150.644),
                zoom: 8,
                mapTypeId: google.maps.MapTypeId.ROADMAP
            };
            var map = new google.maps.Map(document.getElementById("map_canvas"),
                    mapOptions);
        }
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
    <div id="map_canvas" style="height: 80%; width: 90%; padding: 30px 30px; ">
    </div>
</div>




<g:render template="/layouts/footer" />
</body>
</html>
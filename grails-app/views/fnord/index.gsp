<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 18.09.13
  Time: 18:11
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Fnord</title>

    <meta name="layout" content="main" />

    <g:javascript src="i2maps/i2maps.js" />


    <%-- some css --%>


    <%--
    <g:resource file="css/i2maps/menu.css" />
    <g:resource file="css/i2maps/ui.panel.css" />
    <g:resource file="css/i2maps/i2maps.css" />
    <g:resource file="css/i2maps/jquery-ui-aristo.css" />
    --%>

</head>
<body>



<div>

    <button id="button_play_pause" onClick="button_clicked()"><b>Play</b></button>
    <div id="map" style="background-color: #eee; width:94%; height:94%; position: absolute; left:5%; top:2% padding-top:1px"></div>

    <div id="info_box" class="panel" style="position: fixed; right:5%; bottom:5%; height: 200px; width: 200px; margin-left: 5px; opacity: 0.9;">
        <h3>Info</h3>
        <div>
            <div id="attributes" style="height: 100px;"></div>
        </div>
    </div>

</div>


<r:layoutResources></r:layoutResources>

</body>
</html>
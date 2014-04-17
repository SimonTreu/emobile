<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 10.04.14
  Time: 14:57
--%>



<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>

    <meta name="layout" content="main" />

</head>

<body>

${countTargetReached} of ${carsCount} reached their targets ( ${percentageReached} % success )

Simulation Time: ${simulationTime} h

<g:if test="${fillingStationFileUUID != null}">
    <img class="stats" src="${createLink( controller:'stats', action:'showStatsFile', params: [ fileUUID : fillingStationFileUUID ] ) }" />
</g:if>


<g:if test="${timeFileUUID != null}">
    <img class="stats" src="${createLink( controller:'stats', action:'showStatsFile', params: [ fileUUID : timeFileUUID ] ) }" />
</g:if>

<g:if test="${distanceFileUUID != null}">
    <img class="stats" src="${createLink( controller:'stats', action:'showStatsFile', params: [ fileUUID : distanceFileUUID ] ) }" />
</g:if>


</body>
</html>


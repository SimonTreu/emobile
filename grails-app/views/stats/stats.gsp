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

Under ${relativeSearchLimit * 100} % cars were searching for filling stations.

Simulation Time: ${simulationTime} h

<table border="0" style="width: 30%" >

    <tr>
        <th>Car Type</th>
        <th>Count</th>
        <th>Target reached</th>
    </tr>

    <g:each in="${carTypes}" var="carType">

        <tr>
            <td>${carType.key}</td>
            <td>${carType.value.count}</td>
            <td>${carType.value.targetReached}</td>
        </tr>

    </g:each>

</table>

<table border="0" style="width: 30%" >

    <tr>
        <th>Filling Station</th>
        <th>Count</th>
        <th>Count Used</th>
        <th>Time in Use [h]</th>
    </tr>
    <g:each in="${fillingStations}" var="fillStation">

        <tr>
            <td>${message( code: 'de.dfki.gs.domain.gasolinestationtype.' + fillStation.key )}</td>
            <td>${fillStation.value.count}</td>
            <td>${fillStation.value.countUsed}</td>
            <td>${fillStation.value.usage}</td>
        </tr>

    </g:each>


</table>


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


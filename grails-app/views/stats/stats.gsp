<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 10.04.14
  Time: 14:57
--%>



<%@ page contentType="text/html;charset=UTF-8" %>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <title></title>

    <meta name="layout" content="main" />

</head>

<body>

Experiment config: Cars were searching for filling stations with energy < ${relativeSearchLimit * 100} %.
<br/>

Global Success Rate: ${countTargetReached} of ${carsCount} reached their targets ( ${percentageReached} % success )
<br/>


Simulation Time: ${simulationTime} h
<br/>

Execution Time: ${simTimeMillis} ms or ${simTimeMillis / ( 1000 * 60 * 60 )} hrs
<br/>

Get all car results in a cvs file: ( count of stations, power of stations, behaviour of searching, successful (1) or not (0) )
<g:form controller="stats" action="createCsv" >

    <g:hiddenField name="simulationId" value="${simulationId}" />
    <g:submitButton name="Create CSV-File" value="Create CSV-File" />

</g:form>

<br/>

Show Results  detour-Distance per Planned-Distance :



    <g:each in="${relativeSearchLimits}" var="relS"  >

        <g:form controller="stats" action="detourPicture">

            <g:hiddenField name="simulationId" value="${simulationId}" />
            <g:hiddenField name="relativeSearchLimit" value="${relS}" />

            <g:submitButton name="${relS}" value="Show for ${relS} %"   />

        </g:form>

    </g:each>

Show Results  detour-time per Planned-Time :



<g:each in="${relativeSearchLimits}" var="relS"  >

    <g:form controller="stats" action="detourTimePicture">

        <g:hiddenField name="simulationId" value="${simulationId}" />
        <g:hiddenField name="relativeSearchLimit" value="${relS}" />

        Loading Included: <g:checkBox name="withLoading" value="${true}" />

        <g:submitButton name="${relS}" value="Show for ${relS} %"   />

    </g:form>

</g:each>



<table border="0" style="width: 40%" >

    <tr>
        <th>Car Type</th>
        <th>Count</th>
        <th>Target reached</th>
        <th>Success Rate [%]</th>
    </tr>

    <g:each in="${carTypes}" var="carType">

        <tr>
            <td>${carType.key}</td>
            <td>${carType.value.count}</td>
            <td>${carType.value.targetReached}</td>
            <td>${ ( carType.value.count > 0 )? Math.round( ( carType.value.targetReached / carType.value.count ) * 100 ):'n.a.' }</td>
        </tr>

    </g:each>

</table>

<h2>Detailed Statistics</h2>

<%-- for each car type --%>

<g:each in="${carTypes}" var="carType">

    <div style="border: 1px solid #FAD614">

        <g:form controller="stats" action="showDetailsPicture">

            <h3>Details for ${carType.key}</h3>
            <table border="0" style="width: 80%" >
                <tr>
                    <th>Distance <br/>Class <br/>[ km ]</th>
                    <th>Count</th>
                    <th>Target <br/>Reached</th>
                    <th>Success Rate <br/>[ % ]</th>
                    <th>Mean <br/>Real Distance <br/>[ km ]</th>
                    <th>Mean <br/>Diff Distance <br/>[ km ]</th>

                    <th>Mean <br/>Planned Time <br/>[ h:mm ]</th>
                    <th>Mean <br/>Time Used<br/>[ h:mm ]</th>

                    <th>Mean <br/>Loading Time <br/>[ h:mm ]</th>
                    <th>Mean <br/>OverTime <br/>[ h:mm ]</th>
                </tr>

                <g:each in="${carType.value.details}" var="detail">

                    <g:if test="${detail.count > 0}">

                        <tr>
                            <td>${detail.plannedDistanceClass}</td>
                            <td>${detail.count}</td>
                            <td>${detail.targetReached}</td>
                            <td>${detail.successRate}</td>
                            <td>${detail.meanRealDistance}</td>

                            <td>${ detail.meanDiffRealPlannedDistance.equals( "n.a." )?'n.a.':Math.round( detail.meanDiffRealPlannedDistance * 1000 ) / 1000  }</td>


                            <td>${detail.meanPlannedTime}</td>
                            <td>${detail.meanRealTime}</td>

                            <td>${detail.meanTimeForLoading}</td>
                            <td>${detail.meanDiffRealPllanedTime}</td>
                        </tr>

                    </g:if>
                </g:each>

            </table>

            <g:hiddenField name="experimentResultId" value="${experimentResultId}" />
            <g:hiddenField name="carType" value="${carType.key}" />

            <g:submitButton name="distance" value="Show Distance Picture"  />
            <g:submitButton name="time" value="Show Time Picture" />

        </g:form>
    </div>
</g:each>



<table border="0" style="width: 40%" >

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


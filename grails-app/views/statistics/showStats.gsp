<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 02.10.14
  Time: 15:47
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
</head>

<body>

    <%--
        for each fleet in stats map print out a tabl
        #e
    --%>
    <g:each in="${stats}" var="fleetStat" >

        Fleet Name: ${fleetStat.name} with ${fleetStat.carResults.size()}

        Distribution: ${fleetStat.distribution} from ${fleetStat.plannedFromKm} km to ${fleetStat.plannedToKm} km


        All cars:

        <%--
        <table border="1">
            <tr>
                <th>Planned Time</th>
                <th>Planned Distance</th>
                <th>Real Distance</th>
                <th>Real Time</th>
                <th>Loading Time</th>
                <th>Energy Loaded</th>
                <th>Energy Demanded</th>
                <th>Real Driving Time</th>
            </tr>
            <tr>
                <td>${fleetStat.stats.allCars.plannedTime.mean}</td>
                <td>${fleetStat.stats.allCars.plannedDistance.mean}</td>
                <td>${fleetStat.stats.allCars.realDistance.mean}</td>
                <td>${fleetStat.stats.allCars.realTime.mean}</td>
                <td>${fleetStat.stats.allCars.loadingTime.mean}</td>
                <td>${fleetStat.stats.allCars.energyLoaded.mean}</td>
                <td>${fleetStat.stats.allCars.energyDemanded.mean}</td>
                <td>${fleetStat.stats.allCars.realDrivingTime.mean}</td>
            </tr>
        </table>
        --%>


    </g:each>


</body>
</html>
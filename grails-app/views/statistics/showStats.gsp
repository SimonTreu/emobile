<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 02.10.14
  Time: 15:47
--%>

<%@ page import="de.dfki.gs.utils.TimeCalculator" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <script type='text/javascript' src="${resource(dir: 'js', file: 'prefix-free.js')}"></script>
    <script type='text/javascript' src="${resource(dir: 'js', file: 'jquery-1.9.0.js')}"></script>
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
    <link rel="apple-touch-icon" href="${resource(dir: 'images', file: 'apple-touch-icon.png')}">
    <link rel="apple-touch-icon" sizes="114x114" href="${resource(dir: 'images', file: 'apple-touch-icon-retina.png')}">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'mobile.css')}" type="text/css">


    <link href='http://fonts.googleapis.com/css?family=Droid+Sans' rel='stylesheet' type='text/css'>

    <link rel='stylesheet' href="${resource(dir: 'css', file: 'style.css')}" type='text/css' />
    <link rel='stylesheet' href="${resource(dir: 'css', file: 'iconic.css')}" type='text/css' />
</head>

<body>
<g:render template="/login/topbar"/>
<script type="text/javascript">

    function handleCheckBoxClick( cb ) {

        var cbNameField = cb.name.split( "::" );

        var fleetName = cbNameField[ 0 ];
        var carTypeName = cbNameField[ 1 ];
        var columnName = cbNameField[ 2 ];

        var allField = document.getElementById( fleetName + "::" + carTypeName + "::all" );
        var successfulField = document.getElementById( fleetName + "::" + carTypeName + "::successful" );
        var failedField = document.getElementById( fleetName + "::" + carTypeName + "::failed" );



        if ( columnName == "all" || columnName == "successful" || columnName == "failed" ) {
            // click was on a row

            var columnElements = document.getElementsByClassName( fleetName + "::" + carTypeName );
            for ( var i = 0; i < columnElements.length; i++ ) {

                var hua = columnElements[ i ];

                var currentColumnSplit = hua.name.split( "::" );
                var currentColumnName = currentColumnSplit[ currentColumnSplit.length - 1 ];

                if ( cb.checked && hua.checked ) {
                    // highlight green
                    var fieldElement = document.getElementById( columnName + "::" + fleetName + "::" + carTypeName + "::" + currentColumnName );

                    fieldElement.style.color = "#00AA00";

                } else {
                    // highlight normal!
                    var fieldElement = document.getElementById( columnName + "::" + fleetName + "::" + carTypeName + "::" + currentColumnName );

                    fieldElement.style.color = "black";

                }

            }


        } else {
            // click was on a column

            var allRowField = document.getElementById( fleetName + "::" + carTypeName + "::all" );
            var successRowField = document.getElementById( fleetName + "::" + carTypeName + "::successful" );
            var failedRowField = document.getElementById( fleetName + "::" + carTypeName + "::failed" );

            var currentName = cb.name;


            if ( allRowField.checked && cb.checked ) {

                var fieldElement = document.getElementById( "all::" + fleetName + "::" + carTypeName + "::" + columnName );
                fieldElement.style.color = "#00AA00";

            } else {

                var fieldElement = document.getElementById( "all::" + fleetName + "::" + carTypeName + "::" + columnName );
                fieldElement.style.color = "black";

            }

            if ( successRowField.checked && cb.checked ) {

                var fieldElement = document.getElementById( "successful::" + fleetName + "::" + carTypeName + "::" + columnName );
                fieldElement.style.color = "#00AA00";

            } else {
                var fieldElement = document.getElementById( "successful::" + fleetName + "::" + carTypeName + "::" + columnName );
                fieldElement.style.color = "black";
            }

            if ( failedRowField.checked && cb.checked ) {

                var fieldElement = document.getElementById( "failed::" + fleetName + "::" + carTypeName + "::" + columnName );
                fieldElement.style.color = "#00AA00";

            } else {
                var fieldElement = document.getElementById( "failed::" + fleetName + "::" + carTypeName + "::" + columnName );
                fieldElement.style.color = "black";
            }


        }

    }

</script>
    <%--
        for each fleet in stats map print out a tabl
        #e
    --%>


<div class="pContainerConfigure">
    <g:each in="${stats.fleets}" var="fleetStat" >
        <fieldset>
            <legend>
                <g:message code="templates.configuration.fleet._distribution.fleetname"/>: ${fleetStat.name}
                <g:message code="templates.configuration.fleet._distribution.with"/> ${fleetStat.carResults.size()}
                <g:message code="templates.configuration.fleet._distribution.car"/> |
                <g:message code="templates.configuration.fleet._distribution.distribution"/>
                ${fleetStat.distribution} from ${fleetStat.plannedFromKm} km to ${fleetStat.plannedToKm} km
            </legend>

            <g:each in="${fleetStat.carTypes}" var="carType" >
                <g:form id="allCarTypes" controller="statistics" action="showPicture">

                    <g:hiddenField name="carTypeName" value="${carType.name}" />
                    <g:hiddenField name="fleetName" value="${fleetStat.name}" />
                    <g:hiddenField name="experimentRunResultId" value="${experimentRunResultId}" />

                    <div class="contentImage">
                        <div class="rowUp">
                            <div class="leftBoldBig">
                                <span class="cartype">CarType: <span class="cartypeBold"> ${carType.name} </span></span>
                                <span class="cartype">Successful: <span class="cartypeBold">${carType.countSuccessful}</span> of <span class="cartypeBold">${carType.countAll}</span></span>
                                <span class="cartype">Failed: <span class="cartypeBold">${carType.countFails}</span> of <span class="cartypeBold">${carType.countAll}</span></span>
                            </div>
                        </div>

                        </br></br>

                        <table class="table">
                            <tr class="table" align="center">
                                <td width="100px">
                                    Success
                                </td>


                                        <td width="100px" class="shiebenTitle">
                                            <g:checkBox name="${fleetStat.name}::${carType.name}::plannedTime"
                                                        id="${fleetStat.name}::${carType.name}::plannedTime"
                                                        class="${fleetStat.name}::${carType.name}"
                                                        onclick="handleCheckBoxClick(this);" />
                                            Planned Time
                                        </td>

                                        <td width="100px" class="shiebenTitle">
                                            <g:checkBox name="${fleetStat.name}::${carType.name}::realDrivingTime"
                                                        class="${fleetStat.name}::${carType.name}"
                                                        onclick="handleCheckBoxClick(this);"
                                                        id="${fleetStat.name}::${carType.name}::realDrivingTime" />
                                            Real Driving Time
                                        </td>
                                        <td width="100px" class="shiebenTitle">
                                            <g:checkBox name="${fleetStat.name}::${carType.name}::realTime"
                                                        class="${fleetStat.name}::${carType.name}"
                                                        onclick="handleCheckBoxClick(this);"
                                                        id="${fleetStat.name}::${carType.name}::realTime" />
                                            Real Time
                                        </td>
                                        <td width="100px" class="shiebenTitle">
                                            <g:checkBox name="${fleetStat.name}::${carType.name}::loadingTime"
                                                        class="${fleetStat.name}::${carType.name}"
                                                        onclick="handleCheckBoxClick(this);"
                                                        id="${fleetStat.name}::${carType.name}::loadingTime" />
                                            Loading Time
                                        </td>

                                        <td width="100px" class="shiebenTitle">
                                            <g:checkBox name="${fleetStat.name}::${carType.name}::plannedDistance"
                                                        class="${fleetStat.name}::${carType.name}"
                                                        onclick="handleCheckBoxClick(this);"
                                                        id="${fleetStat.name}::${carType.name}::plannedDistance" />
                                            Planned Distance
                                        </td>
                                        <td width="100px" class="shiebenTitle">
                                            <g:checkBox name="${fleetStat.name}::${carType.name}::realDistance"
                                                        class="${fleetStat.name}::${carType.name}"
                                                        onclick="handleCheckBoxClick(this);"
                                                        id="${fleetStat.name}::${carType.name}::realDistance" />
                                            Real Distance
                                        </td>

                                        <td width="100px" class="shiebenTitle">
                                            <g:checkBox name="${fleetStat.name}::${carType.name}::energyLoaded"
                                                        class="${fleetStat.name}::${carType.name}"
                                                        onclick="handleCheckBoxClick(this);"
                                                        id="${fleetStat.name}::${carType.name}::energyLoaded" />
                                            Energy Loaded
                                        </td>
                                        <td width="100px" class="shiebenTitle">
                                            <g:checkBox name="${fleetStat.name}::${carType.name}::energyDemanded"
                                                        class="${fleetStat.name}::${carType.name}"
                                                        onclick="handleCheckBoxClick(this);"
                                                        id="${fleetStat.name}::${carType.name}::energyDemanded" />
                                            Energy Demanded
                                        </td>

                                        </tr>

                                        <tr class="table">

                                            <td width="100px" class="shieben">
                                                <g:checkBox name="${fleetStat.name}::${carType.name}::all"
                                                            id="${fleetStat.name}::${carType.name}::all"
                                                            class="${fleetStat.name}::${carType.name}"
                                                            onclick="handleCheckBoxClick( this )" />
                                                All
                                            </td>

                                            <td class="shieben" width="100px" id="all::${fleetStat.name}::${carType.name}::plannedTime">
                                                ${TimeCalculator.readableTime( carType.stats.allCars.plannedTime.mean )}
                                            </td>
                                            <td class="shieben" width="100px" id="all::${fleetStat.name}::${carType.name}::realDrivingTime">
                                                ${TimeCalculator.readableTime( carType.stats.allCars.realDrivingTime.mean )}
                                            </td>
                                            <td class="shieben" width="100px" id="all::${fleetStat.name}::${carType.name}::realTime">
                                                ${TimeCalculator.readableTime( carType.stats.allCars.realTime.mean )}
                                            </td>
                                            <td class="shieben" width="100px" id="all::${fleetStat.name}::${carType.name}::loadingTime">
                                                ${TimeCalculator.readableTime( carType.stats.allCars.loadingTime.mean )}
                                            </td>

                                            <td class="shieben" width="100px" id="all::${fleetStat.name}::${carType.name}::plannedDistance">
                                                ${carType.stats.allCars.plannedDistance.mean}
                                            </td>
                                            <td class="shieben" width="100px" id="all::${fleetStat.name}::${carType.name}::realDistance">
                                                ${carType.stats.allCars.realDistance.mean}
                                            </td>

                                            <td class="shieben" width="100px" id="all::${fleetStat.name}::${carType.name}::energyLoaded">
                                                ${carType.stats.allCars.energyLoaded.mean}
                                            </td>
                                            <td class="shieben" width="100px" id="all::${fleetStat.name}::${carType.name}::energyDemanded">
                                                ${carType.stats.allCars.energyDemanded.mean}
                                            </td>

                                        </tr>

                                        <tr class="table">

                                            <td width="100px" class="shieben">
                                                <g:checkBox name="${fleetStat.name}::${carType.name}::successful"
                                                            id="${fleetStat.name}::${carType.name}::successful"
                                                            class="${fleetStat.name}::${carType.name}"
                                                            onclick="handleCheckBoxClick( this )" />
                                                Successful
                                            </td>
                                            <td class="shieben" width="100px" id="successful::${fleetStat.name}::${carType.name}::plannedTime">
                                                ${TimeCalculator.readableTime( carType.stats.succeededCars.plannedTime.mean )}
                                            </td>
                                            <td class="shieben" width="100px" id="successful::${fleetStat.name}::${carType.name}::realDrivingTime">
                                                ${TimeCalculator.readableTime( carType.stats.succeededCars.realDrivingTime.mean )}
                                            </td>
                                            <td class="shieben" width="100px" id="successful::${fleetStat.name}::${carType.name}::realTime">
                                                ${TimeCalculator.readableTime( carType.stats.succeededCars.realTime.mean )}
                                            </td>
                                            <td class="shieben" width="100px" id="successful::${fleetStat.name}::${carType.name}::loadingTime">
                                                ${TimeCalculator.readableTime( carType.stats.succeededCars.loadingTime.mean )}
                                            </td>

                                            <td class="shieben" width="100px" id="successful::${fleetStat.name}::${carType.name}::plannedDistance">
                                                ${carType.stats.succeededCars.plannedDistance.mean}
                                            </td>
                                            <td class="shieben" width="100px" id="successful::${fleetStat.name}::${carType.name}::realDistance">
                                                ${carType.stats.succeededCars.realDistance.mean}
                                            </td>

                                            <td class="shieben" class="shieben" width="100px" id="successful::${fleetStat.name}::${carType.name}::energyLoaded">
                                                ${carType.stats.succeededCars.energyLoaded.mean}
                                            </td>
                                            <td class="shieben" width="100px" id="successful::${fleetStat.name}::${carType.name}::energyDemanded">
                                                ${carType.stats.succeededCars.energyDemanded.mean}
                                            </td>

                                        </tr>

                                        <tr class="table">

                                            <td width="100px" class="shieben">
                                                <g:checkBox name="${fleetStat.name}::${carType.name}::failed"
                                                            id="${fleetStat.name}::${carType.name}::failed"
                                                            class="${fleetStat.name}::${carType.name}"
                                                            onclick="handleCheckBoxClick( this )" />
                                                Failed
                                            </td>

                                            <td class="shieben" width="100px" id="failed::${fleetStat.name}::${carType.name}::plannedTime">
                                                ${TimeCalculator.readableTime( carType.stats.failedCars.plannedTime.mean )}
                                            </td>
                                            <td class="shieben" width="100px" id="failed::${fleetStat.name}::${carType.name}::realDrivingTime">
                                                ${TimeCalculator.readableTime( carType.stats.failedCars.realDrivingTime.mean )}
                                            </td>
                                            <td class="shieben" width="100px" id="failed::${fleetStat.name}::${carType.name}::realTime">
                                                ${TimeCalculator.readableTime( carType.stats.failedCars.realTime.mean )}
                                            </td>
                                            <td class="shieben" width="100px" id="failed::${fleetStat.name}::${carType.name}::loadingTime">
                                                ${TimeCalculator.readableTime( carType.stats.failedCars.loadingTime.mean )}
                                            </td>

                                            <td class="shieben" width="100px" id="failed::${fleetStat.name}::${carType.name}::plannedDistance">
                                                ${carType.stats.failedCars.plannedDistance.mean}
                                            </td>
                                            <td class="shieben" width="100px" id="failed::${fleetStat.name}::${carType.name}::realDistance">
                                                ${carType.stats.failedCars.realDistance.mean}
                                            </td>

                                            <td class="shieben" width="100px" id="failed::${fleetStat.name}::${carType.name}::energyLoaded">
                                                ${carType.stats.failedCars.energyLoaded.mean}
                                            </td>
                                            <td class="shieben" width="100px" id="failed::${fleetStat.name}::${carType.name}::energyDemanded">
                                                ${carType.stats.failedCars.energyDemanded.mean}
                                            </td>

                                        </tr>
                                        <tr>
                                            <td>
                                            <div class="rowMiddleWithoutBorder2">
                                                <g:submitButton name="Show Picture" value="Show Picture" id="${fleetStat.name}::${carType.name}"/>
                                            </div>
                                            </td>
                                        </tr>
                               </table>


                        </div>

                    </g:form>
                </g:each>
    </fieldset>
    </g:each>
</div>



</br></br>
</br></br>
</br></br>
<b>Filling Stations</b>

<%-- HUA --%>
<div class="pContainerConfigure">
<g:each in="${stats.groups}" var="groupStat" >
<fieldset>
<legend>
    Group name: ${groupStat.name}

</legend>

<g:each in="${groupStat.stationTypes}" var="stationType" >
<g:form id="allCarTypes2" controller="statistics" action="showStationPicture">

<g:hiddenField name="stationTypeName" value="${stationType.name}" />
<g:hiddenField name="groupName" value="${groupStat.name}" />
<g:hiddenField name="experimentRunResultId" value="${experimentRunResultId}" />

<div class="contentImage">
    <div class="rowUp">
        <div class="leftBoldBig">
            <span class="cartype">StationType: <span class="cartypeBold"> ${stationType.name} </span></span>
<span class="cartype">Successful: <span class="cartypeBold">${stationType.stats.succeededStations.timeInUse.valuez.size()}</span> of <span class="cartypeBold">${stationType.stats.allStations.timeInUse.valuez.size()}</span></span>
<span class="cartype">Failed: <span class="cartypeBold">${stationType.stats.failedStations.timeInUse.valuez.size()}</span> of <span class="cartypeBold">${stationType.stats.allStations.timeInUse.valuez.size()}</span></span>
</div>
</div>


    </br></br>

<table class="table">
    <tr class="table" align="center">
        <td width="100px">
            Success
        </td>


        <td width="100px" class="shiebenTitle">
            <g:checkBox name="${groupStat.name}::${stationType.name}::timeInUse"
                        id="${groupStat.name}::${stationType.name}::timeInUse"
                        class="${groupStat.name}::${stationType.name}"
                        onclick="handleCheckBoxClick(this);" />
            Time in Use Mean
        </td>

        <td width="100px" class="shiebenTitle">
            <g:checkBox name="${groupStat.name}::${stationType.name}::timeInUseSum"
                        id="${groupStat.name}::${stationType.name}::timeInUseSum"
                        class="${groupStat.name}::${stationType.name}"
                        onclick="handleCheckBoxClick(this);" />
            Time in Use Sum
        </td>

        <td width="100px" class="shiebenTitle">
            <g:checkBox name="${groupStat.name}::${stationType.name}::timeLiving"
                        class="${groupStat.name}::${stationType.name}"
                        onclick="handleCheckBoxClick(this);"
                        id="${groupStat.name}::${stationType.name}::timeLiving" />
            Time Living
        </td>
        <td width="100px" class="shiebenTitle">
            <g:checkBox name="${groupStat.name}::${stationType.name}::failedToRoute"
                        class="${groupStat.name}::${stationType.name}"
                        onclick="handleCheckBoxClick(this);"
                        id="${groupStat.name}::${stationType.name}::failedToRoute" />
            Failed to Route
        </td>

    </tr>

    <tr class="table">

        <td width="100px" class="shieben">
            <g:checkBox name="${groupStat.name}::${stationType.name}::all"
                        id="${groupStat.name}::${stationType.name}::all"
                        class="${groupStat.name}::${stationType.name}"
                        onclick="handleCheckBoxClick( this )" />
            All
        </td>

        <td class="shieben" width="100px" id="all::${groupStat.name}::${stationType.name}::timeInUse">
            ${TimeCalculator.readableTime( stationType.stats.allStations.timeInUse.mean )}
        </td>
        <td class="shieben" width="100px" id="all::${groupStat.name}::${stationType.name}::timeInUseSum">
            ${TimeCalculator.readableTime( stationType.stats.allStations.timeInUse.sum )}
        </td>
        <td class="shieben" width="100px" id="all::${groupStat.name}::${stationType.name}::timeLiving">
            ${TimeCalculator.readableTime( stationType.stats.allStations.timeLiving.mean )}
        </td>
        <td class="shieben" width="100px" id="all::${groupStat.name}::${stationType.name}::failedToRoute">
            ${( stationType.stats.allStations.failedToRoute.mean )}
        </td>

    </tr>

    <tr class="table">

        <td width="100px" class="shieben">
            <g:checkBox name="${groupStat.name}::${stationType.name}::successful"
                        id="${groupStat.name}::${stationType.name}::successful"
                        class="${groupStat.name}::${stationType.name}"
                        onclick="handleCheckBoxClick( this )" />
            Successful
        </td>

        <td class="shieben" width="100px" id="successful::${groupStat.name}::${stationType.name}::timeInUse">
            ${TimeCalculator.readableTime( stationType.stats.succeededStations.timeInUse.mean )}
        </td>
        <td class="shieben" width="100px" id="successful::${groupStat.name}::${stationType.name}::timeInUseSum">
            ${TimeCalculator.readableTime( stationType.stats.succeededStations.timeInUse.sum )}
        </td>
        <td class="shieben" width="100px" id="successful::${groupStat.name}::${stationType.name}::timeLiving">
            ${TimeCalculator.readableTime( stationType.stats.succeededStations.timeLiving.mean )}
        </td>
        <td class="shieben" width="100px" id="successful::${groupStat.name}::${stationType.name}::failedToRoute">
            ${( stationType.stats.succeededStations.failedToRoute.mean )}
        </td>

    </tr>

    <tr class="table">

        <td width="100px" class="shieben">
            <g:checkBox name="${groupStat.name}::${stationType.name}::failed"
                        id="${groupStat.name}::${stationType.name}::failed"
                        class="${groupStat.name}::${stationType.name}"
                        onclick="handleCheckBoxClick( this )" />
            Failed
        </td>

        <td class="shieben" width="100px" id="failed::${groupStat.name}::${stationType.name}::timeInUse">
            ${TimeCalculator.readableTime( stationType.stats.failedStations.timeInUse.mean )}
        </td>
        <td class="shieben" width="100px" id="failed::${groupStat.name}::${stationType.name}::timeInUseSum">
            ${TimeCalculator.readableTime( stationType.stats.failedStations.timeInUse.sum )}
        </td>
        <td class="shieben" width="100px" id="failed::${groupStat.name}::${stationType.name}::timeLiving">
            ${TimeCalculator.readableTime( stationType.stats.failedStations.timeLiving.mean )}
        </td>
        <td class="shieben" width="100px" id="failed::${groupStat.name}::${stationType.name}::failedToRoute">
            ${( stationType.stats.failedStations.failedToRoute.mean )}
        </td>

    </tr>
    <tr>
        <td>
            <div class="rowMiddleWithoutBorder2">
                <g:submitButton name="Show Picture" value="Show Picture" id="${groupStat.name}::${stationType.name}"/>
            </div>
        </td>
    </tr>
</table>


</div>

</g:form>
</g:each>
</fieldset>
</g:each>
</div>


</body>
</html>
            <%--</div>--%>

            <%--
            Mean Results of All Cars In Fleet:

            <table border="1">
                <tr>
                    <th>Planned Time</th>
                    <th>Real Driving Time</th>
                    <th>Real Time</th>
                    <th>Loading Time</th>

                    <th>Planned Distance</th>
                    <th>Real Distance</th>

                    <th>Energy Loaded</th>
                    <th>Energy Demanded</th>

                </tr>
                <tr>
                    <td>${TimeCalculator.readableTime( fleetStat.stats.allCars.plannedTime.mean )}</td>
                    <td>${TimeCalculator.readableTime( fleetStat.stats.allCars.realDrivingTime.mean )}</td>
                    <td>${TimeCalculator.readableTime( fleetStat.stats.allCars.realTime.mean )}</td>
                    <td>${TimeCalculator.readableTime( fleetStat.stats.allCars.loadingTime.mean )}</td>

                    <td>${fleetStat.stats.allCars.plannedDistance.mean}</td>
                    <td>${fleetStat.stats.allCars.realDistance.mean}</td>

                    <td>${fleetStat.stats.allCars.energyLoaded.mean}</td>
                    <td>${fleetStat.stats.allCars.energyDemanded.mean}</td>

                </tr>
            </table>

            Mean Results of Successful Cars In Fleet:

            <table border="1">
                <tr>
                    <th>Planned Time</th>
                    <th>Real Driving Time</th>
                    <th>Real Time</th>
                    <th>Loading Time</th>

                    <th>Planned Distance</th>
                    <th>Real Distance</th>

                    <th>Energy Loaded</th>
                    <th>Energy Demanded</th>

                </tr>
                <tr>
                    <td>${TimeCalculator.readableTime( fleetStat.stats.succeededCars.plannedTime.mean )}</td>
                    <td>${TimeCalculator.readableTime( fleetStat.stats.succeededCars.realDrivingTime.mean )}</td>
                    <td>${TimeCalculator.readableTime( fleetStat.stats.succeededCars.realTime.mean )}</td>
                    <td>${TimeCalculator.readableTime( fleetStat.stats.succeededCars.loadingTime.mean )}</td>

                    <td>${fleetStat.stats.succeededCars.plannedDistance.mean}</td>
                    <td>${fleetStat.stats.succeededCars.realDistance.mean}</td>

                    <td>${fleetStat.stats.succeededCars.energyLoaded.mean}</td>
                    <td>${fleetStat.stats.succeededCars.energyDemanded.mean}</td>

                </tr>
            </table>

            Mean Results of Failed Cars In Fleet:

            <table border="1">
                <tr>
                    <th>Planned Time</th>
                    <th>Real Driving Time</th>
                    <th>Real Time</th>
                    <th>Loading Time</th>

                    <th>Planned Distance</th>
                    <th>Real Distance</th>

                    <th>Energy Loaded</th>
                    <th>Energy Demanded</th>

                </tr>
                <tr>
                    <td>${TimeCalculator.readableTime( fleetStat.stats.failedCars.plannedTime.mean )}</td>
                    <td>${TimeCalculator.readableTime( fleetStat.stats.failedCars.realDrivingTime.mean )}</td>
                    <td>${TimeCalculator.readableTime( fleetStat.stats.failedCars.realTime.mean )}</td>
                    <td>${TimeCalculator.readableTime( fleetStat.stats.failedCars.loadingTime.mean )}</td>

                    <td>${fleetStat.stats.failedCars.plannedDistance.mean}</td>
                    <td>${fleetStat.stats.failedCars.realDistance.mean}</td>

                    <td>${fleetStat.stats.failedCars.energyLoaded.mean}</td>
                    <td>${fleetStat.stats.failedCars.energyDemanded.mean}</td>

                </tr>
            </table>
            --%>


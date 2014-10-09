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
</head>

<body>
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

    <g:each in="${stats.fleets}" var="fleetStat" >

        <div style="text-align:left; padding:10px; border:thin solid #808080; margin:15px">

            Fleet Name: ${fleetStat.name} with ${fleetStat.carResults.size()} cars
            <br/>

            Distribution: ${fleetStat.distribution} from ${fleetStat.plannedFromKm} km to ${fleetStat.plannedToKm} km

            <div style="text-align:left; padding:10px; border:thin solid #808080; margin:15px">

                Infos:



                <g:each in="${fleetStat.carTypes}" var="carType" >

                    <div style="text-align:left; padding:10px; border:thin solid #808080; margin:15px">

                        CarType: ${carType.name}
                        <br/>
                        Successful: ${carType.countSuccessful} of ${carType.countAll}
                        <br/>
                        Failed: ${carType.countFails} of ${carType.countAll}

                        <div style="text-align:left; padding:10px; border:thin solid #808080; margin:15px">

                            <table border="1">
                                <tr>
                                    <th width="100px">
                                        Success
                                    </th>
                                    <th width="100px">
                                        <g:checkBox name="${fleetStat.name}::${carType.name}::plannedTime"
                                                    id="${fleetStat.name}::${carType.name}::plannedTime"
                                                    class="${fleetStat.name}::${carType.name}"
                                                    onclick="handleCheckBoxClick(this);" />
                                        Planned Time
                                    </th>
                                    <th width="100px">
                                        <g:checkBox name="${fleetStat.name}::${carType.name}::realDrivingTime"
                                                    class="${fleetStat.name}::${carType.name}"
                                                    onclick="handleCheckBoxClick(this);"
                                                    id="${fleetStat.name}::${carType.name}::realDrivingTime" />
                                        Real Driving Time
                                    </th>
                                    <th width="100px">
                                        <g:checkBox name="${fleetStat.name}::${carType.name}::realTime"
                                                    class="${fleetStat.name}::${carType.name}"
                                                    onclick="handleCheckBoxClick(this);"
                                                    id="${fleetStat.name}::${carType.name}::realTime" />
                                        Real Time
                                    </th>
                                    <th width="100px">
                                        <g:checkBox name="${fleetStat.name}::${carType.name}::loadingTime"
                                                    class="${fleetStat.name}::${carType.name}"
                                                    onclick="handleCheckBoxClick(this);"
                                                    id="${fleetStat.name}::${carType.name}::loadingTime" />
                                        Loading Time
                                    </th>

                                    <th width="100px">
                                        <g:checkBox name="${fleetStat.name}::${carType.name}::plannedDistance"
                                                    class="${fleetStat.name}::${carType.name}"
                                                    onclick="handleCheckBoxClick(this);"
                                                    id="${fleetStat.name}::${carType.name}::plannedDistance" />
                                        Planned Distance
                                    </th>
                                    <th width="100px">
                                        <g:checkBox name="${fleetStat.name}::${carType.name}::realDistance"
                                                    class="${fleetStat.name}::${carType.name}"
                                                    onclick="handleCheckBoxClick(this);"
                                                    id="${fleetStat.name}::${carType.name}::realDistance" />
                                        Real Distance
                                    </th>

                                    <th width="100px">
                                        <g:checkBox name="${fleetStat.name}::${carType.name}::energyLoaded"
                                                    class="${fleetStat.name}::${carType.name}"
                                                    onclick="handleCheckBoxClick(this);"
                                                    id="${fleetStat.name}::${carType.name}::energyLoaded" />
                                        Energy Loaded
                                    </th>
                                    <th width="100px">
                                        <g:checkBox name="${fleetStat.name}::${carType.name}::energyDemanded"
                                                    class="${fleetStat.name}::${carType.name}"
                                                    onclick="handleCheckBoxClick(this);"
                                                    id="${fleetStat.name}::${carType.name}::energyDemanded" />
                                        Energy Demanded
                                    </th>

                                </tr>
                            </table>


                            <table border="1">

                                <tr>
                                    <td width="100px">
                                        <g:checkBox name="${fleetStat.name}::${carType.name}::all"
                                                    id="${fleetStat.name}::${carType.name}::all"
                                                    class="${fleetStat.name}::${carType.name}"
                                                    onclick="handleCheckBoxClick( this )" />
                                        All
                                    </td>

                                    <td width="100px" id="all::${fleetStat.name}::${carType.name}::plannedTime">
                                        ${TimeCalculator.readableTime( carType.stats.allCars.plannedTime.mean )}
                                    </td>
                                    <td width="100px" id="all::${fleetStat.name}::${carType.name}::realDrivingTime">
                                        ${TimeCalculator.readableTime( carType.stats.allCars.realDrivingTime.mean )}
                                    </td>
                                    <td width="100px" id="all::${fleetStat.name}::${carType.name}::realTime">
                                        ${TimeCalculator.readableTime( carType.stats.allCars.realTime.mean )}
                                    </td>
                                    <td width="100px" id="all::${fleetStat.name}::${carType.name}::loadingTime">
                                        ${TimeCalculator.readableTime( carType.stats.allCars.loadingTime.mean )}
                                    </td>

                                    <td width="100px" id="all::${fleetStat.name}::${carType.name}::plannedDistance">
                                        ${carType.stats.allCars.plannedDistance.mean}
                                    </td>
                                    <td width="100px" id="all::${fleetStat.name}::${carType.name}::realDistance">
                                        ${carType.stats.allCars.realDistance.mean}
                                    </td>

                                    <td width="100px" id="all::${fleetStat.name}::${carType.name}::energyLoaded">
                                        ${carType.stats.allCars.energyLoaded.mean}
                                    </td>
                                    <td width="100px" id="all::${fleetStat.name}::${carType.name}::energyDemanded">
                                        ${carType.stats.allCars.energyDemanded.mean}
                                    </td>

                                </tr>
                            </table>


                            <table border="1">

                                <tr>
                                    <td width="100px">
                                        <g:checkBox name="${fleetStat.name}::${carType.name}::successful"
                                                    id="${fleetStat.name}::${carType.name}::successful"
                                                    class="${fleetStat.name}::${carType.name}"
                                                    onclick="handleCheckBoxClick( this )" />
                                        Successful
                                    </td>
                                    <td width="100px" id="successful::${fleetStat.name}::${carType.name}::plannedTime">
                                        ${TimeCalculator.readableTime( carType.stats.succeededCars.plannedTime.mean )}
                                    </td>
                                    <td width="100px" id="successful::${fleetStat.name}::${carType.name}::realDrivingTime">
                                        ${TimeCalculator.readableTime( carType.stats.succeededCars.realDrivingTime.mean )}
                                    </td>
                                    <td width="100px" id="successful::${fleetStat.name}::${carType.name}::realTime">
                                        ${TimeCalculator.readableTime( carType.stats.succeededCars.realTime.mean )}
                                    </td>
                                    <td width="100px" id="successful::${fleetStat.name}::${carType.name}::loadingTime">
                                        ${TimeCalculator.readableTime( carType.stats.succeededCars.loadingTime.mean )}
                                    </td>

                                    <td width="100px" id="successful::${fleetStat.name}::${carType.name}::plannedDistance">
                                        ${carType.stats.succeededCars.plannedDistance.mean}
                                    </td>
                                    <td width="100px" id="successful::${fleetStat.name}::${carType.name}::realDistance">
                                        ${carType.stats.succeededCars.realDistance.mean}
                                    </td>

                                    <td width="100px" id="successful::${fleetStat.name}::${carType.name}::energyLoaded">
                                        ${carType.stats.succeededCars.energyLoaded.mean}
                                    </td>
                                    <td width="100px" id="successful::${fleetStat.name}::${carType.name}::energyDemanded">
                                        ${carType.stats.succeededCars.energyDemanded.mean}
                                    </td>

                                </tr>
                            </table>


                            <table border="1">

                                <tr>
                                    <td width="100px">
                                        <g:checkBox name="${fleetStat.name}::${carType.name}::failed"
                                                    id="${fleetStat.name}::${carType.name}::failed"
                                                    class="${fleetStat.name}::${carType.name}"
                                                    onclick="handleCheckBoxClick( this )" />
                                        Failed
                                    </td>

                                    <td width="100px" id="failed::${fleetStat.name}::${carType.name}::plannedTime">
                                        ${TimeCalculator.readableTime( carType.stats.failedCars.plannedTime.mean )}
                                    </td>
                                    <td width="100px" id="failed::${fleetStat.name}::${carType.name}::realDrivingTime">
                                        ${TimeCalculator.readableTime( carType.stats.failedCars.realDrivingTime.mean )}
                                    </td>
                                    <td width="100px" id="failed::${fleetStat.name}::${carType.name}::realTime">
                                        ${TimeCalculator.readableTime( carType.stats.failedCars.realTime.mean )}
                                    </td>
                                    <td width="100px" id="failed::${fleetStat.name}::${carType.name}::loadingTime">
                                        ${TimeCalculator.readableTime( carType.stats.failedCars.loadingTime.mean )}
                                    </td>

                                    <td width="100px" id="failed::${fleetStat.name}::${carType.name}::plannedDistance">
                                        ${carType.stats.failedCars.plannedDistance.mean}
                                    </td>
                                    <td width="100px" id="failed::${fleetStat.name}::${carType.name}::realDistance">
                                        ${carType.stats.failedCars.realDistance.mean}
                                    </td>

                                    <td width="100px" id="failed::${fleetStat.name}::${carType.name}::energyLoaded">
                                        ${carType.stats.failedCars.energyLoaded.mean}
                                    </td>
                                    <td width="100px" id="failed::${fleetStat.name}::${carType.name}::energyDemanded">
                                        ${carType.stats.failedCars.energyDemanded.mean}
                                    </td>

                                </tr>
                            </table>



                        </div>
                    </div>
                </g:each>



            </div>


            <br/>

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


        </div>

    </g:each>


</body>
</html>
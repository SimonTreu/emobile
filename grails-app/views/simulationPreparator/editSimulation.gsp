<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 07.04.14
  Time: 16:28
--%>



<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Simulation</title>
    <meta name="layout" content="main" />
</head>


<body>

    <div id="configuration" style="width: 60%; float: left; border-right: 1px solid rgb(248, 248, 248);">

        <!-- error field -->
        <div id="errorMessages" style="color: yellow" >
            <g:if test="${cmd?.hasErrors()}">

                ${cmd?.errors?.getFieldError( "count" )?.defaultMessage}
                ${cmd?.errors?.getFieldError( "targets" )?.defaultMessage}

            </g:if>
        </div>


        <!-- simulationName, carTypeCars, routesCount, gasolineStationTypesStations -->

        <table border="0" style="width: 100%" >

            <tr>
                <td>Car-Type</td>
                <td>Count</td>
                <td>Mean Distance [ km ]</td>
                <td>Add Instances</td>
                <td>Linear Distance [ km ]</td>
                <td>Submit</td>
            </tr>

            <g:each in="${carTypeCars}" var="item">

                <g:form action="addCarTypeInstances" >

                    <input type="hidden" name="simulationId" value="${simulationId}" />
                    <input type="hidden" name="carTypeId" value="${item.key.id}" />

                    <tr>
                        <td>${item.key.name}</td>
                        <td>${item.value.count}</td>
                        <td>${item.value.meanDistance}</td>

                        <td><g:textField name="count" style="width: 40px" /></td>
                        <td><g:select name="linearDistance" from="[50,100,150,200,250,300,350,400,450,500]" /></td>

                        <%-- <td><g:textField name="linearDistance" style="width: 40px" /></td> --%>
                        <td><g:submitButton name="Submit" /></td>
                    </tr>
                </g:form>

            </g:each>

        </table>

        <table border="0" style="width: 100%">
            <tr>
                <td>Filling Station Type</td>
                <td>Count</td>
                <td>Delete All</td>
                <td>Add Instances</td>
                <td>Submit</td>
            </tr>

            <g:each in="${gasolineStationTypesStations}" var="item">

                <g:form action="addFillingStationInstances" >
                    <input type="hidden" name="simulationId" value="${simulationId}" />
                    <input type="hidden" name="fillingType" value="${item.key}" />

                    <tr>
                        <td>${message( code: 'de.dfki.gs.domain.gasolinestationtype.' + item.key )} </td>
                        <td>${item.value}</td>
                        <td>
                            <g:actionSubmit value="Delete" action="deleteGasolineStations" />
                        </td>
                        <td><g:textField name="count" style="width: 40px"/></td>
                        <td><g:submitButton name="Submit" /></td>
                    </tr>

                </g:form>

            </g:each>

        </table>

    </div>

    <div id="player" style="width: 40%; position: fixed; top: 100px; right: -20px">
        Execute Experiment

        <g:form action="executeExperiment" controller="simulationExecutor">
            <input type="hidden" name="simulationId" value="${simulationId}" />

            <g:submitButton name="Execute"/>

        </g:form>

    </div>

    <div id="clearer" style="clear: left"></div>


</body>
</html>


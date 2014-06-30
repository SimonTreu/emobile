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
    <title>Simulation</title>
    <meta name="layout" content="main" />
</head>
<body>

<div style="" class="pContainer">
    <div class="dContainer">
        <div class="d1">
            <g:form controller="simulation" action="init">
                <g:select name="selectedSimulationId" from="${availableSimulations}" optionKey="id" optionValue="name" value="${selectedSimulationId}" ></g:select>
                <g:submitButton name="Select" value="Select" ></g:submitButton>
            </g:form>

            <%--
            <p>Create Simulation</p>
            <g:form controller="simulation" action="create">
                <p>Simulation name: </p>
                <g:textField name="simulationName" id="simulationName" value="" />

                <p>create random routes:</p>
                <g:textField name="createRandomRoutes" id="createRandomRoutes" value="" />

                <p>create random stations:</p>
                <g:textField name="createRandomStations" id="createRandomStations" value="" />


                <g:submitButton name="Submit" value="Save" ></g:submitButton>

            </g:form>
            --%>

        </div>
        <div class="d2">
            <%-- if sim is selected, model is populated here --%>
            <g:if test="${selectedSimulation != null}">



                <g:formRemote name="showsim" action="openLayersWithAction" on404="alert('not found!')"
                              url="[  controller: 'mapView', action: 'openLayersWithAction']" >

                    <g:hiddenField name="simulationId" value="${selectedSimulation.id}" />

                    <p>Name: ${selectedSimulation.name} <button type="submit"><i class="icon icon-warning-sign"></i>Edit in Map</button> </p>
                    <p>Simulation has ${simulationRoutes.size()} routes and ${selectedSimulation.gasolineStations.size()} electricity filling stations</p>

                    <%--
                    <ul>
                        <g:each var="route" in="${simulationRoutes}">
                            <li>
                                <p>Car Type: ${route.carType}</p>
                                <p>Initial Persons: ${route.initialPersons}</p>
                                <p>Initial Energy: ${route.initialEnergy}</p>
                                <p>Edges: ${route.track?.edges?.size()}</p>
                            </li>
                        </g:each>
                    </ul>
                    --%>

                </g:formRemote>


                <g:formRemote name="showsim" action="showRouters" on404="alert('not found!')"
                              url="[  controller: 'mapView', action: 'showRouters']" >

                    <g:hiddenField name="simulationId" value="${selectedSimulation.id}" />


                </g:formRemote>

                <%--
                <g:formRemote name="runsim" action="startSimulation" on404="alert('not found!')"
                              url="[  controller: 'simulation', action: 'startSimulation']" >

                    <g:hiddenField name="simulationId" value="${selectedSimulation.id}" />
                    <button type="submit"><i class="icon icon-warning-sign"></i>Start Simulation</button> </p>

                </g:formRemote>
                --%>
                <g:formRemote name="runsim" action="showPlaySimulation" on404="alert('not found!')"
                              url="[  controller: 'mapView', action: 'showPlaySimulation']" >

                    <g:hiddenField name="simulationId" value="${selectedSimulation.id}" />
                    <button type="submit"><i class="icon icon-warning-sign"></i>Start Simulation</button> </p>

                </g:formRemote>
            </g:if>
        </div>
        <div class="d3"></div>

        <div style="clear: both;" class="clear"></div>
    </div>
</div>

</body>
</html>

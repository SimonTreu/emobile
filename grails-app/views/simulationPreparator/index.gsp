<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 07.04.14
  Time: 13:16
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

            <!-- select available simulation -->
            <g:form controller="simulationPreparator" action="editSimulation">
                <g:select name="selectedSimulationId" from="${simulations}" optionKey="id" optionValue="name" value="${selectedSimulationId}" ></g:select>
                <g:submitButton name="Select" value="Select" ></g:submitButton>
            </g:form>

            <!-- create a new simulation -->
            <p>Create Simulation</p>
            <g:form controller="simulationPreparator" action="createSimulation">
                <p>Simulation name: </p>
                <g:textField name="simulationName" id="simulationName" value="" />

                <!-- select cars and count per type -->


                <!-- select route types and how many via targets (from..to) -->


                <!-- select filling stations -->

                <g:submitButton name="Submit" value="Create" ></g:submitButton>

            </g:form>

        </div>

        <div class="d3"></div>

        <div style="clear: both;" class="clear"></div>
    </div>
</div>

</body>
</html>


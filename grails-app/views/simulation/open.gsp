<%--
  Created by IntelliJ IDEA.
  User: anbu02
  Date: 04.07.14
  Time: 15:30
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Simulation open</title>
    <meta name="layout" content="main" />
</head>

<body>
<div class="pContainer">
<g:form controller="simulation" action="open">
    <div class="layoutLeft">
        <div class="contentLeft">
            <div class="rowU">
                <div class="left"><b>All simulations:</b></div>
                <div class="right"></div>
                <div class="clear"></div>
            </div>

            <div class="rowL">
                <div class="left"></div>
                <div class="right"><g:select name="selectedSimulationId" from="${availableSimulations}" optionKey="id" optionValue="name" value="${selectedSimulationId}" ></g:select></div>
                <div class="clear"></div>
            </div>
        </div>
    </div>
</g:form>
</div>
</body>
</html>
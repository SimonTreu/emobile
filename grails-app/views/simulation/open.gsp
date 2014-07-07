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
            <div class="row">
                <g:if test="${availableSimulations!=[]}">
                <g:each in="${availableSimulations}" var="item">
                        <div class="left">Simulation name:</div>
                        <div class="right"> ${item.name}  <span class="detalized"> (1 routes, 2 electricity filling stations)</span></div>
                        <div class="clear"></div>
                </g:each>
                </g:if>
                <g:if test="${availableSimulations==[]}">
                    There is no simulations...
                </g:if>
            </div>



        </div>
    </div>
</g:form>
</div>
</body>
</html>
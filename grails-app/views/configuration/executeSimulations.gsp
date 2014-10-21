<%--
  Created by IntelliJ IDEA.
  User: anbu02
  Date: 15.10.14
  Time: 10:08
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Execute Simulations</title>
    <meta name="layout" content="main" />
</head>

<body>
<div class="pContainerResent">
    <div class="d1">
        <fieldset>
            <legend> Executable Simulations </legend>
            <div class="layoutResent">
                <g:if test="${ configurations.size() > 0}">
                    <table>
                        <tr>
                            <th>Number</th>
                            <th>Description</th>
                            <th>Execute Button</th>
                        </tr>

                    <g:each in="${configurations}" var="conf">
                        <tr>
                            <td>${conf.configurationId} Simulation</td>
                            <td>${conf.fleetInfo} Cars and  ${conf.stationsInfo} Filling Stations</td>
                             <td>
                                <g:form controller="execution" action="executeExperiment">
                                        <div class="layoutButton3">
                                            <span class="layoutButtonM3"></span>
                                            <g:hiddenField name="relativeSearchLimit" value="50" />
                                            <g:hiddenField name="configurationId" value="${conf.configurationId}"/>
                                            <span class="layoutButtonR3">
                                                <g:submitButton name="send" value="EXECUTE"/>
                                            </span>
                                        </div>
                                </g:form>
                            </td>
                        </tr>
                    </g:each>
                </g:if>
                <g:if test="${configurations.size() == null}">
                    <span class="simDesc">There is no available simulations</span>
                </g:if>
            </table>

            </div>
        </fieldset>
    </div>
</div>
</body>
</html>
<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 10.07.14
  Time: 16:01
--%>

<%@ page import="de.dfki.gs.domain.utils.GroupStatus; de.dfki.gs.domain.utils.FleetStatus"  contentType="text/html;charset=UTF-8" %>

<html>

<head>
    <title>Open resent</title>
    <meta name="layout" content="main" />
</head>

<body>
<div class="pContainerResent">
    <div class="d1">
        <fieldset>
            <legend> Open resent </legend>
            <div class="layoutResent">
                <table>
                    <g:if test="${ configurations.size() > 0}">

                                <tr>
                                    <th>Number</th>
                                    <th>Description</th>
                                    <th>Link</th>
                                </tr>

                        <g:each in="${configurations}" var="conf">
                                <tr>
                                    <td>${conf.configurationId} Simulation</td>
                                    <td>${conf.fleetInfo} Cars and  ${conf.stationsInfo} Filling Stations</td>
                                    <td> <g:link uri="/configuration/index?configurationStubId=${conf.configurationId}" url="/configuration/index?configurationStubId=${conf.configurationId}">link</g:link></td>

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
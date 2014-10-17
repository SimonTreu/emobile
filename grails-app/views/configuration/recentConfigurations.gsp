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
                <g:if test="${ configurations.size() > 0}">
                <table>
                    <tr>
                        <th>Number</th>
                        <th>Description</th>
                        <th>Link</th>
                    </tr>

                        <g:each in="${configurations}" var="conf">
                            <tr>
                                <td>${conf.configurationId}</td>
                                <td></td>
                                <td><a href="http://localhost:8080/emobilesim/configuration/index?configurationStubId=${conf.configurationId}">xaxa</a></td>

                            </tr>
                        </g:each>
                    </g:if>
                <g:if test="${configurations.size() == null}">
                    <span class="simDesc">There is no avauble simulations</span>
                </g:if>
                </table>

            </div>
        </fieldset>
    </div>
</div>
</body>
</html>
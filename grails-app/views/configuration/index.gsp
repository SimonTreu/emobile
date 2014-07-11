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

<div class="pContainerBig">

    <div class="d1">
        <fieldset>
            <legend> Configure Simulation </legend>

            <div class="layout">
                <div class="layoutLeft">
                    <div class="contentLeft2">
                        <div class="rowU">
                            <div class="left"><b><g:message code="simulation.index.fleetconfiguration" /></b></div>
                            <div class="right"></div>
                            <div class="clear"></div>
                        </div>

                        <g:if test="${availableFleets != null && availableFleets.size() > 0}">
                            <div class="row">
                                <div class="left4"><g:message code="simulation.index.existentfleet"/></div>
                                <div class="right2">

                                    <g:form controller="configuration" action="addExistentFleetToConfiguration">
                                        <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                                        <g:select name="fleetId" from="${availableFleets}" optionKey="id" optionValue="name" />
                                        <g:submitButton name="add" value="Add Fleet to Simulation" />
                                    </g:form>

                                </div>
                                <div class="clear"></div>
                            </div>
                        </g:if>


                        <g:if test="${addedFleets != null && addedFleets.size() > 0}">
                            <g:each in="${addedFleets}" var="addedFleet">

                                <g:form controller="configuration" action="removeFleetFromConfiguration">
                                <%--<g:message code="simulation.index.addedfleet"/>--%>
                                    <div class="row">
                                        <div class="left2">
                                            ${addedFleet.name} with ${addedFleet.cars.size()} cars
                                        </div>
                                        <div class="right3">

                                            <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                                            <g:hiddenField name="fleetId" value="${addedFleet.id}"/>
                                            <g:submitButton name="removeFleet" value="Remove Fleet From Simulation"/>

                                        </div>
                                        <div class="clear"></div>
                                    </div>
                                </g:form>
                            </g:each>
                        </g:if>

                        <div class="row">
                            <div class="left"></div>
                            <div class="right"></div>
                            <div class="clear"></div>
                        </div>
                        <div class="rowL">


                            <g:form action="createFleetView">
                                <div class="left1"><g:message code="simulation.index.createnewfleet"/></div>
                                <div class="right4">
                                    <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>

                                    <g:submitToRemote class="addButton" url="[action: 'createFleetView']" update="updateMe" name="submit" value="Create" />
                                    <%--<img width="22px"src="${g.resource( dir: '/images', file: 'add.png' )}">--%>
                                </div>
                                <div class="clear"></div>
                            </g:form>


                        </div>
                    </div>
                </div>
                <div class="layoutRight">
                    <div class="contentLeft1">
                        <div class="rowU">
                            <div class="leftbig"><b><g:message code="simulation.index.fillingconfiguration"/></b></div>
                        </div>

                        <g:if test="${availableFillingStationGroups != null && availableFillingStationGroups.size() > 0}">
                            <div class="row">
                                <div class="left4"><g:message code="simulation.index.selectgroup"/></div>
                                <div class="right2">
                                    <g:form controller="configuration" action="addExistentGroupToConfiguration">
                                        <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                                        <g:select name="groupId" from="${availableFillingStationGroups}" optionKey="id" optionValue="name" />
                                        <g:submitButton name="add" value="Add Group to Simulation" />
                                    </g:form>
                                </div>
                                <div class="clear"></div>
                            </div>
                        </g:if>

                        <g:if test="${addedFillingStationGroups != null && addedFillingStationGroups.size() > 0}">
                            <g:each in="${addedFillingStationGroups}" var="addedGroup">

                                <g:form controller="configuration" action="removeGroupFromConfiguration">

                                <%--<g:message code="simulation.index.addedfleet"/>--%>
                                    <div class="row">
                                        <div class="left2">
                                            ${addedGroup.name} with ${addedGroup.fillingStations.size()} Filling Stations
                                        </div>
                                        <div class="right3">

                                            <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                                            <g:hiddenField name="groupId" value="${addedGroup.id}"/>
                                            <g:submitButton name="removeGroup" value="Remove Group From Simulation"/>

                                        </div>
                                        <div class="clear"></div>
                                    </div>
                                </g:form>
                            </g:each>
                        </g:if>


                        <div class="row">
                            <div class="left"></div>
                            <div class="right"></div>
                            <div class="clear"></div>
                        </div>
                        <div class="rowL">


                            <g:form action="createGroupView">
                                <div class="left1"><g:message code="simulation.index.createnewgroup"/></div>
                                <div class="right4">
                                    <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>

                                    <g:submitToRemote class="addButton" url="[action: 'createGroupView']" update="updateMe" name="submit" value="Create" />
                                    <%--<img width="22px"src="${g.resource( dir: '/images', file: 'add.png' )}">--%>
                                </div>
                                <div class="clear"></div>
                            </g:form>


                        </div>
                    </div>
                </div>
                <div class="layoutImage">
                    <div class="contentRight">
                        <img width="30px"src="${g.resource( dir: '/images', file: 'weather.png' )}"><br><br>
                        <img width="30px"src="${g.resource( dir: '/images', file: 'settings.png' )}"><br><br><br><br>
                        <img width="30px"src="${g.resource( dir: '/images', file: 'car.png' )}"><br>
                        <img width="44px"src="${g.resource( dir: '/images', file: 'station.png' )}">
                    </div>
                </div>

            </div>
            <br><br><br>
            <div class="layoutButton">
                <span class="layoutButtonL"><g:submitButton name="send" value="CANCEL"/></span>
                <span class="layoutButtonM"></span>
                <span class="layoutButtonR"><g:submitButton name="send" value="SAVE"/></span>
            </div>


        </fieldset>
    </div>


    <div id="updateMe"></div>

 </div>

</body>
</html>

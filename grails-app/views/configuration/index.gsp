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

                    <g:if test="${fillingStationGroups != null && fillingStationGroups.size() > 0}">
                        <div class="row">
                            <div class="left"><g:message code="simulation.index.selectgroup"/></div>
                            <div class="right">
                                <g:select name="fillingStationGroup" from="${fillingStationGroups}" optionKey="id" optionValue="name" />
                            </div>
                            <div class="clear"></div>
                        </div>
                    </g:if>


                    <div class="row">
                        <div class="left"></div>
                        <div class="right"></div>
                        <div class="clear"></div>
                    </div>
                    <div class="rowL">
                        <div class="left"><g:message code="simulation.index.createnewgroup"/></div>
                        <div class="right"><a class="addButton" href="#join_form1" id="join_pop1"><img width="22px"src="${g.resource( dir: '/images', file: 'add.png' )}"><span class="addButtonText"> add</span></a></div>
                        <div class="clear"></div>
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
            <span class="layoutButtonL">
                <g:form controller="configuration" action="removeStubConfiguration">

                    <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                    <g:submitButton name="cancel" value="Cancel"/>

                </g:form>
            </span>
            <span class="layoutButtonM"></span>
            <span class="layoutButtonR">
                <g:form controller="configuration" action="saveFinishedConfiguration">

                    <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                    <g:submitButton name="save" value="Save"/>

                </g:form>
            </span>
        </div>


    </fieldset>
</div>


<div id="updateMe"></div>

</div>

</body>
</html>

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
    <meta name="layout" content="mainConfiguration" />

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
            <div class="left"><b><g:message code="simulation.index.fleetconfiguration"/></b></div>
            <div class="right"></div>
            <div class="clear"></div>
        </div>

        <div class="rowSpace">
            <div class="clear"></div>
        </div>

        <div class="rowGroup">

            <div class="rowBrightGrey">

                <div class="leftLongBold">
                    Select Fleets for Simulation
                </div>
                <div class="right1">
                </div>
                <div class="clear"></div>
            </div>

            <g:if test="${availableFleets != null && availableFleets.size() > 0}">
                <div class="row1">
                    <div class="left4"><g:message code="simulation.index.existentfleet"/></div>
                    <div class="right2">

                        <g:form controller="configuration" action="addExistentFleetToConfiguration">
                            <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                            <g:select name="fleetId" from="${availableFleets}" optionKey="id" optionValue="${{it.name+' ('+it.cars?.size()+' Cars)'}}" />
                            <g:submitButton name="add" value="Add Fleet to Simulation" />
                        </g:form>

                    </div>
                    <div class="clear"></div>
                </div>
            </g:if>

            <div class="row2">
                <g:form action="createFleetView">
                    <div class="left4">Not enough Fleets?</div>
                    <div class="right2">
                        <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>

                        <g:submitToRemote class="addButton"
                                          url="[action: 'createFleetView']"
                                          update="updateMe"
                                          name="submit"
                                          value="Create New Fleet" />
                        <%--<img width="22px"src="${g.resource( dir: '/images', file: 'add.png' )}">--%>
                    </div>
                    <div class="clear"></div>
                </g:form>


            </div>

        </div>

        <div class="rowSpace">
            <div class="clear"></div>
        </div>



        <div class="rowGroup">

            <div class="rowBrightGrey">

                <div class="leftLongBold">
                    Collected Fleets for Simulation
                </div>
            </div>

            <g:if test="${addedFleets != null && addedFleets.size() > 0}">
                <g:each in="${addedFleets}" var="addedFleet">


                <%--<g:message code="simulation.index.addedfleet"/>--%>
                    <div class="row1">
                        <g:if test="${addedFleet.routesConfigured == true}">
                            <div class="left5">
                                ${addedFleet.name} ( ${addedFleet.cars.size()} cars )
                                <br/>All Routes are configured
                            </div>
                        </g:if>
                        <g:else>
                            <div class="left5">
                                ${addedFleet.name} ( ${addedFleet.cars.size()} cars )
                                <br/>Routes have to be configured
                            </div>
                        </g:else>

                        <div class="right2">

                            <g:form controller="configuration" action="removeFleetFromConfiguration">

                                <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                                <g:hiddenField name="fleetId" value="${addedFleet.id}"/>
                                <g:submitButton name="removeFleet" value="Unselect"/>

                            </g:form>

                        </div>

                        <div class="right2">

                            <g:if test="${addedFleet.routesConfigured == true}">

                                Show Routes

                            </g:if>
                            <g:else>
                                <g:form action="createRouteSelectorView">

                                    <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                                    <g:hiddenField name="fleetId" value="${addedFleet.id}"/>
                                    <g:submitToRemote class="addButton"
                                                      url="[action: 'createRouteSelectorView']"
                                                      update="updateMe"
                                                      name="submit"
                                                      value="Configure Routes" />

                                </g:form>
                            </g:else>

                        </div>


                        <div class="clear"></div>


                    </div>


                </g:each>

            </g:if>

            <div class="row2">
                <div class="left"></div>
                <div class="right"></div>
                <div class="clear"></div>
            </div>


        </div>

        <div class="rowSpace">
            <div class="clear"></div>
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
    <span class="layoutButtonL">
        <span class="addButtonCancel"><g:link controller="sim" action="">CANCEL</g:link></span>
        <%--<g:submitToRemote class="addButton" url="[action: '/front/startSimulation']" update="sim" name="submit" value="CANCEL" />--%>
    </span>
    <span class="layoutButtonM"></span>
    <span class="layoutButtonR">
        <span class="addButtonCancel"><g:link controller="sim" action="">SAVE</g:link></span>
        <%--<g:submitButton name="sim" value="SAVE"/>--%>
    </span>
</div>


</fieldset>
</div>


<div id="updateMe"></div>

</div>

</body>
</html>
<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 18.09.13
  Time: 18:11
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="de.dfki.gs.domain.utils.GroupStatus; de.dfki.gs.domain.utils.FleetStatus"  contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><g:message code="configuration.index.newsimulation"/></title>
    <meta name="layout" content="mainConfiguration" />
    <g:javascript library="jquery-1.9.0" />

    <g:javascript src="application.js" />

    <g:javascript src="ol/OpenLayers.js" />
    <script type="text/javascript" src="http://openstreetmap.org/openlayers/OpenStreetMap.js"></script>

    <script src="http://maps.google.com/maps/api/js?v=3&amp;sensor=false"></script>


</head>
<body>
<div class="pContainerConfigure">
<fieldset>
<legend> <g:message code="configuration.index.configuresimulation"/> </legend>
<div class="layout">
<div class="layoutLeft">
    <div class="contentLeftBigConfiguration">
        <div class="rowUp">
            <div class="leftBold"><g:message code="simulation.index.fleetconfiguration"/></div>
            <div class="right0PX"></div>
            <div class="clear"></div>
        </div>

        <div class="rowSpace">
            <div class="clear"></div>
        </div>

        <div class="rowGroup">
            <div class="rowBrightGrey">
                <div class="leftConfigurationLong">
                    <g:message code="configuration.index.selectfleets"/>
                </div>
                <div class="right0PX">
                </div>
                <div class="clear"></div>
            </div>

            <g:if test="${availableFleets != null && availableFleets.size() > 0}">
                <div class="rowMiddleWithoutBorder">
                    <div class="leftConfiguration"><g:message code="simulation.index.existentfleet"/></div>
                    <div class="rightOnlyButton">
                        <g:form controller="configuration" action="addExistentFleetToConfiguration">
                            <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                            <g:select name="fleetId" from="${availableFleets}" optionKey="id" optionValue="${{it.name+' ('+it.cars?.size()+' Cars)'}}" />
                            <g:submitButton name="add" value="Add Fleet to Simulation" />
                        </g:form>
                    </div>
                    <div class="clear"></div>
                </div>
            </g:if>

            <div class="rowMiddleWithoutBorder2">
                <g:form action="createFleetView">
                    <div class="leftCarTypes">Not enough Fleets?</div>
                    <div class="rightOnlyButton">
                        <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                        <g:submitToRemote class="addButton"
                                          url="[action: 'createFleetView']"
                                          update="updateMe"
                                          name="submit"
                                          value="Create New Fleet" />
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
                <div class="leftConfigurationLong">
                    <g:message code="configuration.index.collectfleets"/>
                </div>
            </div>
            <g:if test="${addedFleets != null && addedFleets.size() > 0}">
                <g:each in="${addedFleets}" var="addedFleet">
                <%--<g:message code="simulation.index.addedfleet"/>--%>
                    <div class="rowMiddleWithoutBorder">
                        <g:if test="${addedFleet.fleetStatus == FleetStatus.CONFIGURED}">
                            <div class="leftCollectFleets">
                                ${addedFleet.name} ( ${addedFleet.cars.size()} cars ) <span class="littleText"><g:message code="configuration.index.allroutes"/></span>
                            </div>
                        </g:if>
                        <g:if test="${addedFleet.fleetStatus == FleetStatus.SCHEDULED_FOR_CONFIGURING}">
                            <div class="leftCollectFleets">
                                ${addedFleet.name}  ( ${addedFleet.cars.size()} cars ) <span class="littleText"><g:message code="configuration.index.scheduleroute"/></span>
                            </div>
                        </g:if>

                        <g:if test="${addedFleet.fleetStatus == FleetStatus.NOT_CONFIGURED}">
                            <div class="leftCollectFleets">
                                ${addedFleet.name}  ( ${addedFleet.cars.size()} cars ) <span class="littleText"><g:message code="configuration.index.routesconfigured"/></span>
                            </div>
                        </g:if>
                        <div class="right65PX">
                            <g:form controller="configuration" action="removeFleetFromConfiguration">
                                <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                                <g:hiddenField name="fleetId" value="${addedFleet.id}"/>
                                <g:submitButton name="removeFleet" value="Unselect"/>
                            </g:form>
                        </div>
                        <div class="right100PX">
                            <g:if test="${addedFleet.fleetStatus == FleetStatus.CONFIGURED}">
                                <g:form action="showFleetRoutesOnMap">

                                    <g:hiddenField name="configurationStubId" value="${configurationStubId}" />

                                    <g:submitToRemote class="addButton"
                                                      url="[action: 'showFleetRoutesOnMap']"
                                                      update="updateMe"
                                                      name="showRoutes"
                                                      value="Show Routes" />


                                </g:form>

                            </g:if>
                            <g:if test="${addedFleet.fleetStatus == FleetStatus.SCHEDULED_FOR_CONFIGURING}">
                                <g:message code="configuration.index.pleasewait"/>
                            </g:if>
                            <g:if test="${addedFleet.fleetStatus == FleetStatus.NOT_CONFIGURED}">
                                <g:form action="createRouteSelectorView">
                                    <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                                    <g:hiddenField name="fleetId" value="${addedFleet.id}"/>
                                    <g:submitToRemote class="addButton"
                                                      url="[action: 'createRouteSelectorView']"
                                                      update="updateMe"
                                                      name="submit"
                                                      value="Configure Routes" />
                                </g:form>
                            </g:if>
                        </div>
                        <div class="clear"></div>
                    </div>
                </g:each>
            </g:if>

            <div class="rowMiddleWithoutBorder2">
                <div class="left0PX"></div>
                <div class="right0PX"></div>
                <div class="clear"></div>
            </div>
        </div>
        <div class="rowSpace">
            <div class="clear"></div>
        </div>
    </div>
</div>

<div class="layoutRight">
    <div class="contentRightBigConfiguration">
        <div class="rowUp">
            <div class="leftbig">
                <g:message code="simulation.index.fillingconfiguration"/>
            </div>
            <div class="right0PX"></div>
            <div class="clear"></div>
        </div>

        <div class="rowSpace">
            <div class="clear"></div>
        </div>

        <div class="rowGroup">
            <div class="rowBrightGrey">
                <div class="leftConfigurationLong">
                    <g:message code="configuration.index.selectfillingstation"/>
                </div>
                <div class="right0PX">
                </div>
                <div class="clear"></div>
            </div>

            <g:if test="${availableFillingStationGroups != null && availableFillingStationGroups.size() > 0}">
                <div class="rowMiddleWithoutBorder">
                    <div class="leftConfiguration"><g:message code="simulation.index.selectgroup"/></div>
                    <div class="rightOnlyButton">
                        <g:form controller="configuration" action="addExistentGroupToConfiguration">
                            <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                            <g:select name="groupId" from="${availableFillingStationGroups}" optionKey="id" optionValue="${{it.name+' ('+it.fillingStations?.size()+' Stations)'}}" />
                            <g:submitButton name="add" value="Add Group to Simulation" />
                        </g:form>
                    </div>
                    <div class="clear"></div>
                </div>
            </g:if>
            <div class="rowMiddleWithoutBorder2">
                <g:form action="createGroupView">
                    <div class="leftCarTypes"><g:message code="simulation.index.createnewgroup"/></div>
                    <div class="rightOnlyButton">
                        <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                        <g:submitToRemote class="addButton" url="[action: 'createGroupView']" update="updateMe" name="submit" value="Create New Group" />
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
                <div class="leftConfigurationLong">
                    <g:message code="configuration.index.collectedfillingstation"/>
                </div>
                <div class="right0PX">
                </div>
                <div class="clear"></div>

            </div>

            <g:if test="${addedGroups != null && addedGroups.size() > 0}">
                <g:each in="${addedGroups}" var="addedGroup">

                    <g:form controller="configuration" action="removeGroupFromConfiguration">
                    <%--<g:message code="simulation.index.addedfleet"/>--%>
                        <div class="rowMiddleWithoutBorder">
                        <%--<div class="leftCollectFleets">
                            ${addedGroup.name} with ${addedGroup.fillingStations.size()*2} Filling Stations
                        </div>--%>

                            <g:if test="${addedGroup.groupStatus == GroupStatus.CONFIGURED}">
                                <div class="leftCollectFleets">
                                    ${addedGroup.name} (${addedGroup.fillingStations.size()}) <span class="littleText"><g:message code="configuration.index.allstations"/></span>
                                </div>
                            </g:if>
                            <g:if test="${addedGroup.groupStatus == GroupStatus.SCHEDULED_FOR_CONFIGURING}">
                                <div class="leftCollectFleets">
                                    ${addedGroup.name} ( ${addedGroup.fillingStations.size()} ) <span class="littleText"><g:message code="configuration.index.schedulestation"/></span>
                                </div>
                            </g:if>

                            <g:if test="${addedGroup.groupStatus == GroupStatus.NOT_CONFIGURED}">
                                <div class="leftCollectFleets">
                                    ${addedGroup.name} ( ${addedGroup.fillingStations.size()} )  <span class="littleText"> <g:message code="configuration.index.stationsconfigured"/></span>
                                </div>
                            </g:if>

                            <div class="right65PX">
                                <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                                <g:hiddenField name="groupId" value="${addedGroup.id}"/>
                                <g:submitButton name="removeGroup" value="Unselect"/>
                            </div>

                            <div class="right100PX">
                                <g:if test="${addedGroup.groupStatus == GroupStatus.CONFIGURED}">
                                    <g:form action="showGroupStationsOnMap">
                                        <g:hiddenField name="configurationStubId" value="${configurationStubId}" />
                                        <g:submitToRemote class="addButton"
                                                          url="[action: 'showGroupStationsOnMap']"
                                                          update="updateMe"
                                                          name="showGroups"
                                                          value="Show Stations" />

                                    </g:form>
                                </g:if>
                                <g:if test="${addedGroup.groupStatus == GroupStatus.SCHEDULED_FOR_CONFIGURING}">
                                    <g:message code="configuration.index.pleasewait"/>
                                </g:if>
                                <g:if test="${addedGroup.groupStatus == GroupStatus.NOT_CONFIGURED}">
                                    <g:form action="createGroupSelectorView">
                                        <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                                        <g:hiddenField name="groupId" value="${addedGroup.id}"/>
                                        <g:submitToRemote class="addButton"
                                                          url="[action: 'createGroupSelectorView']"
                                                          update="updateMe"
                                                          name="submit"
                                                          value="Configure Stations" />
                                    </g:form>
                                </g:if>
                            </div>
                            <div class="clear"></div>
                        </div>
                    </g:form>

                </g:each>
            </g:if>
            <div class="rowMiddleWithoutBorder2">
                <div class="left0PX"></div>
                <div class="right0PX"></div>
                <div class="clear"></div>
            </div>
        </div>

        <div class="rowSpace">
            <div class="clear"></div>
        </div>


    </div>
</div>

<div class="layoutImage">
    <div class="contentRight">
        <img width="30px"src="${g.resource( dir: '/images', file: 'weather.png' )}"><br><br>
        <img width="30px"src="${g.resource( dir: '/images', file: 'settings.png' )}"><br><br><br><br>
        <img width="30px"src="${g.resource( dir: '/images', file: 'car.png' )}"><br>
        <img width="44px"src="${g.resource( dir: '/images', file: 'station.png' )}"><br><br><br>
        <a href="#descriptionButton"><img  width="22px"src="${g.resource( dir: '/images', file: 'plus.png' )}"></a>

    </div>
</div>

</div>

<g:form action="saveFinishedConfiguration">
    <div id="descriptionButton" class="descriptionSim">
        <span class="simDesc">Please name your Simulation:</span>
        <g:textField name="configurationName" value="${configurationName}" />
        <span class="simDesc">Please describe your Simulation:</span>
        <g:textArea name="configurationDescription" value="${configurationDescription}" rows="3" cols="20" />
    </div>
    <br><br><br>

    <div class="layoutButton">
        <span class="layoutButtonL">
            <span class="addButtonCancel"><g:link controller="sim" action="">CANCEL</g:link></span>
            <%--<g:submitToRemote class="addButton" url="[action: '/front/startSimulation']" update="sim" name="submit" value="CANCEL" />--%>
        </span>
                    <g:if test="${(savedGroups >= 1 && savedFleets >= 1) ||
                                  ( configuredGroups >= 1 &&  savedFleets >= 1 ) ||
                                  (configuredFleets >= 1 && savedGroups >= 1)
                    }">
                            <span class="layoutButtonM"></span>
                            <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                            <span class="layoutButtonR"><g:submitButton name="send" value="SAVE"/></span>
                    </g:if>
    </div>
</g:form>

<g:form controller="execution" action="executeExperiment">
    <g:if test="${configuredGroups >= 1 && configuredFleets >= 1}">
                    <div class="layoutButton">
                        <span class="layoutButtonM"></span>
                        <g:hiddenField name="relativeSearchLimit" value="50" />
                        <g:hiddenField name="configurationId" value="${configurationStubId}"/>
                        <span class="layoutButtonR">
                            <g:submitButton name="send" value="EXECUTE"/>
                        </span>
                    </div>
    </g:if>
</g:form>


</fieldset>
<div id="updateMe">

</div>
</div>

</body>
</html>

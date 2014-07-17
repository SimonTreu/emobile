<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 18.09.13
  Time: 18:11
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="de.dfki.gs.domain.utils.FleetStatus" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><g:message code="configuration.index.newsimulation"/></title>
    <meta name="layout" content="mainConfiguration" />

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
                                    Select Fleets for Simulation
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
                                    Collected Fleets for Simulation
                                </div>
                            </div>
                            <g:if test="${addedFleets != null && addedFleets.size() > 0}">
                                <g:each in="${addedFleets}" var="addedFleet">
                                <%--<g:message code="simulation.index.addedfleet"/>--%>
                                    <div class="rowMiddleWithoutBorder">
                                        <g:if test="${addedFleet.fleetStatus == FleetStatus.CONFIGURED}">
                                            <div class="leftCollectFleets">
                                                ${addedFleet.name} ( ${addedFleet.cars.size()} cars ) <span class="littleText">All Routes are configured</span>
                                            </div>
                                        </g:if>
                                        <g:if test="${addedFleet.fleetStatus == FleetStatus.SCHEDULED_FOR_CONFIGURING}">
                                            <div class="leftCollectFleets">
                                                ${addedFleet.name}  ( ${addedFleet.cars.size()} cars ) <span class="littleText"> Routes scheduled to Configure</span>
                                            </div>
                                        </g:if>

                                        <g:if test="${addedFleet.fleetStatus == FleetStatus.NOT_CONFIGURED}">
                                            <div class="leftCollectFleets">
                                                ${addedFleet.name}  ( ${addedFleet.cars.size()} cars ) <span class="littleText">  Routes have to be configured </span>
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
                                                    <g:submitButton name="showRoutes" value="Show Routes"/>
                                                </g:if>
                                                <g:if test="${addedFleet.fleetStatus == FleetStatus.SCHEDULED_FOR_CONFIGURING}">
                                                    Please wait for Configuring Routes
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
                    <div class="contentLeftBigConfiguration">
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
                                    Select Filling Stations for Simulation
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
                                            <g:select name="groupId" from="${availableFillingStationGroups}" optionKey="id" optionValue="name" />
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
                                            <g:submitToRemote class="addButton" url="[action: 'createGroupView']" update="updateMe" name="submit" value="Create" />
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
                                        Collected Filling Stations for Simulation
                                    </div>
                                    <div class="right0PX">
                                    </div>
                                    <div class="clear"></div>

                                </div>

                                <g:if test="${addedFillingStationGroups != null && addedFillingStationGroups.size() > 0}">
                                    <g:each in="${addedFillingStationGroups}" var="addedGroup">
                                        <g:form controller="configuration" action="removeGroupFromConfiguration">
                                                <%--<g:message code="simulation.index.addedfleet"/>--%>
                                        <div class="rowMiddleWithoutBorder2">
                                            <div class="leftCollectFleets">
                                                ${addedGroup.name} with ${addedGroup.fillingStations.size()} Filling Stations
                                            </div>
                                            <div class="rightOnlyBigButton">
                                                <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                                                <g:hiddenField name="groupId" value="${addedGroup.id}"/>
                                                <g:submitButton name="removeGroup" value="Remove Group From Simulation"/>
                                            </div>
                                            <div class="clear"></div>
                                        </div>
                                        </g:form>
                                    </g:each>
                                </g:if>
                            </div>
                            <div class="rowMiddleWithoutBorder2">
                                <div class="left0PX"></div>
                                <div class="right0PX"></div>
                                <div class="clear"></div>
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
                    <g:form action="saveFinishedConfiguration">
                        <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                        <span class="layoutButtonR"><g:submitButton name="send" value="SAVE"/></span>
                    </g:form>
                </div>
        </fieldset>
        <div id="updateMe"></div>
    </div>

</body>
</html>
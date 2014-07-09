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

<div class="pContainer">

<div class="d1">
    <fieldset>
        <legend> Configure Simulation </legend>

        <div class="layout">
            <div class="layoutLeft">
                <div class="contentLeft">
                    <div class="rowU">
                        <div class="left"><b><g:message code="simulation.index.fleetconfiguration" /></b></div>
                        <div class="right"></div>
                        <div class="clear"></div>
                    </div>

                    <g:if test="${availableFleets != null && availableFleets.size() > 0}">
                        <div class="row">
                            <div class="left"><g:message code="simulation.index.existentfleet"/></div>
                            <div class="right">

                                <g:form controller="configuration" action="addExistentFleetToConfiguration">
                                    <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                                    <g:select name="fleetId" from="${availableFleets}" optionKey="id" optionValue="name" />
                                    <g:submitButton name="add" value="Add Fleet to Simulation" />
                                </g:form>



                            </div>
                            <div class="clear"></div>
                        </div>
                    </g:if>

                    <g:message code="simulation.index.addedfleet"/>
                    <g:if test="${addedFleets != null && addedFleets.size() > 0}">

                        <g:each in="${addedFleets}" var="addedFleet">

                            <g:form controller="configuration" action="removeFleetFromConfiguration">

                                <div class="row">
                                    <div class="left">
                                        ${addedFleet.name} with ${addedFleet.cars.size()} cars
                                    </div>
                                    <div class="right">

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
                            <div class="right">
                                <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>

                                <g:submitToRemote class="addButton" url="[action: 'createFleetView']" update="updateMe" name="submit" value="Create new Fleet" />
                                <img width="22px"src="${g.resource( dir: '/images', file: 'add.png' )}">
                            </div>
                            <div class="clear"></div>
                        </g:form>


                    </div>
                </div>
            </div>
            <div class="layoutRight">
                <div class="contentLeft">
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
            <span class="layoutButtonL"><g:submitButton name="send" value="CANCEL"/></span>
            <span class="layoutButtonM"></span>
            <span class="layoutButtonR"><g:submitButton name="send" value="SAVE"/></span>
        </div>


    </fieldset>
</div>


<div id="updateMe"></div>

<!-- Add fleet form -->

<!--END  Add fleet form -->


<!-- Add electric station form -->
<a href="#x" class="overlay" id="join_form1"></a>
<div class="popup">
    <div class="layout">

        <div class="layoutLeft" id="cell">
            <div class="contentLeft">
                <div class="layoutCellU">
                    <div class="leftbig"><b><g:message code="simulation.index.selectfillingstations"/></b></div>
                </div>
                <div class="layoutCell">

                    <TABLE id="dataTable1"  border="0">
                        <TR class="cars">
                            <TD><INPUT type="checkbox" name="chk"/></TD>
                            <TD align="">
                                <%--
                                <g:select name="electricStations" from="${1..100}" />
                                --%>
                            </TD>
                            <td align=""> &nbsp;&nbsp; <g:message code="simulation.index.stationoftype"/> &nbsp;&nbsp;</td>
                            <%--
                            <TD align="">
                                <g:select name="${electricStations}" from="${electricStations}"/>
                            </TD>
                            --%>
                        </TR>
                    </TABLE>

                </div>

                <div class="layoutCellL">
                    <span class="leftR">
                        <a class="addButton" onclick="deleteRow('dataTable1')"><img width="22px" src="${g.resource( dir: '/images', file: 'delete.png' )}"><span class="addButtonText"> <g:message code="simulation.index.deletestationgroup"/></span></a>
                    </span>
                    <div class="right">
                        <a class="addButton" onclick="addRow('dataTable1')"><img width="22px" src="${g.resource( dir: '/images', file: 'add.png' )}"><span class="addButtonText"><g:message code="simulation.index.addfurtherstations"/></span></a>
                    </div>
                </div>
            </div>
        </div>

        <div class="layoutRight">
            <div id="tabs-container1">
                <ul class="tabs-menu1">
                    <li class="current"><a href="#tab-10"><g:message code="simulation.index.privatestations"/></a></li>
                    <li><a href="#tab-20"><g:message code="simulation.index.publicstations"/></a></li>
                    <li><a href="#tab-30"><g:message code="simulation.index.both"/></a></li>
                </ul>
                <div class="tab">
                    <div id="tab-10" class="tab-content1">
                        <p>Lorlis.</p>
                    </div>
                    <div id="tab-20" class="tab-content1">
                        <p>
                            <input type="file">
                        </p>

                    </div>
                    <div id="tab-30" class="tab-content1">
                        <p> Proin bibendum consectetur elit, ha. </p>
                    </div>

                </div>
            </div>

        </div>


    </div>
    <br><br><br>

    <div class="layoutButton">
        <span class="layoutButtonL"><g:submitButton name="send" value="CANCEL"/></span>
        <span class="layoutButtonM"></span>
        <span class="layoutButtonR"><g:submitButton name="send" value="SAVE"/></span>
    </div>
    <a class="close" href="#close"></a>
</div>
<!--END Add electric station form -->



</div>

</body>
</html>

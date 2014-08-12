
<div id="openModal" class="modalDialog">
    <div>
        <g:form controller="configuration" action="createFleet">
        <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
            <div class="contentModalWindowFleet">
                <div class="rowUp">
                    <div class="leftbig"><b><g:message code="simulation.index.selectcarstype"/></b></div>
                </div>
                <div class="rowMiddle">
                    <div class="leftModal">
                        <g:message code="templates.configuration.fleet._createFleet.setnamegroup"/>
                     </div>
                    <div class="rightOnlyButton">
                       <g:textField name="nameForFleet" value="${generatedName}"/>
                    </div>
                </div>
                <div class="rowMiddle">
                    <div class="left140PX">
                       <g:select name="carCount" from="${1..100}" /> &nbsp;&nbsp;
                       <g:message code="simulation.index.carstype"/>
                    </div>
                    <div class="right235PX">
                        <g:select name="carTypeId" from="${availableCarTypes}" optionKey="id" optionValue="name" />
                        &nbsp;&nbsp;
                                        <%--
                                        <g:hiddenField name="availableCarTypes" value="${availableCarTypes}"/>
                                        <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                                        --%>
                                        <g:hiddenField name="fleetStubId" value="${fleetStubId}"/>
                                        <g:submitToRemote class="addButton"
                                                          url="[action: 'updateFleetOfConfiguration']"
                                                          update="updateCar"
                                                          name="submit"
                                                          value="Add to Fleet" />
                     </div>
                       <div class="clear"></div>
                    </div>
                    <div class="rowUnknown" id="updateCar"></div>
                    <div class="rowDown">
                        <div class="left0PX">
                             <%--<g:submitButton name="createCar" value="Cancel"/>--%>
                            <input type="button" value="Cancel" onclick="window.location.href=window.location.href"/>
                        </div>
                        <div class="right80PX">
                             <g:hiddenField name="fleetStubId" value="${fleetStubId}"/>
                            <g:submitButton name="createFleet" value="Create Fleet"/>
                        </div>
                        <div class="clear"></div>
                    </div>
            </div>
        </g:form><a class="close" title="Close" href=""></a>
    </div>
</div>
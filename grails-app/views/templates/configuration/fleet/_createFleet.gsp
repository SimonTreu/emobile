
<div id="openModal" class="modalDialog">
    <div class="layout">
        <g:form controller="configuration" action="createFleet">
         <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
            <div class="contentLeft1">
                                <div class="rowU">
                                    <div class="leftbig"><b><g:message code="simulation.index.selectcarstype"/></b></div>
                                </div>

                                <div class="row">

                                    <div class="left5">
                                        <g:message code="templates.configuration.fleet._createFleet.setnamegroup"/>
                                    </div>

                                    <div class="right2">
                                        <g:textField name="nameForFleet" value="${generatedName}"/>
                                    </div>

                                </div>

                                <div class="row">
                                    <div class="left3">
                                        <g:select name="carCount" from="${1..100}" /> &nbsp;&nbsp;
                                        <g:message code="simulation.index.carstype"/>
                                    </div>
                                    <div class="right2">
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

                                <div class="rowUnknown" id="updateCar">

                                </div>

                                <div class="rowL">
                                    <div class="left2">
                                        <%--<g:submitButton name="createCar" value="Cancel"/>--%>
                                        <input type="button" value="Cancel" onclick="window.location.href=window.location.href"/>
                                    </div>
                                    <div class="right2">
                                        <g:hiddenField name="fleetStubId" value="${fleetStubId}"/>
                                        <g:submitButton name="createFleet" value="Create Fleet"/>
                                    </div>
                                    <div class="clear"></div>
                                </div>
            </div>
        </g:form>
    </div>
</div>
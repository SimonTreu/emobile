<div id="openModal" class="modalDialog">
    <div class="layout">
        <g:form controller="configuration" action="createGroup">
            <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
            <div class="contentLeft1">
                <div class="rowU">
                    <div class="leftbig"><b><%--<<g:message code="simulation.index.selectcarstype"/>--%>Select filling stations for group</b></div>
                </div>
                <div class="row">
                    <div class="left3">
                        <g:select name="stationCount" from="${1..100}" /> &nbsp;&nbsp;
                        <%--<g:message code="simulation.index.carstype"/>--%>
                        Filling Station Types:
                    </div>
                    <div class="right2">
                        <g:select name="stationTypeId" from="${availableFillingStationTypes}" optionKey="id" optionValue="name" />
                        &nbsp;&nbsp;

                        <g:hiddenField name="groupStubId" value="${groupStubId}"/>
                        <g:submitToRemote class="addButton"
                                          url="[action: 'updateGroupOfConfiguration']"
                                          update="updateFillingStation"
                                          name="submit"
                                          value="Add to group" />
                    </div>
                    <div class="clear"></div>
                </div>

                <div class="rowUnknown" id="updateFillingStation">

                </div>

                <div class="rowL">
                    <div class="left2">
                        <%--<g:submitButton name="createCar" value="Cancel"/>--%>
                        <input type="button" value="Cancel" onclick="window.location.href=window.location.href"/>
                    </div>
                    <div class="right2">
                        <g:submitButton name="createGroup" value="Create Group"/>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </g:form>
    </div>
</div>
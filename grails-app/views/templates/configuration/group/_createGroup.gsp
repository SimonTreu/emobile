<div id="openModal" class="modalDialog">
    <div>
        <g:form controller="configuration" action="createGroup">
            <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
            <div class="contentLeft1">
                <div class="rowUp">
                    <div class="leftbig"><b><%--<<g:message code="simulation.index.selectcarstype"/>--%>Select filling stations for group</b></div>
                </div>


                <div class="rowMiddle">

                    <div class="leftModal">
                        <g:message code="templates.configuration.group._createGroup.setname"/>
                    </div>

                    <div class="rightOnlyButton">
                        <g:textField name="nameForGroup" value="${generatedName}"/>
                    </div>

                </div>

                <div class="rowMiddle">
                    <div class="left180PX">
                        <g:select name="stationCount" from="${1..100}" /> &nbsp;&nbsp;
                        <%--<g:message code="simulation.index.carstype"/>--%>
                        <g:message code="templates.configuration.group._createGroup.fillingstation"/>
                    </div>
                    <div class="right200PX">
                        <g:select name="stationTypeId" from="${availableFillingStationTypes}" optionKey="id" optionValue="name" />
                        &nbsp;&nbsp;

                        <g:hiddenField name="groupStubId" value="${groupStubId}"/>
                        <g:submitToRemote class="addButton"
                                          url="[action: 'updateGroupOfConfiguration']"
                                          update="updateFillingStation"
                                          name="submit"
                                          value="Add to Group" />
                    </div>
                    <div class="clear"></div>
                </div>

                <div class="rowUnknown" id="updateFillingStation">

                </div>

                <div class="rowDown">
                    <div class="left0PX">
                        <%--<g:submitButton name="createCar" value="Cancel"/>--%>
                        <input type="button" value="Cancel" onclick="window.location.href=window.location.href"/>
                    </div>
                    <div class="right90PX">
                        <g:hiddenField name="groupStubId" value="${groupStubId}"/>
                        <g:submitButton name="createGroup" value="Create Group"/>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </g:form><a class="close" title="Close" href=""></a>
    </div>
</div>
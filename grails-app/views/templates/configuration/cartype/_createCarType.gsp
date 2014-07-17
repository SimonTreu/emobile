<div id="openModal" class="modalDialog">
    <div>
        <g:form controller="configuration" action="createCarType">

            <div class="contentModalWindow">
                <div class="rowUp">
                    <div class="leftbig"><b><g:message code="templates.configuration.cartype._createcartype.createnewcar"/></b></div>

                </div>
                <div>
                    <div class="rowMiddle">
                        <div class="leftModal"><g:message code="templates.configuration.cartype._createcartype.carname"/></div>
                        <div class="rightOnlyButton"><g:textField name="carName" id="carName" value=""/></div>
                        <div class="clear"></div>
                    </div>
                    <div class="rowMiddle">
                        <div class="leftModal"><g:message code="templates.configuration.cartype._createcartype.energydemand"/></div>
                        <div class="rightOnlyButton"><g:textField name="energyDemand" id="energyDemand" value=""/></div>
                        <div class="clear"></div>
                    </div>
                    <div class="rowMiddle">
                        <div class="leftModal"><g:message code="templates.configuration.cartype._createcartype.maxcapacity"/></div>
                        <div class="rightOnlyButton"> <g:textField name="maxEnergyCapacity" id="maxEnergyCapacity" value=""/></div>
                        <div class="clear"></div>
                    </div>
                    <div class="rowDown">
                        <div class="left0PX">
                            <%--<g:submitButton name="createCar" value="Cancel"/>--%>
                            <input type="button" value="Cancel" onclick="window.location.href=window.location.href"/>
                        </div>
                        <div class="right0PX">
                            <g:submitButton name="createCar" value="Create"/>
                        </div>
                        <div class="clear"></div>
                    </div>
                </div>
            </div>

        </g:form>
    </div>
</div>





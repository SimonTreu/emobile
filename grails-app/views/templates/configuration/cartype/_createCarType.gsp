<div id="openModal" class="modalDialog">
    <div class="layout">
        <g:form controller="configuration" action="createCarType">

            <div class="contentLeft">
                <div class="rowU">
                    <div class="leftbig"><b><g:message code="templates.configuration.cartype._createcartype.createnewcar"/></b></div>

                </div>
                <div>
                    <div class="row">
                        <div class="left5"><g:message code="templates.configuration.cartype._createcartype.carname"/></div>
                        <div class="right2"><g:textField name="carName" id="carName" value=""/></div>
                        <div class="clear"></div>
                    </div>
                    <div class="row">
                        <div class="left5"><g:message code="templates.configuration.cartype._createcartype.energydemand"/></div>
                        <div class="right2"><g:textField name="energyDemand" id="energyDemand" value=""/></div>
                        <div class="clear"></div>
                    </div>
                    <div class="row">
                        <div class="left5"><g:message code="templates.configuration.cartype._createcartype.maxcapacity"/></div>
                        <div class="right2"> <g:textField name="maxEnergyCapacity" id="maxEnergyCapacity" value=""/></div>
                        <div class="clear"></div>
                    </div>
                    <div class="rowL">
                        <div class="left2">
                            <%--<g:submitButton name="createCar" value="Cancel"/>--%>
                            <input type="button" value="Cancel" onclick="window.location.href=window.location.href"/>
                        </div>
                        <div class="right2">
                            <g:submitButton name="createCar" value="Create"/>
                        </div>
                        <div class="clear"></div>
                    </div>
                </div>
            </div>

        </g:form>
    </div>
</div>





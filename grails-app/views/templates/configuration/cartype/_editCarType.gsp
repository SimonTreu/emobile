
<div id="openModal" class="modalDialog">
<div class="layout">
    <g:form controller="configuration" action="editCarType">

        <div class="contentLeft">
            <div class="rowU">
                <div class="leftbig"><b><g:message code="templates.configuration.cartype._createcartype.editnewcar"/></b></div>
            </div>
            <div>
                <div class="row">
                    <div class="left5"><g:message code="templates.configuration.cartype._createcartype.carname"/></div>
                    <div class="right2"><g:textField name="carName" id="carName" value="${carTypeName}"/></div>
                    <div class="clear"></div>
                </div>
                <div class="row">
                    <div class="left5"><g:message code="templates.configuration.cartype._createcartype.energydemand"/></div>
                    <div class="right2"><g:textField name="energyDemand" id="energyDemand" value="${energyConsumption}"/></div>
                    <div class="clear"></div>
                </div>
                <div class="row">
                    <div class="left5"><g:message code="templates.configuration.cartype._createcartype.maxcapacity"/></div>
                    <div class="right2"> <g:textField name="maxEnergyCapacity" id="maxEnergyCapacity" value="${maxEnergyLoad}"/></div>
                    <div class="clear"></div>
                </div>

                <div class="rowL">
                    <div class="left2">
                        <%--<g:submitButton name="createCar" value="Cancel"/>--%>
                        <input type="button" value="Cancel" onclick="window.location.href=window.location.href"/>
                    </div>
                    <div class="right2">
                        <g:hiddenField name="carTypeId" value="${carTypeId}"/>
                        <g:submitButton name="createCar" value="Update"/>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>
    </g:form>
</div>
</div>
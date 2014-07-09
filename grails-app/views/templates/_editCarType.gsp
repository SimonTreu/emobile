<a href="#x" class="overlay1" id="join_form"></a>
<div class="popup1">
<div class="layout">
    <g:form controller="configuration" action="editCarType">

        <div class="contentLeft">
            <div class="rowU">
                <div class="leftbig"><b>Create new car</b></div>
            </div>
            <div>
                <div class="row">
                    <div class="left2">Car name</div>
                    <div class="right2"><g:textField name="carName" id="carName" value="${carTypeName}"/></div>
                    <div class="clear"></div>
                </div>
                <div class="row">
                    <div class="left2"> Energy Demand in [ kWh / 100 km ]</div>
                    <div class="right2"><g:textField name="energyDemand" id="energyDemand" value="${energyConsumption}"/></div>
                    <div class="clear"></div>
                </div>
                <div class="row">
                    <div class="left2">  Maximal Battery Capacity in kW</div>
                    <div class="right2"> <g:textField name="maxEnergyCapacity" id="maxEnergyCapacity" value="${maxEnergyLoad}"/></div>
                    <div class="clear"></div>
                </div>

                <div class="rowL">
                    <div class="left2">
                        <g:submitButton name="createCar" value="Cancel"/>
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
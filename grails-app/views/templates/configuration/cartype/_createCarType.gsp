<a href="#x" ></a>
<div class="popup1">
    <div class="layout">
        <g:form controller="configuration" action="createCarType">

            <div class="contentLeft">
                <div class="rowU">
                    <div class="leftbig"><b>Create new car</b></div>
                </div>
                <div>
                    <div class="row">
                        <div class="left2">Car name</div>
                        <div class="right2"><g:textField name="carName" id="carName" value=""/></div>
                        <div class="clear"></div>
                    </div>
                    <div class="row">
                        <div class="left2"> Energy Demand in [ kWh / 100 km ]</div>
                        <div class="right2"><g:textField name="energyDemand" id="energyDemand" value=""/></div>
                        <div class="clear"></div>
                    </div>
                    <div class="row">
                        <div class="left2">  Maximal Battery Capacity in kW</div>
                        <div class="right2"> <g:textField name="maxEnergyCapacity" id="maxEnergyCapacity" value=""/></div>
                        <div class="clear"></div>
                    </div>
                    <div class="rowL">
                        <div class="left2">
                            <g:submitButton name="createCar" value="Cancel"/>
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





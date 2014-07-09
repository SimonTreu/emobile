
<div style="border: 1px">

    <g:form controller="configuration" action="editCarType">

        <table>

            <tr>
                <td>
                    Name of Car
                </td>
                <td>
                    <g:textField name="carName" id="carName" value="${carTypeName}"/>
                </td>
            </tr>
            <tr>
                <td>
                    Energy Demand in [ kWh / 100 km ]
                </td>
                <td>
                    <g:textField name="energyDemand" id="energyDemand" value="${energyConsumption}"/>
                </td>
            </tr>
            <tr>
                <td>
                    Maximal Battery Capacity in kW
                </td>
                <td>
                    <g:textField name="capacity" id="capacity" value="${maxEnergyLoad}"/>
                </td>
            </tr>

        </table>

        <g:hiddenField name="carTypeId" value="${carTypeId}"/>

        <g:submitButton name="createCar" value="Update"/>

    </g:form>
</div>
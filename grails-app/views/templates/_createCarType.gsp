
<div style="border: 1px">

    <g:form controller="configuration" action="createCarType">

        <table>

            <tr>
                <td>
                    Name of Car
                </td>
                <td>
                    <g:textField name="carName" id="carName"/>
                </td>
            </tr>
            <tr>
                <td>
                    Energy Demand in [ kWh / 100 km ]
                </td>
                <td>
                    <g:textField name="energyDemand" id="energyDemand"/>
                </td>
            </tr>
            <tr>
                <td>
                    Maximal Battery Capacity in kW
                </td>
                <td>
                    <g:textField name="maxEnergyCapacity" id="maxEnergyCapacity"/>
                </td>
            </tr>

        </table>


        <g:submitButton name="createCar" value="Create"/>

    </g:form>
</div>
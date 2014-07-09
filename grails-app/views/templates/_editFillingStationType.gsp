
<div style="border: 1px">

    <g:form controller="configuration" action="editFillingStationType">

        <table>

            <tr>
                <td>
                    Name Filling Station Type
                </td>
                <td>
                    <g:textField name="fillingStationTypeName" id="fillingStationTypeName" value="${fillingStationTypeName}"/>
                </td>
            </tr>
            <tr>
                <td>
                    Power in [ kW ]
                </td>
                <td>
                    <g:textField name="power" id="power" value="${power}"/>
                </td>
            </tr>

        </table>

        <g:hiddenField name="fillingStationTypeId" value="${fillingStationTypeId}"/>

        <g:submitButton name="createFillingStation" value="Update"/>

    </g:form>
</div>
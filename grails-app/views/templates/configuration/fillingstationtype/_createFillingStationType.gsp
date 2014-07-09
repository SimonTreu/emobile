
<div style="border: 1px">

    <g:form controller="configuration" action="createFillingStationType">

        <table>

            <tr>
                <td>
                    Name Filling Station Type
                </td>
                <td>
                    <g:textField name="fillingStationTypeName" id="fillingStationTypeName"/>
                </td>
            </tr>
            <tr>
                <td>
                    Power in [ kw ]
                </td>
                <td>
                    <g:textField name="power" id="power"/>
                </td>
            </tr>

        </table>


        <g:submitButton name="createFillingStationType" value="Create"/>

    </g:form>
</div>
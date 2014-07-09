<table id="dataTable"  border="0">

    <tr class="cars">

        <td align="">
            <g:select name="carNumber" from="${1..100}" />
        </td>

        <td align="">
            <g:message code="simulation.index.carstype"/>
        </td>

        <td>
            <g:select name="carTypeSelect" from="${availableCarTypes}" optionKey="id" optionValue="name" />
        </td>

        <td>
            <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
            <g:hiddenField name="fleetStubId" value="${fleetStubId}"/>
            <g:hiddenField name="availableCarTypes" value="${availableCarTypes}"/>
            <g:submitToRemote class="addButton"
                              url="[action: 'updateFleetOfConfiguration']"
                              update="updateCar${uuid}"
                              name="submit"
                              value="Add to Fleet" />
            <img width="22px"src="${g.resource( dir: '/images', file: 'add.png' )}">

        </td>

    </tr>
</table>

<div class="layoutCell" id="updateCar${uuid}"></div>

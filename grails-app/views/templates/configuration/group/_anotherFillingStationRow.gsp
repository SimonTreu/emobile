<div class="cars">
    <div class="row">
        <div class="left3">
            <g:select name="count" from="${1..100}" /> &nbsp;&nbsp;
            <g:message code="simulation.index.carstype"/>
        </div>
        <div class="right2">
            <g:select name="carTypeSelect" from="${availableFillingStationTypes}" optionKey="id" optionValue="name" />
            &nbsp;&nbsp;
            <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
            <g:hiddenField name="groupStubId" value="${groupStubId}"/>
            <g:hiddenField name="availableFillingStations" value="${availableFillingStationTypes}"/>
            <g:submitToRemote class="addButton"
                              url="[action: 'updateGroupOfConfiguration']"
                              update="updateCar${uuid}"
                              name="submit"
                              value="Add to Group" />
        </div>
        <div class="clear"></div>
    </div>
</div>

<div id="updateCar${uuid}"></div>

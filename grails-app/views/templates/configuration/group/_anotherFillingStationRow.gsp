<div class="cars">
    <div class="rowMiddle">
        <div class="left140PX">
            <g:select name="stationCountList" from="${1..100}" /> &nbsp;&nbsp;
            <g:message code="simulation.index.carstype"/>
        </div>
        <div class="right235PX">
            <g:select name="stationTypeId" from="${availableFillingStationTypes}" optionKey="id" optionValue="name" />
            &nbsp;&nbsp;

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

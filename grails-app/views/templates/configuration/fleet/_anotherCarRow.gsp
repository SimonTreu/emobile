<div class="cars">
<div class="rowMiddle">
    <div class="left140PX">
        <g:select name="carCountList" from="${1..100}" /> &nbsp;&nbsp;
        <g:message code="simulation.index.carstype"/>
    </div>
    <div class="right235PX">
        <g:select name="carTypeSelect" from="${availableCarTypes}" optionKey="id" optionValue="name" />
        &nbsp;&nbsp;
        <%-- <g:hiddenField name="configurationStubId" value="${configurationStubId}"/> --%>
        <%-- <g:hiddenField name="fleetStubId" value="${fleetStubId}"/> --%>
        <%-- <g:hiddenField name="availableCarTypes" value="${availableCarTypes}"/> --%>
        <g:submitToRemote class="addButton"
                  url="[action: 'updateFleetOfConfiguration']"
                  update="updateCar${uuid}"
                  name="submit${uuid}"
                  value="Add to Fleet"
                  id="submit${uuid}" />
</div>
<div class="clear"></div>
</div>
</div>

<div id="updateCar${uuid}"></div>

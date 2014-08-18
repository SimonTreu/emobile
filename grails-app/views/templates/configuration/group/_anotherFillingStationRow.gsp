<div class="stations">
    <div class="rowMiddle">
        <div class="left180PX">
            <g:select name="stationCountList" from="${1..100}" /> &nbsp;&nbsp;
            <g:message code="templates.configuration.group._createGroup.fillingstation"/>
        </div>
        <div class="right211PX">
            <g:select name="stationTypeSelect" from="${availableFillingStationTypes}" optionKey="id" optionValue="name" />
            &nbsp;&nbsp;

            <g:submitToRemote class="addButton"
                              url="[action: 'updateGroupOfConfiguration']"
                              update="updateFillingStation${uuid}"
                              name="submit"
                              value="Add to Group" />
        </div>
        <div class="clear"></div>
    </div>
</div>

<div id="updateFillingStation${uuid}"></div>

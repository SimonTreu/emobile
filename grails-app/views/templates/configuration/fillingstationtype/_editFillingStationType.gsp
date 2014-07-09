
<div id="openModal" class="modalDialog">
    <div class="layout">
        <g:form controller="configuration" action="editFillingStationType">

            <div class="contentLeft">
                <div class="rowU">
                    <div class="leftbig"><b>Edit filling station</b></div>
                </div>
                <div>
                    <div class="row">
                        <div class="left2">Filling station name</div>
                        <div class="right2"><g:textField name="fillingStationTypeName" id="fillingStationTypeName" value="${fillingStationTypeName}"/></div>
                        <div class="clear"></div>
                    </div>
                    <div class="row">
                        <div class="left2"> Power in [ kW ]</div>
                        <div class="right2"><g:textField name="power" id="power" value="${power}"/></div>
                        <div class="clear"></div>
                    </div>


                    <div class="rowL">
                        <div class="left2">
                            <%--<g:submitButton name="createCar" value="Cancel"/>--%>
                            <input type="button" value="Cancel" onclick="window.location.href=window.location.href"/>
                        </div>
                        <div class="right2">
                            <g:hiddenField name="fillingStationTypeId" value="${fillingStationTypeId}"/>

                            <g:submitButton name="createFillingStation" value="Update"/>
                        </div>
                        <div class="clear"></div>
                    </div>
                </div>
            </div>
        </g:form>
    </div>
</div>



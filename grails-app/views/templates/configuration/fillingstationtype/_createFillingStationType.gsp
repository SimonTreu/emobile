<div id="openModal" class="modalDialog">
    <div class="layout">
        <g:form controller="configuration" action="createFillingStationType">

            <div class="contentLeft">
                <div class="rowU">
                    <div class="leftbig"><b>Create new filling station</b></div>

                </div>
                <div>
                    <div class="row">
                        <div class="left2">Filling station name</div>
                        <div class="right2"><g:textField name="fillingStationTypeName" id="fillingStationTypeName" value=""/></div>
                        <div class="clear"></div>
                    </div>
                    <div class="row">
                        <div class="left2"> Power in [ kw ]</div>
                        <div class="right2"><g:textField name="power" id="power" value=""/></div>
                        <div class="clear"></div>
                    </div>

                    <div class="rowL">
                        <div class="left2">
                            <%--<g:submitButton name="createCar" value="Cancel"/>--%>
                            <input type="button" value="Cancel" onclick="window.location.href=window.location.href"/>
                        </div>
                        <div class="right2">
                            <g:submitButton name="createFillingStationType" value="Create"/>
                        </div>
                        <div class="clear"></div>
                    </div>
                </div>
            </div>

        </g:form>
    </div>
</div>

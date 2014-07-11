<div id="openModal" class="modalDialogRoutes">

    <div class="settingsWindowBig">

        <div class="layout">

            <g:form controller="configuration" action="createDistributionForFleet">

                <div class="contentLeftRoutes">

                    <div class="rowU">
                        <div class="leftbig"><b>Select Routes of Fleet-Cars</b></div>
                    </div>

                    <div class="row">

                        <div class="left3">

                            Select a Distribution

                        </div>

                        <div class="right3">

                            <g:select name="selectedDist" from="${distributions}" optionKey="key" optionValue="${it}" />

                        </div>

                    </div>

                    <div class="row">

                        <div class="left80">
                            From [ km ]
                        </div>

                        <div class="left80">
                            <g:select name="fromKm" from="${(10..550).step( 10 )}" />
                        </div>

                        <div class="left80">
                            To [ km ]
                        </div>

                        <div class="left80">
                            <g:select name="toKm" from="${(10..550).step( 10 )}" />
                        </div>


                    </div>

                    <div class="clear"></div>



                    <div class="rowL">
                        <div class="left2">
                            <%--<g:submitButton name="createCar" value="Cancel"/>--%>
                            <input type="button" value="Cancel" onclick="window.location.href=window.location.href"/>
                        </div>
                        <div class="right2">
                            <g:submitButton name="createFleet" value="Create Fleet"/>
                        </div>
                        <div class="clear"></div>
                    </div>
                </div>



            </g:form>

        </div>

    </div>



</div>



<g:each in="${distributions}" var="dist">

    ${dist.key} ${dist}

</g:each>

<g:each in="${cars}" var="car">

    ${car.name} (${car.id})

</g:each>

for Fleet ${fleetId}
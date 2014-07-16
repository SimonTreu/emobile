<script language="javascript">
    $(document).ready(function() {
        $(".tabs-menu a").click(function(event) {
            event.preventDefault();
            $(this).parent().addClass("current");
            $(this).parent().siblings().removeClass("current");
            var tab = $(this).attr("href");
            $(".tab-content").not(tab).css("display", "none");
            $(tab).fadeIn();
        });
    });
</script>

<div id="openModal" class="modalDialogRoutes">
    <div class="settingsWindowBig">
        <fieldset class="fieldSet100Percent">
            <legend> <g:message code="templates.configuration.fleet._distribution.selectroutes"/> </legend>
                <div class="layout">
                    <div class="layoutLeftLittle">
                        <div class="contentLeft4">
                            <g:form controller="configuration" action="createDistributionForFleet">
                                <div class="contentLeftRoutes">
                                    <div class="rowU1">
                                        <div class="leftbig"><b><g:message code="templates.configuration.fleet._distribution.properties"/></b></div>
                                    </div>
                                    <div>
                                        <div class="clear"></div>
                                    </div>
                                    <div class="row1">
                                        <div class="left2">
                                            <b>Car number</b>
                                        </div>
                                        <div class="left2">
                                            <b>Fleet name</b>
                                        </div>
                                        <div class="left2">
                                            <b>${fleetName}</b>
                                        </div>
                                    </div>
                                    <g:each in="${carTypes}" var="carType">
                                        <div class="row1">
                                            <div class="left2">
                                                ${carType.value.size()}
                                            </div>
                                            <div class="left2">
                                                of
                                            </div>
                                            <div class="left2">
                                                ${carType.key.name}
                                            </div>
                                        </div>
                                    </g:each>
                                </div>
                            </g:form>
                        </div>
                    </div>
                    <div class="layoutRightLittle">
                        <div id="tabs-container">
                            <ul class="tabs-menu">
                                <li class="current"><a href="#tab-1"><g:message code="simulation.index.distributed"/></a></li>
                                <li><a href="#tab-2"><g:message code="simulation.index.ownroutes"/></a></li>
                                <li><a href="#tab-3"><g:message code="simulation.index.showonmap"/></a></li>
                            </ul>
                            <div class="tab">

                                <g:form controller="configuration" action="setDistributionForFleet">
                                    <div id="tab-1" class="tab-content-buttons-bottomed">


                                        <div class="rowSpace">
                                            <div class="clear">

                                            </div>
                                        </div>

                                        <div class="row1">
                                            <div class="left4">
                                                <g:message code="templates.configuration.fleet._distribution.selectdistribution"/>
                                            </div>
                                            <div class="right2">
                                                <g:select name="selectedDist" from="${distributions}" optionKey="key" optionValue="${it}" />
                                            </div>
                                            <div class="clear"></div>
                                        </div>
                                        <div class="row1">
                                            <div class="left75">
                                                <g:message code="templates.configuration.fleet._distribution.from"/>
                                            </div>
                                            <div class="left60">
                                                <g:select name="fromKm" from="${(10..550).step( 10 )}" />
                                            </div>
                                            <div class="right65">
                                                <g:message code="templates.configuration.fleet._distribution.to"/>
                                            </div>
                                            <div class="right60">
                                                <g:select name="toKm" from="${(10..550).step( 10 )}" />
                                            </div>

                                            <div class="clear"></div>
                                        </div>

                                        <div class="clear"></div>

                                        <div class="rowL1Bottom">

                                            <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                                            <g:hiddenField name="fleetId" value="${fleetId}"/>
                                            <div class="right2-bottomed">
                                                <g:submitButton name="setDistribution" value="Save Distribution for Fleet"/>
                                            </div>

                                            <div class="clear"></div>

                                        </div>

                                    </div>
                                </g:form>
                                <div id="tab-2" class="tab-content">
                                    <p>
                                        <input type="file">
                                    </p>

                                </div>
                                <div id="tab-3" class="tab-content">
                                    <p>Duis  </p>
                                </div>

                            </div>
                        </div>

                    </div>
    </div>
  </fieldset>
</div>
</div>


<g:each in="${distributions}" var="dist">

    ${dist.key} ${dist}

</g:each>

<g:each in="${cars}" var="car">

    ${car.name} (${car.id})

</g:each>

for Fleet ${fleetId}
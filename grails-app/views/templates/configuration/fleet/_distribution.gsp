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
                                    <div class="rowUp">
                                        <div class="leftbig"><b><g:message code="templates.configuration.fleet._distribution.properties"/></b></div>
                                    </div>
                                    <div>
                                        <div class="clear"></div>
                                    </div>
                                    <div class="rowMiddleWithoutBorder">
                                        <div class="left0PX">
                                            <b><g:message code="templates.configuration.fleet._distribution.carnumber"/></b>
                                        </div>
                                        <div class="left0PX">
                                            <b><g:message code="templates.configuration.fleet._distribution.fleetname"/></b>
                                        </div>
                                        <div class="left0PX">
                                            <b>${fleetName}</b>
                                        </div>
                                    </div>
                                    <g:each in="${carTypes}" var="carType">
                                        <div class="rowMiddleWithoutBorder2">
                                            <div class="left0PX">
                                                ${carType.value.size()}
                                            </div>
                                            <div class="left0PX">
                                                of
                                            </div>
                                            <div class="left0PX">
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

                                    <div id="tab-1" class="tab-content">
                                        <g:form controller="configuration" action="setDistributionForFleet">
                                            <div class="rowSpace">
                                                <div class="clear"></div>
                                            </div>
                                            <div class="contentLeft1">
                                                <div class="rowUp">
                                                    <div class="leftbig">Distribution settings:</div>
                                                </div>
                                                <div class="rowMiddle">
                                                    <div class="leftDistribution">
                                                        <g:message code="templates.configuration.fleet._distribution.selectdistribution"/>
                                                    </div>
                                                    <div class="rightDistribution">
                                                        <g:select name="selectedDist" from="${distributions}" optionKey="key" optionValue="${it}" />
                                                    </div>
                                                    <div class="clear"></div>
                                                </div>

                                                <div class="rowMiddle">
                                                    <div class="leftDistribution60PX">
                                                        <g:message code="templates.configuration.fleet._distribution.from"/>
                                                    </div>
                                                    <div class="leftDistributionButton">
                                                        <g:select name="fromKm" from="${(10..95).step( 5 ) + ( 100..500 ).step( 20 )}" />
                                                    </div>
                                                    <div class="right50PX">
                                                        <g:message code="templates.configuration.fleet._distribution.to"/>
                                                    </div>
                                                    <div class="right60">
                                                        <g:select name="toKm" from="${(10..95).step( 5 ) + ( 100..500 ).step( 20 )}" />
                                                    </div>

                                                    <div class="clear"></div>
                                                </div>

                                                <div class="clear"></div>

                                                <div class="rowDown">
                                                    <div class="leftLongBold"></div>
                                                    <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                                                    <g:hiddenField name="fleetId" value="${fleetId}"/>
                                                    <div class="right2-bottomed">
                                                        <g:submitButton name="setDistribution" value="Save Distribution for Fleet"/>
                                                    </div>

                                                    <div class="clear"></div>
                                                </div>
                                            </div>
                                        </g:form>
                                    </div>

                                <div id="tab-2" class="tab-content">
                                    <div class="contentLeft1">
                                        <div class="rowUp">
                                            <div class="leftbig">Distribution settings:</div>
                                        </div>
                                        <div class="rowMiddle">
                                            <div class="leftDistributionFile">
                                                <input type="file">
                                            </div>
                                            <div class="rightDistribution">

                                            </div>
                                            <div class="clear"></div>
                                        </div>

                                        <div class="rowDown">
                                            <div class="leftLongBold"></div>
                                            <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                                            <g:hiddenField name="fleetId" value="${fleetId}"/>
                                            <div class="right2-bottomed">
                                                <g:submitButton name="setDistribution" value="Save Distribution for Fleet"/>
                                            </div>

                                            <div class="clear"></div>
                                        </div>
                                    </div>
                                </div>
                                <div id="tab-3" class="tab-content">
                                    <div id="map" class="olMap" style="z-index:1">
                                    <div id="gmap_div"></div>
                                </div>

                            </div>
                        </div>
                    </div>

                </div>
        </fieldset><a class="close" title="Close" href=""></a>
    </div>
</div>


<g:each in="${distributions}" var="dist">

    ${dist.key} ${dist}

</g:each>

<g:each in="${cars}" var="car">

    ${car.name} (${car.id})

</g:each>

for Fleet ${fleetId}
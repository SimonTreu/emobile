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
            <legend> <g:message code="templates.configuration.group._distribution.selectstations"/> </legend>
            <div class="layout">
                <div class="layoutLeftLittle">
                    <div class="contentLeft4">
                        <g:form controller="configuration" action="createDistributionForGroup">
                            <div class="contentLeftRoutes">
                                <div class="rowUp">
                                    <div class="leftbig"><b><g:message code="templates.configuration.group._distribution.properties"/></b></div>
                                </div>
                                <div>
                                    <div class="clear"></div>
                                </div>
                                <div class="rowMiddleWithoutBorder">
                                    <div class="left0PX">
                                        <b><g:message code="templates.configuration.group._distribution.stationsnumber"/></b>
                                    </div>
                                    <div class="left0PX">
                                        <b><g:message code="templates.configuration.group._distribution.groupname"/></b>
                                    </div>
                                    <div class="left0PX">
                                        <b>${groupName}</b>
                                    </div>
                                </div>

                                <g:each in="${groupTypes}" var="groupType">
                                    <div class="rowMiddleWithoutBorder2">
                                        <table>
                                            <tr>
                                                <td width="60px" align="center" valign="middle">${groupType.value.size()}</td>
                                                <td width="60px" valign="middle">of</td>
                                                <td width="60px" valign="middle">${groupType.key}</td>
                                            </tr>
                                         </table>
                                    </div>
                                </g:each>

                            </div>
                        </g:form>
                    </div>
                </div>
                <div class="layoutRightLittle">
                    <div id="tabs-container">
                        <ul class="tabs-menu">
                            <li class="current"><a href="#tab-1"><g:message code="templates.configuration.group._distribution.random"/></a></li>
                            <li><a href="#tab-2"><g:message code="templates.configuration.group._distribution.onMap"/></a></li>
                            <li><a href="#tab-3"><g:message code="templates.configuration.group._distribution.file"/></a></li>
                        </ul>
                        <div class="tab">

                            <div id="tab-1" class="tab-content">
                                <br>
                                <div class="contentLeft3">
                                    <div class="rowUp">
                                        <div class="leftbig">Electric stations settings:</div>
                                    </div>
                                    <div class="rowMiddle">
                                        <div class="leftDistributionFile2">
                                            <br>
                                            <g:radio name="myGroup" value="1" label="Random Stations"/><span> Random Stations - ${groupNumber.value.size()} stations</span>
                                            <br><br>
                                            <g:radio name="myGroup" value="2" label="Random Stations"/><span> Public Stations - 142 stations</span>
                                            <br><br>
                                        </div>
                                        <div class="rightDistribution">

                                        </div>
                                        <div class="clear"></div>
                                    </div>

                                    <div class="rowDown">
                                        <div class="leftLongBold"></div>
                                        <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                                        <g:hiddenField name="groupId" value="${groupId}"/>
                                        <div class="right2-bottomed">
                                            <g:submitButton name="setDistribution" value="Save stations"/>
                                        </div>

                                        <div class="clear"></div>
                                    </div>
                                </div>
                            </div>

                            <div id="tab-2" class="tab-content">

                            </div>

                            <div id="tab-3" class="tab-content">
                                <br>
                                <div class="contentLeft1">
                                    <div class="rowUp">
                                        <div class="leftbig">Electric stations settings:</div>
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
                                        <g:hiddenField name="groupId" value="${groupId}"/>
                                        <div class="right2-bottomed">
                                            <g:submitButton name="setDistribution" value="Save stations from File"/>
                                        </div>

                                        <div class="clear"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div><a class="close" title="Close" href=""></a>
        </fieldset>
    </div>

</div>


<g:each in="${distributions}" var="dist">

    ${dist.key} ${dist}

</g:each>

<g:each in="${cars}" var="car">

    ${car.name} (${car.id})

</g:each>

for Group ${fleetId}
<table border="0">
    <tr>
        <td width="90px" align="left">
            <img align="left" width="80px"  src="${g.resource( dir: '/images', file: 'logo_efahrung.png' )}"
        </td>
        <td>
            <%--<div id='menu'>
                <ul>
                   <li class='active'><g:link controller="simulation" action="init"><span>Simulate</span></g:link>
                        <ul>
                            <li><g:link controller="simulation" action="init"><span>New</span></g:link></li>
                            <li><g:link controller="simulation" action="init"><span>Open...</span></g:link></li>
                            <li><g:link controller="simulation" action="init"><span>Open resent</span></g:link></li>
                            <li><g:link controller="simulation" action="init"><span>Load from file</span></g:link></li>
                            <li><g:link controller="simulation" action="init"><span>Log out</span></g:link></li>

                        </ul>
                   </li>
                   <li>
                       <g:link controller="simulationPreparator" action="index"><span>Execute</span></g:link>
                   </li>
                   <li>
                       <g:link controller="simulationPreparator" action="index" params="[ viewOnly : 'true' ]" view="index"><span>View</span></g:link>
                   </li>
                   <li class='has-sub last'><g:link controller="mapView" action="listUsages"><span>Extras</span></g:link>
                        <ul>
                            <li><g:link controller="mapView" action="listUsages"><span>Charging stations</span></g:link></li>
                            <li><a href='#'><span>Driver profile</span></a></li>
                            <li class='last'><a href='#'><span>OBU Statistics</span></a></li>
                        </ul>
                    </li>
                    <li>
                        <g:link controller="simulationPreparator" action="index"><span>Help</span></g:link>
                    </li>

                    <li>
                        <g:link controller="simulationPreparator" action="index"><span> fogot password</span></g:link>
                    </li>

               </ul>
            </div>--%>

            <div class="wrap">
                <nav>
                    <ul id="menu">
                        <li><a href="#"><span></span> <g:message code="layouts._topbar.simulate"/></a>
                            <ul>
                                <li><g:link controller="simulation" action="init"><g:message code="layouts._topbar.new"/> </g:link></li>
                                <li><a href="#"><g:message code="layouts._topbar.open"/></a></li>
                                <li><a href="#"><g:message code="layouts._topbar.openresent"/></a></li>
                                <li><a href="#"><g:message code="layouts._topbar.loadfromfile"/></a></li>
                                <li><a href="#"><g:message code="layouts._topbar.logout"/></a></li>
                            </ul>
                        </li>
                        <li><a href="#"><span class=""></span> <g:message code="layouts._topbar.execute"/></a>
                        </li>
                        <li><a href="#"><span class=""></span><g:message code="layouts._topbar.view"/></a>
                            <ul>
                                <li><a href="#"><g:message code="layouts._topbar.viewresults"/></a></li>
                                <li><a href="#"><g:message code="layouts._topbar.exportinfile"/></a></li>
                            </ul>
                        </li>
                        <li><g:link controller="extras" action="index"><span class=""></span><g:message code="layouts._topbar.extras"/></g:link>
                            <ul>
                                <li><g:link controller="extras" action="index"><g:message code="layouts._topbar.addnewcartype"/></g:link></li>
                                <li><g:link controller="extras" action="station"><g:message code="layouts._topbar.addnewelectricstation"/></g:link></li>
                                <li><g:link controller="mapView" action="listUsages"><g:message code="layouts._topbar.electricstationstatistic"/></g:link></li>
                            </ul>
                        </li>
                        <li><a href="#"><span class=""></span><g:message code="layouts._topbar.help"/></a>
                            <ul>
                                <li><a href="#"><g:message code="layouts._topbar.help"/></a></li>
                                <li><a href="#"><g:message code="layouts._topbar.contact"/></a></li>
                            </ul>
                        </li>

                        <span class="registration1">
                            <li class="regristrationText">
                                <input type="text" size="10" value="<g:message code="layouts._topbar.username"/>">
                            </li>

                            <li class="regristrationText">
                                <input type="password" size="10" value="<g:message code="layouts._topbar.password"/>">
                            </li>

                            <li class="regristrationText">
                                <input type="submit" value="<g:message code="layouts._topbar.login"/>">
                            </li>
                            <span class="registrationButtons">
                                <g:link class="registration" controller="fogotPassword" action="index"><g:message code="layouts._topbar.fogotpassword"/></g:link> <p>
                                <g:link class="registration" controller="registration" action="index"><g:message code="layouts._topbar.registration"/></g:link>
                            </span>
                        </span>
                    </ul>
                    <div class="clearfix"></div>
                </nav>
            </div>

        </td>
        <td width="120px" align="right">
            <img class="dfkiLogo" align="right" width="110px"  src="${g.resource( dir: '/images', file: 'dfki_logo.png' )}"
        </td>
    </tr>
</table>
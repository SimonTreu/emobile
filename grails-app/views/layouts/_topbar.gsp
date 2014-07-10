<table border="0" xmlns="http://www.w3.org/1999/html">
    <tr>
        <td width="90px" align="left">
            <img class="efahrungLogo" align="left" width="80px"  src="${g.resource( dir: '/images', file: 'logo_efahrung.png' )}"
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
                                <li><g:link controller="configuration" action="index"><g:message code="layouts._topbar.new"/> </g:link></li>
                                <li><g:link controller="simulation" action="open"><g:message code="layouts._topbar.open"/></g:link></li>
                                <li><g:link controller="configuration" action="showRecentlyEditedConfiguration"><g:message code="layouts._topbar.openresent"/></g:link> </li>
                                <li><g:link controller="simulation" action="load"><g:message code="layouts._topbar.loadfromfile"/></g:link></li>
                                <li><a href="<g:createLink controller="login" action="logout" />"><g:message code="layouts._topbar.logout"/></a></li>
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
                                <%-- <li><g:link controller="configuration" action="showCarTypes"><g:message code="layouts._topbar.addnewcartype"/></g:link></li> --%>
                                <li><g:link controller="configuration" action="showCarTypes"><g:message code="layouts._topbar.addnewcartype"/></g:link></li>
                                <li><g:link controller="configuration" action="showFillingStationTypes"><g:message code="layouts._topbar.addnewelectricstation"/></g:link></li>
                                <li><g:link controller="mapView" action="listUsages"><g:message code="layouts._topbar.electricstationstatistic"/></g:link></li>
                            </ul>
                        </li>
                        <li><a href="#"><span class=""></span><g:message code="layouts._topbar.help"/></a>
                            <ul>
                                <li><a href="#"><g:message code="layouts._topbar.help"/></a></li>
                                <li><a href="#"><g:message code="layouts._topbar.contact"/></a></li>
                            </ul>
                        </li>
                        <span id="signup">
                            <g:if test="${errors != null}" >

                                Errors: ${errors}

                            </g:if>
                            <span id='login'>
                                <span class='inner'>

                                    <span class="registration1">
                                    <form>

                                    </form action='${postUrl}' method='POST' id='loginForm' class='cssform' autocomplete='off'>
                                            <span class="loggedIn">
                                                <span class="loggedIndrin"><g:message code="layouts._topbar.logged"/>&nbsp;
                                                    <b>${welcome?.givenName} ${welcome?.familyName}</b>
                                                </span>
                                                <a class="logout" href="<g:createLink controller="login" action="logout" />"> <g:message code="layouts._topbar.logout2"/></a>
                                            </span>
                                    </form>
                                    <%--<form action='${postUrl}' method='POST' id='loginForm' class='cssform' autocomplete='off'>
                                    <li class="regristrationText">
                                        <input type="text" size="10" name='j_username' id='username' value="<g:message code="layouts._topbar.username"/>">
                                    </li>

                                    <li class="regristrationText">
                                        <input type="password" size="10" name='j_password' id='password' value="<g:message code="layouts._topbar.password"/>">
                                    </li>

                                    <li class="regristrationText">
                                        <input type="submit" value="<g:message code="layouts._topbar.login"/>">
                                    </li>
                                    <span class="registrationButtons">
                                        <g:link class="registration" controller="fogotPassword" action="index"><g:message code="layouts._topbar.fogotpassword"/></g:link> <p>
                                        <g:link class="registration" controller="registration" action="index"><g:message code="layouts._topbar.registration"/></g:link>
                                    </span>
                                    </form>--%>

                                       <%-- <script type='text/javascript'>
                                            <!--
                                            (function(){
                                                document.forms['loginForm'].elements['j_username'].focus();
                                            })();
                                            // -->
                                        </script>--%>
                                    </span>
                                </span>
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
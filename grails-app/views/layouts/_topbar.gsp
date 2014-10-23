<table border="0" xmlns="http://www.w3.org/1999/html">
    <tr>
        <td width="90px" align="left">
            <img class="efahrungLogo" align="left" width="80px"  src="${g.resource( dir: '/images', file: 'logo_efahrung.png' )}"/>
        </td>
        <td>
            <div class="wrap">
                <nav>
                    <ul id="menu">
                        <li><g:link controller="configuration" action="index"><g:message code="layouts._topbar.simulate"/></g:link>
                            <ul>
                                <li><g:link controller="configuration" action="index"><g:message code="layouts._topbar.new"/> </g:link></li>
                                <li><g:link controller="configuration" action="showRecentlyEditedConfiguration"><g:message code="layouts._topbar.open"/></g:link></li>
                                <li><g:link controller="configuration" action="loadFromFile"><g:message code="layouts._topbar.loadfromfile"/></g:link></li>
                                <li><a href="<g:createLink controller="login" action="logout" />"><g:message code="layouts._topbar.logout"/></a></li>
                            </ul>
                        </li>

                        <li><g:link controller="configuration" action="executeSimulations"><g:message code="layouts._topbar.execute"/></g:link></li>
                        <li><g:link controller="configuration" action="viewSimulations"><g:message code="layouts._topbar.view"/></g:link></li>

                        <li><g:link controller="configuration" action="showCarTypes"><g:message code="layouts._topbar.extras"/></g:link>
                            <ul>
                                <li><g:link controller="configuration" action="showCarTypes"><g:message code="layouts._topbar.addnewcartype"/></g:link></li>
                                <li><g:link controller="configuration" action="showFillingStationTypes"><g:message code="layouts._topbar.addnewelectricstation"/></g:link></li>
                                <li><g:link controller="mapView" action="listUsages"><g:message code="layouts._topbar.electricstationstatistic"/></g:link></li>
                            </ul>
                        </li>

                        <li><g:link controller="configuration" action="help"><g:message code="layouts._topbar.help"/></g:link></li>

                        <li><g:link controller="configuration" action="contact"><g:message code="layouts._topbar.contact"/></g:link></li>

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
                                                    <b>
                                                        <%-- ${welcome?.givenName} ${welcome?.familyName} --%>
                                                        <es:welcomeLoggedInUser/>
                                                    </b>
                                                </span>
                                                <a class="logout" href="<g:createLink controller="login" action="logout" />"> <g:message code="layouts._topbar.logout2"/></a>
                                            </span>
                                    </form>
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
            <img class="dfkiLogo" align="right" width="110px"  src="${g.resource( dir: '/images', file: 'dfki_logo.png' )}"/>
        </td>
    </tr>
</table>
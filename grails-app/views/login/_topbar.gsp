<table border="0">
    <tr>
        <td width="90px" align="left">
            <img class="efahrungLogo" align="left" width="80px"  src="${g.resource( dir: '/images', file: 'logo_efahrung.png' )}"/>
        </td>
        <td>
            <div class="wrap">
                <nav>
                    <ul id="menu">
                        <li><a><span></span> <g:message code="layouts._topbar.simulate"/></a>
                            <ul>
                                <li><a><g:message code="layouts._topbar.new"/> </a></li>
                                <li><a><g:message code="layouts._topbar.open"/></a></li>
                                <%--<li><a><g:message code="layouts._topbar.openresent"/></a></li>--%>
                                <li><a><g:message code="layouts._topbar.loadfromfile"/></a></li>
                                <li><a><g:message code="layouts._topbar.logout"/></a></li>
                            </ul>
                        </li>
                        <li><a><span class=""></span> <g:message code="layouts._topbar.execute"/></a>
                        </li>
                        <li><a><span class=""></span><g:message code="layouts._topbar.view"/></a>
                            <ul>
                                <%--<li><a><g:message code="layouts._topbar.viewresults"/></a></li>
                                <li><a><g:message code="layouts._topbar.exportinfile"/></a></li>--%>
                            </ul>
                        </li>
                        <li><a></span><g:message code="layouts._topbar.extras"/></a>
                            <ul>
                                <li><a><g:message code="layouts._topbar.addnewcartype"/></a></li>
                                <li><a><g:message code="layouts._topbar.addnewelectricstation"/></a></li>
                                <li><a><g:message code="layouts._topbar.electricstationstatistic"/></a></li>
                            </ul>
                        </li>
                        <li><a><span class=""></span><g:message code="layouts._topbar.help"/></a>
                            <ul>
                                <li><a><g:message code="layouts._topbar.help"/></a></li>
                                <li><a><g:message code="layouts._topbar.contact"/></a></li>
                            </ul>
                        </li>
                        <%--<span id="signup">
                            <g:if test="${errors != null}" >

                                Errors: ${errors}

                            </g:if>
                            <span id='login'>
                                <span class='inner'>

                                    <span class="registration1">
                                        <form action='${postUrl}' method='POST' id='loginForm' class='cssform' autocomplete='off'>
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
                                        </form>
                                       <script type='text/javascript'>
                                            <!--
                                            (function(){
                                                document.forms['loginForm'].elements['j_username'].focus();
                                            })();
                                            // -->
                                        </script>
                                    </span>
                                </span>
                            </span>
                        </span>--%>

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
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
                        <li><a href="#"><span></span> Simulate</a>
                            <ul>
                                <li><g:link controller="simulation" action="init">New </g:link></li>
                                <li><a href="#">Open...</a></li>
                                <li><a href="#">Open resent</a></li>
                                <li><a href="#">Load from file</a></li>
                                <li><a href="#">Log out</a></li>
                            </ul>
                        </li>
                        <li><a href="#"><span class=""></span> Execute</a>
                        </li>
                        <li><a href="#"><span class=""></span> View</a>
                            <ul>
                                <li><a href="#">View results</a></li>
                                <li><a href="#">Export results in file</a></li>
                            </ul>
                        </li>
                        <li><a href="#"><span class=""></span> Extras</a>
                            <ul>
                                <li><g:link controller="extras" action="index">Add new car type</g:link></li>
                                <li><a href="#">Add new electric station</a></li>
                                <li><a href="#">Electric station statistic</a></li>
                            </ul>
                        </li>
                        <li><a href="#"><span class=""></span> Help</a>
                            <ul>
                                <li><a href="#">Help</a></li>
                                <li><a href="#">Contact</a></li>
                            </ul>
                        </li>

                        <span class="registration1">
                            <li class="regristrationText">
                                <input type="text" size="10" value="user name">
                            </li>

                            <li class="regristrationText">
                                <input type="password" size="10" value="password">
                            </li>

                            <li class="regristrationText">
                                <input type="submit" value="log in">
                            </li>
                            <span class="registrationButtons">
                                <g:link class="registration" controller="fogotPassword" action="index">fogot password</g:link> <p>
                                <g:link class="registration" controller="registration" action="index">registration</g:link>
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
<%--<div id="menu">
    <nobr>

        <ul id="Navigation">


 <li>
     <g:link controller="simulation" action="init"><b>Simulation on Map</b></g:link>
 </li>

 <li>
     <g:link controller="simulationPreparator" action="index"><b>Configure Simulation</b></g:link>
 </li>

 <li>
     <g:link controller="simulationPreparator" action="index" params="[ viewOnly : 'true' ]" view="index"><b>Start Experiment</b></g:link>
 </li>

</ul>

</nobr>
</div>--%>

<div id='cssmenu'>

           <ul>
           <li class='active'><g:link controller="simulation" action="init"><span>Simulation on map</span></g:link></li>
           <li><g:link controller="simulationPreparator" action="index"><span>Configure simulation</span></g:link></li>
           <li><g:link controller="simulationPreparator" action="index" params="[ viewOnly : 'true' ]" view="index"><span>Start experiment</span></g:link></li>
           <li class='has-sub last'><a href='#'><span>Statistics</span></a>
            <ul>
                <li><a href='#'><span>Charging stations</span></a></li>
                <li><a href='#'><span>Driver profile</span></a></li>
                <li class='last'><a href='#'><span>OBU Statistics</span></a></li>
            </ul>
           </li>
           </ul>

</div>
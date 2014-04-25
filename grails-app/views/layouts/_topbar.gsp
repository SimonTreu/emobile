<div id="menu">
    <nobr>

        <ul id="Navigation">

            <%--
            <li>
                <g:link controller="mapView" action="googleMaps">Google Maps</g:link>
            </li>


            <li>
                <g:link controller="mapView" action="openLayersWithAction">Map</g:link>
            </li>
            --%>
            <li>
                <g:link controller="simulation" action="init">Simulation on Map</g:link>
            </li>

            <li>
                <g:link controller="simulationPreparator" action="index">Configure Simulation</g:link>
            </li>

            <li>
                <g:link controller="simulationPreparator" action="index" params="[ viewOnly : 'true' ]" view="index">Start Experiment</g:link>
            </li>
        </ul>


    </nobr>
</div>
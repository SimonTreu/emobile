
<r:require module="slider_resources" />
<r:layoutResources></r:layoutResources>

<div id="dd" class="lightboxmenu">

    <nobr>
        <div class="lightbox-title">Edit Simulation Route for TrackId ${trackId}</div>

        <span class="lightbox-close">
            <img src="${g.resource( dir: '/images', file: 'close.png' )}"
                 id="closeButtonForEdit"
                 width="30"
                 height="30"
                 alt="Close"
                 onclick="showTrackInfos( 'remove',4 )"
                 style="" />

        </span>
    </nobr>

</div>

<script>

    // save a copy of the select options in a javascript obj
    var mySelectOptions = ${maxEnergies};

    $(document).ready(function () {

        var $selectPack = $("#packSelector");
        var $selectE = $("#eSelector");

        $selectPack.change( function( e ) {
            // determine value selected
            var selectPackValue = $selectPack.val();  /* use .text() if you do not assign values to each select option */

            // determine available options based off hashmap
            var select2OptionsHtml = "";
            for( var i = 0; i <= selectPackValue; i++ ) {

                select2OptionsHtml += "<option>" + i + "</option>";

            }

            // insert new options into dom
            $selectE.html(select2OptionsHtml);

        });

    })

</script>


<div id="llk" class="lightboxselection">

    <g:formRemote name="save"
                  action="saveTrackInfo"
                  url="[action: 'saveTrackInfo']"
                  onComplete="saveTrackInfos()" >


        <g:hiddenField name="simulationRouteId" value="${simulationRouteId}" />


        <table class="lightboxselectiontable" >

            <tr>
                <td>
                    <g:message code="de.dfki.gs.domain.SimulationRoute.initialPersons" />
                </td>
                <td>
                    <g:select title="Initial Persons" name="initialPersons" from="${1..10}" value="${initialPersons}" />
                </td>
            </tr>
            <tr>
                <td>
                    <g:message code="de.dfki.gs.domain.SimulationRoute.carType" />
                </td>
                <td>
                    <g:select title="${message( code: 'de.dfki.gs.domain.carType' )}"
                              name="carType"
                              from="${carTypes}"
                              value="${selectedCarType.id}"
                              optionValue="name"
                              optionKey="id"
                    />
                </td>
            </tr>


            <tr>
                <td>
                    <g:message code="de.dfki.gs.domain.SimulationRoute.initialEnergy" />
                </td>
                <td> <%-- slider --%>

                    <script type="text/javascript">
                        $(document).ready(function(){
                            $("#slider").easySlider({
                                auto: true,
                                continuous: true,
                                numeric: true
                            });
                        });
                    </script>

                    <div id="slider"></div>

                    <g:select id="eSelector"
                              title="${message( code: 'de.dfki.gs.domain.SimulationRoute.initialEnergy')}"
                              name="initialEnergy"
                              from="${0..100}"
                              value="${initialEnergy}" />
                </td>
            </tr>

            <%--
            <tr>
                <td>
                    <g:message code="de.dfki.gs.domain.SimulationRoute.energyDrain" />
                </td>
                <td>
                    <g:select name="selectedEnergyDrain"
                              title="${message( code: 'de.dfki.gs.domain.SimulationRoute.energyDrain' )}"
                              from="${energyDrains}"
                              value="${selectedEnergyDrain}" />
                </td>
            </tr>
            --%>

        </table>


        <div id="llkkssd" class="lightboxtasks">

            <%--
            <g:hiddenField name="trackId" value="${trackId}" />
            <g:actionSubmit class="lightbox-submit-delete" value="Delete" name="remove" action="deleteTrack" onComplete="deleteTrackInfos( ${trackId} )">

            </g:actionSubmit>
            --%>
            <button type="button" name="myButton" class="lightbox-submit-delete"
                    onclick="${remoteFunction( action:'deleteTrack', controller: 'mapView', params: [ trackId : trackId ] ) }; deleteTrackInfos( ${trackId} )">Delete</button>

            
            <button type="submit" class="lightbox-submit-save"><i class="icon icon-warning-sign"></i> Save and Close</button>

        </div>


    <%-- submit Button: onchange: close this div, see above --%>

    </g:formRemote>



</div>
<r:layoutResources></r:layoutResources>

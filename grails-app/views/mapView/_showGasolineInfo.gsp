<div id="lkksd" class="lightboxmenu">
    <nobr>

        <div class="lightbox-title"><g:message code="de.dfki.gs.domain.GasolineStation.editChargingStation" /> ${name?:gasolineId}</div>

        <span class="lightbox-close">

            <img src="${g.resource( dir: '/images', file: 'close.png' )}"
                 id="closeButtonForEdit"
                 width="30"
                 height="30"
                 alt="Close"
                 onclick="showGasolineInfos( 'remove',4 )"
                 style="" />

        </span>

    </nobr>
</div>


<div class="lightboxselection">

    <g:formRemote name="save"
                  action="saveGasolineInfo"
                  url="[action: 'saveGasolineInfo']"
                  onComplete="saveGasolineInfos( '${gasolineId}', '${g.createLink( controller: 'mapView', action: 'retrieveTypeForGasoline' )}' )" >


        <g:hiddenField name="gasolineId" value="${gasolineId}" />

        <table class="lightboxselectiontable" >

            <tr>
                <td>
                    <g:message code="de.dfki.gs.domain.GasolineStation.type" />
                </td>
                <td>
                    <g:select title="Gasoline Type"
                              name="gasolineType"
                              from="${gasolineTypes}"
                              value="${selectedType}"
                    />
                </td>
            </tr>

        </table>

        <div id="dksff" class="lightboxtasks">

            <button type="button" name="myButton" class="lightbox-submit-delete"
                    onclick="${remoteFunction( action:'deleteGasolineStation', controller: 'mapView', params: [ gasolineId : gasolineId ] ) }; deleteGasolineStation( ${gasolineId} )">Delete</button>


            <button type="submit" class="lightbox-submit-save"><i class="icon icon-warning-sign"></i>Save and Close</button>

        </div>


    <%-- submit Button: onchange: close this div, see above --%>

    </g:formRemote>




</div>
<r:layoutResources></r:layoutResources>

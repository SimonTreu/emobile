<g:form controller="configuration" action="createFleet">

    <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>

    <div>
        <div>

            <div>
                <div>
                    <div class="layoutCellU">
                        <div class="leftbig"><b><g:message code="simulation.index.selectcarstype"/></b></div>
                    </div>
                    <div class="layoutCell">

                        <table id="dataTable"  border="0">

                            <tr class="cars">

                                <td align="">
                                    <g:select name="count" from="${1..100}" />
                                </td>

                                <td align="">
                                    <g:message code="simulation.index.carstype"/>
                                </td>

                                <td>
                                    <g:select name="carTypeId" from="${availableCarTypes}" optionKey="id" optionValue="name" />
                                </td>

                                <td>
                                    <g:hiddenField name="availableCarTypes" value="${availableCarTypes}"/>
                                    <g:hiddenField name="configurationStubId" value="${configurationStubId}"/>
                                    <g:hiddenField name="fleetStubId" value="${fleetStubId}"/>
                                    <g:submitToRemote class="addButton"
                                                      url="[action: 'updateFleetOfConfiguration']"
                                                      update="updateCar"
                                                      name="submit"
                                                      value="Add to Fleet" />
                                    <img width="22px"src="${g.resource( dir: '/images', file: 'add.png' )}">

                                </td>

                            </tr>
                        </table>

                    </div>

                    <div class="layoutCell" id="updateCar"></div>


                </div>
            </div>



        </div>


    </div>

    <g:submitButton name="createFleet" value="Create Fleet"/>

</g:form>
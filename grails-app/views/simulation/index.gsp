<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 18.09.13
  Time: 18:11
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Simulation</title>
    <meta name="layout" content="main" />
    <script language="javascript">
        function addRow(tableID) {

            var table = document.getElementById(tableID);

            var rowCount = table.rows.length;
            var row = table.insertRow(rowCount);

            var colCount = table.rows[0].cells.length;

            for(var i=0; i<colCount; i++) {

                var newcell = row.insertCell(i);

                newcell.innerHTML = table.rows[0].cells[i].innerHTML;
                //alert(newcell.childNodes);
                switch(newcell.childNodes[0].type) {
                    case "text":
                        newcell.childNodes[0].value = "";
                        break;
                    case "checkbox":
                        newcell.childNodes[0].checked = false;
                        break;
                    case "select-one":
                        newcell.childNodes[0].selectedIndex = 0;
                        break;
                }
            }
        }

        function deleteRow(tableID) {
            try {
                var table = document.getElementById(tableID);
                var rowCount = table.rows.length;

                for(var i=0; i<rowCount; i++) {
                    var row = table.rows[i];
                    var chkbox = row.cells[0].childNodes[0];
                    if(null != chkbox && true == chkbox.checked) {
                        if(rowCount <= 1) {
                            alert("Cannot delete all the rows.");
                            break;
                        }
                        table.deleteRow(i);
                        rowCount--;
                        i--;
                    }


                }
            }catch(e) {
                alert(e);
            }
        }
    </script>
    <script>
        $(document).ready(function() {
            $(".tabs-menu a").click(function(event) {
                event.preventDefault();
                $(this).parent().addClass("current");
                $(this).parent().siblings().removeClass("current");
                var tab = $(this).attr("href");
                $(".tab-content").not(tab).css("display", "none");
                $(tab).fadeIn();
            });

            $(".tabs-menu1 a").click(function(event) {
                event.preventDefault();
                $(this).parent().addClass("current");
                $(this).parent().siblings().removeClass("current");
                var tab = $(this).attr("href");
                $(".tab-content1").not(tab).css("display", "none");
                $(tab).fadeIn();
            });
        });
    </script>
</head>
<body>

<div class="pContainer">

        <form>
            <div class="d1">
            <fieldset>
                <legend>new simulation:</legend>

                <div class="layout">
                    <div class="layoutLeft">
                        <div class="contentLeft">
                            <div class="rowU">
                                <div class="left"><b><g:message code="simulation.index.fleetconfiguration" /></b></div>
                                <div class="right"></div>
                                <div class="clear"></div>
                            </div>
                           <div class="row">
                                <div class="left"><g:message code="simulation.index.existentfleet"/></div>
                                <div class="right"><select size="1">
                                    <option value="1">1</option>
                                    <option value="2">2</option>
                                </select></div>
                                <div class="clear"></div>
                            </div>

                            <div class="row">
                                <div class="left"></div>
                                <div class="right"></div>
                                <div class="clear"></div>
                            </div>
                          <div class="rowL">
                                <div class="left"><g:message code="simulation.index.createnewfleet"/></div>
                                <div class="right"><a class="addButton" href="#join_form" id="join_pop"><img width="22px"src="${g.resource( dir: '/images', file: 'add.png' )}"><span class="addButtonText"> add</span></a></div>
                                <div class="clear"></div>
                          </div>
                        </div>
                    </div>
                    <div class="layoutRight">
                        <div class="contentLeft">
                            <div class="rowU">
                                <div class="leftbig"><b><g:message code="simulation.index.fillingconfiguration"/></b></div>
                                </div>
                            <div class="row">
                                <div class="left"><g:message code="simulation.index.selectgroup"/></div>
                                <div class="right"><select size="1">
                                    <option value="1">1</option>
                                    <option value="2">2</option>
                                </select></div>
                                <div class="clear"></div>
                            </div>

                            <div class="row">
                                <div class="left"></div>
                                <div class="right"></div>
                                <div class="clear"></div>
                            </div>
                            <div class="rowL">
                                <div class="left"><g:message code="simulation.index.createnewgroup"/></div>
                                <div class="right"><a class="addButton" href="#join_form1" id="join_pop1"><img width="22px"src="${g.resource( dir: '/images', file: 'add.png' )}"><span class="addButtonText"> add</span></a></div>
                                <div class="clear"></div>
                            </div>
                        </div>
                    </div>
                    <div class="layoutImage">
                        <div class="contentRight">
                            <img width="30px"src="${g.resource( dir: '/images', file: 'weather.png' )}"><br><br>
                            <img width="30px"src="${g.resource( dir: '/images', file: 'settings.png' )}"><br><br><br><br>
                            <img width="30px"src="${g.resource( dir: '/images', file: 'car.png' )}"><br>
                            <img width="44px"src="${g.resource( dir: '/images', file: 'station.png' )}">
                        </div>
                    </div>

                </div>
                <br><br><br>
                <div class="layoutButton">
                    <span class="layoutButtonL"><g:submitButton name="send" value="CANCEL"/></span>
                    <span class="layoutButtonM"></span>
                    <span class="layoutButtonR"><g:submitButton name="send" value="SAVE"/></span>
                </div>


            </fieldset>
            </div>

            <!-- Add fleet form -->
            <a href="#x" class="overlay" id="join_form"></a>
            <div class="popup">
                <div class="layout">

                    <div class="layoutLeft">
                        <div class="contentLeft">
                            <div class="layoutCellU">
                                <div class="leftbig"><b><g:message code="simulation.index.selectcarstype"/></b></div>
                            </div>
                            <div class="layoutCell">

                                  <TABLE id="dataTable"  border="0">
                                    <TR class="cars">
                                        <TD><INPUT type="checkbox" name="chk"/></TD>
                                        <TD align="">
                                            <select size="1">
                                                <option value="1">1</option>
                                                <option value="2">2</option>
                                            </select>
                                        </TD>
                                        <td align=""> &nbsp;&nbsp; <g:message code="simulation.index.carstype"/> &nbsp;&nbsp;</td>
                                        <TD align="">
                                            <g:each in="${carTypeCars}" var="item">
                                                <g:form action="addCarTypeInstances" >  x
                                            <g:select name="${item.key.name}" from="${item.key.name}"/>
                                                </g:form>

                                            </g:each>
                                        </TD>
                                    </TR>
                                </TABLE>

                            </div>

                            <div class="layoutCellL">
                                <span class="leftR">
                                    <a class="addButton" onclick="deleteRow('dataTable')"><img width="22px" src="${g.resource( dir: '/images', file: 'delete.png' )}"><span class="addButtonText"> delete car group</span></a>
                                </span>
                                <div class="right">
                                    <a class="addButton" onclick="addRow('dataTable')"><img width="22px" src="${g.resource( dir: '/images', file: 'add.png' )}"><span class="addButtonText"> add further car</span></a>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="layoutRight">
                        <div id="tabs-container">
                            <ul class="tabs-menu">
                                <li class="current"><a href="#tab-1">Distributed</a></li>
                                <li><a href="#tab-2">Own routes</a></li>
                                <li><a href="#tab-3">Show on map</a></li>
                            </ul>
                            <div class="tab">
                                <div id="tab-1" class="tab-content">
                                    <p>Lorlis.</p>
                                </div>
                                <div id="tab-2" class="tab-content">
                                    <p>
                                        <input type="file">
                                    </p>

                                </div>
                                <div id="tab-3" class="tab-content">
                                    <p>Duis egestas fermentum ipsum et commodo. Proin bibendum consectetur elit, ha. </p>
                                </div>

                            </div>
                        </div>

                    </div>


                </div>
                <br><br><br>

                <div class="layoutButton">
                    <span class="layoutButtonL"><g:submitButton name="send" value="CANCEL"/></span>
                    <span class="layoutButtonM"></span>
                    <span class="layoutButtonR"><g:submitButton name="send" value="SAVE"/></span>
                </div>
                <a class="close" href="#close"></a>
            </div>
            <!--END  Add fleet form -->


        <!-- Add electric station form -->
        <a href="#x" class="overlay" id="join_form1"></a>
        <div class="popup">
            <div class="layout">

                <div class="layoutLeft" id="cell">
                    <div class="contentLeft">
                        <div class="layoutCellU">
                            <div class="leftbig"><b>Select filling stations for fleet:</b></div>
                        </div>
                        <div class="layoutCell">

                            <TABLE id="dataTable1"  border="0">
                                <TR class="cars">
                                    <TD><INPUT type="checkbox" name="chk"/></TD>
                                    <TD align="">
                                        <select size="1">
                                            <option value="1">1</option>
                                            <option value="2">2</option>
                                        </select>
                                    </TD>
                                    <td align=""> &nbsp;&nbsp; Stations of type: &nbsp;&nbsp;</td>
                                    <TD align="">
                                        <SELECT name="car">
                                            <option value="22,2 kW">22,2 kW</option>
                                            <option value="11,1 kW">11,1 kW</option>
                                        </SELECT>
                                    </TD>
                                </TR>
                            </TABLE>

                        </div>

                        <div class="layoutCellL">
                            <span class="leftR">
                                <a class="addButton" onclick="deleteRow('dataTable1')"><img width="22px" src="${g.resource( dir: '/images', file: 'delete.png' )}"><span class="addButtonText"> delete station group</span></a>
                            </span>
                            <div class="right">
                                <a class="addButton" onclick="addRow('dataTable1')"><img width="22px" src="${g.resource( dir: '/images', file: 'add.png' )}"><span class="addButtonText"> add further stations</span></a>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="layoutRight">
                    <div id="tabs-container1">
                        <ul class="tabs-menu1">
                            <li class="current"><a href="#tab-10">Public stations</a></li>
                            <li><a href="#tab-20">Public stations</a></li>
                            <li><a href="#tab-30">Both</a></li>
                        </ul>
                        <div class="tab">
                            <div id="tab-10" class="tab-content1">
                                <p>Lorlis.</p>
                            </div>
                            <div id="tab-20" class="tab-content1">
                                <p>
                                    <input type="file">
                                </p>

                            </div>
                            <div id="tab-30" class="tab-content1">
                                <p>Duis egestas fermentum ipsum et commodo. Proin bibendum consectetur elit, ha. </p>
                            </div>

                        </div>
                    </div>

                </div>


            </div>
            <br><br><br>

            <div class="layoutButton">
                <span class="layoutButtonL"><g:submitButton name="send" value="CANCEL"/></span>
                <span class="layoutButtonM"></span>
                <span class="layoutButtonR"><g:submitButton name="send" value="SAVE"/></span>
            </div>
            <a class="close" href="#close"></a>
        </div>
        <!--END Add electric station form -->


        </form>

    </div>

</body>
</html>

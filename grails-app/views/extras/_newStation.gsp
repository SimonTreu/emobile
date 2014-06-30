<%--
  Created by IntelliJ IDEA.
  User: anbu02
  Date: 28.06.14
  Time: 23:57
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
</head>

<body>

    <div class="newCar">
        <div class="contentLeft">
            <div class="layoutCellU">
                <div class="leftbig"><b>Electric stations types:</b></div>
            </div>
            <div class="layoutCell">

                <TABLE id="dataTable1"  border="0">
                    <TR class="cars">
                        <TD></TD>
                        <TD align="">

                        </TD>
                        <td align=""> &nbsp;&nbsp; 3,7 kW &nbsp;&nbsp;</td>
                        <TD align="" valign="center">
                            <a class="addButton" onclick=""><img width="22px" src="${g.resource( dir: '/images', file: 'edit.png' )}"><span class="addButtonText">  edit</span></a>
                        </TD>
                    </TR>
                </TABLE>

            </div>

            <div class="layoutCell">
                <span class="leftR">

                </span>
                <div class="right">
                    <a class="addButton" onclick=""><img width="22px" src="${g.resource( dir: '/images', file: 'add.png' )}"><span class="addButtonText"> add new station type</span></a>
                </div>
            </div>
        </div>
    </div>

</body>
</html>
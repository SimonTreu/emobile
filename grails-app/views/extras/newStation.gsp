<%--
  Created by IntelliJ IDEA.
  User: anbu02
  Date: 28.06.14
  Time: 23:57
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Add new station type</title>
    <meta name="layout" content="main" />
</head>

<body>

    <div class="newCar">
        <div class="contentLeft">
            <div class="layoutCellU">
                <div class="leftbig"><b>Electric stations types:</b></div>
            </div>
            <div class="layoutCell">

                <table id="dataTable1"  border="0">
                    <g:each in="${electricStations}" var="item">
                        <TR class="cars">
                            <td align="">
                                &nbsp;&nbsp; ${item} &nbsp;&nbsp;
                            </td>
                            <TD align="" valign="center">
                                <a class="addButton" onclick=""><img width="16px" src="${g.resource( dir: '/images', file: 'edit.png' )}">
                                    <span class="addButtonText1">  edit</span></a>
                            </TD>
                        </TR>
                    </g:each>
                </TABLE>

            </div>

            <div class="layoutCellL">
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
<%--
  Created by IntelliJ IDEA.
  User: anbu02
  Date: 28.06.14
  Time: 23:56
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Add new car type</title>
    <meta name="layout" content="main" />

</head>

<body>
<div class="pContainer">

<%-- to edit rows --%>
    <g:each in="${fillingStationTypes}" var="fillingStationType">
        <div class="left1"> &nbsp;&nbsp; ${fillingStationType.name} &nbsp;&nbsp; </div>
        <div class="right1">

            <g:form action="editFillingStationTypeView">

                <g:hiddenField name="fillingStationTypeId" value="${fillingStationType.id}"/>
                <g:submitToRemote class="addButton" url="[ action: 'editFillingStationTypeView' ]" update="updateMe" name="submit" value="Edit" />

            </g:form>


        </div>
        <div class="clear"></div>
    </g:each>


<%-- create-new --%>
    <g:form action="createFillingStationTypeView">

        spooky hooky, add carspicture.. +css
        <g:submitToRemote class="addButton" url="[action: 'createFillingStationTypeView']" update="updateMe" name="submit" value="Create" />

    </g:form>

<%-- this shpuld be displayed as "lightbox" --%>
    <div id="updateMe"></div>

</div>


</body>
</html>
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
        <g:each in="${carTypes}" var="carType">
            <div class="left1"> &nbsp;&nbsp; ${carType.carType.name} &nbsp;&nbsp; </div>
            <div class="right1">

                <g:form action="editCarTypeView">

                    <g:hiddenField name="carTypeId" value="${carType.carType.id}"/>
                    <g:submitToRemote class="addButton" url="[ action: 'editCarTypeView' ]" update="updateMe" name="submit" value="Edit" />

                </g:form>


            </div>
            <div class="clear"></div>
        </g:each>


        <%-- create-new --%>
        <g:form action="createCarTypeView">

            spooky hooky, add carspicture.. +css
            <g:submitToRemote class="addButton" url="[action: 'createCarTypeView']" update="updateMe" name="submit" value="Create" />

        </g:form>

        <%-- this shpuld be displayed as "lightbox" --%>
        <div id="updateMe"></div>

    </div>


</body>
</html>
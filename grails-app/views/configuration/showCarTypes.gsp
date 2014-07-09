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
        <div class="newCar">
        <%-- to edit rows --%>
            <div class="contentLeftCar">
            <div class="rowU">
                <div class="left1"><b>Car types:</b></div>
                <div class="right1"></div>
                <div class="clear"></div>
            </div>
            <div class="row">
                <g:each in="${carTypes}" var="carType">
                    <div class="left1"> &nbsp;&nbsp; ${carType.carType.name} &nbsp;&nbsp; </div>
                    <div class="right1">

                        <g:form action="editCarTypeView">

                            <g:hiddenField name="carTypeId" value="${carType.carType.id}"/>

                            <g:submitToRemote class="addButton" url="[ action: 'editCarTypeView' ]" update="updateMe" name="submit" value="edit">
                                <img width="16px" src="${g.resource( dir: '/images', file: 'edit.png' )}">
                            </g:submitToRemote>

                        </g:form>

                    </div>
                    <div class="clear"></div>
                </g:each>
                </div>

                <div class="rowL">
                    <g:form action="createCarTypeView">
                        <div class="left1"></div>
                        <div class="right2"> <g:submitToRemote class="addButton" url="[action: 'createCarTypeView']" update="updateMe" name="submit" value="create new car type" /><img width="22px"src="${g.resource( dir: '/images', file: 'add.png' )}"></div>
                        <div class="clear"></div>
                    </g:form>
                </div>

            </div>


        <%-- create-new --%>

        <%-- this shpuld be displayed as "lightbox" --%>
            <div id="updateMe"></div>


            <!--END  Add fleet form -->


      </div>
    </div>


</body>
</html>
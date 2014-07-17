<%--
  Created by IntelliJ IDEA.
  User: anbu02
  Date: 28.06.14
  Time: 23:56
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><g:message code="configuration.showFillingStationTypes.addnewfillingstation"/></title>
    <meta name="layout" content="main" />

</head>

<body>
<div class="pContainer">
    <div class="carTypes">
        <div class="rowUp">
            <div class="leftBold"><b><g:message code="configuration.showFillingStationTypes.electrictypes"/></b></div>
            <div class="right0PX"></div>
            <div class="clear"></div>
        </div>
        <div class="rowMiddle">
            <g:each in="${fillingStationTypes}" var="fillingStationType">
                <div class="leftCarTypes"> &nbsp;&nbsp; ${fillingStationType.name} &nbsp;&nbsp; </div>
                <div class="right0PX">

                    <g:form action="editFillingStationTypeView">

                        <g:hiddenField name="fillingStationTypeId" value="${fillingStationType.id}"/>
                        <g:submitToRemote class="addButton" url="[ action: 'editFillingStationTypeView' ]" update="updateMe" name="submit" value="Edit" />

                    </g:form>


                </div>
                <div class="clear"></div>
            </g:each>
        </div>
        <%-- create-new --%>
        <div class="rowDown">
            <g:form action="createFillingStationTypeView">
                <div></div>
                <div class="rightOnlyButton">
                    <g:submitToRemote class="addButton" url="[action: 'createFillingStationTypeView']" update="updateMe" name="submit" value="Create" />
                </div>
                <div class="clear"></div>
            </g:form>
        </div>
    </div>
<%-- this shpuld be displayed as "lightbox" --%>
    <div id="updateMe"></div>
</div>

</body>
</html>
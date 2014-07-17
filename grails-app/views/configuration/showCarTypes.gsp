<%--
  Created by IntelliJ IDEA.
  User: anbu02
  Date: 28.06.14
  Time: 23:56
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><g:message code="configuration.showCarTypes.addnewcar"/></title>
    <meta name="layout" content="main" />

</head>

<body>
    <div class="pContainer">
        <div class="carTypes">
            <div class="rowUp">
                <div class="leftBold"><b><g:message code="configuration.showCarTypes.cartypes"/></b></div>
                <div class="right0PX"></div>
                <div class="clear"></div>
            </div>
            <div class="rowMiddle">
                <g:each in="${carTypes}" var="carType">
                    <div class="leftCarTypes"> &nbsp;&nbsp; ${carType.carType.name} &nbsp;&nbsp;</div>
                    <div class="right0PX">
                        <g:form action="editCarTypeView">
                            <g:hiddenField name="carTypeId" value="${carType.carType.id}"/>
                            <g:submitToRemote class="addButton" url="[ action: 'editCarTypeView' ]" update="updateMe" name="submit" value="edit" id="openModal"/>
                       </g:form>
                    </div>
                    <div class="clear"></div>
                </g:each>
            </div>
            <div class="rowDown">
                <g:form action="createCarTypeView">
                    <div></div>
                    <div class="rightOnlyButton">
                        <g:submitToRemote class="addButton" url="[action: 'createCarTypeView']" update="updateMe" name="submit" value="create new car type" />
                    </div>
                    <div class="clear"></div>
                </g:form>
            </div>
            <div id="updateMe"></div>
        </div>
    </div>
</body>
</html>
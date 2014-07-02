<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 01.07.14
  Time: 22:11
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
</head>

<body>

    <table>

        <tr>
            <th>Username</th>
            <th>Given Name</th>
            <th>Family Name</th>
            <th>Allow</th>

        </tr>

        <g:each in="${pendingUsers}" var="pendingUser">

            <g:form controller="login" action="allowPersonToLogin" >

                <tr>
                    <td>${pendingUser.username}</td>
                    <td>${pendingUser.givenName}</td>
                    <td>${pendingUser.familyName}</td>
                    <g:hiddenField name="personId" value="${pendingUser.id}"/>
                    <td><g:submitButton name="Allow" /></td>
                </tr>

            </g:form>

        </g:each>
    </table>




</body>
</html>
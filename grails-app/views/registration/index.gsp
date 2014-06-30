<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Registration</title>
    <meta name="layout" content="main" />
</head>
<body>

<div class="registrationForm">
    <form method="POST" action="submit">
        <fieldset>
            <legend><g:message code="registration.index.registration"/></legend>
        <table>
            <tr><td height="5"></td>
            <tr>
                <td weight="20">
                <td align="center" class="registrationTitle"></td>
            </tr>
            <tr><td height="20"></td>
            <tr>
                <td><g:message code="registration.index.name"/></td><td><g:textField name="name"/></td>
            </tr>
            <tr><td height="10"></td>
            <tr>
                <td><g:message code="registration.index.firma"/></td><td><g:textField name="firma"/></td>
            </tr>
            <tr><td height="10"></td>
            <tr>
                <td><g:message code="registration.index.email"/></td><td><g:textField name="email"/></td>
            </tr>
            <tr><td height="10"></td>
            <tr>
                <td><g:message code="registration.index.user"/></td><td><g:textField name="username"/></td>
            </tr>
            <tr><td height="10"></td>
            <tr>
                <td><g:message code="registration.index.password"/></td><td><input name="password" type="password"/></td>
            </tr>
            <tr><td height="20"></td>
            <tr  align="right">
                <td weight="20">
                <td align="right"><g:submitButton name="send" value="SEND"/></td>
            </tr>
            <tr><td height="10"></td>
        </table>
            </fieldset>
    </form>
</div>
</body>
</html>

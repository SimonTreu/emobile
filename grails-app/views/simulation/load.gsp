<%--
  Created by IntelliJ IDEA.
  User: anbu02
  Date: 07.07.14
  Time: 11:02
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Load from file</title>
    <meta name="layout" content="main" />
</head>

<body>

<div class="pContainer">
    <g:form action='' method='POST' enctype='multipart/form-data'>
        <span class="simulations"><b>Available simulations:</b></span><g:select class="simulations" name="${availableSimulations.name}" from="${availableSimulations.name}"/>&nbsp;&nbsp;
        <input class="simulations" type='submit' name='upload_btn' value='Export results to file'>
    </g:form>
</div>
</body>
</html>
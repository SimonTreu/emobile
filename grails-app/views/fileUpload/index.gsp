<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 20.09.13
  Time: 17:46
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <meta name="layout" content="main" />
</head>
<body>

Upload Form: <br />
<g:uploadForm controller="fileUpload" action="upload" enctype="multipart/form-data" method="post">
    <input type="file" name="myFile" />
    <input type="submit" />
</g:uploadForm>


</body>
</html>
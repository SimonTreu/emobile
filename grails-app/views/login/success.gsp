<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 01.07.14
  Time: 16:43
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <script type='text/javascript' src="${resource(dir: 'js', file: 'prefix-free.js')}"></script>
    <script type='text/javascript' src="${resource(dir: 'js', file: 'jquery-1.9.0.js')}"></script>
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
    <link rel="apple-touch-icon" href="${resource(dir: 'images', file: 'apple-touch-icon.png')}">
    <link rel="apple-touch-icon" sizes="114x114" href="${resource(dir: 'images', file: 'apple-touch-icon-retina.png')}">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'mobile.css')}" type="text/css">


    <link href='http://fonts.googleapis.com/css?family=Droid+Sans' rel='stylesheet' type='text/css'>

    <link rel='stylesheet' href="${resource(dir: 'css', file: 'style.css')}" type='text/css' />
    <link rel='stylesheet' href="${resource(dir: 'css', file: 'iconic.css')}" type='text/css' />
</head>

<body>
<g:render template="/login/topbar"/>
    <span class="confirmEmail">
        <p>
            Welcome ${givenName} ${familyName}!

            <br><br>

            You can login now: <g:link uri="${loginLink}" url="${loginLink}"> <span class="loginName">login </span></g:link>
        </p>

    </span>

</body>
</html>
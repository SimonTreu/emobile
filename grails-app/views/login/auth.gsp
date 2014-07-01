<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 03.02.14
  Time: 14:57
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0;">
    <link rel="apple-touch-icon" href="images/template/engage.png"/>
</head>
<body>

<div id="signup">

    TestLink: ${testLink}

    Sign up with valid email address, name and company:
    Given Name:
    Family Name:
    Email Address:
    Company:

    <g:if test="${errors != null}" >

        Errors: ${errors}

    </g:if>


    <g:form controller="login" action="signin" id="signinForm" class="cssform" autocomplete="off">

        <p>
            <label for="signinUserName">Email Address</label>
            <g:textField name="signinUserName" id="signinUserName" value="" />
        </p>
        <p>
            <label for="signinGivenName">Given Name</label>
            <g:textField name="signinGivenName" id="signinGivenName" value="" />
        </p>
        <p>
            <label for="signinFamilyName">Family Name</label>
            <g:textField name="signinFamilyName" id="signinFamilyName" value="" />
        </p>


        <p>
            <label for="password">Password</label>
            <g:passwordField name="password" />
        </p>
        <p>
            <label for="confirm">Confirm Password</label>
            <g:passwordField name="confirm" />
        </p>

        <g:submitButton name="signinButton" value="Sign In"/>

    </g:form>


    Confirmation will be sent to this address and invalidates after one day.


</div>

<div id='login'>
    <div class='inner'>
        <g:if test='${flash.message}'>
            <div class='login_message'>${flash.message}</div>
        </g:if>
        <div class='fheader'>Please Login..</div>
        <form action='${postUrl}' method='POST' id='loginForm' class='cssform' autocomplete='off'>
            <p>
                <label for='username'>Login ID</label>
                <input type='text' class='text_' name='j_username' id='username' />
            </p>
            <p>
                <label for='password'>Password</label>
                <input type='password' class='text_' name='j_password' id='password' />
            </p>
            <p>
                <label for='remember_me'>Remember me</label>
                <input type='checkbox' class='chk' name='${rememberMeParameter}' id='remember_me'
                       <g:if test='${hasCookie}'>checked='checked'</g:if> />
            </p>
            <p>
                <input type='submit' value='Login' />
            </p>
        </form>
    </div>
</div>
<script type='text/javascript'>
    <!--
    (function(){
        document.forms['loginForm'].elements['j_username'].focus();
    })();
    // -->
</script>
</body>
</html>
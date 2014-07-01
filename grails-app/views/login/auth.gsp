<html>
<head>

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

<div id="page">
    <div id="spinner" class="spinner" style="display: none;">
        <img src="${createLinkTo(dir:'images',   file:'spinner.gif')}"
             alt="Spinner" />
    </div>
    <g:render template="/login/topbar"/>

    TestLink: ${testLink}

    Sign up with valid email address, name and company:
    Given Name:
    Family Name:
    Email Address:
    Company:
    <div id="content">
    <div id="signup">



        <g:if test="${errors != null}" >

            Errors: ${errors}

        </g:if>

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

        <%--<g:form controller="login" action="signin" id="signinForm" class="cssform" autocomplete="off">


            <g:textField name="signinUserName" id="signinUserName" value="" />
            <g:textField name="signinGivenName" id="signinGivenName" value="" />
            <g:textField name="signinFamilyName" id="signinFamilyName" value="" />


            <g:submitButton name="signinButton" value="Sign In"/>

        </g:form>


        Confirmation will be sent to this address and invalidates after one day.--%>


    </div>

    <div id='login'>
        <div class='inner'>
            <g:if test='${flash.message}'>
                <div class='login_message'>${flash.message}</div>
            </g:if>
            <div class='fheader'>Please Login!</div>
            <%--<form action='${postUrl}' method='POST' id='loginForm' class='cssform' autocomplete='off'>
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
            </form>--%>
        </div>
    </div>
    <script type='text/javascript'>
        <!--
        (function(){
            document.forms['loginForm'].elements['j_username'].focus();
        })();
        // -->
    </script>
    </div>


    <g:render template="/layouts/footer" />
</div>
</body>
</html>




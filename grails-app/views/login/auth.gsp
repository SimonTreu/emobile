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


    <div id="content">
    <div id="signup">



        <g:if test="${errors != null}" >

            Errors: ${errors}

        </g:if>

        <div class="pContainer">


                <div class="d1">
        <fieldset>
            <legend><g:message code="login.auth.registrate" /> or <g:message code="login.auth.login"/></legend>

            <div class="layout">
                <div class="layoutLeft">
                    <div class="contentLeft">
                        <g:form controller="login" action="signin" id="signinForm" class="cssform" autocomplete="off">
                            <div class="rowU">
                                <div class="left"><b><g:message code="login.auth.registrate" /></b></div>
                                <div class="right"></div>
                                <div class="clear"></div>
                            </div>
                            <div class="row">
                                <div class="left"><g:message code="login.auth.givenname"/></div>
                                <div class="right">
                                    <g:textField name="signinGivenName" id="signinGivenName" value="" />
                                </div>
                                <div class="clear"></div>
                            </div>

                            <div class="row">
                                <div class="left">
                                    <g:message code="login.auth.familyname"/>
                                </div>
                                <div class="right">
                                    <g:textField name="signinFamilyName" id="signinFamilyName" value="" />
                                </div>
                                <div class="clear"></div>
                            </div>

                            <div class="row">
                                <div class="left">
                                    <g:message code="login.auth.email"/>
                                </div>
                                <div class="right">
                                    <g:textField name="signinUserName" id="signinUserName" value="" />
                                </div>
                                <div class="clear"></div>
                            </div>

                            <div class="row">
                                <div class="left">
                                    <g:message code="login.auth.password"/>
                                </div>
                                <div class="right">
                                    <g:passwordField name="password" />
                                </div>
                                <div class="clear"></div>
                            </div>

                            <div class="row">
                                <div class="left">
                                    <g:message code="login.auth.cofirmpassword"/>
                                </div>
                                <div class="right">
                                    <g:passwordField name="confirm" />
                                </div>
                                <div class="clear"></div>
                            </div>

                            <div class="rowL">
                                <div class="left"></div>
                                <div class="right"><g:submitButton name="signinButton" value="Sign Up" /></div>
                                <div class="clear"></div>
                            </div>
                        </div>
                    </g:form>
                </div>
                <div class="layoutRightSignIn">
                    <div class="contentLeftSignIn">
                        <form action='${postUrl}' method='POST' id='loginForm' class='cssform' autocomplete='off'>
                            <div class="rowU">
                                <div class="leftbig"><b><g:message code="login.auth.login"/></b></div>
                            </div>
                            <div class="row">
                                <div class="left"><g:message code="login.auth.username"/></div>
                                <div class="right"><input type="text" size="22%" name='j_username' id='username' value=""></div>
                                <div class="clear"></div>
                            </div>

                            <div class="row">
                                <div class="left"><g:message code="login.auth.password"/></div>
                                <div class="right"><input type="password" size="22%" name='j_password' id='password' value="" /></div>
                                <div class="clear"></div>
                            </div>
                            <div class="rowL">
                                <div class="left"></div>
                                <div class="right"><input type="submit" value="<g:message code="layouts._topbar.login"/>"></div>
                                <div class="clear"></div>
                            </div>
                        </form>
                    </div>

                    <div class="contentLeftSignIn">
                        <%--<form action='${postUrl}' method='POST' id='loginForm' class='cssform' autocomplete='off'>--%>
                            <div class="rowU">
                                <div class="leftbig"><b><g:message code="login.auth.fogotPassword"/></b></div>
                            </div>
                            <div class="row">
                                <div class="left"><g:message code="login.auth.email"/></div>
                                <div class="right"><input type="text" size="20" name='j_username' id='' value=""></div>
                                <div class="clear"></div>
                            </div>

                            <div class="rowL">
                                <div class="left"></div>
                                <div class="right"><input type="submit" value="<g:message code="login.auth.send"/>"></div>
                                <div class="clear"></div>
                            </div>
                        <%--</form>--%>
                    </div>
                </div>
                <%--<div class="layoutImage">
                    <div class="contentRight">
                      <img width="30px"src="${g.resource( dir: '/images', file: 'weather.png' )}"><br><br>
                        <img width="30px"src="${g.resource( dir: '/images', file: 'settings.png' )}"><br><br><br><br>
                        <img width="30px"src="${g.resource( dir: '/images', file: 'car.png' )}"><br>
                        <img width="44px"src="${g.resource( dir: '/images', file: 'station.png' )}">
                    </div>
                </div>--%>


            </div>
            <br><br><br>
            <%--<div class="layoutButton">
                <span class="layoutButtonL"><g:submitButton name="send" value="CANCEL"/></span>
                <span class="layoutButtonM"></span>
                <span class="layoutButtonR"><g:submitButton name="send" value="SAVE"/></span>
            </div>--%>
            <span class="confirmationMessage">Confirmation will be sent to this address and invalidates shortly.</span>

        </fieldset>
        </div>
        </div>





        <%--<g:form controller="login" action="signin" id="signinForm" class="cssform" autocomplete="off">

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

        </g:form>--%>





    </div>

    <div id='login'>
        <div class='inner'>
            <g:if test='${flash.message}'>
                <div class='login_message'>${flash.message}</div>
            </g:if>
            <div class='fheader'></div>
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




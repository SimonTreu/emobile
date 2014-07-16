<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 10.07.14
  Time: 16:01
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>

<head>

    <title>Open resent</title>
    <meta name="layout" content="main" />

</head>

<body>
<div class="pContainerResent">
    <div class="d1">
        <fieldset>
            <legend> Open resent </legend>
            <div class="layoutResent">

                <table>

                    <tr>
                        <th>Configuration</th>
                        <th>Info text</th>
                        <th>Button</th>
                    </tr>

                    <g:each in="${configurations}" var="conf">

                        <tr>
                            <td>${conf.configurationId}</td>
                            <td>hua</td>
                            <td>Button</td>

                        </tr>

                    </g:each>

                </table>
            </div>
        </fieldset>
    </div>
</div>

</body>
</html>
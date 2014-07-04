<%--
  Created by IntelliJ IDEA.
  User: anbu02
  Date: 28.06.14
  Time: 23:56
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Add new station type</title>
    <meta name="layout" content="main" />
    <script>
        $(document).ready(function() {
            $(".tabs-menu a").click(function(event) {
                event.preventDefault();
                $(this).parent().addClass("current");
                $(this).parent().siblings().removeClass("current");
                var tab = $(this).attr("href");
                $(".tab-content").not(tab).css("display", "none");
                $(tab).fadeIn();
            });

            $(".tabs-menu1 a").click(function(event) {
                event.preventDefault();
                $(this).parent().addClass("current");
                $(this).parent().siblings().removeClass("current");
                var tab = $(this).attr("href");
                $(".tab-content1").not(tab).css("display", "none");
                $(tab).fadeIn();
            });
        });
    </script>
</head>

<body>
<div class="pContainer">
    <div class="newCar">
        <form>
            <div class="d1">
                <div class="contentLeftCar">
                    <div class="rowU">
                        <div class="left1"><b>Electric stations types:</b></div>
                        <div class="right1"></div>
                        <div class="clear"></div>
                    </div>
                    <div class="row">
                        <g:each in="${electricStations}" var="item">
                            <div class="left1"> &nbsp;&nbsp; ${item} &nbsp;&nbsp; </div>
                            <div class="right1">
                                <a class="addButton" href="#join_form" id="join_pop"><img width="16px" src="${g.resource( dir: '/images', file: 'edit.png' )}">
                                    <span class="addButtonText1">  edit</span></a>
                            </div>
                            <div class="clear"></div>
                        </g:each>
                    </div>

                    <div class="rowL">
                        <div class="left1"></div>
                        <div class="right2"><a class="addButton" href="#join_form" id="join_pop"><img width="22px"src="${g.resource( dir: '/images', file: 'add.png' )}"><span class="addButtonText"> add new station type</span></a></div>
                        <div class="clear"></div>
                    </div>
                </div>

                <a href="#x" class="overlay1" id="join_form"></a>
                <div class="popup1">
                    <div class="layout">


                        <div class="contentLeft">
                            <div class="rowU">
                                <div class="leftbig"><b>Create new station</b></div>
                            </div>
                            <div>
                                <div class="row">
                                    <div class="left">Station name</div>
                                    <div class="right"><input type="text" size="22%" value=""/></div>
                                    <div class="clear"></div>
                                </div>
                                <div class="row">
                                    <div class="left">Station power kW</div>
                                    <div class="right"><input type="text" size="22%" value=""/></div>
                                    <div class="clear"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <br><br><br>

                    <div class="layoutButton">
                        <span class="layoutButtonL"><g:submitButton name="send" value="CANCEL"/></span>
                        <span class="layoutButtonM"></span>
                        <span class="layoutButtonR"><g:submitButton name="send" value="SAVE"/></span>
                    </div>
                    <a class="close" href="#close"></a>
                </div>
                <!--END  Add fleet form -->

            </div>
        </form>
    </div>
</div>
</body>
</html>
<%--
  Created by IntelliJ IDEA.
  User: glenn
  Date: 08.04.14
  Time: 11:00
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html class="js">
<head>
    <title>Simulation</title>

    <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
    <g:javascript library="jquery" />
    <g:javascript src="application.js" />

    <r:layoutResources/>
    <meta name="layout" content="main" />

</head>

<body onload="drawExperimentContainer( ${routeCount} );">

<script type="text/javascript">

    var g_playing = false;
    var namesDrawn = false;
    var maxTotalKm = 0;


    function toggle_button_clicked() {
        if(g_playing)
        {
            pause();
        }

        else
        {
            play();
        }
    }

    function pause() {

        window.clearInterval( active );

        // call controller simulation PAUSE:
        jQuery.ajax({
            url: '${g.createLink( controller: 'simulationExecutor', action: 'pauseExperiment' )}',
            type: "POST",
            success: function( data ) {

            }
        });

        g_playing = !g_playing;
        var button = document.getElementById('button_play_pause');
        button.innerHTML = "<b>Play</b>";
    }

    function play() {
        var button = document.getElementById('button_play_pause');
        button.innerHTML = "<b>Pause</b>";
        g_playing = true;

        // action to the controller:
        jQuery.ajax({
            url: '${g.createLink( controller: 'simulationExecutor', action: 'proceedExperiment' )}',
            type: "POST",
            success: function( data ) {

            }
        });

        active = window.setInterval( function() {
                    showInfoPane();
                }, 1000 // 1000 ms
        );

    }

    function showInfoPane() {

        // get infos for one simulationRoutes
        jQuery.ajax({
            url: '${ g.createLink( controller: 'simulationExecutor', action: 'getInfo' ) }',
            data: JSON.stringify( { data: { sessionId : "${sessionId}" } } ),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            type: "POST",
            success: function( data ) {

                var info = data[ 'info' ]

                drawCarInfos( info )


                // $( '#speedInfo').html( speed );

            },
            error: function( data ) {

                // console.log( data );

            }
        });

    }

    function drawCarInfos( info ) {

        var canvas = document.getElementById('experimentContainer');

        var nameCanvas = document.getElementById('nameContainer');
        // canvas.width = canvas.width;

        if (canvas.getContext){

            // use getContext to use the canvas for drawing
            var ctx = canvas.getContext('2d');

            ctx.clearRect ( 0 , 0 , 1200 , 500 );

            var routeCount = info.length;
            var sizeForRoute = 1200 / routeCount;

            // find maxTotalKm
            if ( maxTotalKm == 0 ) {

                for ( var dp = 0; dp < routeCount; dp++ ) {
                    var car = info[ dp ];
                    var totalKmToDrive = car[ 'totalKmToDrive' ];
                    if ( totalKmToDrive > maxTotalKm ) {
                        maxTotalKm = totalKmToDrive;
                    }
                }

            }




            /**
            * drawing status of cars
             * ROUTE DRIVEN
             */
            for ( var dp = 0; dp < routeCount; dp++ ) {

                var car = info[ dp ];

                // ctx.fillStyle="#112233";
                ctx.fillStyle="rgba(10, 2, 45, 0.2)"

                // max height of rect
                var x = ( car[ 'totalKmToDrive' ] / maxTotalKm ) * 500;

                var routeAccomblished = x * car[ 'drivenKm' ] / car[ 'totalKmToDrive' ];

                // upperLeft.x, upperLeft.y, width, height
                ctx.fillRect( dp * sizeForRoute, 500-routeAccomblished, sizeForRoute, routeAccomblished );

                ctx.stroke();

            }


            for ( var dp = 0; dp < routeCount; dp++ ) {

                var car = info[ dp ];

                // ctx.fillStyle="#112233";
                ctx.fillStyle="rgba(10, 2, 45, 0.2)"

                // max height of rect
                var x = ( car[ 'totalKmToDrive' ] / maxTotalKm ) * 500;

                // upperLeft.x, upperLeft.y, width, height
                ctx.fillRect( dp * sizeForRoute, 500-x, sizeForRoute, 3 );

                ctx.stroke();

            }



            /**
             * drawing names of cars only once
             */
            if ( namesDrawn == false ) {


                var nameCtx = nameCanvas.getContext('2d');
                nameCtx.beginPath();
                nameCtx.font = "12px sans-serif ";
                nameCtx.fillStyle = "black";
                for ( var tx = 0; tx < routeCount; tx++ ) {

                    var car = info[ tx ];

                    nameCtx.fillText( car[ 'carName' ], tx * sizeForRoute, ( (tx%5)*10 ) + 10 );

                }
                nameCtx.stroke();
                namesDrawn = true;

            }


            /**
             * drawing status of cars
             * BATTERY STATUS
             */
            ctx.beginPath();
            for ( var db = 0; db < routeCount; db++ ) {

                var car = info[ db ];

                ctx.fillStyle="rgba(32, 45, 21, 0.2)"

                var batteryFill = ( 500 / 100 ) * car[ 'batteryFilledPercentage' ] ;

                // upperLeft.x, upperLeft.y, width, height
                ctx.fillRect( db * sizeForRoute, 500-batteryFill, sizeForRoute, batteryFill );


                ctx.strokeStyle = "#AABBCC";
                ctx.stroke();

            }


        }

    }

    function drawExperimentContainer( routeCount ){

        // get the canvas element using the DOM
        var canvas = document.getElementById('experimentContainer');
        var nameCanvas = document.getElementById( 'nameContainer' );


        // Make sure we don't execute when canvas isn't supported
        if (canvas.getContext){

            // use getContext to use the canvas for drawing
            var ctx = canvas.getContext('2d');
            var nameCtx = nameCanvas.getContext( '2d' );

            for (var x = 0.5; x < 1200.6; x += 10) {
                ctx.moveTo(x, 0);
                ctx.lineTo(x, 500);
            }

            for (var y = 0.5; y < 500.6; y += 10) {
                ctx.moveTo(0, y);
                ctx.lineTo(1200, y);
            }

            ctx.strokeStyle = "#eee";
            ctx.stroke();

            ctx.beginPath();

            var sizeForRoute = 1200 / routeCount;

            ctx.fillStyle="#AABBCC";
            nameCtx.fillStyle="#AABBCC";
            for ( var i = 0; i < routeCount; i++ ) {

                ctx.fillRect( i * sizeForRoute, 0, sizeForRoute, 500 );
                nameCtx.fillRect( i * sizeForRoute, 0, sizeForRoute, 50 );

            }

            ctx.strokeStyle = "#AABBCC";
            ctx.stroke();
            nameCtx.stroke();


        } else {
            alert('You need Safari or Firefox 1.5+ to see this demo.');
        }

    }

</script>

<div>
    ${routeCount}
    <button id="button_play_pause" onClick="toggle_button_clicked()"><b>Play</b></button>
    <br/>
    <button
            id="button_stopp"
            type="submit"
            onclick="location.href='${createLink( controller: 'simulationExecutor', action: 'stopExperiment', params: [ simulationId: simulationId ] ) }'"
    >
        <i class="icon icon-warning-sign"></i>Stop</button>
</div>

<br/>

<%--
<g:form id="hua" action="getInfo" >

    <input type="hidden" name="sessionId" value="${sessionId}" />

    <g:submitButton name="Get Info" />
</g:form>
--%>

<canvas id="experimentContainer" width="1200" height="500"></canvas>
<canvas id="nameContainer" width="1200" height="50"></canvas>


<g:render template="/layouts/footer" />


<r:layoutResources></r:layoutResources>
</body>
</html>
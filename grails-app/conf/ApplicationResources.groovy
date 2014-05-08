modules = {
    application {
        resource url:'js/application.js'
    }

    jquery {
        resource url:'/js/jq/jquery-1.9.1.js'
    }

    jqueryui {
        dependsOn 'jquery'
        resource url:'/js/jq/jquery-ui.js'
    }

    'slider_resources' {
        resource url: 'css/screen.css'
        resource url: 'js/easySlider1.7.js'
    }

}
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

}
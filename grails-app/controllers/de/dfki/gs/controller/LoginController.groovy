package de.dfki.gs.controller

import de.dfki.gs.controller.commands.SigninCommandObject
import grails.plugin.springsecurity.SpringSecurityUtils

class LoginController {


    def grailsApplication
    def authenticationTrustResolver
    def springSecurityService

    def index = {

        log.error( "frontpage requested" )

        if (springSecurityService.isLoggedIn()) {

            redirect uri: SpringSecurityUtils.securityConfig.successHandler.defaultTargetUrl

        }
        else {
            redirect action: 'auth', params: params
        }
    }

    /**
     * Show the login page.
     */
    def auth = {

        log.error( "auth.." )

        def config = SpringSecurityUtils.securityConfig

        if (springSecurityService.isLoggedIn()) {

            redirect uri: config.successHandler.defaultTargetUrl
            return
        }


        def savedUrl = session.SPRING_SECURITY_SAVED_REQUEST_KEY?.requestURL
        if( session.SPRING_SECURITY_SAVED_REQUEST_KEY?.queryString ) savedUrl = "${ savedUrl }?${ session.SPRING_SECURITY_SAVED_REQUEST_KEY?.queryString }"

        /*
        if( savedUrl?.contains( "/reader/" ) && session.SPRING_SECURITY_SAVED_REQUEST_KEY?.queryString?.contains( "&flash=timeout") ) {
            flash.message = "Your session has timed out"
        }
        */

        def savedUrlWithoutPrefix = null
        if( savedUrl != null ) savedUrlWithoutPrefix = savedUrl - grailsApplication.config.grails.serverURL

        String view = 'auth'
        String postUrl = "${request.contextPath}${config.apf.filterProcessesUrl}"
        render view: view, model: [
                postUrl: postUrl,
                rememberMeParameter: config.rememberMe.parameter,
                savedUrl: savedUrl
        ]
    }

    def logout = {

        redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl

    }


    def signin = {

        log.error( "params: ${params}" )

        SigninCommandObject cmd = new SigninCommandObject()
        bindData( cmd, params )

        if ( !cmd.validate() && cmd.hasErrors() ) {

            render view: 'auth', model: [ errors : cmd.errors ]

        }

        log.error( "hua! success" )

    }

}

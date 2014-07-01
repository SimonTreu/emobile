package de.dfki.gs.controller

import de.dfki.gs.controller.commands.ConfirmCommandObject
import de.dfki.gs.controller.commands.SigninCommandObject
import de.dfki.gs.domain.Person
import grails.plugin.springsecurity.SpringSecurityUtils

import javax.mail.SendFailedException

class LoginController {


    def grailsApplication
    def authenticationTrustResolver
    def springSecurityService
    def mailService
    def grailsLinkGenerator

    def personService

    def index = {

        log.error( "frontpage requested" )

        log.error( "loggedIn? ${((Person)springSecurityService.currentUser).username}" )

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

        def savedUrlWithoutPrefix = null
        if( savedUrl != null ) savedUrlWithoutPrefix = savedUrl - grailsApplication.config.grails.serverURL

        String view = 'auth'
        String postUrl = "${request.contextPath}${config.apf.filterProcessesUrl}"

        String testLink = "${grailsLinkGenerator.serverBaseURL}"
        log.error( "testLink: ${testLink}" )

        render view: view, model: [
                postUrl: postUrl,
                rememberMeParameter: config.rememberMe.parameter,
                savedUrl: savedUrl,
                testLink : "${grailsLinkGenerator.serverBaseURL - "emobilesim"}"
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

        def m = personService.createSigninPerson( cmd.signinGivenName, cmd.signinFamilyName, cmd.signinUserName, cmd.password )

        if ( m.person != null ) {

            Person p = m.person

            String confirmLink = "${grailsLinkGenerator.serverBaseURL}" - "/emobilesim" + createLink( controller: "login", action: "confirm",  params: [ username : p.username, confirmationCode : p.confirmationCode ] )


            log.error( "try sending " )
            try {
                mailService.sendMail {
                    to "${p.username}"
                    from "emobilesim-team"
                    subject "Your registration request"
                    body "Please click following link and log in: ${confirmLink}"
                }
            } catch ( SendFailedException e ) {
                log.error( "tried to send mail to ${p.username}", e )
            }


        }

        log.error( "hua! success: ${m}" )

        render view: "waiting"
    }

    def confirm = {

        log.error( "params: ${params}" )

        ConfirmCommandObject cmd = new ConfirmCommandObject()
        bindData( cmd, params )

        if ( !cmd.validate() && cmd.hasErrors() ) {

            // show errorPage
            log.error( "confirmation not successful: ${cmd.errors}" )

            redirect action: 'index'
        } else {

            Person p = Person.findByUsername( cmd.username )
            p.enabled = true
            p.accountLocked = false

            if ( !p.save( flush: true ) ) {
                log.error( "failed to save person: ${p.errors}" )
            }

            def m = [ : ]
            m.givenName = p.givenName
            m.familyName = p.familyName

            m.loginLink = "${grailsLinkGenerator.serverBaseURL}" - "/emobilesim" + createLink( controller: "front", action: "init" )

            render view: "success", model: m

        }

    }

}

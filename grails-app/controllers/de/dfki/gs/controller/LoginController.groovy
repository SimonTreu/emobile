package de.dfki.gs.controller

import de.dfki.gs.bootstrap.BootstrapHelper
import de.dfki.gs.controller.commands.AllowPersonCommandObject
import de.dfki.gs.controller.commands.ConfirmCommandObject
import de.dfki.gs.controller.commands.SigninCommandObject
import de.dfki.gs.domain.Person
import de.dfki.gs.domain.PersonRole
import de.dfki.gs.domain.Role
import grails.plugin.springsecurity.SpringSecurityUtils

import javax.mail.SendFailedException

class LoginController {


    def grailsApplication
    def authenticationTrustResolver
    def springSecurityService
    def mailService
    def grailsLinkGenerator
    def sendMailService

    def personService

    def index = {

        log.error( "frontpage requested" )
        def config = SpringSecurityUtils.securityConfig

        // log.error( "loggedIn? ${((Person)springSecurityService.currentUser).username}" )

        if (springSecurityService.isLoggedIn()) {

            // redirect uri: SpringSecurityUtils.securityConfig.successHandler.defaultTargetUrl


            // redirect( controller: "front", action: "init" )
            redirect uri: config.successHandler.defaultTargetUrl



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

                sendMailService.sendConfirmationMailToSigningUser( p, confirmLink )

            } catch ( SendFailedException e ) {
                log.error( "tried to send mail to ${p.username}", e )
            }


        }

        log.error( "hua! success: ${m}" )

        render view: "waiting"
    }

    /**
     * this method completes signing request of the user
     * next step is to allow user to log in
     */
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

            personService.confirmPersonAsUser( p )

            sendMailService.sendConfirmationRequestToAdmins( p )

            def m = [ : ]
            m.givenName = p.givenName
            m.familyName = p.familyName

            // m.loginLink = "${grailsLinkGenerator.serverBaseURL}" - "/emobilesim" + createLink( controller: "front", action: "init" )

            render view: "success", model: m

        }

    }

    def showPendingUsers() {

        def m = [ : ]

        Person requestingUser = (Person)springSecurityService.currentUser
        Role adminRole = Role.findByAuthority( "ROLE_ADMIN" )
        List<PersonRole> adminPersonRoles = PersonRole.findAllByRoleAndPerson( adminRole, requestingUser )

        if ( adminPersonRoles != null && adminPersonRoles.size() > 0 ) {
            List<Person> pendingPersons = Person.findAllByAccountLocked( true )

            m.pendingUsers = pendingPersons

        }



        render view: "pendings", model: m
    }

    def allowPersonToLogin() {

        AllowPersonCommandObject cmd = new AllowPersonCommandObject()

        bindData( cmd, params )

        if ( !cmd.validate() && cmd.hasErrors() ) {

            log.error( "Validation of requesting sign in user failed: ${cmd.errors}" )

        } else {

            Person person = Person.get( cmd.personId )
            personService.allowPersonToLogin( person )

            sendMailService.sendLoginAllowedMailToPerson( person )

        }



    }

}

package de.dfki.gs.security

import grails.plugin.springsecurity.SpringSecurityUtils
import org.apache.commons.logging.LogFactory
import org.springframework.security.core.Authentication

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by glenn on 01.07.14.
 */
class LogoutSecurityEventListener {

    private static def log = LogFactory.getLog( LogoutSecurityEventListener.class )

    void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        def principal = authentication?.principal
        String className = SpringSecurityUtils.securityConfig.userLookup.userDomainClassName

        log.error( "logout event listener.." )
    }

}

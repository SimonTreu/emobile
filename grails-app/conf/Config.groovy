// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

// usually is false by default, but we need sessionId to identify simulations
grails.views.enable.jsessionid=true

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [
    all:           '*/*',
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

environments {
    development {
        grails.logging.jul.usebridge = true

        // grails.serverURL = "http://localhost:8080"
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"

        // grails.serverURL = "http://serv-4101.kl.dfki.de:9090"
    }

    lnv {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }

}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    //info   'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
    //       'org.hibernate'
}

energyConfig {
    initialEnergy           = 500    // kWh
    initialPersons          = 1
    maxEnergy               = 500    // kWh
    energyPrice             = 25     // ct/kWh
    // batteryDrain            = 10.5   // kWh/100km
    batteryDrain            = 50.0   // kWh/100km
    batteryLevelLimitToStop = 1   // with 0.05% of energy we stop the ride
    // batteryLevelLimitToFill = 0.2    // it's time to look for a filling station
    batteryLevelLimitToFill = 20    // in % !!  it's time to look for a filling station

    fillingSlow             = 0.04   // kW/s    (ca 3h for 400kW)
    fillingFast             = 0.24   // kW/s  (ca 1/2h for 400)
    fillingMiddle           = 0.12   // kW/s

    batteries {
        type0 = 5
        type1 = 10
        type2 = 50
        type3 = 100
        type4 = 200
        type5 = 300
        type6 = 400
        type7 = 500
    }

    // batteries               = [ "50", "100", "200", "300", "400", "500" ]  // kWh
}


// Uncomment and edit the following lines to start using Grails encoding & escaping improvements

/* remove this line 
// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside null
                scriptlet = 'none' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
remove this line */

grails.cache.config = {
    osmGraphCache {
        maxElementsInMemory 2
        eternal false
        timeToIdleSeconds 86400
        timeToLiveSeconds 86400
        overflowToDisk false
        maxElementsOnDisk 0
        diskPersistent false
        diskExpiryThreadIntervalSeconds 120
        memoryStoreEvictionPolicy 'LRU'
    }
}

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'de.dfki.gs.domain.users.Person'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'de.dfki.gs.domain.users.PersonRole'
grails.plugin.springsecurity.authority.className = 'de.dfki.gs.domain.users.Role'
grails.plugin.springsecurity.requestMap.className = 'de.dfki.gs.domain.users.RequestMap'
grails.plugin.springsecurity.securityConfigType = 'Requestmap'


grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	'/':                              ['permitAll'],
	'/index':                         ['permitAll'],
	'/index.gsp':                     ['permitAll'],
	'/**/js/**':                      ['permitAll'],
	'/**/css/**':                     ['permitAll'],
	'/**/images/**':                  ['permitAll'],
	'/**/favicon.ico':                ['permitAll']
]

grails.plugin.springsecurity.successHandler.defaultTargetUrl = '/sim'
grails.plugin.springsecurity.auth.loginFormUrl = '/login/index'
grails.plugin.springsecurity.auth.failureHandler.defaultFailureUrl = '/?login_error=1'
grails.plugin.springsecurity.loginFormUrl = '/login/index'
grails.plugin.springsecurity.failureHandler.defaultFailureUrl = '/?login_error=1'
grails.plugin.springsecurity.adh.errorPage = '/?denied=1'
grails.plugin.springsecurity.sessionFixationPrevention.alwaysCreateSession=true
grails.plugin.springsecurity.useSecurityEventListener = false

grails.plugin.springsecurity.logout.handlerNames = [
        'rememberMeServices',
        'securityContextLogoutHandler'
        // 'securityLogoutEventListener'
]


grails.plugin.springsecurity.rememberMe.persistent = true
grails.plugin.springsecurity.rememberMe.persistentToken.domainClassName = 'de.dfki.gs.domain.users.PersistentLogin'



grails {
    mail {
        host = "smtp.gmail.com"
        port = 465
        username = "emobiledfki@gmail.com"
        password = "emobiledfki1"
        props = ["mail.smtp.auth":"true",
                 "mail.smtp.socketFactory.port":"465",
                 "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
                 "mail.smtp.socketFactory.fallback":"false"]
    }
}






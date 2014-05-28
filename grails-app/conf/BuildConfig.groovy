grails.servlet.version = "3.0"
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.war.file = "target/${appName}.war"

// uncomment (and adjust settings) to fork the JVM to isolate classpaths
//grails.project.fork = [
//   run: [maxMemory:1024, minMemory:64, debug:false, maxPerm:256]
//]
/*
grails.project.fork = [
        test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true], // configure settings for the test-app JVM
        run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256], // configure settings for the run-app JVM
        debug: [ maxMemory: 768, minMemory: 64, debug: true, maxPerm: 256],
        war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256], // configure settings for the run-war JVM
        console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]// configure settings for the Console UI JVM
]
*/

grails.project.dependency.resolver = "maven"

grails.project.dependency.resolution = {

    // The line pom true tells Grails to parse Maven's pom.xml and load dependencies from there.
    // see: http://grails.org/doc/2.1.0/guide/single.html#mavenIntegration
    // pom true

    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        //inherits true // Whether to inherit repository definitions from plugins

        mavenRepo "http://download.osgeo.org/webdav/geotools/"
        mavenRepo "http://repo.opengeo.org"


        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()


        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.

        runtime 'mysql:mysql-connector-java:5.1.26'

        runtime "org.geotools:gt-shapefile:8-SNAPSHOT"
        runtime "org.geotools:gt-swing:8-SNAPSHOT"
        runtime "org.geotools:gt-epsg-hsql:8-SNAPSHOT"
        runtime "org.geotools.jdbc:gt-jdbc-postgis:8-SNAPSHOT"
        runtime "org.geotools:gt-jdbc:8-SNAPSHOT"
        runtime "org.geotools:gt-graph:8-SNAPSHOT"

        runtime( "org.jfree:jfreechart:1.0.17" ) {
            excludes "xml-apis", "servlet-api", "junit"
        }
        //compile ""
        //compile ""

    }

    plugins {
        runtime ":hibernate:3.6.10.2"
        // runtime ":hibernate:2.3.1"

        runtime ":jquery:1.8.3"
        runtime ":resources:1.2"

        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.5"

        // build ":tomcat:$grailsVersion"
        build ":tomcat:7.0.42"
        // build ":tomcat:2.3.1"

        runtime ":database-migration:1.3.2"

        compile ":cache:1.1.1"

        compile ":quartz:1.0.1"
        compile ":calendar:1.2.1"
        compile ":google-visualization:0.7"
        // compile ':cache-ehcache:1.0.0'
    }
}

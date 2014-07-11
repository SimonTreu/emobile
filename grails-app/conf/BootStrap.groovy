import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Point
import de.dfki.gs.bootstrap.BootstrapHelper
import de.dfki.gs.domain.simulation.Car
import de.dfki.gs.domain.simulation.CarType
import de.dfki.gs.domain.simulation.FillingStation
import de.dfki.gs.domain.simulation.FillingStationGroup
import de.dfki.gs.domain.simulation.FillingStationType
import de.dfki.gs.domain.simulation.Fleet
import de.dfki.gs.domain.simulation.Simulation
import de.dfki.gs.domain.simulation.SimulationRoute
import de.dfki.gs.domain.simulation.Track
import de.dfki.gs.domain.users.Company
import de.dfki.gs.service.RouteService
import de.dfki.gs.threadutils.NotifyingBlockingThreadPoolExecutor
import de.dfki.gs.utils.LatLonPoint
import org.geotools.graph.path.Path
import org.geotools.graph.structure.Edge
import org.geotools.graph.structure.basic.BasicEdge
import org.geotools.graph.structure.basic.BasicNode
import org.opengis.feature.simple.SimpleFeature

import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

class BootStrap {

    def routeService

    def grailsApplication

    transient def securityContextPersistenceFilter


    def createDefaultFleet() {

        Company company = Company.findByName( "dfki" )

        if ( company == null ) {
            log.error( "no company for name dfki found" )
            return
        }

        Fleet dfki1Fleet = new Fleet(
            company: company,
            name: "Dfki-Fleet One",
            stub: false
        )

        if ( !dfki1Fleet.save( flush: true, failOnError: true ) ) {

            log.error( "failed to save fleet: ${dfki1Fleet.errors}" )
            return
        }

        CarType carType1 = CarType.get( 1 )
        if ( carType1 == null ) {
            log.error( "no cartype found" )
            return
        }
        10.times {
            Car car = new Car(
                carType: carType1,
                name: "car no.${it}"
            )
            if ( !car.save( flush: true, failOnError: true ) ) {
                log.error( "failed to save car: ${car.errors}" )
            }
        }

        CarType carType2 = CarType.get( 2 )
        if ( carType2 == null ) {
            log.error( "no cartype found" )
            return
        }
        10.times {
            Car car = new Car(
                    carType: carType2,
                    name: "car no.${it}"
            )
            if ( !car.save( flush: true, failOnError: true ) ) {
                log.error( "failed to save car: ${car.errors}" )
            }
        }

        List<Car> twentyCars = Car.findAll()

        twentyCars.each { Car car ->

            dfki1Fleet.addToCars( car )

        }

        if ( !dfki1Fleet.save( flush: true, failOnError: true ) ) {

            log.error( "failed to update dfkifleet: ${dfki1Fleet.errors}" )

        }



        Fleet dfki2Fleet = new Fleet(
                company: company,
                name: "Dfki-Fleet Two",
                stub: false
        )

        if ( !dfki2Fleet.save( flush: true, failOnError: true ) ) {

            log.error( "failed to save fleet: ${dfki2Fleet.errors}" )
            return
        }

        CarType carType3 = CarType.get( 3 )
        if ( carType3 == null ) {
            log.error( "no cartype found" )
            return
        }
        List<Car> twentyOtherCars = new ArrayList<Car>()
        10.times {
            Car car = new Car(
                    carType: carType3,
                    name: "car no.${it}"
            )
            if ( !car.save( flush: true, failOnError: true ) ) {
                log.error( "failed to save car: ${car.errors}" )
            }
            twentyOtherCars.add( car )
        }

        CarType carType4 = CarType.get( 4 )
        if ( carType4 == null ) {
            log.error( "no cartype found" )
            return
        }
        10.times {
            Car car = new Car(
                    carType: carType4,
                    name: "car no.${it}"
            )
            if ( !car.save( flush: true, failOnError: true ) ) {
                log.error( "failed to save car: ${car.errors}" )
            }
            twentyOtherCars.add( car )
        }



        twentyOtherCars.each { Car car ->

            dfki2Fleet.addToCars( car )

        }

        if ( !dfki2Fleet.save( flush: true, failOnError: true ) ) {

            log.error( "failed to update dfkifleet: ${dfki2Fleet.errors}" )

        }


    }

    def createDefaultGroup() {

        Company company = Company.findByName( "dfki" )

        if ( company == null ) {
            log.error( "no company for name dfki found" )
            return
        }

        FillingStationGroup dfki1Group = new FillingStationGroup(
                company: company,
                name: "Dfki-Group One",
        )

        if ( !dfki1Group.save( flush: true, failOnError: true ) ) {

            log.error( "failed to save group: ${dfki1Group.errors}" )
            return
        }

        List<FillingStation> dfki1GroupStations = new ArrayList<FillingStation>()
        FillingStationType.findAll().each { FillingStationType fillingStationType ->

            10.times {

                Point point = routeService.getRandomValidPoint()

                FillingStation station = new FillingStation(
                        fillingStationType: fillingStationType,
                        name: "${fillingStationType.name}-${it}",
                        lat: point.coordinate.x.toFloat(),
                        lon: point.coordinate.y.toFloat()
                )

                if ( !station.save( flush: true, failOnError: true ) ) {

                    log.error( "fail : ${station.errors}" )

                } else {
                    dfki1Group.addToFillingStations( station )
                }

            }

        }

        if ( !dfki1Group.save( flush: true, failOnError: true ) ) {

            log.error( "failed to update (add fillingStations) dfkigroup: ${dfki1Group.errors}" )

        }


    }


    def createDefaultCarTypes() {

        BootstrapHelper helper = new BootstrapHelper()

        helper.createDefaultCarTypeIfNotExist()

    }


    def createDefaultFillingStationTypes() {

        BootstrapHelper helper = new BootstrapHelper()

        helper.createDefaultFillingStationTypesIfNotExits()

    }



    def securityStuff() {

        securityContextPersistenceFilter.forceEagerSessionCreation = true


        def helper = new BootstrapHelper()

        def roleAdmin = helper.findOrCreateRole("ROLE_ADMIN")
        def roleEmployee = helper.findOrCreateRole("ROLE_EMPLOYEE")
        def roleUser = helper.findOrCreateRole("ROLE_USER")

        Company company = helper.findOrCreateCompany( "dfki" )

        // def userUser = helper.findOrCreatePersonInRole("glenn.schuetze@gmail.com", "Glenn", "Schütze", roleAdmin)
        def andreyUser = helper.findOrCreatePersonInRole( company, "andrey.bukhman@dfki.de", "Andrey", "Bukhman", roleUser )
        helper.findOrCreatePersonInRole( company, "andrey.bukhman@dfki.de", "Andrey", "Bukhman", roleAdmin )


        helper.findOrCreatePersonInRole( company, "glennsen@googlemail.com", "Glenn", "Schütze", roleUser)
        helper.findOrCreatePersonInRole( company, "glennsen@googlemail.com", "Glenn", "Schütze", roleAdmin)


        // helper.findOrCreateRequestmap( "/login/success", "IS_AUTHENTICATED_ANONYMOUSLY" )

        helper.findOrCreateRequestmap( "/sim", "ROLE_USER" )

        helper.findOrCreateRequestmap( "/simulation", "ROLE_USER" )

        helper.findOrCreateRequestmap( "/configuration", "ROLE_USER" )

        //

        // helper.findOrCreateRequestmap( "/gas", "ROLE_USER" )
        //helper.findOrCreateRequestmap( "/logout", "ROLE_USER" )

        // helper.findOrCreateRequestmap( "/gasapi", "IS_AUTHENTICATED_ANONYMOUSLY" )
        helper.findOrCreateRequestmap( "/**", "IS_AUTHENTICATED_ANONYMOUSLY" )


    }

    def init = { servletContext ->

        /**
         * MOP: appending 'grailsApplication'-bean to each of the domain objects
         */
        for ( dc in grailsApplication.domainClasses ) {
            dc.clazz.metaClass.getGrailsApplication = { -> grailsApplication }
            dc.clazz.metaClass.static.getGrailsApplication = { -> grailsApplication }
        }



        log.error( "check security config.." )
        securityStuff()
        log.error( " .. security config finished" )

        log.error( "preloading feature graph into application scope.." )
        routeService.getFeatureGraph( "osmGraph" )

        log.error( "create some default car types if not exist.." )
        createDefaultCarTypes()
        log.error( "..created or found: ${CarType.findAllByCompany( Company.findByName( "dfki" ) )*.name }" )

        log.error( "create some default electric stations.." )
        createDefaultFillingStationTypes()
        log.error( "..created or found: ${FillingStationType.findAllByCompany( Company.findByName( "dfki" ) )*.name} " )


        log.error( "create default fleet.." )
        createDefaultFleet()

        log.error( "create default group.." )
        createDefaultGroup()



    }
    def destroy = {
    }
}

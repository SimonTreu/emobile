import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Point
import de.dfki.gs.bootstrap.BootstrapHelper
import de.dfki.gs.domain.simulation.Car
import de.dfki.gs.domain.simulation.CarType
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

    def createDefaultCarTypes() {

        if ( CarType.count() < 1 ) {

            Company company = Company.findByName( "dfki" )

            // car BMW i3
            CarType bmwI3 = new CarType(
                    name: "BMW i3",
                    energyConsumption: 13.5,
                    maxEnergyLoad:     18.8,
                    company: company
            );


            if ( !bmwI3.save( flush: true ) ) {
                log.error( "failed to save BMW i3: ${bmwI3.errors}" )
            }

            // car MB Vito E-CELL
            CarType vito = new CarType(
                    name: "MB Vito E-CELL",
                    energyConsumption: 25.2,
                    maxEnergyLoad: 32,
                    company: company
            );

            if ( !vito.save( flush: true ) ) {
                log.error( "failed to save VITO : ${vito.errors}" )
            }

            // mitsubishi
            CarType mitsubishi = new CarType(
                    name: "Mitsubishi i-MiEV",
                    energyConsumption: 13.5,
                    maxEnergyLoad: 13,
                    company: company
            );

            if ( !mitsubishi.save( flush: true ) ) {
                log.error( "failed to save mitsubishi : ${mitsubishi.errors}" )
            }

            // Nissan
            CarType nissan = new CarType(
                    name: "Nissan Leaf",
                    energyConsumption: 17.4,
                    maxEnergyLoad: 24,
                    company: company
            );

            if ( !nissan.save( flush: true ) ) {
                log.error( "failed to save nissan : ${nissan.errors}" )
            }

            // Opel
            CarType opel = new CarType(
                    name: "Opel Ampera",
                    energyConsumption: 10.5,
                    maxEnergyLoad: 8,
                    company: company
            );

            if ( !opel.save( flush: true ) ) {
                log.error( "failed to save opel : ${opel.errors}" )
            }

            // Renault kangoo
            CarType renault = new CarType(
                    name: "Renault Kangoo Z.E.",
                    energyConsumption: 16,
                    maxEnergyLoad: 22,
                    company: company
            );

            if ( !renault.save( flush: true ) ) {
                log.error( "failed to save renault : ${renault.errors}" )
            }

            // renault zoe
            CarType renaultZoe = new CarType(
                    name: "Renault ZOE",
                    energyConsumption: 16,
                    maxEnergyLoad: 22,
                    company: company
            );

            if ( !renaultZoe.save( flush: true ) ) {
                log.error( "failed to save renaultZoe : ${renaultZoe.errors}" )
            }

            // vw
            CarType vw = new CarType(
                    name: "VW E-up!",
                    energyConsumption: 11.7,
                    maxEnergyLoad: 19,
                    company: company
            );

            if ( !vw.save( flush: true ) ) {
                log.error( "failed to save vw : ${vw.errors}" )
            }

            // a.s.o.
        }


    }


    def createDefaultElectricStations () {
        if (FillingStationType.count() < 1) {
            Company company = Company.findByName( "dfki" )

            //filling station 2,3 kW
            FillingStationType station2_3 = new FillingStationType(
                    name: "AC 2,3KW",
                    power: 2.3,
                    company: company
            );

            if ( !station2_3.save( flush: true ) ) {
                log.error( "failed to save station2_3: ${station2_3.errors}" )
            }

            //filling station 3,7 kW
            FillingStationType station3_7 = new FillingStationType(
                    name: "AC 3,7KW",
                    power: 3.7,
                    company: company
            );

            if ( !station3_7.save( flush: true ) ) {
                log.error( "failed to save station3_7: ${station3_7.errors}" )
            }
            //filling station 11,1 kW
            FillingStationType station_11 = new FillingStationType(
                    name: "AC 11,1KW",
                    power: 11.1,
                    company: company
            );

            if ( !station_11.save( flush: true ) ) {
                log.error( "failed to save station_11: ${station_11.errors}" )
            }
            //filling station 22,2 kW
            FillingStationType station_22 = new FillingStationType(
                    name: "AC 22,2KW",
                    power: 22.2,
                    company: company
            );

            if ( !station_22.save( flush: true ) ) {
                log.error( "failed to save station_22: ${station_22.errors}" )
            }
            //filling station 43 kW
            FillingStationType station_43 = new FillingStationType(
                    name: "AC 43KW",
                    power: 43,
                    company: company
            );

            if ( !station_43.save( flush: true ) ) {
                log.error( "failed to save station_43: ${station_43.errors}" )
            }
            //filling station 49,8 kW
            FillingStationType station_49 = new FillingStationType(
                    name: "DC 49,8KW",
                    power: 49.8,
                    company: company
            );

            if ( !station_49.save( flush: true ) ) {
                log.error( "failed to save station_49: ${station_49.errors}" )
            }
        }
    }

    def createBigSim( String simName, long howMuchRoutes, RouteService myRS ) {

        def xMin = 13.14248
        def xMax = 13.60871
        def xDist = xMax - xMin

        def yMin = 52.40010
        def yMax = 52.62450
        def yDist = yMax - yMin

        Simulation sim = new Simulation( name: simName )
        if ( !sim.save( flush: true ) ) {
            log.error( "failed to save simulation: ${sim.errors}" )
        }

        def saved = 0;




        int poolSize = 600;      // the count of currently paralellized threads
        int queueSize = 1200;    // recommended - twice the size of the poolSize
        int threadKeepAliveTime = 15;
        TimeUnit threadKeepAliveTimeUnit = TimeUnit.SECONDS;
        int maxBlockingTime = 10;
        TimeUnit maxBlockingTimeUnit = TimeUnit.MILLISECONDS;
        Callable<Boolean> blockingTimeoutCallback = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                // log.error("*** Still waiting for task insertion... ***");
                // nothing to be done here..
                return true; // keep waiting
            }
        };

        NotifyingBlockingThreadPoolExecutor threadPoolExecutorForPoints =
                new NotifyingBlockingThreadPoolExecutor(poolSize, queueSize,
                        threadKeepAliveTime, threadKeepAliveTimeUnit,
                        maxBlockingTime, maxBlockingTimeUnit, blockingTimeoutCallback);

        List<LatLonPoint> allPoints = Collections.synchronizedList( new ArrayList<LatLonPoint>() )

        long counter
        for ( counter = 0; counter < howMuchRoutes+1; counter++ ) {

            threadPoolExecutorForPoints.execute( new Runnable() {

                @Override
                void run() {

                    Random r = new Random();
                    double randomStartX = xMin + ( xDist ) * r.nextDouble();
                    double randomStartY = yMin + ( yDist ) * r.nextDouble();

                    Coordinate start  = new Coordinate( randomStartX, randomStartY );

                    Coordinate coordinate = myRS.getNearestValidPoint( start )

                    allPoints.add( new LatLonPoint( x: coordinate.x, y: coordinate.y ) )

                    log.error( "collected ${allPoints.size()} points" )
                }

            }
            );
        }

        def done1 = false

        int x = 50
        while ( !done1 && x > 0 ) {

            x--
            done1 = threadPoolExecutorForPoints.await( 20, TimeUnit.MILLISECONDS )

        }

        log.error( "added ${counter} valid points" )



        NotifyingBlockingThreadPoolExecutor threadPoolExecutor =
                new NotifyingBlockingThreadPoolExecutor(poolSize, queueSize,
                        threadKeepAliveTime, threadKeepAliveTimeUnit,
                        maxBlockingTime, maxBlockingTimeUnit, blockingTimeoutCallback);



        def pairs = allPoints.collate( 2, 1, false );

        List<ArrayList<BasicEdge>> allRoutes = Collections.synchronizedList( new ArrayList<ArrayList<BasicEdge>>() )

        for ( List startTarget : pairs ) {

            threadPoolExecutor.execute( new Runnable() {

                @Override
                void run() {

                    Coordinate start  = new Coordinate( startTarget.get( 0 ).x, startTarget.get( 0 ).y );
                    Coordinate target = new Coordinate( startTarget.get( 1 ).x, startTarget.get( 1 ).y );

                    ArrayList<BasicEdge> path1 = myRS.calculatePath( start, target )

                    if ( path1 && path1.size() > 1 ) {
                        ArrayList<BasicEdge> pathRepaired = myRS.repairEdges( path1 )

                        if ( pathRepaired && pathRepaired.size()  > 1 ) {
                            allRoutes.add( pathRepaired )
                            log.error( "added route nr.: ${pathRepaired.size()}" )
                        }

                    }



                }

            }
            );
        }

        def done = false

        while ( !done ) {

            done = threadPoolExecutor.await( 20, TimeUnit.MILLISECONDS )

        }

        log.error( "all ${allRoutes.size()} routes calculated, now saving" )

        for ( ArrayList<BasicEdge> rr : allRoutes ) {

            ArrayList<List<BasicEdge>> multiTargetRoute1 = new ArrayList<List<BasicEdge>>()

            multiTargetRoute1.add( rr )

            Long id1;

            if ( rr ) {

                id1 = myRS.persistRoute( multiTargetRoute1, false );

            }

            if ( id1 ) {

                SimulationRoute simRoute1 = new SimulationRoute(
                        track: Track.get( id1 ),
                        carType: CarType.audi.toString(),
                        initialPersons: 3,
                        initialEnergy: 477d,
                        maxEnergy: 500,
                        energyDrain: 50
                )
                if ( !simRoute1.save() ) {
                    log.error( "failed to save simulation route: ${simRoute1.errors}" )
                }

                sim.addToSimulationRoutes( simRoute1 )

            }


            if ( !sim.save() ) {
                log.error( "failed to save simulation: ${sim.errors}" )
            } else {
                saved++
                log.error( "saved ${saved} routes" )
            }


        }

        /*
        try {
            boolean done = false;
            do {
                done = threadPoolExecutor.await(20, TimeUnit.MILLISECONDS);
            } while(!done);
        } catch (InterruptedException e) {
            log.error(e);
        }
        */

        log.error( "done" );



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

        log.error( "check security config.." )
        securityStuff()
        log.error( " .. security config finished" )

        log.error( "preloading feature graph into application scope.." )
        routeService.getFeatureGraph( "osmGraph" )

        log.error( "create some default cars.." )
        createDefaultCarTypes()

        log.error( "create some default electric stations.." )
        createDefaultElectricStations()

        log.error( "create default fleet.." )
        createDefaultFleet()




    }
    def destroy = {
    }
}

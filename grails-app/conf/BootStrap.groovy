import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Point
import de.dfki.gs.bootstrap.BootstrapHelper
import de.dfki.gs.domain.simulation.Car
import de.dfki.gs.domain.simulation.CarType
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

        createDefaultCarTypes()


        // log.error( "start bootstrapping and create some routes.." )


        // createDefaultCarTypes();

        def bigSimName = "BigSim500"
        def howMuchRoutes = 500


        /*
        if ( !Simulation.findByName( bigSimName )   ) {
            log.error( "create big sim with ${howMuchRoutes} routes" )
            createBigSim( bigSimName, howMuchRoutes, routeService )
        }

        def midSimName = "BigSim50"
        howMuchRoutes = 50

        if ( !Simulation.findByName( midSimName )   ) {
            log.error( "create mid sim with ${howMuchRoutes} routes" )
            createBigSim( midSimName, howMuchRoutes, routeService )
        }


        if ( Simulation.findByName( "Simulation1" ) && Simulation.findByName( "Simulation2" ) ) {
            log.error( "..already done." )
        } else {


            Simulation sim1 = new Simulation( name: "Simulation1" )
            if ( !sim1.save( flush: true ) ) {
                log.error( "failed to save simulation: ${sim1.errors}" )
            }

            Simulation sim2 = new Simulation( name: "Simulation2" )
            if ( !sim2.save( flush: true ) ) {
                log.error( "failed to save simulation: ${sim2.errors}" )
            }


            // some routes, some simulations
            Coordinate start1  = new Coordinate( 13.274428253175115, 52.46068276562632 );
            Coordinate target1 = new Coordinate( 13.44814956665116,  52.47197749605584 );

            Coordinate start2  = new Coordinate( 13.3635205078127,   52.52966086599651 );
            Coordinate target2 = new Coordinate( 13.270651702881338, 52.530078585246734 );

            Coordinate start3  = new Coordinate( 13.45089614868153,  52.542817113867635 );
            Coordinate target3 = new Coordinate( 13.307215576172183, 52.50657570304971 );


            List<BasicEdge> fullPath1 = new ArrayList<BasicEdge>();
            List<BasicEdge> fullPath2 = new ArrayList<BasicEdge>();
            List<BasicEdge> fullPath3 = new ArrayList<BasicEdge>();



            ArrayList<BasicEdge> path1 = routeService.calculatePath( start1, target1 )
            path1 = routeService.repairEdges( path1 )

            ArrayList<BasicEdge> path2 = routeService.calculatePath( start2, target2 )
            path2 = routeService.repairEdges( path2 )

            ArrayList<BasicEdge> path3 = routeService.calculatePath( start3, target3 )
            path3 = routeService.repairEdges( path3 )

            ArrayList<List<BasicEdge>> multiTargetRoute1 = new ArrayList<List<BasicEdge>>()
            ArrayList<List<BasicEdge>> multiTargetRoute2 = new ArrayList<List<BasicEdge>>()
            ArrayList<List<BasicEdge>> multiTargetRoute3 = new ArrayList<List<BasicEdge>>()

            multiTargetRoute1.add( path1 )
            multiTargetRoute2.add( path2 )
            multiTargetRoute3.add( path3 )

            Long id1;
            Long id2;
            Long id3;

            if ( path1 ) {
                id1 = routeService.persistRoute( multiTargetRoute1 );
            }

            if ( path2 ) {
                id2 = routeService.persistRoute( multiTargetRoute2 );
            }

            if ( path3 ) {
                id3 = routeService.persistRoute( multiTargetRoute3 );
            }

            if ( id1 ) {
                SimulationRoute simRoute1 = new SimulationRoute(
                        track: Track.get( id1 ),
                        carType: CarType.audi.toString(),
                        initialPersons: 3,
                        initialEnergy: 87d,
                        maxEnergy: 500,
                        energyDrain: 50
                )
                if ( !simRoute1.save( flush: true ) ) {
                    log.error( "failed to save simulation route: ${simRoute1.errors}" )
                }

                sim1.addToSimulationRoutes( simRoute1 )
            }

            if ( id2 ) {
                SimulationRoute simRoute2 = new SimulationRoute(
                        track: Track.get( id2 ),
                        carType: CarType.bmw.toString(),
                        initialPersons: 5,
                        initialEnergy: 89d,
                        maxEnergy: 500,
                        energyDrain: 10
                )
                if ( !simRoute2.save( flush: true ) ) {
                    log.error( "failed to save simulation route: ${simRoute2.errors}" )
                }

                sim1.addToSimulationRoutes( simRoute2 )
            }

            if ( id3 ) {
                SimulationRoute simRoute3 = new SimulationRoute(
                        track: Track.get( id3 ),
                        carType: CarType.vw.toString(),
                        initialPersons: 7,
                        initialEnergy: 15d,
                        maxEnergy: 500,
                        energyDrain: 25
                )
                if ( !simRoute3.save( flush: true ) ) {
                    log.error( "failed to save simulation route: ${simRoute3.errors}" )
                }
                sim2.addToSimulationRoutes( simRoute3 )
            }

            if ( !sim1.save( flush: true ) ) {
                log.error( "failed to save simulation: ${sim1.errors}" )
            }

            if ( !sim2.save( flush: true ) ) {
                log.error( "failed to save simulation: ${sim2.errors}" )
            }

        }
        */
    }
    def destroy = {
    }
}

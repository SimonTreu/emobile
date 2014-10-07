package de.dfki.gs.service

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Point
import de.dfki.gs.domain.simulation.Car
import de.dfki.gs.domain.simulation.CarType
import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.simulation.FillingStation
import de.dfki.gs.domain.simulation.FillingStationGroup
import de.dfki.gs.domain.simulation.FillingStationType
import de.dfki.gs.domain.simulation.Fleet
import de.dfki.gs.domain.simulation.Route
import de.dfki.gs.domain.simulation.Simulation
import de.dfki.gs.domain.simulation.SimulationRoute
import de.dfki.gs.domain.simulation.TrackEdge
import de.dfki.gs.domain.utils.Distribution
import de.dfki.gs.domain.utils.FleetStatus
import de.dfki.gs.domain.utils.GroupStatus
import de.dfki.gs.domain.utils.TrackEdgeType
import de.dfki.gs.threadutils.NotifyingBlockingThreadPoolExecutor
import de.dfki.gs.utils.Calculater
import de.dfki.gs.utils.LatLonPoint
//import grails.plugin.cache.Cacheable
import grails.util.Environment
import org.geotools.data.DataStore
import org.geotools.data.DataStoreFinder
import org.geotools.data.simple.SimpleFeatureCollection
import org.geotools.data.simple.SimpleFeatureSource
import org.geotools.feature.FeatureIterator
import org.geotools.feature.simple.SimpleFeatureImpl
import org.geotools.graph.build.feature.FeatureGraphGenerator
import org.geotools.graph.build.line.LineStringGraphGenerator
import org.geotools.graph.path.AStarShortestPathFinder
import org.geotools.graph.path.Path
import org.geotools.graph.structure.Graph
import org.geotools.graph.structure.basic.BasicEdge
import org.geotools.graph.structure.basic.BasicNode
import org.geotools.graph.traverse.standard.AStarIterator
import org.opengis.feature.Feature
import org.geotools.graph.structure.Edge
import org.springframework.context.ApplicationContext

import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

class RouteService {

    def grailsApplication
    def statisticService


    List<TrackEdge> convertToUnsavedTrackEdges( List<BasicEdge> basicEdges ) {

        List<TrackEdge> trackEdges = new ArrayList<TrackEdge>()
        for ( BasicEdge basicEdge : basicEdges ) {

            Point from = (Point) basicEdge.nodeA.getObject();
            Point to = (Point) basicEdge.nodeB.getObject();

            // BasicEdge featureEdge = graph.
            BasicEdge featureEdge = (BasicEdge) basicEdge.nodeA.getEdge( basicEdge.nodeB )

            SimpleFeatureImpl feature = ( SimpleFeatureImpl ) featureEdge.getObject()

            String featureIdString = feature.getID()
            int f = featureIdString.lastIndexOf( "." ) + 1
            int t = featureIdString.length()
            Long fId = Long.parseLong( featureIdString.substring( f, t ) )

            trackEdges.add(
                    new TrackEdge(
                            type: TrackEdgeType.normal,
                            fromLat: from.getY(),
                            fromLon: from.getX(),
                            toLat: to.getY(),
                            toLon: to.getX(),
                            cost: (Double) feature.getAttribute( "cost" ),
                            gisId: fId,
                            km: (Double) feature.getAttribute( "km" ),
                            streetName: feature.getAttribute( "osm_name" ),
                            kmh: (Integer) feature.getAttribute( "kmh" )
                    )
            )
        }

        return trackEdges
    }


    Car persistRouteToCar( Car car, List<List<BasicEdge>> multiTargetRoute, boolean flush = true ) {

        int listIdx = 0;


        Route route = new Route()
        if ( !route.save( flush: true ) ) {

            log.error( "failed to initially save route: ${route.errors}" )

        }

        for ( List<BasicEdge> edges : multiTargetRoute ) {

            int edgeIdx = 0

            long millis = System.currentTimeMillis()

            for ( BasicEdge edge : edges ) {

                Point from = (Point) edge.nodeA.getObject();
                Point to = (Point) edge.nodeB.getObject();

                // BasicEdge featureEdge = graph.
                BasicEdge featureEdge = (BasicEdge) edge.nodeA.getEdge( edge.nodeB )

                SimpleFeatureImpl feature = ( SimpleFeatureImpl ) featureEdge.getObject()

                String featureIdString = feature.getID()
                int f = featureIdString.lastIndexOf( "." ) + 1
                int t = featureIdString.length()

                Long fId = Long.parseLong( featureIdString.substring( f, t ) )

                // default is "normal" edge
                TrackEdgeType edgeType = TrackEdgeType.normal
                if ( listIdx == 0 && edgeIdx == 0 ) {
                    edgeType = TrackEdgeType.start
                }
                if ( listIdx > 0 && edgeIdx == 0 ) {
                    edgeType = TrackEdgeType.via_target
                }
                if ( listIdx == multiTargetRoute.size() - 1 && edgeIdx == edges.size() - 1 ) {
                    edgeType = TrackEdgeType.target
                }

                TrackEdge trackEdge = new TrackEdge(
                        routeId: route.id,
                        type: edgeType,
                        fromLat: from.getY(),
                        fromLon: from.getX(),
                        toLat: to.getY(),
                        toLon: to.getX(),
                        cost: (Double) feature.getAttribute( "cost" ),
                        gisId: fId,
                        km: (Double) feature.getAttribute( "km" ),
                        streetName: feature.getAttribute( "osm_name" ),
                        kmh: (Integer) feature.getAttribute( "kmh" ) );

                route.addToEdges( trackEdge )


                edgeIdx++
            }

            log.debug( "need ${(System.currentTimeMillis()-millis)} ms to save list of edges for one part of sim Route" )

            listIdx++
        }

        if ( !route.save( flush: true ) ) {
            log.error( "failed to save route: ${route.errors}" )
        }

        car.route = route
        car.routesConfigured = true

        if ( !car.save( flush: true ) ) {
            log.error( "failed to save car: ${car.errors}" )
        }

        return car
    }

    SimulationRoute persistRoute( SimulationRoute simulationRoute, List<List<BasicEdge>> multiTargetRoute, boolean flush = true ) {

        // Track track = new Track( simulationRoute: simulationRoute )

        /*
        if ( !track.save( flush: true ) ) {
            log.error( "failed to save track: ${track.errors}" )
        }
        */

        // Graph graph = getFeatureGraph()

        int routeIdx = 0;

        for ( List<BasicEdge> edges : multiTargetRoute ) {

            int edgeIdx = 0

            long millis = System.currentTimeMillis()

            for ( BasicEdge edge : edges ) {

                Point from = (Point) edge.nodeA.getObject();
                Point to = (Point) edge.nodeB.getObject();

                // BasicEdge featureEdge = graph.
                BasicEdge featureEdge = (BasicEdge) edge.nodeA.getEdge( edge.nodeB )

                SimpleFeatureImpl feature = ( SimpleFeatureImpl ) featureEdge.getObject()

                String featureIdString = feature.getID()
                int f = featureIdString.lastIndexOf( "." ) + 1
                int t = featureIdString.length()

                Long fId = Long.parseLong( featureIdString.substring( f, t ) )

                // default is "normal" edge
                TrackEdgeType edgeType = TrackEdgeType.normal
                if ( routeIdx == 0 && edgeIdx == 0 ) {
                    edgeType = TrackEdgeType.start
                }
                if ( routeIdx > 0 && edgeIdx == 0 ) {
                    edgeType = TrackEdgeType.via_target
                }
                if ( routeIdx == multiTargetRoute.size() - 1 && edgeIdx == edges.size() - 1 ) {
                    edgeType = TrackEdgeType.target
                }

                TrackEdge trackEdge = new TrackEdge(
                        simulationRouteId: simulationRoute.id,
                        type: edgeType,
                        fromLat: from.getY(),
                        fromLon: from.getX(),
                        toLat: to.getY(),
                        toLon: to.getX(),
                        cost: (Double) feature.getAttribute( "cost" ),
                        gisId: fId,
                        km: (Double) feature.getAttribute( "km" ),
                        streetName: feature.getAttribute( "osm_name" ),
                        kmh: (Integer) feature.getAttribute( "kmh" ) );

                /*
                if ( !trackEdge.save( flush: flush ) ) {
                    log.error( "failed to save a trackEdge: ${trackEdge.errors}" )
                }
                */

                simulationRoute.addToEdges( trackEdge )

                edgeIdx++
            }

            log.debug( "need ${(System.currentTimeMillis()-millis)} ms to save list of edges for one part of sim Route" )

            routeIdx++
        }

        if ( !simulationRoute.save( flush: true ) ) {
            log.error( "failed to save track: ${simulationRoute.errors}" )
        }

        return simulationRoute
    }

    /**
     *
     * @param graphName
     * @return
     */
    //@Cacheable("osmGraphCache")
    public Graph getFeatureGraph( String graphName ) {

        long millis = System.currentTimeMillis()

        // TODO: find some caching mechanism

        ApplicationContext appContext = grailsApplication.getMainContext()
        Graph graph = (Graph) appContext.servletContext.getAttribute( "featureGraph" )

        if ( graph ) {
            log.debug( "..obtaining osm graph took ${(System.currentTimeMillis()-millis)} ms" )
            return graph
        }

        DataStore dataStore = null;

        try {
            Map<String,Object> params = new HashMap<String,Object>();

            if ( Environment.current == 'development' ) {

                params.put( "dbtype", "postgis");
                params.put( "host", "localhost");
                params.put( "port", 5432);
                params.put( "schema", "public");
                params.put( "database", "gis2");
                params.put( "user", "postgres");
                params.put( "passwd", "quirin154");

            } else {

                params.put( "dbtype", "postgis");
                params.put( "host", "abomasus.de");
                params.put( "port", 5433 );
                params.put( "schema", "public");
                params.put( "database", "gis2");
                params.put( "user", "postgres");
                params.put( "passwd", "quirin154");

            }




            dataStore = DataStoreFinder.getDataStore(params);

            //SimpleFeatureSource featureSource = dataStore.getFeatureSource("planet_osm_line");
            SimpleFeatureSource featureSource = dataStore.getFeatureSource("osm_2po_4pgr");

            SimpleFeatureCollection fCollection = featureSource.getFeatures();

            //create a linear graph generate
            LineStringGraphGenerator lineStringGen = new LineStringGraphGenerator();
            FeatureGraphGenerator featureGen = new FeatureGraphGenerator( lineStringGen );




//throw all the features into the graph generator
            FeatureIterator iter = fCollection.features();
            try {
                while(iter.hasNext()){
                    Feature feature = iter.next();
                    featureGen.add( feature );
                }
            } finally {
                iter.close();
            }
            graph = featureGen.getGraph();

            appContext.servletContext.setAttribute( "featureGraph", graph )

        } catch ( Exception e ) {
            log.error( "Graph could not be initialized!", e )
        } finally {
            dataStore.dispose()
        }

        log.debug( "..obtaining osm graph took ${(System.currentTimeMillis()-millis)} ms" )

        return graph
    }

    Coordinate getNearestValidPoint( Coordinate coordinate ) {

        org.geotools.graph.structure.Node nearestNode = null

        Graph graph = getFeatureGraph( "osmGraph" )

        nearestNode = findClosestNode( coordinate, graph )


        Point p = (Point) nearestNode.getObject();
        return new Coordinate( p.getX(), p.getY() );
    }


    GasolineStation saveGasoline( Coordinate coordinate, String fillingType, boolean flush = true ) {

        Double fillingPortion = 0.000001;

        switch ( fillingType ) {
            case GasolineStationType.AC_2_3KW.toString() :
                fillingPortion = 2.3 / (60*60);
                break;
            case GasolineStationType.AC_3_7KW.toString() :
                fillingPortion = 3.7 / (60*60);
                break;
            case GasolineStationType.AC_7_4KW.toString() :
                fillingPortion = 4.7 / (60*60);
                break;
            case GasolineStationType.AC_11_1KW.toString() :
                fillingPortion = 11.1 / (60*60);
                break;
            case GasolineStationType.AC_22_2KW.toString() :
                fillingPortion = 22.2 / (60*60);
                break;
            case GasolineStationType.AC_43KW.toString() :
                fillingPortion = 43 / (60*60);
                break;
            case GasolineStationType.DC_49_8KW.toString() :
                fillingPortion = 49.8 / (60*60);
                break;
            default:
                fillingPortion = 0.000638;
                break;
        }

        fillingPortion  = Math.round( fillingPortion  * 1000000 ) / 1000000

        // lat = y
        GasolineStation gasolineStation = new GasolineStation(
                lon: coordinate.x,
                lat: coordinate.y,
                type: fillingType,
                fillingPortion: fillingPortion
        )

        if ( !gasolineStation.save( flush: flush ) ) {
            log.error( "failed to safe gasoline station: ${gasolineStation.errors}" )
            return null
        }

        return gasolineStation
    }


    public Point getRandomValidPoint() {

        return getRandomValidPointsFromMap( 1 ).get( 0 )

    }

    public List<Point> getRandomValidPointsFromMap( int count ) {

        List<Point> foundPoints = new ArrayList<Point>( count )

        Graph graph = getFeatureGraph( "osmGraph" )

        Collection<org.geotools.graph.structure.Node> nodes = (Collection<org.geotools.graph.structure.Node>) graph.getNodes();
        List<org.geotools.graph.structure.Node> validNodes = new ArrayList<org.geotools.graph.structure.Node>( nodes )

        int sizeValidNodes = validNodes.size()

        Random random = new Random();

        for ( int i = 0; i < count; i++ ) {

            int randomIndex = random.nextInt( sizeValidNodes )

            org.geotools.graph.structure.Node stationNode = validNodes.get( randomIndex )

            foundPoints.add( (Point) stationNode.getObject() )
        }

        return foundPoints
    }


    public void createRandomStations( Long stationCount, Long simulationId, String fillingType ) {

        Graph graph = getFeatureGraph( "osmGraph" )

        Collection<org.geotools.graph.structure.Node> nodes = (Collection<org.geotools.graph.structure.Node>) graph.getNodes();
        List<org.geotools.graph.structure.Node> validNodes = new ArrayList<org.geotools.graph.structure.Node>( nodes )

        int sizeValidNodes = validNodes.size()

        Random random = new Random();
        Simulation simulation = Simulation.get( simulationId );

        for ( int i = 0; i < stationCount; i++ ) {

            int randomIndex = random.nextInt( sizeValidNodes )

            org.geotools.graph.structure.Node stationNode = validNodes.get( randomIndex )

            Point stationPoint =  (Point) stationNode.getObject();

            GasolineStation gas = saveGasoline( new Coordinate( stationPoint.x, stationPoint.y ), fillingType, false )

            simulation.addToGasolineStations( gas )

        }


        if ( !simulation.save( flush: true ) ) {
            log.error( "failed to save simulation: ${simulation.errors}" )
        } else {
            log.debug( "saved ${simulation.gasolineStations.size()} gasoline stations for simulation ${simulationId}" )
        }

    }

    public List<List<org.geotools.graph.structure.Node>> createViaNodesWithRandomKm( List<Double> distances ) {

        double distanceFactor = 1.3

        List<List<org.geotools.graph.structure.Node>> routeStartTargetsList = new ArrayList<List<org.geotools.graph.structure.Node>>()

        for ( Double dist : distances ) {

            double fixedKm = dist * distanceFactor


            org.geotools.graph.structure.Node startNode = getRandomNode();
            org.geotools.graph.structure.Node nodeRunner = startNode;

            List<org.geotools.graph.structure.Node> startAndTargets = new ArrayList<org.geotools.graph.structure.Node>()
            startAndTargets.add( startNode )

            double havSums = 0;

            boolean finished = false;

            while( !finished ) {

                org.geotools.graph.structure.Node targetNode = getRandomNode()

                double havNodeRunnerTargetNode = Calculater.haversine(
                        ((Point) nodeRunner.getObject()).x,
                        ((Point) nodeRunner.getObject()).y,
                        ((Point) targetNode.getObject()).x,
                        ((Point) targetNode.getObject()).y
                )

                if ( havSums + havNodeRunnerTargetNode > ( fixedKm * 1.01 ) ) {
                    // skip runnerNode, it is to far away
                } else if ( havSums + havNodeRunnerTargetNode < ( fixedKm * 0.99 ) ) {
                    // take it
                    // log.error( "dist -- ${havNodeRunnerTargetNode} from ${((Point) nodeRunner.getObject()).x} : ${((Point) nodeRunner.getObject()).y}  to ${((Point) targetNode.getObject()).x} : ${((Point) targetNode.getObject()).y}" )

                    startAndTargets.add( targetNode )

                    havSums += havNodeRunnerTargetNode;
                    nodeRunner = targetNode;
                } else {
                    // finished
                    // log.error( "dist -- ${havNodeRunnerTargetNode} from ${((Point) nodeRunner.getObject()).x} : ${((Point) nodeRunner.getObject()).y}  to ${((Point) targetNode.getObject()).x} : ${((Point) targetNode.getObject()).y}" )

                    startAndTargets.add( targetNode );
                    havSums += havNodeRunnerTargetNode;

                    finished = true
                }

            }

            routeStartTargetsList.add( startAndTargets );

        }

        return routeStartTargetsList;

    }

    public List<List<org.geotools.graph.structure.Node>> createViaNodesWithFixedKm( long routeCount, double fixedKm ) {

        /**
         * TODO: 1.152 is a correction value to fix the error in haversine
         */
        // fixedKm = fixedKm * 1.152
        fixedKm = fixedKm * 1.3


        List<List<org.geotools.graph.structure.Node>> routeStartTargetsList = new ArrayList<List<org.geotools.graph.structure.Node>>()
        for ( long i = 0; i < routeCount; i++ ) {

            org.geotools.graph.structure.Node startNode = getRandomNode();
            org.geotools.graph.structure.Node nodeRunner = startNode;

            List<org.geotools.graph.structure.Node> startAndTargets = new ArrayList<org.geotools.graph.structure.Node>()
            startAndTargets.add( startNode )

            double havSums = 0;

            boolean finished = false;

            while( !finished ) {

                org.geotools.graph.structure.Node targetNode = getRandomNode()

                double havNodeRunnerTargetNode = Calculater.haversine(
                        ((Point) nodeRunner.getObject()).x,
                        ((Point) nodeRunner.getObject()).y,
                        ((Point) targetNode.getObject()).x,
                        ((Point) targetNode.getObject()).y
                )

                if ( havSums + havNodeRunnerTargetNode > ( fixedKm * 1.01 ) ) {
                    // skip runnerNode, it is to far away
                } else if ( havSums + havNodeRunnerTargetNode < ( fixedKm * 0.99 ) ) {
                    // take it
                    // log.error( "dist -- ${havNodeRunnerTargetNode} from ${((Point) nodeRunner.getObject()).x} : ${((Point) nodeRunner.getObject()).y}  to ${((Point) targetNode.getObject()).x} : ${((Point) targetNode.getObject()).y}" )

                    startAndTargets.add( targetNode )

                    havSums += havNodeRunnerTargetNode;
                    nodeRunner = targetNode;
                } else {
                    // finished
                    // log.error( "dist -- ${havNodeRunnerTargetNode} from ${((Point) nodeRunner.getObject()).x} : ${((Point) nodeRunner.getObject()).y}  to ${((Point) targetNode.getObject()).x} : ${((Point) targetNode.getObject()).y}" )

                    startAndTargets.add( targetNode );
                    havSums += havNodeRunnerTargetNode;

                    finished = true
                }

            }

            routeStartTargetsList.add( startAndTargets );

        }

        return routeStartTargetsList;
    }

    public Long calculatePathFromCoordinates( Long simulationId, List<LatLonPoint> points ) {

        Graph graph = getFeatureGraph( "osmGraph" )

        Simulation simulation = Simulation.get( simulationId )

        if ( simulation == null ) {
            return null
        }

        List<org.geotools.graph.structure.Node> nodes = new ArrayList<org.geotools.graph.structure.Node>()
        points.each { LatLonPoint latLonPoint ->

            nodes.add( findClosestNode(
                    new Coordinate( latLonPoint.x, latLonPoint.y ),
                    graph
            ) )

        }

        if ( nodes.size() != points.size() ) {
            return null
        }

        List<List<BasicEdge>> multiTargetRoute = new ArrayList<List<BasicEdge>>()
        def pairs = nodes.collate( 2, 1, false );

        pairs.each {

            List<BasicEdge> edges = calculatePathFromNodes( it[0], it[1] )
            if ( edges.size() < 1 ) {
                return null
            }

            multiTargetRoute.add( repairEdges( edges ) )

        }


        SimulationRoute simulationRoute = new SimulationRoute(
                simulation: simulation,
                carType: CarType.get( 1 ),
                initialPersons: 1
        )


        if ( !simulationRoute.save() ) {
            log.error( "failed to save simulation route: ${simulationRoute.errors}" )
        } else {
            log.debug( "saved simulation route with id: ${simulationRoute.id}" )
        }


        long millis = System.currentTimeMillis()
        simulationRoute = persistRoute( simulationRoute,  multiTargetRoute, false )

        double sumEdgesKm = simulationRoute.edges.sum { it.km }
        log.error( "saved track with ${sumEdgesKm} km" )

        simulationRoute.plannedDistance = sumEdgesKm;

        simulation.addToSimulationRoutes( simulationRoute )


        if ( !simulation.save() ) {
            log.error( "failed to save simulation: ${simulation.errors}" )
            return null
        }

        return simulationRoute.id
    }

    public List<BasicEdge> calculatePathFromNodes( org.geotools.graph.structure.Node s, org.geotools.graph.structure.Node t ) {

        Path path = null;

        List<BasicEdge> edges = new ArrayList<BasicEdge>();

        Graph graph = getFeatureGraph( "osmGraph" )

        AStarIterator.AStarFunctions functions = new AStarIterator.AStarFunctions( t ) {

            /**
             * should return the real costs for getting from n1 to n2
             *
             * @param n1
             * @param n2
             * @return
             */
            public double cost(AStarIterator.AStarNode n1, AStarIterator.AStarNode n2) {

                // TODO: implement a good cost function which is made for electricity

                Edge edge = n1.getNode().getEdge( n2.node )
                SimpleFeatureImpl feature = (SimpleFeatureImpl) edge.getObject()

                Double o = (Double) feature.getAttribute( "km" )
                if ( o && o >= 0 ) {

                } else {
                    o = 0.1
                }

                // Double o = (Double) feature.getAttribute( "cost" )

                // now lets weight the "km" with maximum speed, to prefer Stadtautobahn
                Integer speed = (Integer) feature.getAttribute( "kmh" );

                if ( speed && speed > 0 ) {

                } else {
                    speed = 30
                }


                // TODO : prefer streets with speed < 60, 80 ( find paper again !)

                Double costs = 7 * o;
                if ( speed <= 30 ) {
                    costs = 6 * o;
                } else if ( speed > 30 && speed < 80 ) {
                    costs = 3 * o;
                } else if ( speed >= 80 ) {
                    costs = 1 * o;
                }

                // Double costs = ( Math.pow( 100/speed, 2) ) * o


                // the real cost until now + real costs from n1 to n2
                // return n1.getG() + o;
                return costs
            }

            /**
             * providing haversine distance with multiplied speed as a heuristic
             *
             * @param n
             * @return
             */
            public double h( org.geotools.graph.structure.Node n ) {

                Point from = (Point) n.getObject();
                Point to = (Point) t.getObject();

                // multiplied by 1 because of weighting the distance with speed...
                // for not overestimating we take 1
                double hav = Calculater.haversine( from.x, from.y, to.x, to.y );
                // log.error( "from ${from.x} : ${from.y}  to: ${to.x} : ${to.y}   -> hav: ${hav}" )
                /**
                 * 6:     appr 30 km/h
                 * 1.152: fix error of hav
                 */
                return 6 * hav * 1.152;

                // return getDist( from, to );
                // return getManhatten( from, to );
            }

        };


        AStarShortestPathFinder pf = new AStarShortestPathFinder( graph, s, t,   functions );

        pf.calculate();
        pf.finish();

//find some destinations to calculate paths to

        // Node target = QuickStart.findClosest( new Coordinate( 0.1, 0.1 ), graph );



//calculate the paths


        try {
            path = pf.getPath();
            //path.riterator().next()

            org.geotools.graph.structure.Node previous = null;
            org.geotools.graph.structure.Node node = null;
            if ( path != null ) {

                for ( Iterator ritr = path.riterator(); ritr.hasNext(); ) {

                    node = ( org.geotools.graph.structure.Node ) ritr.next();
                    if ( previous != null ) {
                        // adding the edge between them into vector
                        edges.add( node.getEdge( previous ) )

                    }
                    previous = node

                }

            }
        } catch (  Exception e  ) {
            log.error( "failed to get path from astar algorithm" )
            log.debug( e )
        }


        return edges;

    }


    public List<Route> createRandomFixedDistanceRoutes( long routeCount, double fixedKm ) {

        List<Route> routes = new ArrayList<Route>()

        List<List<org.geotools.graph.structure.Node>> routeStartTargetsList = createViaNodesWithFixedKm( routeCount, fixedKm );

        // initialized with size of routeStartTargetsList
        List<List<List<BasicEdge>>> routesToPersist = Collections.synchronizedList( new ArrayList<ArrayList<List<BasicEdge>>>() );
        // ArrayBlockingQueue<List<List<BasicEdge>>> routesToPersist = new ArrayBlockingQueue<ArrayList<List<BasicEdge>>>( routeStartTargetsList.size() )

        int poolSize = 32;      // the count of currently paralellized threads
        int queueSize = 64;    // recommended - twice the size of the poolSize
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

        int cc = 0;

        for ( List<org.geotools.graph.structure.Node> routeStartTargetList : routeStartTargetsList ) {

            log.debug( "start thread no ${++cc}  of ${routeStartTargetsList.size()} " )

            def pairs = routeStartTargetList.collate( 2, 1, false );

            threadPoolExecutorForPoints.execute( new Runnable() {

                @Override
                void run() {

                    List<List<BasicEdge>> multiTargetRoute = new ArrayList<List<BasicEdge>>()

                    boolean pathBroken = false

                    for ( List<org.geotools.graph.structure.Node> pairList : pairs ) {

                        /*
                        Point startPoint =  (Point) pairList.get( 0 ).getObject();
                        Point targetPoint = (Point) pairList.get( 1 ).getObject();

                        Coordinate currentStart  = new Coordinate( startPoint.y, startPoint.x );
                        Coordinate currentTarget = new Coordinate( targetPoint.y, targetPoint.x );

                        List<BasicEdge> pathEdges = calculatePath( currentStart, currentTarget );
                        */

                        List<BasicEdge> pathEdges = calculatePathFromNodes( pairList.get( 0 ), pairList.get( 1 ) )

                        /**
                         * if this happens, all the routes are worthless
                         */
                        if ( pathEdges.size() == 0 ) {
                            // log.error( "path is broken !! from ${currentStart.y} : ${currentStart.x}  to  ${currentTarget.y} : ${currentTarget.x}" )
                            log.error( "path broken.. from: ${pairList.get( 0 ).toString()}  to: ${pairList.get( 1 ).toString()}" )
                            pathBroken = true
                            // return
                        } else {
                            // repair all edges' direction
                            pathEdges = repairEdges( pathEdges )

                            // adding repaired edges to multiTargetRoute
                            multiTargetRoute.add( pathEdges )
                        }

                    }

                    if ( !pathBroken ) {
                        routesToPersist.add( multiTargetRoute )
                        log.debug( "added a non broken path: ${multiTargetRoute} as path" )
                    }

                }

            } );

        }

        def done1 = false
        int doneCount = 0;

        int waiter = 1500000
        while ( !done1 && waiter >= 0 && doneCount < routeStartTargetsList.size() ) {
            done1 = threadPoolExecutorForPoints.await( 20, TimeUnit.MILLISECONDS )
            waiter--
            if ( waiter%100 == 0 ) {
                log.error( "waiter: ${waiter} -- finished threads: ${threadPoolExecutorForPoints.runnables.size()} of ${routeStartTargetsList.size()}" )
            }
            doneCount = threadPoolExecutorForPoints.runnables.size()
        }

        log.debug( "added ${routesToPersist.size()} valid routes" )





        for ( List<List<BasicEdge>> multiTargetRoute : routesToPersist ) {

            int routeIdx = 0;

            Route route = new Route()
            // save route to have id
            if ( !route.save( flush: true ) ) {

                log.error( "failed to initially save route: ${route.errors}" )

            }


            for ( List<BasicEdge> partOfMultiTargetRoute : multiTargetRoute ) {


                int edgeIdx = 0

                long millis = System.currentTimeMillis()

                for ( BasicEdge basicEdge : partOfMultiTargetRoute ) {

                    Point from = (Point) basicEdge.nodeA.getObject();
                    Point to = (Point) basicEdge.nodeB.getObject();

                    // BasicEdge featureEdge = graph.
                    BasicEdge featureEdge = (BasicEdge) basicEdge.nodeA.getEdge( basicEdge.nodeB )

                    SimpleFeatureImpl feature = ( SimpleFeatureImpl ) featureEdge.getObject()

                    String featureIdString = feature.getID()
                    int f = featureIdString.lastIndexOf( "." ) + 1
                    int t = featureIdString.length()

                    Long fId = Long.parseLong( featureIdString.substring( f, t ) )

                    // default is "normal" edge
                    String edgeType = TrackEdgeType.normal.toString()
                    if ( routeIdx == 0 && edgeIdx == 0 ) {
                        edgeType = TrackEdgeType.start.toString()
                    }
                    if ( routeIdx > 0 && edgeIdx == 0 ) {
                        edgeType = TrackEdgeType.via_target.toString()
                    }
                    if ( routeIdx == multiTargetRoute.size() - 1 && edgeIdx == partOfMultiTargetRoute.size() - 1 ) {
                        edgeType = TrackEdgeType.target.toString()
                    }

                    TrackEdge trackEdge = new TrackEdge(
                            routeId: route.id,
                            type: edgeType,
                            fromLat: from.getY(),
                            fromLon: from.getX(),
                            toLat: to.getY(),
                            toLon: to.getX(),
                            cost: (Double) feature.getAttribute( "cost" ),
                            gisId: fId,
                            km: (Double) feature.getAttribute( "km" ),
                            streetName: feature.getAttribute( "osm_name" ),
                            kmh: (Integer) feature.getAttribute( "kmh" ) );

                    route.addToEdges( trackEdge )

                    edgeIdx++
                }


            }

            if ( !route.save( flush: true ) ) {

                log.error( "failed to save Route: ${route.errors}" )

            } else {

                routes.add( route )

            }

        }

        return routes
    }



    public FillingStationGroup createGaussianDistributedFillingStations( FillingStationGroup group ) {

        Graph graph = getFeatureGraph( "osmGraph" )

        // TODO: impl distribution conform positions
        Collection<org.geotools.graph.structure.Node> nodes = (Collection<org.geotools.graph.structure.Node>) graph.getNodes();
        List<Coordinate> coordinates = new ArrayList<Coordinate>();


        // determine center for mean
        for ( org.geotools.graph.structure.Node node : nodes ) {
            Point point = (Point) node.getObject();
            coordinates.add( point.coordinate );
        }
        Coordinate center = Calculater.centerOfArea( coordinates );

        int count = group.fillingStations.size();
        double[][] samples = statisticService.generateRandomGaussianVectors( count, center.x, center.y, 0.04, 0.06 )

        int cc = 0;
        for ( FillingStation fillingStation : group.fillingStations ) {

            fillingStation = FillingStation.get( fillingStation.id )

            org.geotools.graph.structure.Node n = findClosestNode( new Coordinate( samples[ 0 ][ cc ], samples[ 1 ][ cc ] ), graph );

            Point p = (Point) n.getObject()
            fillingStation.lat = p.x
            fillingStation.lon = p.y

            fillingStation.groupsConfigured = true

            if ( !fillingStation.save( flush: true ) ) {
                log.error( "failed to update fillingStation: ${fillingStation.errors}" )
            }

            cc++;
        }

        group.groupsConfigured = true
        group.groupStatus = GroupStatus.CONFIGURED

        if ( !group.save( flush: true ) ) {
            log.error( "failed to update fillingStationGroup: ${group.errors}" )
        }

        return group
    }

    public FillingStationGroup createEqualDistributedFillingStations( FillingStationGroup group ) {

        Graph graph = getFeatureGraph( "osmGraph" )

        // TODO: impl distribution conform positions
        Collection<org.geotools.graph.structure.Node> nodes = (Collection<org.geotools.graph.structure.Node>) graph.getNodes();



        return group
    }

    public FillingStationGroup createRandomPositionsForFillingStations( Long groupId ) {

        FillingStationGroup group = FillingStationGroup.get( groupId )

        if ( group.distribution == Distribution.NORMAL_DISTRIBUTION ) {

            group = createGaussianDistributedFillingStations( group );

        } else if ( group.distribution == Distribution.EQUAL_DISTRIBUTION ) {

            // TODO: implement me!
            group = createEqualDistributedFillingStations( group );

        }





        log.error( "hua!" )

        return group
    }

    public Fleet createRandomDistanceRoutesForFleet( Long fleetId ) {

        Fleet fleet = Fleet.get( fleetId )

        if ( fleet.fleetStatus == FleetStatus.CONFIGURED ) {
            return fleet
        }

        /**
         * this may be done only once, otherwise it is not the valid distribution
         */
        List<Double> distances = statisticService.generateRandomListFromDistribution(
                fleet.cars.size(),
                fleet.plannedFromKm,
                fleet.plannedToKm,
                fleet.distribution
        )

        log.error( "created following ${fleet.distribution} distributed randoms: ${distances}" )

        Map<Car,Double> carDistanceMap= new HashMap<Car,Double>()

        /**
         * filling car distance map to have the distance for each car ready to go
         */
        int idx = 0;
        for ( Car car : fleet.cars ) {

            car = Car.get( car.id )

            carDistanceMap.put( car, distances.get( idx ) )

            idx++

        }

        while ( carDistanceMap.size() > 0 ) {

            List<Double> openDistances = new ArrayList<Double>( carDistanceMap.values() )
            log.error( "dd" )

            List<List<org.geotools.graph.structure.Node>> routeStartTargetsList = createViaNodesWithRandomKm( openDistances );

            // initialized with size of routeStartTargetsList
            List<List<List<BasicEdge>>> routesToPersist = Collections.synchronizedList( new ArrayList<ArrayList<List<BasicEdge>>>() );

            int poolSize = 32;      // the count of currently paralellized threads
            int queueSize = 64;    // recommended - twice the size of the poolSize
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

            int cc = 0;

            for ( List<org.geotools.graph.structure.Node> routeStartTargetList : routeStartTargetsList ) {

                log.debug( "start thread no ${++cc}  of ${routeStartTargetsList.size()} " )

                def pairs = routeStartTargetList.collate( 2, 1, false );

                threadPoolExecutorForPoints.execute( new Runnable() {

                    @Override
                    void run() {

                        List<List<BasicEdge>> multiTargetRoute = new ArrayList<List<BasicEdge>>()

                        boolean pathBroken = false

                        for ( List<org.geotools.graph.structure.Node> pairList : pairs ) {

                            List<BasicEdge> pathEdges = calculatePathFromNodes( pairList.get( 0 ), pairList.get( 1 ) )

                            /**
                             * if this happens, all the routes are worthless
                             */
                            if ( pathEdges.size() == 0 ) {

                                log.error( "path broken.. from: ${pairList.get( 0 ).toString()}  to: ${pairList.get( 1 ).toString()}" )
                                pathBroken = true

                            } else {
                                // repair all edges' direction
                                pathEdges = repairEdges( pathEdges )

                                // adding repaired edges to multiTargetRoute
                                multiTargetRoute.add( pathEdges )
                            }

                        }

                        if ( !pathBroken ) {
                            routesToPersist.add( multiTargetRoute )
                            log.debug( "added a non broken path: ${multiTargetRoute} as path" )
                        }

                    }

                } );

            }

            def done1 = false
            int doneCount = 0;

            //int waiter = 1500000
            int waiter = 1500
            while ( !done1 && waiter >= 0 && doneCount < routeStartTargetsList.size() ) {
                done1 = threadPoolExecutorForPoints.await( 20, TimeUnit.MILLISECONDS )
                waiter--
                if ( waiter%100 == 0 ) {
                    log.error( "waiter: ${waiter} -- finished threads: ${threadPoolExecutorForPoints.runnables.size()} of ${routeStartTargetsList.size()}" )
                }
                doneCount = threadPoolExecutorForPoints.runnables.size()
            }

            log.debug( "added ${routesToPersist.size()} valid routes" )

            /**
             * fill the cars with found routes and persist to db
             */
            for ( List<List<BasicEdge>> multiTargetRoute : routesToPersist ) {

                List<Car> carsToPersistList = new ArrayList<Car>( carDistanceMap.keySet() )

                Car car = carsToPersistList.get( 0 )

                persistRouteToCar( car, multiTargetRoute )

                carDistanceMap.remove( car )
            }

        }

        log.error( "finished" )

        fleet.routesConfigured = true
        fleet.fleetStatus = FleetStatus.CONFIGURED
        if ( !fleet.save( flush: true ) ) {
            log.error( "failed to save fleet: ${fleet.errors}" )
        }

        return fleet
    }

    public void createRandomFixedDistanceRoutes( long routeCount, Long simulationId, double fixedKm, long carTypeId ) {

        long millisAll = System.currentTimeMillis()


        List<List<org.geotools.graph.structure.Node>> routeStartTargetsList = createViaNodesWithFixedKm( routeCount, fixedKm );



        // initialized with size of routeStartTargetsList
        List<List<List<BasicEdge>>> routesToPersist = Collections.synchronizedList( new ArrayList<ArrayList<List<BasicEdge>>>() );
        // ArrayBlockingQueue<List<List<BasicEdge>>> routesToPersist = new ArrayBlockingQueue<ArrayList<List<BasicEdge>>>( routeStartTargetsList.size() )

        int poolSize = 32;      // the count of currently paralellized threads
        int queueSize = 64;    // recommended - twice the size of the poolSize
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

        int cc = 0;

        for ( List<org.geotools.graph.structure.Node> routeStartTargetList : routeStartTargetsList ) {

            log.debug( "start thread no ${++cc}  of ${routeStartTargetsList.size()} " )

            def pairs = routeStartTargetList.collate( 2, 1, false );

            threadPoolExecutorForPoints.execute( new Runnable() {

                @Override
                void run() {

                    List<List<BasicEdge>> multiTargetRoute = new ArrayList<List<BasicEdge>>()

                    boolean pathBroken = false

                    for ( List<org.geotools.graph.structure.Node> pairList : pairs ) {

                        /*
                        Point startPoint =  (Point) pairList.get( 0 ).getObject();
                        Point targetPoint = (Point) pairList.get( 1 ).getObject();

                        Coordinate currentStart  = new Coordinate( startPoint.y, startPoint.x );
                        Coordinate currentTarget = new Coordinate( targetPoint.y, targetPoint.x );

                        List<BasicEdge> pathEdges = calculatePath( currentStart, currentTarget );
                        */

                        List<BasicEdge> pathEdges = calculatePathFromNodes( pairList.get( 0 ), pairList.get( 1 ) )

                        /**
                         * if this happens, all the routes are worthless
                         */
                        if ( pathEdges.size() == 0 ) {
                            // log.error( "path is broken !! from ${currentStart.y} : ${currentStart.x}  to  ${currentTarget.y} : ${currentTarget.x}" )
                            log.error( "path broken.. from: ${pairList.get( 0 ).toString()}  to: ${pairList.get( 1 ).toString()}" )
                            pathBroken = true
                            // return
                        } else {
                            // repair all edges' direction
                            pathEdges = repairEdges( pathEdges )

                            // adding repaired edges to multiTargetRoute
                            multiTargetRoute.add( pathEdges )
                        }

                    }

                    if ( !pathBroken ) {
                        routesToPersist.add( multiTargetRoute )
                        log.debug( "added a non broken path: ${multiTargetRoute} as path" )
                    }

                }

            } );

        }

        def done1 = false
        int doneCount = 0;

        int waiter = 1500000
        while ( !done1 && waiter >= 0 && doneCount < routeStartTargetsList.size() ) {
            done1 = threadPoolExecutorForPoints.await( 20, TimeUnit.MILLISECONDS )
            waiter--
            if ( waiter%100 == 0 ) {
                log.error( "waiter: ${waiter} -- finished threads: ${threadPoolExecutorForPoints.runnables.size()} of ${routeStartTargetsList.size()}" )
            }
            doneCount = threadPoolExecutorForPoints.runnables.size()
        }

        log.debug( "added ${routesToPersist.size()} valid routes" )



        Simulation simulation = Simulation.get( simulationId );

        // now save the routes..
        int countSavedRoutes = 0;
        CarType carType = CarType.get( carTypeId )
        for ( List<List<BasicEdge>> multiTargetRoute : routesToPersist ) {


            SimulationRoute simulationRoute = new SimulationRoute(
                    simulation: simulation,
                    carType: carType,
                    initialPersons: 1
            )



            // TODO: neccessary??

            if ( !simulationRoute.save() ) {
                log.error( "failed to save simulation route: ${simulationRoute.errors}" )
            } else {
                log.debug( "saved simulation route with id: ${simulationRoute.id}" )
            }


            long millis = System.currentTimeMillis()
            simulationRoute = persistRoute( simulationRoute,  multiTargetRoute, false )

            double sumEdgesKm = simulationRoute.edges.sum { it.km }
            log.error( "saved track with ${sumEdgesKm} km" )
            /*
            double havSum = track.edges.sum { TrackEdge trackEdge ->
                Calculater.haversine( trackEdge.fromLon, trackEdge.fromLat, trackEdge.toLon, trackEdge.toLat )
            }
            log.error( "hav sais: ${havSum} km" )
            */

            simulationRoute.plannedDistance = sumEdgesKm;

            log.debug( "-- persisiting route took ${(System.currentTimeMillis() - millis)} ms" )

            // TODO: neccessary??
            // simulationRoute.track = track
            /*
            if ( !simulationRoute.save() ) {
                log.error( "failed to save simulation route: ${simulationRoute.errors}" )
            } else {
                log.error( "saved simulation route with id: ${simulationRoute.id}" )
            }
            */

            log.debug( " --- added simRoute nr ${++countSavedRoutes} / ${routesToPersist.size()} to simulation" )

            simulation.addToSimulationRoutes( simulationRoute )

        }

        if ( !simulation.save() ) {
            log.error( "failed to save simulation: ${simulation.errors}" )
        }

        log.error( "finished" )
    }

    public org.geotools.graph.structure.Node getRandomNode() {

        Random random = new Random();
        Graph graph = getFeatureGraph( "osmGraph" )

        Collection<org.geotools.graph.structure.Node> nodes = (Collection<org.geotools.graph.structure.Node>) graph.getNodes();
        List<org.geotools.graph.structure.Node> validNodes = new ArrayList<org.geotools.graph.structure.Node>( nodes )
        int sizeValidNodes = validNodes.size()

        return validNodes.get( random.nextInt( sizeValidNodes ) )
    }

    public void createRandomRoutes( Long routeCount, Long simulationId, long viaTargets, long carTypeId ) {

        // randomly pick viaTargets out of this range:
        // int maxViaTargets = 10

        Graph graph = getFeatureGraph( "osmGraph" )

        Collection<org.geotools.graph.structure.Node> nodes = (Collection<org.geotools.graph.structure.Node>) graph.getNodes();
        List<org.geotools.graph.structure.Node> validNodes = new ArrayList<org.geotools.graph.structure.Node>( nodes )

        int sizeValidNodes = validNodes.size()

        log.debug( "have a set of ${sizeValidNodes} nodes.." )

        List<List<org.geotools.graph.structure.Node>> routeStartTargetsList = new ArrayList<List<org.geotools.graph.structure.Node>>()

        for ( int i = 0; i < routeCount; i++ ) {

            List<org.geotools.graph.structure.Node> startAndTargets = new ArrayList<org.geotools.graph.structure.Node>()

            Random random = new Random();


            // adding random start Node
            startAndTargets.add( validNodes.get( random.nextInt( sizeValidNodes ) ) )

            // adding random via Nodes
            for ( int j = 0; j < viaTargets; j++ ) {
                startAndTargets.add( validNodes.get( random.nextInt( sizeValidNodes ) ) )
            }

            // adding random target Node
            startAndTargets.add( validNodes.get( random.nextInt( sizeValidNodes ) ) )

            // put into list
            routeStartTargetsList.add( startAndTargets )

            log.debug( "added ${startAndTargets} into list" )
        }

        // initialized with size of routeStartTargetsList
        List<List<List<BasicEdge>>> routesToPersist = Collections.synchronizedList( new ArrayList<ArrayList<List<BasicEdge>>>() );
        // ArrayBlockingQueue<List<List<BasicEdge>>> routesToPersist = new ArrayBlockingQueue<ArrayList<List<BasicEdge>>>( routeStartTargetsList.size() )

        int poolSize = 128;      // the count of currently paralellized threads
        int queueSize = 256;    // recommended - twice the size of the poolSize
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

        int cc = 0;

        for ( List<org.geotools.graph.structure.Node> routeStartTargetList : routeStartTargetsList ) {

            log.debug( "start thread no ${++cc}  of ${routeStartTargetsList.size()} " )

            def pairs = routeStartTargetList.collate( 2, 1, false );

            threadPoolExecutorForPoints.execute( new Runnable() {

                @Override
                void run() {

                    List<List<BasicEdge>> multiTargetRoute = new ArrayList<List<BasicEdge>>()

                    // find route from element to element and assign to routesToPersist
                    // def pairs = routeStartTargetList.collate( 2, 1, false );

                    log.debug( "pairs: ${pairs}" )

                    boolean pathBroken = false

                    for ( List<org.geotools.graph.structure.Node> pairList : pairs ) {

                        Point startPoint =  (Point) pairList.get( 0 ).getObject();
                        Point targetPoint = (Point) pairList.get( 1 ).getObject();

                        Coordinate currentStart  = new Coordinate( startPoint.y, startPoint.x );
                        Coordinate currentTarget = new Coordinate( targetPoint.y, targetPoint.x );

                        List<BasicEdge> pathEdges = calculatePath( currentStart, currentTarget );

                        /**
                         * if this happens, all the routes are worthless
                         */
                        if ( pathEdges.size() == 0 ) {
                            log.error( "path is broken !!" )
                            pathBroken = true
                            // return
                        } else {
                            // repair all edges' direction
                            pathEdges = repairEdges( pathEdges )

                            // adding repaired edges to multiTargetRoute
                            multiTargetRoute.add( pathEdges )
                        }

                    }

                    if ( !pathBroken ) {
                        routesToPersist.add( multiTargetRoute )
                        log.debug( "added a non broken path: ${multiTargetRoute} as path" )
                    }

                }

            } );

        }

        def done1 = false
        int doneCount = 0;

        int waiter = 1500000
        while ( !done1 && waiter >= 0 && doneCount < routeStartTargetsList.size() ) {
            done1 = threadPoolExecutorForPoints.await( 20, TimeUnit.MILLISECONDS )
            waiter--
            if ( waiter%100 == 0 ) {
                log.error( "waiter: ${waiter} -- finished threads: ${threadPoolExecutorForPoints.runnables.size()} of ${routeStartTargetsList.size()}" )
            }
            doneCount = threadPoolExecutorForPoints.runnables.size()
        }

        log.debug( "added ${routesToPersist.size()} valid routes" )



        Simulation simulation = Simulation.get( simulationId );

        // now save the routes..
        int countSavedRoutes = 0;
        CarType carType = CarType.get( carTypeId )
        for ( List<List<BasicEdge>> multiTargetRoute : routesToPersist ) {

            SimulationRoute simulationRoute = new SimulationRoute(
                    simulation: simulation,
                    carType: carType,
                    initialPersons: 1
            )



            // TODO: neccessary??

            if ( !simulationRoute.save() ) {
                log.error( "failed to save simulation route: ${simulationRoute.errors}" )
            } else {
                log.debug( "saved simulation route with id: ${simulationRoute.id}" )
            }


            long millis = System.currentTimeMillis()
            simulationRoute = persistRoute( simulationRoute,  multiTargetRoute, false )
            log.debug( "-- persisiting route took ${(System.currentTimeMillis() - millis)} ms" )

            // TODO: neccessary??
            //simulationRoute.track = track
            /*
            if ( !simulationRoute.save() ) {
                log.error( "failed to save simulation route: ${simulationRoute.errors}" )
            } else {
                log.error( "saved simulation route with id: ${simulationRoute.id}" )
            }
            */

            log.debug( " --- added simRoute nr ${++countSavedRoutes} / ${routesToPersist.size()} to simulation" )

            simulation.addToSimulationRoutes( simulationRoute )

        }

        if ( !simulation.save() ) {
            log.error( "failed to save simulation: ${simulation.errors}" )
        }

    }

    public List<BasicEdge> routeToTarget( double currentLat, double currentLon, double targetLat, double targetLon ) {

        List<BasicEdge> routeToTarget = calculatePath(
                new Coordinate( currentLat, currentLon ),
                new Coordinate( targetLat, targetLon )
        )

        return repairEdges( routeToTarget )
    }


    public List<FillingStation> findNClosestFillingStations( double lat, double lon, List<Long> fillingStationIds, int max ) {

        if ( max > fillingStationIds.size() ) {
            max = fillingStationIds.size()
        }

        List<Long> stationIds = new ArrayList<Long>();

        GasolineStation station = findClosestGasolineStation( lat, lon, stations );
        stationIds.add( station )

        List<GasolineStation> currentList = new ArrayList<GasolineStation>( stations );

        for ( int i = 0; i < (max-1); i++ ) {

            currentList.remove( station )

            currentList = new ArrayList<GasolineStation>( currentList )

            station = findClosestGasolineStation( lat, lon, currentList )

            stationIds.add( station )

        }

        return stationIds


    }

    public List<GasolineStation> findNClosestGasolineStations( double lat, double lon, List<GasolineStation> stations, int max ) {

        if ( max > stations.size() ) {
            max = stations.size()
        }

        List<GasolineStation> stationIds = new ArrayList<GasolineStation>();

        GasolineStation station = findClosestGasolineStation( lat, lon, stations );
        stationIds.add( station )

        List<GasolineStation> currentList = new ArrayList<GasolineStation>( stations );

        for ( int i = 0; i < (max-1); i++ ) {

            currentList.remove( station )

            currentList = new ArrayList<GasolineStation>( currentList )

            station = findClosestGasolineStation( lat, lon, currentList )

            stationIds.add( station )

        }

        return stationIds
    }

    public GasolineStation findClosestGasolineStation( double lat, double lon, List<GasolineStation> stations ) {


        def distance = 100000000;
        GasolineStation targetGasolineStation = null

        for ( GasolineStation gasolineStation : stations ) {

            def latToCheck = lat
            def lonToCheck = lon

            def currentDistance = Math.pow( latToCheck - gasolineStation.lat, 2 ) + Math.pow( lonToCheck - gasolineStation.lon, 2 )

            if ( currentDistance < distance ) {

                // found a closer station
                distance = currentDistance
                targetGasolineStation = gasolineStation
            }

        }


        return targetGasolineStation
    }


    List<BasicEdge> calculatePath( Coordinate start, Coordinate target ) {

        Path path = null;

        List<BasicEdge> edges = new ArrayList<BasicEdge>();

        Graph graph = getFeatureGraph( "osmGraph" )

        BasicNode bnStart = new BasicNode(  );

        org.geotools.graph.structure.Node s = findClosestNode( start, graph );
        org.geotools.graph.structure.Node t = findClosestNode( target, graph );

        AStarIterator.AStarFunctions functions = new AStarIterator.AStarFunctions( t ) {

            /**
             * should return the real costs for getting from n1 to n2
             *
             * @param n1
             * @param n2
             * @return
             */
            public double cost(AStarIterator.AStarNode n1, AStarIterator.AStarNode n2) {

                // TODO: implement a good cost function which is made for electricity

                Edge edge = n1.getNode().getEdge( n2.node )
                SimpleFeatureImpl feature = (SimpleFeatureImpl) edge.getObject()

                Double o = (Double) feature.getAttribute( "km" )
                if ( o && o >= 0 ) {

                } else {
                    o = 0.001
                }

                // Double o = (Double) feature.getAttribute( "cost" )

                // now lets weight the "km" with maximum speed, to prefer Stadtautobahn
                Integer speed = (Integer) feature.getAttribute( "kmh" );

                if ( speed && speed > 0 ) {

                } else {
                    speed = 30
                }


                Double costs = 1000 * o;
                if ( speed <= 30 ) {
                    costs = 6 * o;
                } else if ( speed > 30 && speed < 80 ) {
                    costs = 3 * o;
                } else if ( speed >= 80 ) {
                    costs = 1 * o;
                }

                // Double costs = ( Math.pow( 100/speed, 2) ) * o


                // the real cost until now + real costs from n1 to n2
                // return n1.getG() + o;
                return costs
            }

            /**
             * providing haversine distance with multiplied speed as a heuristic
             *
             * @param n
             * @return
             */
            public double h( org.geotools.graph.structure.Node n ) {

                Point from = (Point) n.getObject();
                Point to = (Point) t.getObject();

                // multiplied by 1 because of weighting the distance with speed...
                // for not overestimating we take 1
                return 1 * Calculater.haversine( from.x, from.y, to.x, to.y );

                // return getDist( from, to );
                // return getManhatten( from, to );
            }

        };


        AStarShortestPathFinder pf = new AStarShortestPathFinder( graph, s, t,   functions );

        pf.calculate();
        pf.finish();

//find some destinations to calculate paths to

        // Node target = QuickStart.findClosest( new Coordinate( 0.1, 0.1 ), graph );



//calculate the paths


        try {
            path = pf.getPath();
            //path.riterator().next()

            org.geotools.graph.structure.Node previous = null;
            org.geotools.graph.structure.Node node = null;
            if ( path != null ) {

                for ( Iterator ritr = path.riterator(); ritr.hasNext(); ) {

                    node = ( org.geotools.graph.structure.Node ) ritr.next();
                    if ( previous != null ) {
                        // adding the edge between them into vector
                        edges.add( node.getEdge( previous ) )

                    }
                    previous = node

                }

            }
        } catch (  Exception e  ) {
            log.error( "failed to get path from astar algorithm" )
            log.debug( e )
        }


        return edges;
    }

    private org.geotools.graph.structure.Node findClosestNode( Coordinate coordinate, Graph graph ) {

        org.geotools.graph.structure.Node closest = null;
        double minDist = 0.0;

        Collection<org.geotools.graph.structure.Node> nodes = (Collection<org.geotools.graph.structure.Node>) graph.getNodes();

        for ( org.geotools.graph.structure.Node node : nodes ) {
            Point p = (Point) node.getObject();

            double d = Calculater.haversine( coordinate.y, coordinate.x, p.x, p.y );

            // double d = getDist( coordinate, p );

            if ( d < 0.000001 ) {
                // log.error( "coordinate: ${coordinate.x} : ${coordinate.y}  --- found near dist node: ${p.x} : ${p.y}   with ${d} km"  )
                return node
            }


            if ( closest == null || d < minDist ) {
                minDist = d;
                closest = node;
            }

        }

        // log.error( "-> coordinate: ${coordinate.x} : ${coordinate.y}  --- found near dist node: ${((Point) closest.getObject()).x} : ${((Point) closest.getObject()).y}   with ${d} km"  )

        return closest;
    }

    private static  double getDist( Coordinate coordinate, Point p ) {
        return getDist( coordinate.x, coordinate.y, p.getX(), p.getY() );
    }

    private static double getDist( Point from, Point to ) {
        return getDist( from.getX(), from.getY(), to.getX(), to.getY() );
    }


    private static double getManhatten( Point from, Point to ) {

        return Math.abs( from.y - to.y ) + Math.abs( from.x - to.x )

    }

    /**
     * implementation of euclidean distance
     *
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @return
     */
    private static double getDist( double fromX, double fromY, double toX, double toY ) {
        double dx = fromX - toX;
        double dy = fromY - toY;
        return Math.sqrt( dx * dx + dy * dy );
        // return  dx * dx + dy * dy ;
    }

    List<BasicEdge> repairEdges( List<BasicEdge> pathEdges ) {

        ArrayList<BasicEdge> edgesToPersist = new ArrayList<BasicEdge>()
        int counter = 0
        Integer rememberOldB = 0;

        boolean repaired = true

        for ( BasicEdge edge : pathEdges ) {

            if ( counter == 0 ) {
                // we don't know if this one is correct, just save it
                edgesToPersist.add( edge )
            }
            else if ( counter == 1 ) {
                // here we have to check, whether (0) and (1) is correct, or one of them is false
                org.geotools.graph.structure.Node currentNodeA = edge.nodeA;
                org.geotools.graph.structure.Node currentNodeB = edge.nodeB

                org.geotools.graph.structure.Node zeroNodeA = edgesToPersist.get( 0 ).nodeA
                org.geotools.graph.structure.Node zeroNodeB = edgesToPersist.get( 0 ).nodeB

                if ( zeroNodeB.ID == currentNodeA.ID ) {
                    // everything is fine

                    rememberOldB = currentNodeB.ID
                    edgesToPersist.add( edge )

                } else if ( zeroNodeA.ID == currentNodeA.ID ) {
                    // turn zero
                    BasicEdge turnedEdge = edgesToPersist.get( 0 )
                    edgesToPersist.clear()
                    edgesToPersist.add( new BasicEdge( turnedEdge.nodeB, turnedEdge.nodeA ) )

                    // current is fine
                    rememberOldB = currentNodeB.ID
                    edgesToPersist.add( edge )

                } else if ( zeroNodeA.ID == currentNodeB.ID ) {
                    // turn zero
                    BasicEdge turnedEdge = edgesToPersist.get( 0 )
                    edgesToPersist.clear()
                    edgesToPersist.add( new BasicEdge( turnedEdge.nodeB, turnedEdge.nodeA ) )

                    // turn current
                    rememberOldB = currentNodeA.ID
                    edgesToPersist.add( new BasicEdge( edge.nodeB, edge.nodeA ) )

                } else {
                    repaired = false
                }

            } else {

                if ( edge.nodeA.ID == rememberOldB ) {
                    // everything is fine

                    edgesToPersist.add( edge )

                    rememberOldB = edge.nodeB.ID
                } else if ( edge.nodeB.ID == rememberOldB ) {
                    // almost fine, just turn

                    edgesToPersist.add( new BasicEdge( edge.nodeB, edge.nodeA ) );

                    rememberOldB = edge.nodeA.ID
                } else {
                    // something is completely wrong with the path!
                    repaired = false
                }
            }
            counter++
            //log.error( "osm_id = " +  osmId );
        }

        if ( !repaired ) {
            log.debug( "couldn't repair path list" )
        }

        return edgesToPersist
    }


}

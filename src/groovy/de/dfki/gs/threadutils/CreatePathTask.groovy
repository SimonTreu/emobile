package de.dfki.gs.threadutils

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Point
import de.dfki.gs.service.RouteService
import org.apache.commons.logging.LogFactory
import org.geotools.graph.structure.basic.BasicEdge

import java.util.concurrent.ArrayBlockingQueue

/**
 * @author: glenn
 * @since: 10.12.13
 */
class CreatePathTask extends Thread {

    private static def log = LogFactory.getLog( CreatePathTask.class )

    List<org.geotools.graph.structure.Node> routeStartTargetList
    RouteService routeService
    ArrayBlockingQueue<List<List<BasicEdge>>> routesToPersist

    public CreatePathTask(
            List<org.geotools.graph.structure.Node> routeStartTargetList,
            RouteService routeService,
            ArrayBlockingQueue<List<List<BasicEdge>>> routesToPersist ) {

        this.routeStartTargetList = routeStartTargetList
        this.routeService = routeService
        this.routesToPersist = routesToPersist

    }


    @Override
    void run() {
        List<List<BasicEdge>> multiTargetRoute = new ArrayList<List<BasicEdge>>()

        // find route from element to element and assign to routesToPersist
        def pairs = routeStartTargetList.collate( 2, 1, false );

        log.debug( "pairs: ${pairs}" )

        boolean pathBroken = false

        for ( List<org.geotools.graph.structure.Node> pairList : pairs ) {

            Point startPoint =  (Point) pairList.get( 0 ).getObject();
            Point targetPoint = (Point) pairList.get( 1 ).getObject();

            Coordinate currentStart  = new Coordinate( startPoint.x, startPoint.y );
            Coordinate currentTarget = new Coordinate( targetPoint.x, targetPoint.y );

            List<BasicEdge> pathEdges = routeService.calculatePath( currentStart, currentTarget );

            if ( pathEdges.size() == 0 ) {
                pathBroken = true
                return
            }

            // repair all edges' direction
            pathEdges = routeService.repairEdges( pathEdges )

            // adding repaired edges to multiTargetRoute
            multiTargetRoute.add( pathEdges )

        }

        if ( !pathBroken ) {
            routesToPersist.add( multiTargetRoute )
            log.debug( "added: ${multiTargetRoute} as path" )
        }




    }
}

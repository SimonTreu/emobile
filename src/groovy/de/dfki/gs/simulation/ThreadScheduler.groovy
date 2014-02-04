package de.dfki.gs.simulation

import de.dfki.gs.utils.LatLonPoint
import org.apache.commons.logging.LogFactory

/**
 * @author: glenn
 * @since: 06.11.13
 */
class ThreadScheduler extends Thread {

    private static def log = LogFactory.getLog( ThreadScheduler.class )

    long TIME_TO_STOP

    Map<Long, SimulationThreadTask> tasks

    boolean traffic
    boolean trafficLight

    boolean canceled = false

    /**
     * whenever Scheduler was instantiated the status is "create"
     */
    SchedulerStatus status = SchedulerStatus.create

    /**
     * the simulation time, counted in seconds (1 step means 1 second), starting with initially 0
     */
    Long currentTime

    /**
     * defines how fast time is running, technically the thread process has to wait for
     * interval length for each iteration
     */
    Long interval


    void init(  Map<Long,SimulationThreadTask> taskMap,
                long timeToStop      = Long.MAX_VALUE,
                long interval        = 30,
                boolean traffic      = false,
                boolean trafficLight = false ) {

        tasks = taskMap
        TIME_TO_STOP = timeToStop
        this.interval = interval
        this.traffic = traffic
        this.trafficLight = trafficLight

        currentTime = 0

        status = SchedulerStatus.init
    }

    // TODO: synchrinized ??
    void setInterval( int scaleValue ) {

        interval = new Long( scaleValue )

    }

    void run() {

        status = SchedulerStatus.play

        /**
         * stepping through all simulationTasks
         */
        for ( SimulationThreadTask task : tasks.values() ) {
            task.start()
        }

    }



    Double getDrivenKm( long simulationRouteId ) {
        SimulationThreadTask task = tasks.get( new Long( simulationRouteId ) )
        return task.getDrivenKm( currentTime )
    }

    Integer getCurrentSpeed( long simulationRouteId ) {
        SimulationThreadTask task = tasks.get( new Long( simulationRouteId ) )
        return task.getCurrentKmh()
    }


    LatLonPoint getCurrentPosition( long simulationRouteId ) {
        SimulationThreadTask task = tasks.get( new Long( simulationRouteId ) )
        return  task.getCurrentPosition( currentTime )
    }

    def getTime() {

        def seconds = currentTime as BigInteger
        def secD = ( seconds.remainder( 60g ) ) as BigInteger
        seconds = seconds - secD
        def minutes = ( seconds / 60 ) as BigInteger
        def minD = minutes.remainder( 60g )
        minutes = minutes - minD
        def hours = ( minutes / 60 ) as BigInteger
        def hoursD = hours.remainder( 60g )

        def secDisplay = secD<10?"0${secD}":secD
        def minDisplay = minD<10?"0${minD}":minD

        def res = "${hoursD}:${minDisplay}:${secDisplay}"

        return res
    }

    def getAllCarInfos( long simulationRouteId ) {
        SimulationThreadTask task = tasks.get( new Long( simulationRouteId ) )
        return task.dto( currentTime )
    }


    public void pause() {

        log.debug( "try to pause scheduler at ${currentTime}" )
        status = SchedulerStatus.pause
    }

    public void proceed() {
        status = SchedulerStatus.play

        synchronized( this ) {
            this.notify();
        }

    }

    public void cancel() {

        /**
         * first, free scheduler from pause status, to let while condition to be checked
         */
        if ( status == SchedulerStatus.pause ) {
            proceed()
        }

        canceled = true
        log.debug( "try to cancel" )
    }

}

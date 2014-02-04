package de.dfki.gs.simulation

import de.dfki.gs.utils.LatLonPoint
import org.apache.commons.logging.LogFactory

/**
 * the scheduler has a timer and for every run, timer( long ) starts with 0
 * stepInterval
 *
 * @author: glenn
 * @since: 23.10.13
 */
class Scheduler extends Thread {

    private static def log = LogFactory.getLog( Scheduler.class )

    long TIME_TO_STOP

    Map<Long, SimulationTask> tasks

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


    void init(  Map<Long,SimulationTask> taskMap,
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

        while( status != SchedulerStatus.finished &&
               currentTime < TIME_TO_STOP &&
               !canceled ) {

            /**
             * stepping through all simulationTasks
             */
            for ( SimulationTask task : tasks.values() ) {
                task.step( currentTime )
            }
            currentTime++

            // only for debug
            // if ( currentTime%10 == 0 ) {
                log.debug( "currentTime: ${currentTime}" )
            // }

            /**
             * this can pause the thread
             */
            synchronized ( this ) {
                while ( status == SchedulerStatus.pause ) {
                    try {
                        wait();
                    } catch (Exception e) {
                        log.error( "cannot wait status is ${status}", e )
                    }
                }
            }

            /**
             * wait for interval seconds
             */
            try {
                sleep( interval )
                // log.error( "try to sleep: ${interval} ms" )
            } catch ( InterruptedException e ) {
                interrupt();
                log.error( "failed to wait for ${interval} at time: ${currentTime}", e )
            }

        }


        status = SchedulerStatus.finished
        log.debug( "finished" )
    }


    SimulationTask getTaskById( Long id ) {

        return tasks.get( id )

    }


    Double getDrivenKm( long simulationRouteId ) {
        SimulationTask task = tasks.get( new Long( simulationRouteId ) )
        return task.getDrivenKm( currentTime )
    }

    Integer getCurrentSpeed( long simulationRouteId ) {
        SimulationTask task = tasks.get( new Long( simulationRouteId ) )
        return task.getCurrentKmh()
    }


    LatLonPoint getCurrentPosition( long simulationRouteId ) {
        SimulationTask task = tasks.get( new Long( simulationRouteId ) )
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
        SimulationTask task = tasks.get( new Long( simulationRouteId ) )
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

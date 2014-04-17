package de.dfki.gs.model.elements

import de.dfki.gs.simulation.SchedulerStatus
import org.apache.commons.logging.LogFactory

import java.util.concurrent.BrokenBarrierException
import java.util.concurrent.CyclicBarrier

/**
 * Created by glenn on 14.04.14.
 */
abstract class Agent extends Thread {

    private static def log = LogFactory.getLog( Agent.class )

    SchedulerStatus status = SchedulerStatus.create

    boolean canceled = false

    long currentTime = 0

    Long interval = 2

    CyclicBarrier barrier;

    int personalId;



    abstract def step( long currentTime );

    abstract def finish();


    @Override
    public void run() {

        // log.error( "started running thread: ${this.id}" )

        status = SchedulerStatus.play

        while( status != SchedulerStatus.finished && !canceled ) {

            /**
             * this can pause the thread
             */
            /*
            synchronized ( this ) {
                while ( status == SchedulerStatus.pause ) {
                    try {
                        wait();
                    } catch (Exception e) {
                        log.error( "cannot wait status is ${status}", e )
                    }
                }
            }
            */

            // the hard goes here!
            step( currentTime )

            currentTime++

            try {

                /**
                 * TODO: check this: time-sync every 300s ( 5min )
                  */
                if ( !canceled && currentTime%300 == 0 ) {
                    barrier.await()
                    // log.error( "pId: ${personalId} - current time: ${this.id} : ${currentTime}" )
                }

            } catch ( InterruptedException ie ) {
                log.error( "barrier interrupted", ie )
            } catch ( BrokenBarrierException be ) {
                log.error( "barrier is broken", be )
            }

        }



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

    void setInterval( int scaleValue ) {

        interval = new Long( scaleValue )

    }

    public void cancel() {

        finish();

        /**
         * first, free scheduler from pause status, to let while condition to be checked
         */
        if ( status == SchedulerStatus.pause ) {
            proceed()
        }

        canceled = true
        log.error( "${personalId} : try to cancel" )

    }

    void setBarrier(CyclicBarrier barrier) {
        this.barrier = barrier
    }

    int getPersonalId() {
        return personalId
    }

    void setPersonalId(int personalId) {
        this.personalId = personalId
    }
}

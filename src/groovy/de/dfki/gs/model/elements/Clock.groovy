package de.dfki.gs.model.elements

import de.dfki.gs.simulation.SchedulerStatus
import org.apache.commons.logging.LogFactory

/**
 * Created by glenn on 14.04.14.
 */
class Clock extends Thread {

    private static def log = LogFactory.getLog( Clock.class )

    SchedulerStatus status = SchedulerStatus.create

    boolean canceled = false
    long currentTime = 0

    Long interval = 2



    @Override
    public void run() {

        long timeMillis
        long timeNeeded

        log.error( "started running thread: ${this.id}" )

        status = SchedulerStatus.play


        while( status != SchedulerStatus.finished && !canceled ) {

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



            try {
                // related to 1000ms
                // just wait for reduced interval
                // sleep( Math.max( 0, interval - timeNeeded ) )
                sleep( interval )
            } catch ( Exception e ) {
                log.error( "failed to wait..", e )
            }

            currentTime++

        }



    }

}

package de.dfki.gs.threadutils

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

/**
 * Created by glenn on 17.04.14.
 */
class ThreadPoolExecutorUtils {

    private static Log log = LogFactory.getLog( ThreadPoolExecutorUtils.class );

    public static NotifyingBlockingThreadPoolExecutor createThreadPoolExecutor( int threadPoolSize ) {

        int poolSize = threadPoolSize;      // the count of currently paralellized threads
        //int poolSize = 1;

        int queueSize = 2 * threadPoolSize;    // recommended - twice the size of the poolSize
        //int queueSize = 1;

        int threadKeepAliveTime = 1;
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

        NotifyingBlockingThreadPoolExecutor threadPoolExecutor =
                new NotifyingBlockingThreadPoolExecutor(poolSize, queueSize,
                        threadKeepAliveTime, threadKeepAliveTimeUnit,
                        maxBlockingTime, maxBlockingTimeUnit, blockingTimeoutCallback);

        return threadPoolExecutor;

    }

    public static NotifyingBlockingThreadPoolExecutor createThreadPoolExecutor() {

        return createThreadPoolExecutor( 400 );

    }

    public static void waiting( NotifyingBlockingThreadPoolExecutor executor, long timeToWait ) {

        if ( executor.getTasksInProcess() > 0 ) {

            try {
                long timeToStop = timeToWait;
                boolean done = false;

                while( !done && timeToStop > 0 ) {

                    done = executor.await( 500, TimeUnit.MILLISECONDS);
                    log.error("waiting for " + executor.getTasksInProcess() + " remaining task(s) of " + executor.getRunnables().size());

                    // turn on, if threads should all stop after 6 sec, no matter if there are finished
                    timeToStop = timeToStop - 1;
                } ;
            } catch (InterruptedException e) {
                log.error(e);
            }
        }

    }

    public static void waiting( NotifyingBlockingThreadPoolExecutor executor ) {

        waiting( executor, 600 );

    }

}

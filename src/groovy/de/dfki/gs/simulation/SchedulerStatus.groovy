package de.dfki.gs.simulation

/**
 * @author: glenn
 * @since: 26.10.13
 */
public enum SchedulerStatus {

    create,     // only scheduler object was created, nothing is initialized
    init,       // scheduler object is initialized
    play,       // scheduler is playing
    pause,      // scheduler is pausing
    stop,       // stopped and not init anymore
    finished,   // finished
    isNull      // nothing of all other

}
package op.algorithm;

import op.model.Schedule;

/**
 * Defines the methods that an observer of any scheduler is interested in
 * @author Darcy Cox
 */
public interface SchedulerListener {

    /**
     * Tells the listener that a new schedule has been created
     * @param s The new schedule
     */
    void newSchedule(Schedule s);
}

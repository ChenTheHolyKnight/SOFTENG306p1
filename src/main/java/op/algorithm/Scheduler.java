package op.algorithm;

import op.model.Dependency;
import op.model.Schedule;
import op.model.Task;
import op.model.TaskGraph;
import op.visualization.Visualiser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Base class for any scheduling algorithm implementation.
 * Any subclasses must implement the produceSchedule method.
 */
public abstract class Scheduler {

    // objects that are interested in being updated by the Scheduler
    private List<Visualiser> listeners;

    /**
     * Default constructor for a Scheduler instance
     */
    public Scheduler() {
        this.listeners = new ArrayList<Visualiser>();
    }

    /**
     * Registers a Visualiser instance to observe the Scheduler.
     * @param v the Visualiser to register as a listener
     */
    public void addListener(Visualiser v) {
        this.listeners.add(v);
    }

    /**
     * Produces a valid schedule of the task graph by allocating each task to a given number of processors,
     * while respecting task dependencies.
     * @param numProcessors the number of processors available to schedule tasks onto
     * @return a valid schedule containing the specified number of processors and respecting task dependencies
     */
    public abstract Schedule produceSchedule(int numProcessors);

}

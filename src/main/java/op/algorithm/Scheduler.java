package op.algorithm;

import op.model.Schedule;
import op.visualization.Visualizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for any scheduling algorithm implementation.
 * Any subclasses must implement the produceSchedule method.
 */
public abstract class Scheduler {

    // objects that are interested in being updated by the Scheduler
    private List<Visualizer> listeners;

    /**
     * Default constructor for a Scheduler instance
     */
    public Scheduler() {
        this.listeners = new ArrayList<Visualizer>();
    }

    /**
     * Registers a Visualizer instance to observe the Scheduler.
     * @param v the Visualizer to register as a listener
     */
    public void addListener(Visualizer v) {
        this.listeners.add(v);
    }

    /**
     * Produces a valid schedule of the task graph by allocating each task to a given number of processors,
     * while respecting task dependencies.
     * @return a valid schedule containing the specified number of processors and respecting task dependencies
     */
    public abstract Schedule produceSchedule();

}

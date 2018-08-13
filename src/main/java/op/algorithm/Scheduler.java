package op.algorithm;

import op.model.*;
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
    private int numProcessors;

    /**
     * Constructor for a Scheduler instance
     * @param numProcessors the number of processors to schedule tasks on
     */
    public Scheduler(int numProcessors) {
        this.numProcessors = numProcessors;
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
     * @return a valid schedule containing the specified number of processors and respecting task dependencies
     */
    public abstract Schedule produceSchedule();

    /**
     *
     * @return the number of processors to schedule tasks on
     */
    protected int getNumProcessors() {
        return this.numProcessors;
    }

    /**
     * Gets the earliest possible time of a given task on a given processor, based on a (partial) schedule
     * @param t the task to be scheduled
     * @param s the schedule to be extended
     * @param p the processor the task is to be scheduled on
     * @return The earliest start time of the task on the given processor, based on the given schedule
     */
    protected int getEarliestStartTime(Schedule s, Task t, int p) {
        List<Dependency> incomingEdges = t.getIncomingDependencies();

        // Estimate the earliest start time of the task as the next available time on the processor
        int startTime = s.getNextFreeTimeOfProcessor(p);

        for (Dependency incomingEdge: incomingEdges) {
            Task startTask = incomingEdge.getStartTask();

            // If a dependent task is not scheduled on the same processor, the start time of this task
            // cannot be earlier than the end time of the dependent task plus the communication cost
            if (s.getScheduledTask(startTask).getProcessor() != p) {
                int newStartTime = s.getScheduledTask(startTask).getStartTime()
                        + startTask.getDuration()
                        + incomingEdge.getWeight();

                if (newStartTime > startTime) {
                    startTime = newStartTime;
                }
            }
        }
        return startTime;
    }

}

package op.algorithm;

import op.model.*;
import op.visualization.Visualiser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Base class for any scheduling algorithm implementation.
 * Any subclasses must implement the produceSchedule method.
 * @author Darcy Cox
 */
public abstract class Scheduler {

    /**
     * Enum specifying all available scheduling algorithm implementations
     */
    public enum Implementation {
        PARA("parallel"),
        DFS("dfs"),
        ASTAR("astar"),
        SIMPLE("simple"),
        GREEDY("greedy");

        private String cmdRepresentation;

        Implementation(String cmdRepresentation) {
            this.cmdRepresentation = cmdRepresentation;
        }

        public String getCmdRepresentation() {
            return this.cmdRepresentation;
        }

    }


    // objects that are interested in being updated by the Scheduler
    private List<Visualiser> listeners;
    private int numProcessors;

    /**
     * Constructor for a Scheduler instance
     * @param numProcessors the number of processors to schedule tasks on
     */
    public Scheduler(int numProcessors) {
        this.numProcessors = numProcessors;
        this.listeners = new ArrayList<>();
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

}

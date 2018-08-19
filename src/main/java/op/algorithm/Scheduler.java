package op.algorithm;

import op.model.Schedule;
import op.visualization.Visualizer;
import op.model.*;
import op.visualization.messages.MessageAddNodes;
import op.visualization.messages.MessageEliminateNodes;
import op.visualization.messages.MessageSetOptimalSolution;
import op.visualization.messages.UpdateMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private List<SchedulerListener> listeners;
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
     * Registers a SchedulerListener instance to observe the Scheduler.
     * @param sl the listener to register
     */
    public void addListener(SchedulerListener sl) {
        this.listeners.add(sl);
    }

    /**
     * Informs registered listeners of a new schedule that has been created
     */
    protected void fireNewScheduleUpdate(Schedule s) {
        for (SchedulerListener listener: listeners) {
            listener.newSchedule(s);
        }
    }

    /**
     * Informs registered listeners of the number of nodes visited in the search space.
     */
    protected void fireNodesVisitedUpdate(int numNodesVisited) {
        for (SchedulerListener listener : listeners) {
            listener.updateNodesVisited(numNodesVisited);
        }
    }

    /**
     * Informs registered listeners of the new number of pruned trees that are updated
     */
    protected void fireNumPrunedTreesUpdate(int numPrunedTrees) {
        for (SchedulerListener listener : listeners) {
            listener.updateNumPrunedTrees(numPrunedTrees);
        }
    }

    /**
     * Informs registered listeners of a new best schedule length
     */
    protected void fireBestScheduleLengthUpdate(int bestLength) {
        for (SchedulerListener listener : listeners) {
            listener.updateBestScheduleLength(bestLength);
        }
    }

    /**
     * Informs registered listeners that the optimal schedule has been found
     */
    protected void fireOptimalSolutionFound() {
        for (SchedulerListener listener : listeners) {
            listener.optimalScheduleFound();
        }
    }

    protected List<SchedulerListener> getListeners() {
        return listeners;
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

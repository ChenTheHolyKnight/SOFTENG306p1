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
    private boolean toVisualize;

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
        System.out.println("new listener: " + sl);
        this.listeners.add(sl);
    }

    /**
     * Informs registered listeners of a new schedule that has been created
     * @param s the new schedule
     */
    protected void fireNewScheduleUpdate(Schedule s) {
        for (SchedulerListener listener: listeners) {
            listener.newSchedule(s);
        }
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

//    /**
//     * Informs listeners that new schedules have been created
//     * @param schedule
//     * @param children
//     */
//    protected void newSchedulesUpdate(Schedule schedule, List<Schedule> children) {
//        if (!toVisualize) {
//            return;
//        }
//        Set<String> childIds = new HashSet<>();
//        if (children != null) {
//            for (Schedule child: children) {
//                childIds.add(child.toString());
//            }
//        }
//        informListenersOfUpdate(new MessageAddNodes(schedule.toString(), childIds));
//    }
//
//    /**
//     * Informs listeners that schedules have been removed
//     * @param remaining
//     * @param all
//     */
//    protected void removedSchedulesUpdate(List<Schedule> remaining, List<Schedule> all) {
//        if (!toVisualize) {
//            return;
//        }
//        Set<String> removedIds = new HashSet<>();
//        all.removeAll(remaining);
//        if (all != null) {
//            for (Schedule schedule: all) {
//                removedIds.add(schedule.toString());
//            }
//        }
//        informListenersOfUpdate(new MessageEliminateNodes(removedIds));
//    }
//
//    /**
//     * Informs listeners that the optimal solution has been found
//     * @param optimal
//     */
//    protected void optimalSolutionUpdate(Schedule optimal) {
//        if (!toVisualize) {
//            return;
//        }
//        informListenersOfUpdate(new MessageSetOptimalSolution(optimal.toString()));
//    }

}

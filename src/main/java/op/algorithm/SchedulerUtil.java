package op.algorithm;

import op.algorithm.bound.CostFunctionManager;
import op.model.Dependency;
import op.model.Schedule;
import op.model.Task;
import op.model.TaskGraph;

import java.util.*;

/**
 * A util class can be used by all the schedulers
 */
public class SchedulerUtil {
    /**
     * A method to create a topological order for a given graph
     *
     * @param tasks all the tasks in the input graph
     * @return a list of tasks that sorted in topological order
     */
    public static List<Task> createTopologicalOrder(List<Task> tasks) {
        List<Task> removedTasks;
        List<Task> tasks1 = new ArrayList<>(tasks);
        List<Dependency> dependencies = new ArrayList<>();

        tasks1.forEach(task -> dependencies.addAll(task.getOutgoingDependencies()));
        List<Dependency> removedDep=new ArrayList<>();
        List<Task> outputList=new ArrayList<>();

        while (!tasks1.isEmpty()){
            removedTasks=findEntryPoints(tasks1,dependencies);
            removedTasks.forEach(task -> removedDep.addAll(task.getOutgoingDependencies()));

            outputList.addAll(removedTasks);

            tasks1.removeAll(removedTasks);
            dependencies.removeAll(removedDep);

            removedDep.clear();
            removedTasks.clear();
        }
        return outputList;
    }

    /**
     * This is a method to find the Entry task of the task graph.
     * */
    private static List<Task> findEntryPoints(List<Task> tasks,List<Dependency> dependencies){
        List<Task> starts=new ArrayList<>();

        tasks.forEach(task -> {
            List<Dependency> incoming=new ArrayList<>();
            for(Dependency dependency:dependencies){
                if(dependency.getEndTask().equals(task)){
                    incoming.add(dependency);
                }
            }
            if(incoming.isEmpty()){
                starts.add(task);
            }
        });
        return starts;
    }

    /**
     * Finds the tightest bound for the length of a schedule, given specified cost functions
     * @param s the schedule
     * @param costFunctionManager a list of cost functions
     * @return the tightest bound
     */
    public static int getTightestBound(Schedule s, CostFunctionManager costFunctionManager) {

        // calculate the cost functions using each implementation then take the maximum of them
        // because it will be the tightest lower bound

        return costFunctionManager.calculate(s);
    }


    /**
     * Gets the earliest possible time of a given task on a given processor, based on a (partial) schedule
     * @param t the task to be scheduled
     * @param s the schedule to be extended
     * @param p the processor the task is to be scheduled on
     * @return The earliest start time of the task on the given processor, based on the given schedule
     */
    public static int getEarliestStartTime(Schedule s, Task t, int p) {
        List<Dependency> incomingEdges = t.getIncomingDependencies();

        // Estimate the earliest start time of the task as the next available time on the processor
        int startTime = s.getNextFreeTimeOfProcessor(p);

        for (Dependency incomingEdge : incomingEdges) {
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

    /**
     * gets all free tasks based on a task graph and a (partial) schedule
     *  a free task is one whose dependencies are already scheduled, and is not scheduled itself
     * @param tg The task graph holding information about tasks and their dependencies
     * @param s the schedule to use to determine which tasks are free
     */
    public static List<Task> getFreeTasks(TaskGraph tg, Schedule s) {
        List<Task> freeTasks = new ArrayList<Task>();
        for (Task t : tg.getAllTasks()) {

            if (s.getScheduledTask(t) == null) {
                // the current task is not yet scheduled, check its dependencies
                List<Dependency> deps = t.getIncomingDependencies();

                if (deps.isEmpty()) {
                    freeTasks.add(t); // no dependencies so this task must be free

                } else {

                    boolean dependenciesAllScheduled = true;
                    for (Dependency d : deps) {
                        Task startTask = d.getStartTask();
                        if (s.getScheduledTask(startTask) == null) {
                            // if even one of the tasks dependencies is not scheduled, it is not free
                            dependenciesAllScheduled = false;
                        }
                    }
                    if (dependenciesAllScheduled) {
                        freeTasks.add(t);
                    }
                }
            }
        }

        return freeTasks;
    }

}
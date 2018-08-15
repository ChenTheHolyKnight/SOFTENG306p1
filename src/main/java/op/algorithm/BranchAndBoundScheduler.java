package op.algorithm;

import op.algorithm.bound.CostFunction;
import op.algorithm.prune.Pruner;
import op.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all branch and bound implementations of the scheduling algorithm. Instantiated with a Pruner
 * implementation, which is to be used in the scheduling algorithm.
 * @author Darcy Cox
 */
public abstract class BranchAndBoundScheduler extends Scheduler {

    private Pruner pruner;
    // a branch and bound scheduler may use any combination of cost functions.
    private List<CostFunction> costFunctions;

    /**
     * Creates a BranchAndBoundScheduler instance with the specified Pruner implementation.
     * @param p The Pruner implementation to be used in the scheduling algorithm
     * @param numProcessors the number of processors to schedule tasks on
     * @param cf a list of cost function implementations to use for this scheduler
     */
    public BranchAndBoundScheduler(int numProcessors, Pruner p, List<CostFunction> cf) {
        super(numProcessors);
        this.pruner = p;
        this.costFunctions = cf;
    }

    /**
     * Called by subclasses to access their specified Pruner implementation
     * @return The Pruner implementation for the subclass to use as
     */
    protected Pruner getPruner() {
        return this.pruner;
    }

    /**
     * Called by subclasses to access the CostFunction implementations
     * @return the cost function implementations that have been set for this instance
     */
    protected List<CostFunction> getCostFunctions() { return this.costFunctions; }

    /**
     * Method that branch and bound implementations can use to get the children from a current schedule.
     * Children of a schedule are produced by scheduling one more task onto the provided schedule as many ways as
     * possible.
     * @param s The schedule to get the children of, can be an empty schedule
     * @return A list of the children stemming from the current schedule
     */
    protected List<Schedule> getChildrenOfSchedule(Schedule s) {
        List<Schedule> children = new ArrayList<Schedule>();
        TaskGraph tg = TaskGraph.getInstance();

        List<Task> freeTasks = getFreeTasks(tg, s);

        // build a new schedule for every valid free task to processor allocation and add to the list of children
        for (Task t : freeTasks) {
            for (int i = 1; i <= super.getNumProcessors(); i++) {
                int startTime = super.getEarliestStartTime(s, t, i);
                children.add(new Schedule(s, new ScheduledTask(t, startTime, i)));
            }
        }

        return children;
    }


    // gets all free tasks based on a task graph and a (partial) schedule
    // a free task is one whose dependencies are already scheduled, and is not scheduled itself
    private List<Task> getFreeTasks(TaskGraph tg, Schedule s) {

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

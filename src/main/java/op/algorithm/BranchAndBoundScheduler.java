package op.algorithm;

import op.algorithm.bound.CostFunction;
import op.algorithm.bound.CostFunctionManager;
import op.algorithm.prune.Pruner;
import op.algorithm.prune.PrunerManager;
import op.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all branch and bound implementations of the scheduling algorithm. Instantiated with a Pruner
 * implementation, which is to be used in the scheduling algorithm.
 * @author Darcy Cox
 */
public abstract class BranchAndBoundScheduler extends Scheduler {

    private PrunerManager prunerManager;
    // a branch and bound scheduler may use any combination of cost functions.
    private CostFunctionManager costFunctionManager;

    /**
     * Creates a BranchAndBoundScheduler instance with the specified Pruner implementation.
     * @param p The Pruner Manager to be used in the scheduling algorithm
     * @param numProcessors the number of processors to schedule tasks on
     * @param cfm a cost function manager to use for this scheduler
     */
    public BranchAndBoundScheduler(int numProcessors, PrunerManager p, CostFunctionManager cfm) {
        super(numProcessors);
        this.prunerManager = p;
        this.costFunctionManager = cfm;
    }

    /**
     * Called by subclasses to access their specified PrunerManager
     * @return The Pruner implementation for the subclass to use
     */
    protected PrunerManager getPrunerManager() {
        return this.prunerManager;
    }

    /**
     * Called by subclasses to access the CostFunction implementations
     * @return the cost function manager that has been set for this instance
     */
    protected CostFunctionManager getCostFunctionManager() { 
    	return this.costFunctionManager; 
    	}

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

        List<Task> freeTasks = SchedulerUtil.getFreeTasks(tg, s);

        // build a new schedule for every valid free task to processor allocation and add to the list of children
        for (Task t : freeTasks) {
            for (int i = 1; i <= super.getNumProcessors(); i++) {
                int startTime = SchedulerUtil.getEarliestStartTime(s, t, i);
                children.add(new Schedule(s, new ScheduledTask(t, startTime, i)));
            }
        }

        return children;
    }

}

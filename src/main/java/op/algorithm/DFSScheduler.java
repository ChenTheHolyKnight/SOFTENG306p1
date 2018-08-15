package op.algorithm;

import op.algorithm.bound.CostFunction;
import op.algorithm.prune.Pruner;
import op.algorithm.prune.PrunerManager;
import op.model.Schedule;
import op.model.TaskGraph;

import java.util.List;
import java.util.Stack;

/**
 * Scheduler implementation that uses a DFS branch and bound approach with an arbitrary Pruner implementation to help
 * decide when to bound a certain branch.
 * @author Darcy Cox
 */
public class DFSScheduler extends BranchAndBoundScheduler {

    /**
     * Instantiates a DFSScheduler with the specified params
     * @param numProcessors The number of processors to schedule tasks on
     * @param p The pruner implementation to use
     * @param cf The cost function implementations to use
     */
    public DFSScheduler(int numProcessors, PrunerManager p, List<CostFunction> cf) {
        super(numProcessors, p, cf);
    }

    /**
     * @return a schedule with the optimal length
     */
    @Override
    public Schedule produceSchedule() {

        // variables to keep track of the best schedule so far in the search
        Schedule bestSchedule = null;
        int bestScheduleLength = Integer.MAX_VALUE;
        PrunerManager pm = getPrunerManager();

        // initialize stack with the empty schedule
        Stack<Schedule> scheduleStack =  new Stack<Schedule>();
        scheduleStack.push(new Schedule());

        // start the search, and continue until all possible schedules have been processed
        while (!scheduleStack.isEmpty()) {
            Schedule currentSchedule = scheduleStack.pop();
            if (currentSchedule.isComplete()) {
                // check if the complete schedule is better than our best schedule so far
                if (currentSchedule.getLength() < bestScheduleLength) {
                    bestSchedule = currentSchedule;
                    bestScheduleLength = currentSchedule.getLength();
                }
            } else {
                // not a complete schedule so add children to the stack to be processed later

                List<Schedule> pruned = pm.execute(super.getChildrenOfSchedule(currentSchedule), bestScheduleLength, getNumProcessors());
                for (Schedule s: pruned){
                    if (costFunctionIsPromising(s, bestScheduleLength)) {
                        scheduleStack.push(s);
                    }
                }
            }
        }

        System.out.println("Optimal length: " + bestSchedule.getLength());
        return bestSchedule;
    }

    // helper to tell us when a partial schedule is worth pursuing any further.
    // returns true if the cost function is less than the known best length
    // returns false if the cost function is greater than or equal to the known best, because all schedules based on
    // this schedule are guaranteed to be worse than, or no better than our known best.
    protected boolean costFunctionIsPromising(Schedule s, int bestSoFar) {

        List<CostFunction> costFunctions = super.getCostFunctions();

        // calculate the cost functions using each implementation then take the maximum of them
        // because it will be the tightest lower bound
        int tightestBound = 0;
        for (CostFunction cf : costFunctions) {
            int currentFunc = cf.calculate(s);
            if (currentFunc > tightestBound) {
                tightestBound = currentFunc;
            }
        }

        return tightestBound < bestSoFar;
    }
}

package op.algorithm;

import op.algorithm.bound.CostFunction;
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
     * @param f The cost function implementation to use
     */
    public DFSScheduler(int numProcessors, Pruner p, CostFunction f) {
        super(numProcessors, p, f);
    }

    /**
     * @return a schedule with the optimal length
     */
    @Override
    public Schedule produceSchedule() {

        // variables to keep track of the best schedule so far in the search
        Schedule bestSchedule = null;
        int bestScheduleLength = Integer.MAX_VALUE;
        Pruner p = getPruner();

        // initialize stack with the empty schedule
        Stack<Schedule> scheduleStack =  new Stack<Schedule>();
        Stack<Integer> beenChecked = new Stack<>();
        scheduleStack.push(new Schedule());

        // start the search, and continue until all possible schedules have been processed
        while (!scheduleStack.isEmpty()) {
            Schedule currentSchedule = scheduleStack.pop();
            beenChecked.push(currentSchedule.hashCode());
            if (currentSchedule.isComplete()) {
                // check if the complete schedule is better than our best schedule so far
                if (currentSchedule.getLength() < bestScheduleLength) {
                    bestSchedule = currentSchedule;
                    bestScheduleLength = currentSchedule.getLength();
                    //System.out.println("New best: " + bestScheduleLength);
                }
            } else {
                // not a complete schedule so add children to the stack to be processed later

                List<Schedule> pruned = p.prune(super.getChildrenOfSchedule(currentSchedule), bestScheduleLength, getNumProcessors());
                for (Schedule s: pruned){
                    if (costFunctionIsPromising(s, bestScheduleLength) && !beenChecked.contains(s.hashCode())) {
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
    // this schedule are guaranteed to be worse than our known best.
    private boolean costFunctionIsPromising(Schedule s, int bestSoFar) {
        int costFunction = super.getCostFunction().calculate(s);
        return costFunction < bestSoFar;
    }
}

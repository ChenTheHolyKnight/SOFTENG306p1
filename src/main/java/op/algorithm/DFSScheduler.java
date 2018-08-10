package op.algorithm;

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
     */
    public DFSScheduler(int numProcessors, Pruner p) {
        super(numProcessors, p);
    }

    /**
     * @return a schedule with the optimal length
     */
    @Override
    public Schedule produceSchedule() {

        // variables to keep track of the best schedule so far in the search
        Schedule bestSchedule = null;
        int bestScheduleLength = Integer.MAX_VALUE;

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
                    System.out.println("New best: " + bestScheduleLength);
                }
            } else {
                // not a complete schedule so add children to the stack to be processed later
                for (Schedule s: super.getChildrenOfSchedule(currentSchedule)) {
                    scheduleStack.push(s);
                }
            }
        }

        System.out.println("Optimal length: " + bestSchedule.getLength());
        return bestSchedule;
    }
}

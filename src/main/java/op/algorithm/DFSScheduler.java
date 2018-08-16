package op.algorithm;

import op.algorithm.bound.CostFunction;
import op.algorithm.bound.CostFunctionManager;
import op.algorithm.prune.Pruner;
import op.algorithm.prune.PrunerManager;
import op.model.Schedule;
import op.model.TaskGraph;
import op.visualization.messages.MessageAddNodes;

import java.util.List;
import java.util.Set;
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
     * @param p The pruner manager to use
     * @param cfm The cost function manager to use
     */
    public DFSScheduler(int numProcessors, PrunerManager p, CostFunctionManager cfm) {
        super(numProcessors, p, cfm);
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
        CostFunctionManager cfm = getCostFunctionManager();

        // initialize stack with the empty schedule
        Stack<Schedule> scheduleStack =  new Stack<Schedule>();
        Schedule emptySchedule = new Schedule();
        scheduleStack.push(emptySchedule);
//        newSchedulesUpdate(emptySchedule, null);

        // start the search, and continue until all possible schedules have been processed
        while (!scheduleStack.isEmpty()) {
            Schedule currentSchedule = scheduleStack.pop();
            if (currentSchedule.isComplete()) {
                // check if the complete schedule is better than our best schedule so far
                if (currentSchedule.getLength() < bestScheduleLength) {
                    bestSchedule = currentSchedule;
                    bestScheduleLength = currentSchedule.getLength();
                    System.out.println("firing update, best length so far is " + bestScheduleLength);
                    super.fireNewScheduleUpdate(bestSchedule);
                }
            } else {
                // not a complete schedule so add children to the stack to be processed later
                List<Schedule> children = super.getChildrenOfSchedule(currentSchedule);
                super.fireNodesVisitedUpdate(super.addToNodesVisited(children.size()));

                List<Schedule> pruned = pm.execute(children);
                for (Schedule s: pruned){
                    if (cfm.calculate(s)< bestScheduleLength) {
                        scheduleStack.push(s);
                    }
                }
            }
        }

//        optimalSolutionUpdate(bestSchedule);
        System.out.println("Optimal length: " + bestSchedule.getLength());
        return bestSchedule;
    }
}

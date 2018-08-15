package op.algorithm;

import op.algorithm.bound.CostFunction;
import op.algorithm.bound.CostFunctionManager;
import op.algorithm.prune.Pruner;
import op.algorithm.prune.PrunerManager;
import op.model.Schedule;

import java.util.List;
import java.util.Stack;

/**
 * A runnable object for the parallel implementation of DFS
 * @author Sam Broadhead
 */
public class ParallelRunnable extends DFSScheduler implements Runnable {

    private Stack<Schedule> scheduleStack;
    private Schedule bestSchedule;
    /**
     * Creates a BranchAndBoundScheduler instance with the specified Pruner implementation.
     *
     * @param numProcessors the number of processors to schedule tasks on
     * @param pm           The Pruner implementation to be used in the scheduling algorithm
     * @param cfm          the cost function implementation to use for this scheduler
     */
    public ParallelRunnable(int numProcessors, PrunerManager pm, CostFunctionManager cfm, Stack<Schedule> s) {
        super(numProcessors, pm , cfm);
        this.scheduleStack = s;
    }

    @Override
    public void run() {
        bestSchedule = produceSchedule();
    }
    public Schedule produceSchedule(){
        // variables to keep track of the best schedule so far in the search
        int bestScheduleLength = Integer.MAX_VALUE;
        PrunerManager pm = getPrunerManager();
        CostFunctionManager cfm = getCostFunctionManager();

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

                List<Schedule> pruned = pm.execute(getChildrenOfSchedule(currentSchedule));
                for (Schedule s: pruned){
                    boolean costFunctionIsPromising = cfm.calculate(s) < bestScheduleLength;
                    if (costFunctionIsPromising) {
                        scheduleStack.push(s);
                    }
                }
            }
        }
        return bestSchedule;
    }
    public Schedule getSchedule(){
        return bestSchedule;
    }

}

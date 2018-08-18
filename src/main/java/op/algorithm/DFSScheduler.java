package op.algorithm;

import op.algorithm.bound.CostFunctionManager;
import op.algorithm.prune.PrunerManager;
import op.model.Schedule;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Scheduler implementation that uses a DFS branch and bound approach with an arbitrary Pruner implementation to help
 * decide when to bound a certain branch.
 * @author Darcy Cox
 */
public class DFSScheduler extends BranchAndBoundScheduler  implements Callable<Schedule> {
    private Deque<Schedule> scheduleStack;
    private Deque<Schedule>[] scheduleStacks;
    private int position;
    private AtomicInteger bestScheduleLength;

    /**
     * Instantiates a DFSScheduler with the specified params
     * @param numProcessors The number of processors to schedule tasks on
     * @param pm The pruner manager to use
     * @param cfm The cost function manager to use
     */
    public DFSScheduler(int numProcessors, PrunerManager pm, CostFunctionManager cfm) {
        super(numProcessors, pm, cfm);
        this.scheduleStack = new ArrayDeque<>();
        scheduleStack.push(new Schedule());
        this.bestScheduleLength = new AtomicInteger(Integer.MAX_VALUE);
    }

    /**
     * Constructs a DFS scheduler that will start searching from a specified point in the
     * search space (as specified by the schedules in the stack).
     * @param numProcessors The number of processors to schedule tasks on
     * @param pm The pruner manager to use
     * @param cfm The cost function manager to use
     * @param s A stack containing (partial) schedules to expand upon
     */
    public DFSScheduler(int numProcessors, PrunerManager pm, CostFunctionManager cfm, Deque<Schedule>[] s, int position, AtomicInteger globalBestLength) {
        super(numProcessors, pm, cfm);
        this.scheduleStacks = s;
        this.scheduleStack = s[position];
        this.position = position;
        this.bestScheduleLength = globalBestLength;
    }

    /**
     * @return a schedule with the optimal length
     */
    @Override
    public Schedule produceSchedule() {

        // variables to keep track of the best schedule so far in the search
        Schedule bestSchedule = new Schedule();
        PrunerManager pm = getPrunerManager();
        CostFunctionManager cfm = getCostFunctionManager();
        // start the search, and continue until all possible schedules have been processed
        while (!scheduleStack.isEmpty()) {
//            if(keep) {
//                System.out.println("picked up a new stack");
//                keep = false;
//            }
            Schedule currentSchedule = scheduleStack.pop();
            if (currentSchedule.isComplete()) {
                // check if the complete schedule is better than our best schedule so far
                if (currentSchedule.getLength() < bestScheduleLength.get()) {
                    bestSchedule = currentSchedule;
                    bestScheduleLength.set(currentSchedule.getLength());
                }
            } else {
                // not a complete schedule so add children to the stack to be processed later
                List<Schedule> pruned = pm.execute(super.getChildrenOfSchedule(currentSchedule));
                for (Schedule s: pruned){
                    if (cfm.calculate(s)< bestScheduleLength.get()) {
                        scheduleStack.push(s);
                    }
                }
            }
            if(scheduleStack.isEmpty() && (scheduleStacks!=null)){
//                keep = true;
//                System.out.println("finished my stack");
                getNewStack();
            }
        }

        return bestSchedule;
    }


    @Override
    public Schedule call(){
        return produceSchedule();
    }
    private void getNewStack(){
        int neighbor = (position+1)%scheduleStacks.length;
        scheduleStacks[position] = scheduleStacks[neighbor];
        scheduleStack = scheduleStacks[neighbor];
    }
}

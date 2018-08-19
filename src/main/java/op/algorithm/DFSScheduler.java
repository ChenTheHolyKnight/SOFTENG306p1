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
    private Deque<Schedule> scheduleStack; // the stack for the scheduler to work from
    private Deque<Schedule>[] scheduleStacks; // all possible stacks to work from
    private int position; // the position in the stack array to work from
    private AtomicInteger bestScheduleLength; // the best known length so far

    /**
     * Instantiates a DFSScheduler with the specified params, to be run sequentially. In this case there is only 1
     * stack to work from so the stack array will be size 1 and contain just 1 stack.
     * @param numProcessors The number of processors to schedule tasks on
     * @param pm The pruner manager to use
     * @param cfm The cost function manager to use
     */
    public DFSScheduler(int numProcessors, PrunerManager pm, CostFunctionManager cfm) {
        super(numProcessors, pm, cfm);
        scheduleStacks = new Deque[1];
        scheduleStacks[0] = new ArrayDeque<>();
        scheduleStacks[0].push(new Schedule());
        scheduleStack = scheduleStacks[0];
        bestScheduleLength = new AtomicInteger(Integer.MAX_VALUE);
    }

    /**
     * Constructs a parallel DFS scheduler that will start searching from a specified point in the
     * search space (as specified by the schedules in the stack). The scheduler receives a particular stack from
     * the stack array to search from.
     * @param numProcessors The number of processors to schedule tasks on
     * @param pm The pruner manager to use
     * @param cfm The cost function manager to use
     * @param nodesVisited the reference to the nodes visited counter
     * @param prunedTrees the reference to the pruned trees counter
     * @param globalStacks An array of deques for each thread to work on
     * @param position the position in the globalStacks array to begin work
     * @param globalBestLength the best known length across all threads
     */
    public DFSScheduler(int numProcessors, PrunerManager pm, CostFunctionManager cfm, AtomicInteger nodesVisited,
                        AtomicInteger prunedTrees, Deque<Schedule>[] globalStacks,
                        int position, AtomicInteger globalBestLength) {
        super(numProcessors, pm, cfm, nodesVisited, prunedTrees);
        this.scheduleStacks = globalStacks;
        this.scheduleStack = globalStacks[position];
        this.position = position;
        this.bestScheduleLength = globalBestLength;
    }

    /**
     * @return a schedule with the optimal length
     */
    @Override
    public Schedule produceSchedule() {

        // best schedule seen so far
        Schedule bestSchedule = null;

        // start the search, and continue until all possible schedules have been processed
        while (!scheduleStack.isEmpty()) {

            Schedule currentSchedule = scheduleStack.pop();

            if (currentSchedule.isComplete()) {
                // check if the complete schedule is better than our best schedule so far
                if (currentSchedule.getLength() < bestScheduleLength.get()) {
                    bestSchedule = currentSchedule;
                    bestScheduleLength.set(currentSchedule.getLength());
                    super.fireNewScheduleUpdate(bestSchedule);
                    super.fireBestScheduleLengthUpdate(bestScheduleLength.get());
                }
            } else {
                // not a complete schedule so add children to the stack to be processed later
                List<Schedule> children = super.getChildrenOfSchedule(currentSchedule);
                super.fireNodesVisitedUpdate(super.addToNodesVisited(children.size()));

                List<Schedule> pruned = getPrunerManager().execute(children);
                int numRemovedByCostFunc = pruned.size();

                for (Schedule s: pruned){
                    if (getCostFunctionManager().calculate(s)< bestScheduleLength.get()) {
                        scheduleStack.push(s);
                        numRemovedByCostFunc--;
                    }
                }

                int treesPruned = children.size() - pruned.size() + numRemovedByCostFunc;
                super.fireNumPrunedTreesUpdate(super.addToPrunedTrees(treesPruned));
            }

            // if the scheduler finishes its allocated stack and there are other stacks available
            if(scheduleStack.isEmpty() && scheduleStacks.length > 1){
                getNewStack();
            }
        }
        fireOptimalSolutionFound();
        return bestSchedule;
    }

    /**
     * Method that a thread executor will call when running in parallel.
     * @return the best schedule found by this particular thread.
     * @author Sam Broadhead
     */
    @Override
    public Schedule call(){
        return produceSchedule();
    }

    /**
     * When running in parallel, a thread will pick up a new stack from a neighboring thread upon completion of its
     * stack. It is important that this thread overwrites its finished stack in the stack array with its neighbor's
     * stack so that another thread doesn't pick up an empty stack.
     * @author Sam Broadhead
     */
    private void getNewStack(){
        int neighbor = (position+1)%scheduleStacks.length;
        scheduleStacks[position] = scheduleStacks[neighbor];
        scheduleStack = scheduleStacks[neighbor];
    }
}
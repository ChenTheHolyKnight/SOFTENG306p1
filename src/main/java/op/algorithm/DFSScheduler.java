package op.algorithm;

import op.model.Schedule;
import op.model.TaskGraph;

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

    @Override
    public Schedule produceSchedule() {
        Pruner pruner = super.getPruner();
        TaskGraph tg = TaskGraph.getInstance();
        Schedule bestSchedule = new Schedule();

        Stack<Schedule> nextSchedule =  new Stack<Schedule>();




        return null;
    }
}

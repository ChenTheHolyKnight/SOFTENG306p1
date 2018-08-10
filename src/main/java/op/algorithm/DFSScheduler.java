package op.algorithm;

import op.model.Schedule;

/**
 * Scheduler implementation that uses a DFS branch and bound approach with an arbitrary Pruner implementation to help
 * decide when to bound a certain branch.
 * @author Darcy Cox
 */
public class DFSScheduler extends BranchAndBoundScheduler {

    public DFSScheduler(Pruner p) {
        super(p);
    }

    @Override
    public Schedule produceSchedule() {
        Pruner pruner = super.getPruner();
        return null;
    }
}

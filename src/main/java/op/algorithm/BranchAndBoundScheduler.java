package op.algorithm;

import op.model.ScheduledTask;

/**
 * Base class for all branch and bound implementations of the scheduling algorithm. Instantiated with a Pruner
 * implementation, which is to be used in the scheduling algorithm.
 * @author Darcy Cox
 */
public abstract class BranchAndBoundScheduler extends Scheduler {

    private Pruner pruner;

    /**
     * Creates a BranchAndBoundScheduler instance with the specified Pruner implementation.
     * @param p The Pruner implementation to be used in the scheduling algorithm
     */
    public BranchAndBoundScheduler(int numProcessors, Pruner p) {
        super(numProcessors);
        this.pruner = p;
    }

    /**
     * Called by subclasses to access their specified Pruner implementation
     * @return The Pruner implementation for the subclass to use as
     */
    protected Pruner getPruner() {
        return this.pruner;
    }

}

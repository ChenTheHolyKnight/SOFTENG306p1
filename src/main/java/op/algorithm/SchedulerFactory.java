package op.algorithm;

import op.algorithm.bound.CostFunctionManager;
import op.algorithm.prune.PrunerManager;

import java.util.List;

/**
 * Class to manage the creation of schedulers.
 * @author Darcy Cox
 */
public class SchedulerFactory {

    /**
     * Builds a scheduler using the specified type of
     * @param a The algorithm implementation that the scheduler must use
     * @param numProcessors The number of processors to schedule tasks onto
     * @param costFuncs The cost functions to be used in the scheduler's calculations
     * @return The created scheduler
     */
    public Scheduler createScheduler(Scheduler.Implementation a, int numProcessors,
                                     int numCores, List<CostFunctionManager.Functions> costFuncs,
                                     List<PrunerManager.Pruners> pruners) {
    	CostFunctionManager costFunctionManager = new CostFunctionManager(numProcessors);
        PrunerManager prunerManager = new PrunerManager();
        
        // build the PrunerManager for the scheduler to use, based on the provided Enum values
        if (!pruners.isEmpty()) {
            for (PrunerManager.Pruners pruner : pruners) {
                switch (pruner) {
                    case EQUIVALENT_SCHEDULE:
                        prunerManager.addEquivalentSchedulePruner();
                        break;
                    case NODE_EQUIVALENCE:
                    	prunerManager.addNodeEquivalencePruner();
                    	break;
                }
            }
        }
        
        
        // build the list of cost functions for the scheduler to use, based on the provided Enum values
        if (!costFuncs.isEmpty()) {
            for (CostFunctionManager.Functions cf : costFuncs) {
                switch (cf) {
                    case IDLE_TIME:
                        costFunctionManager.addIdleTimeFunction();;
                        break;
                    case BOTTOM_LEVEL:
                        costFunctionManager.addBottomLevelFunction();;
                        break;
                    case DATA_READY_TIME:
                        costFunctionManager.addDataReadyTimeFunction();
                }
            }
        }

        // construct the scheduler
        Scheduler scheduler = null;
        switch (a) {
            case PARA:
                scheduler = new DFSParallelScheduler(numProcessors, prunerManager, costFunctionManager, numCores);
                break;
            case DFS:
                scheduler = new DFSScheduler(numProcessors, prunerManager, costFunctionManager);
                break;
            case ASTAR:
                scheduler = new AStarScheduler(numProcessors, prunerManager, costFunctionManager);
                break;
            case GREEDY:
                scheduler = new GreedyScheduler(numProcessors);
                break;
            case SIMPLE:
                scheduler = new SimpleScheduler();
                break;
        }

        return scheduler;
    }

}

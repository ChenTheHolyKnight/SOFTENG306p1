package op.algorithm;

import op.algorithm.bound.BottomLevelFunction;
import op.algorithm.bound.CostFunction;
import op.algorithm.bound.EmptyCostFunction;
import op.algorithm.bound.IdleTimeFunction;
import op.algorithm.prune.PrunerManager;
import op.model.Schedule;

import java.util.ArrayList;
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
                                     int numCores, List<CostFunction.Implementation> costFuncs,
                                     List<PrunerManager.Pruners> pruners) {
    	List<CostFunction> costFuncConcrete = new ArrayList<>();
        PrunerManager prunerManager = new PrunerManager();
        
        // build the PrunerManager for the scheduler to use, based on the provided Enum values
        if (!pruners.isEmpty()) {
            for (PrunerManager.Pruners pruner : pruners) {
                switch (pruner) {
                    case EQUIVALENT_SCHEDULE:
                        prunerManager.addEquivalentSchedulePruner();
                    	break;
                    case IDLE_TIME:
                    	prunerManager.addIdleTimePruner();
                        break;
                    case NODE_EQUIVALENCE:
                    	prunerManager.addNodeEquivalencePruner();
                }
            }
        }
        
        
        // build the list of cost functions for the scheduler to use, based on the provided Enum values
        if (!costFuncs.isEmpty()) {
            for (CostFunction.Implementation cf : costFuncs) {
                switch (cf) {
                    case IDLE_TIME:
                        costFuncConcrete.add(new IdleTimeFunction(numProcessors));
                        break;
                    case BOTTOM_LEVEL:
                        costFuncConcrete.add(new BottomLevelFunction());
                }
            }
        }

        // construct the scheduler
        Scheduler scheduler = null;
        switch (a) {
            case PARA:
                scheduler = new DFSParaScheduler(numProcessors, prunerManager, costFuncConcrete, numCores);
                break;
            case DFS:
                scheduler = new DFSScheduler(numProcessors, prunerManager, costFuncConcrete);
                break;
            case ASTAR:
                //TODO
                throw new RuntimeException("AStar is not yet implemented");
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

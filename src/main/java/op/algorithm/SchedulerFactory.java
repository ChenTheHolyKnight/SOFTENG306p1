package op.algorithm;

import op.algorithm.bound.BottomLevelFunction;
import op.algorithm.bound.CostFunction;
import op.algorithm.bound.EmptyCostFunction;
import op.algorithm.bound.IdleTimeFunction;
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
                                     int numCores, List<CostFunction.Implementation> costFuncs) {

        //TODO add list of pruners

        // build the list of cost functions for the scheduler to use, based on the provided Enum values
        List<CostFunction> costFuncConcrete = new ArrayList<>();
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
            case DFS:
                scheduler = new DFSScheduler(numProcessors, new EmptyPruner(), costFuncConcrete);
                break;
            case ASTAR:
                scheduler = new AStarScheduler(numProcessors, new EmptyPruner(), costFuncConcrete);
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

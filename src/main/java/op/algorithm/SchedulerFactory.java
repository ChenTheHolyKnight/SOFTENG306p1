package op.algorithm;

import op.algorithm.bound.EmptyCostFunction;
import op.model.Schedule;

/**
 * Class to manage the creation of schedulers.
 * @author Darcy Cox
 */
public class SchedulerFactory {

    /**
     * Builds a scheduler using the specified type of
     * @param a The algorithm implementation that the scheduler must use
     * @param numProcessors The number of
     * @return The created scheduler
     */
    public Scheduler createScheduler(Algorithm a, int numProcessors, int numCores) {
        //TODO add list of pruners and list of cost funcs so we can build a complex scheduler easily
        Scheduler scheduler = null;
        switch (a) {
            case DFS:
                scheduler = new DFSScheduler(numProcessors, new EmptyPruner(), new EmptyCostFunction());
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

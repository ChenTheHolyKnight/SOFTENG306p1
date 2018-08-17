package op.algorithm;

import op.algorithm.bound.CostFunction;
import op.algorithm.bound.CostFunctionManager;
import op.algorithm.prune.PrunerManager;
import op.model.Schedule;

import java.util.*;

/**
 * Scheduler implementation that uses an A* branch and bound approach with a Pruner implementation to
 * decide when to bound branches, ie. when branches are not worth exploring.
 * @author Victoria Skeggs
 */
public class AStarScheduler extends BranchAndBoundScheduler {

    /**
     * Creates a new A* scheduler
     * @param numProcessors number of processors to schedule the algorithm on
     * @param pm pruner manager
     * @param cm list of cost function implementations to use
     */
    public AStarScheduler(int numProcessors, PrunerManager pm, CostFunctionManager cm) {
        super(numProcessors, pm, cm);
    }


    /**
     * Produces an optimal schedule using an A* approach
     * @return an optimal schedule
     */
    @Override
    public Schedule produceSchedule() {

        // Add empty schedule the first node to be explored
        Queue<Schedule> open = new PriorityQueue<>(11,
                new ScheduleComparator(getCostFunctionManager()));
        open.add(new Schedule());
        PrunerManager pm = getPrunerManager();

        // Start the search
        while (!open.isEmpty()) {
            Schedule currentSchedule = open.remove();
            if (currentSchedule.isComplete()) {

                // If schedule is complete then schedule is optimal
                System.out.println("Optimal length: " + currentSchedule.getLength());
                return currentSchedule;

            } else {
                for (Schedule s: pm.execute(getChildrenOfSchedule(currentSchedule))){
                    open.add(s);
                }
            }
        }

        // Impossible to reach
        return null;
    }
}

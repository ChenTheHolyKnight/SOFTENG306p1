package op.algorithm;

import op.algorithm.bound.CostFunction;
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
     * @param p pruner implementation
     * @param cf list of cost function implementations to use
     */
    public AStarScheduler(int numProcessors, Pruner p, List<CostFunction> cf) {
        super(numProcessors, p, cf);
    }


    /**
     * Produces an optimal schedule using an A* approach
     * @return an optimal schedule
     */
    @Override
    public Schedule produceSchedule() {

        // Add empty schedule the first node to be explored
        Queue<Schedule> open = new PriorityQueue<>(11,
                new ScheduleComparator(getCostFunctions()));
        open.add(new Schedule());

        // Start the search
        while (!open.isEmpty()) {
            Schedule currentSchedule = open.remove();
            if (currentSchedule.isComplete()) {

                // If schedule is complete then schedule is optimal
                System.out.println("Optimal length: " + currentSchedule.getLength());
                return currentSchedule;

            } else {
                for (Schedule s: getChildrenOfSchedule(currentSchedule)){
                    // TODO: Only add promising schedules to the queue
                    open.add(s);
                }
            }
        }

        // Impossible to reach
        return null;
    }
}

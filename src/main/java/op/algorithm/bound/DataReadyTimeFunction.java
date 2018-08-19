package op.algorithm.bound;

import op.algorithm.SchedulerUtil;
import op.model.Dependency;
import op.model.Schedule;
import op.model.Task;
import op.model.TaskGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculates a cost function using the data ready time heuristic. This implementation considers all free tasks in the
 * schedule and calculates the earliest time that they can be scheduled on any processor. This "data ready time" of
 * each free task is then added to that tasks bottom level.
 * @author Darcy
 */
public class DataReadyTimeFunction implements CostFunction {

    private int numProcessors;

    public DataReadyTimeFunction(int numProcessors) {
        this.numProcessors = numProcessors;
    }

    @Override
    public int calculate(Schedule s) {

        List<Task> freeTasks = SchedulerUtil.getFreeTasks(TaskGraph.getInstance(), s);

        int costFunc = 0;
        for (Task t : freeTasks) {
            int earliestStart = Integer.MAX_VALUE;
            for (int i = 1; i <= numProcessors; i++) {
                int currentStart = SchedulerUtil.getEarliestStartTime(s, t, i);
                if (currentStart < earliestStart) {
                    earliestStart = currentStart;
                }
            }

            int drtCostFunc = earliestStart + t.getBottomLevel();
            if (drtCostFunc > costFunc) {
                // we want the tightest lower bound
                costFunc = drtCostFunc;
            }
        }
        return costFunc;
    }
}

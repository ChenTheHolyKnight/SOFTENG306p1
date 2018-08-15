package op.algorithm;

import op.algorithm.bound.CostFunction;
import op.model.Schedule;

import java.util.Comparator;
import java.util.List;

/**
 * Orders schedules. A schedule is ordered before another schedule if the tightest bound of its cost functions is a
 * smaller value.
 *
 * @author Victoria Skeggs
 */
public class ScheduleComparator implements Comparator<Schedule> {

    private List<CostFunction> costFunctions;

    public ScheduleComparator(List<CostFunction> costFunctions) {
        this.costFunctions = costFunctions;
    }

    /**
     * Orders two schedules by deciding which schedule has the tightest bound of its cost functions.
     * @param s1 first schedule
     * @param s2 second schedule
     * @return negative integer if the first schedule has a lower tightest bound; positive integer if the first schedule
     * has a higher tightest bound; 0 if the schedules have the same tightest bound
     */
    @Override
    public int compare(Schedule s1, Schedule s2) {
        return SchedulerUtil.getTightestBound(s1, costFunctions) - SchedulerUtil.getTightestBound(s2, costFunctions);
    }
}

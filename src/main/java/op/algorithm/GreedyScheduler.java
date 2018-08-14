package op.algorithm;

import op.model.*;

import java.util.HashMap;
import java.util.List;

/**
 * Class that produces a good, but not necessarily optimal, solution using a greedy approach.
 *
 * @author Victoria Skeggs
 */
public class GreedyScheduler extends Scheduler {

    private int FIRST_PROCESSOR = 1;
    

    public GreedyScheduler(int numProcessors, boolean toVisualize) {
    	super(numProcessors, toVisualize);
    }

    /**
     * Produces a schedule by ordering the tasks and scheduling them individually by making the best local choice.
     * @return a decent schedule
     */
    @Override
    public Schedule produceSchedule() {
        Schedule schedule = new Schedule();
        int numProcessors = super.getNumProcessors();
        for (Task task: SchedulerUtil.createTopologicalOrder(TaskGraph.getInstance().getAllTasks())) {

            int bestProcessor = FIRST_PROCESSOR;
            int earliestStartTime = Integer.MAX_VALUE;

            for (int processor = FIRST_PROCESSOR; processor <= numProcessors; processor++) {
                int newEarliest = super.getEarliestStartTime(schedule, task, processor);
                if (newEarliest < earliestStartTime) {
                    earliestStartTime = newEarliest;
                    bestProcessor = processor;
                }
            }
            ScheduledTask scheduledTask = new ScheduledTask(task, earliestStartTime, bestProcessor);
            schedule.addScheduledTask(scheduledTask);
        }
        return schedule;
    }

}

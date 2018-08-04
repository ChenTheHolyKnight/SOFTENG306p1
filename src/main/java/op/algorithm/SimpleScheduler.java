package op.algorithm;

import op.model.Schedule;
import op.model.ScheduledTask;
import op.model.Task;
import op.model.TaskGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class containing logic to produce the simplest valid schedule. Used as a fall-back option.
 *
 * @author Victoria Skeggs
 */
public class SimpleScheduler extends Scheduler {

    /**
     * The processor to schedule each task on
     */
    private static final int DEFAULT_PROCESSOR = 1;
    private int startTime;

    public SimpleScheduler() {
        startTime = 0;
    }

    /**
     * Produces a schedule by scheduling every task on the same processor.
     * @return a basic schedule
     */
    @Override
    public Schedule produceSchedule() {
        Schedule schedule = new Schedule();
        for (Task task: SchedulerUtil.createTopologicalOrder(TaskGraph.getInstance().getAllTasks())) {
            ScheduledTask scheduledTask = new ScheduledTask(task, startTime, DEFAULT_PROCESSOR);
            schedule.addScheduledTask(scheduledTask);
            startTime = startTime + task.getDuration();
        }

        return schedule;
    }

}

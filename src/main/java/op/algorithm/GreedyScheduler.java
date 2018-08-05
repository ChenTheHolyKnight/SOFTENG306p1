package op.algorithm;

import op.io.Arguments;
import op.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that produces a good, but not necessarily optimal, solution using a greedy approach.
 *
 * @author Victoria Skeggs
 */
public class GreedyScheduler extends Scheduler {

    private HashMap<Integer, Double> processorNextTime;
    private int FIRST_PROCESSOR = 1;

    public GreedyScheduler() {
        int numProcessors = Arguments.getInstance().getNumProcessors();
        processorNextTime = new HashMap<>();

        for (int processor = FIRST_PROCESSOR; processor <= numProcessors; processor++) {
            processorNextTime.put(processor, 0.0);
        }
    }

    /**
     * Produces a schedule by ordering the tasks and scheduling them individually by making the best local choice.
     * @return a decent schedule
     */
    @Override
    public Schedule produceSchedule() {
        Schedule schedule = new Schedule();
        for (Task task: SchedulerUtil.createTopologicalOrder(TaskGraph.getInstance().getAllTasks())) {

            int bestProcessor = FIRST_PROCESSOR;
            double earliestStartTime = Double.POSITIVE_INFINITY;

            for (int processor = FIRST_PROCESSOR; processor < Arguments.getInstance().getNumProcessors(); processor++) {
                double newEarliest = getEarliestStartTime(schedule, task, processor);
                if (newEarliest < earliestStartTime) {
                    earliestStartTime = newEarliest;
                    bestProcessor = processor;
                }
            }
            ScheduledTask scheduledTask = new ScheduledTask(task, earliestStartTime, bestProcessor);
            schedule.addScheduledTask(scheduledTask);
            processorNextTime.put(bestProcessor, earliestStartTime + task.getDuration());
        }
        return schedule;
    }

    /**
     * Finds the earliest start time a particular task can be scheduled on a particular processor.
     * @param task
     * @param processor
     * @return
     */
    private double getEarliestStartTime(Schedule schedule, Task task, int processor) {
        List<Dependency> incomingEdges = TaskGraph.getInstance().getIncomingDependencies(task);

        // Estimate the earliest start time of the task as the next available time on the processor
        double startTime = processorNextTime.get(processor);

        for (Dependency incomingEdge: incomingEdges) {
            Task startTask = incomingEdge.getStartTask();

            // If a dependent task is not scheduled on the same processor, the start time of this task
            // cannot be earlier than the end time of the dependent task plus the communication cost
            if (schedule.getScheduledTask(startTask).getProcessor() != processor) {
                double newStartTime = schedule.getScheduledTask(startTask).getStartTime() + startTask.getDuration()
                        + incomingEdge.getWeight();

                if (newStartTime > startTime) {
                    startTime = newStartTime;
                }
            }
        }
        return startTime;
    }
}

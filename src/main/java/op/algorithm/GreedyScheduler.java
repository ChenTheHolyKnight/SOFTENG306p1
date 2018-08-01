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
public class GreedyScheduler extends SimpleScheduler {

    private HashMap<Integer, Double> processorNextTime;
    private int FIRST_PROCESSOR = 1;

    private GreedyScheduler() {
        int numProcessors = Arguments.getInstance().getNumProcessors();
        processorNextTime = new HashMap<>();

        for (int processor = FIRST_PROCESSOR; processor <= numProcessors; processor++) {
            processorNextTime.put(processor, 0.0);
        }
    }

    /**
     * Produces a schedule by ordering the tasks and scheduling them individually by making the best local choice.
     * @param numProcessors the number of processors available to schedule tasks onto
     * @return a decent schedule
     */
    @Override
    public Schedule produceSchedule(int numProcessors) {
        Schedule schedule = new Schedule();

        for (Task task: createTopologicalOrder(TaskGraph.getInstance().getAllTasks())) {

            int bestProcessor = FIRST_PROCESSOR;
            double earliestStartTime = Double.POSITIVE_INFINITY;

            for (int processor = FIRST_PROCESSOR; processor < numProcessors; processor++) {
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
        double startTime = processorNextTime.get(processor);
        for (Dependency incomingEdge: incomingEdges) {
            Task startTask = incomingEdge.getStartTask();
            if (schedule.getScheduledTask(startTask).getProcessor() != processor) {
                double newStartTime = schedule.getScheduledTask(startTask).getStartTime();
                if (newStartTime > startTime) {
                    startTime = newStartTime;
                }
            }
        }
        return startTime;
    }
}

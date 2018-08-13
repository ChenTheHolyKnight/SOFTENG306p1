package op.algorithm.bound;

import op.model.Schedule;
import op.model.ScheduledTask;
import op.model.Task;
import op.model.TaskGraph;

/**
 * Idle time implementation of a cost function.
 * Uses the formula: f(s) = (sum(task weights) + total idle time) /  num of processors
 * @author Darcy Cox
 */
public class IdleTimeFunction implements CostFunction {

    private int numProcessors;
    private int taskWeightSum;

    /**
     * Creates a new IdleTimeFunction instance. This cost function implementation needs to know
     * the number of processors that tasks are scheduled on.
     * @param numProcessors The number of processors that tasks are scheduled on
     */
    public IdleTimeFunction(int numProcessors) {
        this.numProcessors = numProcessors;

        // calculate sum of task weights at construction time so it is not re-calculated at every call of calculate()
        this.taskWeightSum = 0;
        for (Task t: TaskGraph.getInstance().getAllTasks()) {
            this.taskWeightSum += t.getDuration();
        }
    }

    @Override
    public int calculate(Schedule s) {
        int idleTime = 0; // stores the idle time across all processors
        for (int i = 1; i <= numProcessors; i++) {
            int expectedStartTime = 0; // time that next task should start if no idle time

            // sum up idle time of each processor that has scheduled tasks on it
            if (s.getScheduledTasksOfProcessor(i) != null) {
                for (ScheduledTask st : s.getScheduledTasksOfProcessor(i)) {
                    // add to idleTime whenever an empty slot is detected between tasks
                    int actualStartTime = st.getStartTime();
                    idleTime += actualStartTime - expectedStartTime;

                    // the next task can start exactly when the current task finishes
                    expectedStartTime = st.getStartTime() + st.getTask().getDuration();
                }
            }

        }

        return (taskWeightSum + idleTime) / numProcessors;
    }
}

package op.algorithm.bound;

import op.model.Schedule;
import op.model.ScheduledTask;

/**
 * Implementation of cost function that uses the bottom level property of tasks in a task graph.
 * Calculates the maximum over all scheduled tasks of the tasks start time + its bottom level.
 * By taking the maximum of these values, we are calculating the minimal time that any schedule extending
 * from the given schedule will take.
 * @author Darcy Cox
 */
public class BottomLevelFunction implements CostFunction {

    @Override
    public int calculate(Schedule s) {
        int costFunc = 0;
        for (ScheduledTask st : s.getAllScheduledTasks()) {
            int minimalTimeToExit = st.getStartTime() + st.getTask().getBottomLevel();
            if (minimalTimeToExit > costFunc) {
                costFunc =  minimalTimeToExit;
            }
        }
        return costFunc;
    }
}

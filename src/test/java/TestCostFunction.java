import op.algorithm.bound.IdleTimeFunction;
import op.model.Schedule;
import op.model.ScheduledTask;
import op.model.Task;
import op.model.TaskGraph;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test class for checking that cost functions are performing as expected
 */
public class TestCostFunction {

    @BeforeClass
    public static void initTaskGraph() {
        List<Task> tasks = new ArrayList<Task>();
        Collections.addAll(tasks,
                new Task("1", 2),
                new Task("2", 3),
                new Task("3", 5),
                new Task("4", 1)
        );
        TaskGraph.initialize(tasks, null);
    }

    @AfterClass
    public static void resetTaskGraph() {
        try {
            TestSingletonUtil.resetTaskGraph();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIdleTimeCostFunctionOneProcessor() {
        IdleTimeFunction func = new IdleTimeFunction(1);

        Schedule s = new Schedule();
        s.addScheduledTask(new ScheduledTask(new Task("1", 2), 0, 1));
        s.addScheduledTask(new ScheduledTask(new Task("2", 3), 2, 1));

        // at this point, there is no idle time on the processor. so cost function should be
        // ((2 + 3 + 5 + 1) + 0) / 1 = 11
        assertEquals(11, func.calculate(s));

        s.addScheduledTask(new ScheduledTask(new Task("3", 5), 10, 1)); // + 5 idle time
        s.addScheduledTask(new ScheduledTask(new Task("4", 1), 15, 1)); // + 0 idle time

        // idle time is 5 so func = ((2 + 3 + 5 + 1) + 5) / 1 = 16
        assertEquals(16, func.calculate(s));
    }

    @Test
    public void testIdleTimeCostFunctionTwoProcessors() {
        IdleTimeFunction func = new IdleTimeFunction(2);

        Schedule s = new Schedule();
        s.addScheduledTask(new ScheduledTask(new Task("1", 2), 0, 1));
        s.addScheduledTask(new ScheduledTask(new Task("2", 3), 0, 2));

        // at this point, there is no idle time on the processor. so cost function should be
        // ((2 + 3 + 5 + 1) + 0) / 2 = 5
        assertEquals(5, func.calculate(s));

        s.addScheduledTask(new ScheduledTask(new Task("3", 5), 7, 1)); // + 5 idle time
        s.addScheduledTask(new ScheduledTask(new Task("4", 1), 6, 2)); // + 3 idle time

        // idle time is 8 so func = ((2 + 3 + 5 + 1) + 8) / 2 = 9
        assertEquals(9, func.calculate(s));
    }
}

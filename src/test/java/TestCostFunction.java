import op.algorithm.bound.BottomLevelFunction;
import op.algorithm.bound.DataReadyTimeFunction;
import op.algorithm.bound.IdleTimeFunction;
import op.io.DotIO;
import op.model.Schedule;
import op.model.ScheduledTask;
import op.model.Task;
import op.model.TaskGraph;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test class for checking that cost functions are performing as expected
 * @author Darcy Cox
 */
public class TestCostFunction {

    private static final String BASE_DIR = "src/main/resources/sample_inputs/";
    private static final String SAMPLE_7 = BASE_DIR + "Nodes_7_OutTree.dot";

    @After
    public void resetTaskGraph() {
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
        initSimpleTaskGraph();
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
        initSimpleTaskGraph();
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

    @Test
    public void testBottomLevelCostFunction() {

        initNodes7Graph();
        List<Task> tasks = TaskGraph.getInstance().getAllTasks();

        BottomLevelFunction blf = new BottomLevelFunction();

        // test empty schedule
        Schedule s = new Schedule();
        assertEquals(0, blf.calculate(s));

        Task t = tasks.get(tasks.indexOf(new Task("0", 5)));
        s.addScheduledTask(new ScheduledTask(t, 0, 1)); // bl = 18
        t = tasks.get(tasks.indexOf(new Task("1", 6)));
        s.addScheduledTask(new ScheduledTask(t, 5, 1)); // bl = 13
        t = tasks.get(tasks.indexOf(new Task("2", 5)));
        s.addScheduledTask(new ScheduledTask(t, 11, 1)); // bl = 5
        // should be the maximum of start time + bottom level of scheduled tasks
        assertEquals(18, blf.calculate(s));
    }

    @Test
    public void testDataReadyTimeCostFunction() {
        initNodes7Graph();

        List<Task> tasks = TaskGraph.getInstance().getAllTasks();

        DataReadyTimeFunction drt = new DataReadyTimeFunction(2); // 2 processors

        // test empty schedule
        Schedule s = new Schedule();
        // for this input graph, and the empty schedule, the free task will be task "0"
        // its data ready time is 0 and its bottom level is 18
        assertEquals(18, drt.calculate(s));

        Task t = tasks.get(tasks.indexOf(new Task("0", 5)));
        s.addScheduledTask(new ScheduledTask(t, 0, 1));
        t = tasks.get(tasks.indexOf(new Task("1", 6)));
        s.addScheduledTask(new ScheduledTask(t, 5, 1));
        t = tasks.get(tasks.indexOf(new Task("2", 5)));
        s.addScheduledTask(new ScheduledTask(t, 11, 1));

        // test case with lots of free tasks. In this case, tasks 3, 4, 5, and 6 are free
        // their DRTs + bottom levels are:
        // 3: 16 + 6 = 22
        // 4: 16 + 4 = 20
        // 5: 15 + 7 = 22
        // 6: 16 + 7 = 23 (tightest bound, should be the result)
        assertEquals(23, drt.calculate(s));
    }


    private static void initSimpleTaskGraph() {
        List<Task> tasks = new ArrayList<Task>();
        Collections.addAll(tasks,
                new Task("1", 2),
                new Task("2", 3),
                new Task("3", 5),
                new Task("4", 1)
        );
        TaskGraph.initialize(tasks, null);
    }

    private static void initNodes7Graph() {
        try {
            new DotIO().dotIn(SAMPLE_7);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

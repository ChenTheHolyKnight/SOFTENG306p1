import op.algorithm.SimpleScheduler;
import op.io.DotIO;
import op.model.Dependency;
import op.model.Task;
import op.model.TaskGraph;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class checking that the dot file IO module correctly initializes the TaskGraph singleton.
 * @author Darcy Cox
 */
public class TestDotIO {

    // constants to hold the paths to each dot file
    private static final String BASE_DIR = "src/main/resources/sample_inputs/";
    private static final String SAMPLE_7 = BASE_DIR + "Nodes_7_OutTree.dot";
    private static final String SAMPLE_8 = BASE_DIR + "Nodes_8_Random.dot";
    private static final String SAMPLE_9 = BASE_DIR + "Nodes_9_SeriesParallel.dot";
    private static final String SAMPLE_10 = BASE_DIR + "Nodes_10_Random.dot";
    private static final String BASIC = BASE_DIR + "test.dot";

    private static DotIO dotIO;

    @BeforeClass
    public static void initIO() {
        dotIO = new DotIO();
    }

    // task graph can only be initialized once so we must reset it after each test
    @After
    public void resetTaskGraph() {
        try {
            SingletonTesting.resetTaskGraph();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBasic() throws IOException {
        List<Task> tasksExpected = new ArrayList<Task>();
        Collections.addAll(
                tasksExpected,
                new Task("1", 2),
                new Task("2", 3),
                new Task("3", 3),
                new Task("4", 2)
        );

        dotIO.dotIn(BASIC);
        List<Task> tasksActual = TaskGraph.getInstance().getAllTasks();
        checkTaskListsAreEquivalent(tasksExpected, tasksActual);
    }


    @Test
    public void testSample7() throws IOException{

        //TODO: check title is the same, tasks, and dependencies
        // expected
        List<Task> tasksExpected = new ArrayList<Task>();
        Collections.addAll(
                tasksExpected,
                new Task("0", 5),
                new Task("1", 6),
                new Task("2", 5),
                new Task("3", 6),
                new Task("4", 4),
                new Task("5", 7),
                new Task("6", 7)
        );

        dotIO.dotIn(SAMPLE_7);
        TaskGraph tg = TaskGraph.getInstance();
        List<Task> tasksActual = tg.getAllTasks();
        checkTaskListsAreEquivalent(tasksExpected, tasksActual);
    }

    @Test
    public void testSample8() throws IOException {
        List<Task> tasksExpected = new ArrayList<Task>();
        Collections.addAll(
                tasksExpected,
                new Task("0", 35),
                new Task("1", 88),
                new Task("2", 176),
                new Task("3", 159),
                new Task("4", 176),
                new Task("6", 141),
                new Task("7", 53),
                new Task("5", 141)
        );

        dotIO.dotIn(SAMPLE_8);
        TaskGraph tg = TaskGraph.getInstance();
        List<Task> tasksActual = tg.getAllTasks();
        checkTaskListsAreEquivalent(tasksExpected, tasksActual);
    }


    private static void checkTaskListsAreEquivalent(List<Task> expected, List<Task> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            // check that all expected tasks are in the actual task list, and vice versa
            Task currentExpected = expected.get(i);
            Task currentActual = actual.get(i);
            assertTrue(expected.contains(currentActual));
            assertTrue(actual.contains(currentExpected));

            // find the task in the actual tasks that matches the current expected task to check
            // and ensure the weight is as expected
            for (Task a: actual) {
                if (currentExpected.getId().equals(a.getId())) {
                    assertEquals(currentExpected.getDuration(), a.getDuration());
                    break;
                }
            }
        }
    }

    private static void checkDepListsAreEquivalent(List<Dependency> expected, List<Dependency> actual){
        assertEquals(expected.size(),actual.size());
        for (int i = 0; i < expected.size(); i++){
            Dependency expectedDep = expected.get(i);
            Dependency actualDep = actual.get(i);
            assertTrue(expected.contains(actualDep));
            assertTrue(actual.contains(expectedDep));
        }
    }


}

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
 * @author Sam Broadhead
 */
public class TestDotIO {

    // constants to hold the paths to each dot file
    private static final String BASE_DIR = "src/main/resources/sample_inputs/";
    private static final String SAMPLE_7 = BASE_DIR + "Nodes_7_OutTree.dot";
    private static final String SAMPLE_8 = BASE_DIR + "Nodes_8_Random.dot";
    private static final String SAMPLE_9 = BASE_DIR + "Nodes_9_SeriesParallel.dot";
    private static final String SAMPLE_10 = BASE_DIR + "Nodes_10_Random.dot"; 
    private static final String SAMPLE_11 = BASE_DIR + "Nodes_11_OutTree.dot";
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
            TestSingletonUtil.resetTaskGraph();
        } catch (NoSuchFieldException | IllegalAccessException e) {
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

        List<Dependency> depsExpected = new ArrayList<Dependency>();
        Collections.addAll(
                depsExpected,
                new Dependency(tasksExpected.get(0),tasksExpected.get(1),1),
                new Dependency(tasksExpected.get(0),tasksExpected.get(2),2),
                new Dependency(tasksExpected.get(1),tasksExpected.get(3),2),
                new Dependency(tasksExpected.get(2),tasksExpected.get(3),1)
        );

        dotIO.dotIn(BASIC);
        TaskGraph tg = TaskGraph.getInstance();
        assertEquals("example",tg.getTitle());
        List<Task> tasksActual = tg.getAllTasks();
        //List<Dependency> depsActual = tg.getAllDependencies();
        checkTaskListsAreEquivalent(tasksExpected, tasksActual);
        //checkDepListsAreEquivalent(depsExpected, depsActual);
    }


    @Test
    public void testSample7() throws IOException{
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

        List<Dependency> depsExpected = new ArrayList<Dependency>();
        Collections.addAll(
                depsExpected,
                new Dependency(tasksExpected.get(0),tasksExpected.get(1),15),
                new Dependency(tasksExpected.get(0),tasksExpected.get(2),11),
                new Dependency(tasksExpected.get(0),tasksExpected.get(3),11),
                new Dependency(tasksExpected.get(1),tasksExpected.get(4),19),
                new Dependency(tasksExpected.get(1),tasksExpected.get(5),4),
                new Dependency(tasksExpected.get(1),tasksExpected.get(6),21)
        );

        dotIO.dotIn(SAMPLE_7);
        TaskGraph tg = TaskGraph.getInstance();
        assertEquals("OutTree-Balanced-MaxBf-3_Nodes_7_CCR_2.0_WeightType_Random",tg.getTitle());
        List<Task> tasksActual = tg.getAllTasks();
        //List<Dependency> depsActual = tg.getAllDependencies();
        checkTaskListsAreEquivalent(tasksExpected, tasksActual);
        //checkDepListsAreEquivalent(depsExpected, depsActual);
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
                new Task("5", 141),
                new Task("6", 141),
                new Task("7", 53)
        );

        List<Dependency> depsExpected = new ArrayList<Dependency>();
        Collections.addAll(
                depsExpected,
                new Dependency(tasksExpected.get(0),tasksExpected.get(1),3),
                new Dependency(tasksExpected.get(0),tasksExpected.get(2),9),
                new Dependency(tasksExpected.get(0),tasksExpected.get(3),7),
                new Dependency(tasksExpected.get(0),tasksExpected.get(4),5),
                new Dependency(tasksExpected.get(0),tasksExpected.get(6),4),
                new Dependency(tasksExpected.get(0),tasksExpected.get(7),9),
                new Dependency(tasksExpected.get(1),tasksExpected.get(4),10),
                new Dependency(tasksExpected.get(1),tasksExpected.get(7),6),
                new Dependency(tasksExpected.get(2),tasksExpected.get(4),8),
                new Dependency(tasksExpected.get(2),tasksExpected.get(5),6),
                new Dependency(tasksExpected.get(2),tasksExpected.get(7),3),
                new Dependency(tasksExpected.get(3),tasksExpected.get(5),5),
                new Dependency(tasksExpected.get(3),tasksExpected.get(6),8),
                new Dependency(tasksExpected.get(4),tasksExpected.get(6),2),
                new Dependency(tasksExpected.get(5),tasksExpected.get(7),4),
                new Dependency(tasksExpected.get(6),tasksExpected.get(7),8)
        );

        dotIO.dotIn(SAMPLE_8);
        TaskGraph tg = TaskGraph.getInstance();
        assertEquals("Random_Nodes_8_Density_2.0_CCR_0.1_WeightType_Random",tg.getTitle());
        List<Task> tasksActual = tg.getAllTasks();
        //List<Dependency> depsActual = tg.getAllDependencies();
        checkTaskListsAreEquivalent(tasksExpected, tasksActual);
        //checkDepListsAreEquivalent(depsExpected, depsActual);
    }
    @Test
    public void testSample9() throws IOException {
        List<Task> tasksExpected = new ArrayList<Task>();
        Collections.addAll(
                tasksExpected,
                new Task("0", 10),
                new Task("1", 7),
                new Task("2", 6),
                new Task("3", 7),
                new Task("4", 5),
                new Task("5", 9),
                new Task("6", 2),
                new Task("7", 2),
                new Task("8", 7)
        );

        List<Dependency> depsExpected = new ArrayList<Dependency>();
        Collections.addAll(
                depsExpected,
                new Dependency(tasksExpected.get(0),tasksExpected.get(2),51),
                new Dependency(tasksExpected.get(0),tasksExpected.get(3),22),
                new Dependency(tasksExpected.get(0),tasksExpected.get(4),44),
                new Dependency(tasksExpected.get(2),tasksExpected.get(6),59),
                new Dependency(tasksExpected.get(2),tasksExpected.get(7),15),
                new Dependency(tasksExpected.get(2),tasksExpected.get(8),59),
                new Dependency(tasksExpected.get(3),tasksExpected.get(1),59),
                new Dependency(tasksExpected.get(4),tasksExpected.get(1),66),
                new Dependency(tasksExpected.get(5),tasksExpected.get(1),37),
                new Dependency(tasksExpected.get(6),tasksExpected.get(5),22),
                new Dependency(tasksExpected.get(7),tasksExpected.get(5),59),
                new Dependency(tasksExpected.get(8),tasksExpected.get(5),59)
        );

        dotIO.dotIn(SAMPLE_9);
        TaskGraph tg = TaskGraph.getInstance();
        assertEquals("SeriesParallel-MaxBf-3_Nodes_9_CCR_10.0_WeightType_Random",tg.getTitle());
        List<Task> tasksActual = tg.getAllTasks();
        //List<Dependency> depsActual = tg.getAllDependencies();
        checkTaskListsAreEquivalent(tasksExpected, tasksActual);
        //checkDepListsAreEquivalent(depsExpected, depsActual);
    }
    @Test
    public void testSample10() throws IOException {
        List<Task> tasksExpected = new ArrayList<Task>();
        Collections.addAll(
                tasksExpected,
                new Task("0", 6),
                new Task("1", 5),
                new Task("2", 5),
                new Task("3", 10),
                new Task("4", 3),
                new Task("5", 7),
                new Task("6", 8),
                new Task("7", 3),
                new Task("8", 8),
                new Task("9", 8)
        );

        List<Dependency> depsExpected = new ArrayList<Dependency>();
        Collections.addAll(
                depsExpected,
                new Dependency(tasksExpected.get(0),tasksExpected.get(3),34),
                new Dependency(tasksExpected.get(0),tasksExpected.get(4),24),
                new Dependency(tasksExpected.get(0),tasksExpected.get(9),44),
                new Dependency(tasksExpected.get(1),tasksExpected.get(2),48),
                new Dependency(tasksExpected.get(1),tasksExpected.get(5),19),
                new Dependency(tasksExpected.get(1),tasksExpected.get(6),39),
                new Dependency(tasksExpected.get(2),tasksExpected.get(3),10),
                new Dependency(tasksExpected.get(2),tasksExpected.get(7),48),
                new Dependency(tasksExpected.get(2),tasksExpected.get(8),48),
                new Dependency(tasksExpected.get(4),tasksExpected.get(6),10),
                new Dependency(tasksExpected.get(4),tasksExpected.get(7),48),
                new Dependency(tasksExpected.get(4),tasksExpected.get(8),48),
                new Dependency(tasksExpected.get(4),tasksExpected.get(9),39),
                new Dependency(tasksExpected.get(6),tasksExpected.get(7),15),
                new Dependency(tasksExpected.get(6),tasksExpected.get(8),39),
                new Dependency(tasksExpected.get(6),tasksExpected.get(9),29),
                new Dependency(tasksExpected.get(7),tasksExpected.get(8),15),
                new Dependency(tasksExpected.get(7),tasksExpected.get(9),34),
                new Dependency(tasksExpected.get(8),tasksExpected.get(9),39)
        );

        dotIO.dotIn(SAMPLE_10);
        TaskGraph tg = TaskGraph.getInstance();
        assertEquals("Random_Nodes_10_Density_1.90_CCR_10.00_WeightType_Random",tg.getTitle());
        List<Task> tasksActual = tg.getAllTasks();
        //List<Dependency> depsActual = tg.getAllDependencies();
        checkTaskListsAreEquivalent(tasksExpected, tasksActual);
        //checkDepListsAreEquivalent(depsExpected, depsActual);
    }
    @Test
    public void testSample11() throws IOException {
        List<Task> tasksExpected = new ArrayList<Task>();
        Collections.addAll(
                tasksExpected,
                new Task("0", 50),
                new Task("1", 70),
                new Task("2", 90),
                new Task("3", 100),
                new Task("4", 40),
                new Task("5", 20),
                new Task("6", 100),
                new Task("7", 80),
                new Task("8", 50),
                new Task("9", 20),
                new Task("10", 20)
        );

        List<Dependency> depsExpected = new ArrayList<Dependency>();
        Collections.addAll(
                depsExpected,
                new Dependency(tasksExpected.get(0),tasksExpected.get(1),9),
                new Dependency(tasksExpected.get(0),tasksExpected.get(2),7),
                new Dependency(tasksExpected.get(0),tasksExpected.get(3),4),
                new Dependency(tasksExpected.get(1),tasksExpected.get(4),10),
                new Dependency(tasksExpected.get(1),tasksExpected.get(5),7),
                new Dependency(tasksExpected.get(1),tasksExpected.get(6),5),
                new Dependency(tasksExpected.get(2),tasksExpected.get(7),5),
                new Dependency(tasksExpected.get(2),tasksExpected.get(8),3),
                new Dependency(tasksExpected.get(2),tasksExpected.get(9),10),
                new Dependency(tasksExpected.get(3),tasksExpected.get(10),4)
        );

        dotIO.dotIn(SAMPLE_11);
        TaskGraph tg = TaskGraph.getInstance();
        assertEquals("OutTree-Balanced-MaxBf-3_Nodes_11_CCR_0.1_WeightType_Random",tg.getTitle());
        List<Task> tasksActual = tg.getAllTasks();
        //List<Dependency> depsActual = tg.getAllDependencies();
        checkTaskListsAreEquivalent(tasksExpected, tasksActual);
        //checkDepListsAreEquivalent(depsExpected, depsActual);
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

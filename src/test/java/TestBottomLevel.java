import op.io.DotIO;
import op.model.Dependency;
import op.model.Task;
import op.model.TaskGraph;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestBottomLevel {
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
        Map<String, Integer> bottomLevelMap = new HashMap<>();
        bottomLevelMap.put("1",7);
        bottomLevelMap.put("2",5);
        bottomLevelMap.put("3",5);
        bottomLevelMap.put("4",2);
        dotIO.dotIn(BASIC);
        TaskGraph tg = TaskGraph.getInstance();
        List<Task> tasks = tg.getAllTasks();
        for (Task t : tasks){
            assertTrue(t.getBottomLevel() == bottomLevelMap.get(t.getId()));
        }
    }
    @Test
    public void testSampleSeven() throws IOException {
        Map<String, Integer> bottomLevelMap = new HashMap<>();
        bottomLevelMap.put("0",18);
        bottomLevelMap.put("1",13);
        bottomLevelMap.put("2",5);
        bottomLevelMap.put("3",6);
        bottomLevelMap.put("4",4);
        bottomLevelMap.put("5",7);
        bottomLevelMap.put("6",7);
        dotIO.dotIn(SAMPLE_7);
        TaskGraph tg = TaskGraph.getInstance();
        List<Task> tasks = tg.getAllTasks();
        for (Task t : tasks){
            assertTrue(t.getBottomLevel() == bottomLevelMap.get(t.getId()));
        }
    }
}

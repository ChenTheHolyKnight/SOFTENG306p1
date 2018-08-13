import op.model.*;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * Test class to ensure the TaskGraph singleton works as expected
 * @author Darcy Cox
 */
public class TestTaskGraphSingleton {

    @After
    public void resetSingleton() {
        try {
            TestSingletonUtil.resetTaskGraph();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testExceptions() {
        try {
            TaskGraph.getInstance();
            fail();
        } catch(UninitializedException e) {}

        try {
            List<Task> tasks = new ArrayList<>();
            List<Dependency> deps = new ArrayList<>();

            TaskGraph.initialize(tasks, null);
            TaskGraph.getInstance();
            TaskGraph.initialize(null, null);
            fail();
        } catch (AlreadyInitializedException e) {}
    }

    @Test
    public void testListsAreUnmodifiable() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("1", 1));
        tasks.add(new Task("1", 1));
        tasks.add(new Task("1", 1));

        List<Dependency> deps = new ArrayList<>();
        deps.add(new Dependency(new Task("1",1),new Task("2",2), 1));
        deps.add(new Dependency(new Task("1",1),new Task("2",2), 1));

        TaskGraph.initialize(tasks, null);
        List<Task> immutableTasks = TaskGraph.getInstance().getAllTasks();

        try {
            immutableTasks.add(new Task("2",3));
            fail();
        } catch (UnsupportedOperationException e) {

        }

        try {
            immutableTasks.remove(0);
            fail();
        } catch (UnsupportedOperationException e) {

        }
    }
}

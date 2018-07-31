import op.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestScheduleClass {
    Schedule schedule=new Schedule();

    @Before
    /**
     * Initialize the graph
     */
    public void init(){
        //initialize the tasks
        Task task1=new Task(1,1);
        Task task2=new Task(2,1);
        List<Task> tasks=new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);

        //initialize the dependencies
        Dependency dependency=new Dependency(task1,task2,1);
        List<Dependency> dependencies=new ArrayList<>();
        dependencies.add(dependency);

        //Create the mock graph
        MockTaskGraph mockTaskGraph=new MockTaskGraph();
        mockTaskGraph.setDependencies(dependencies);
        mockTaskGraph.setTasks(tasks);
    }

    @Test
    public void testIsComplete(){
        //schedule.addTask();
    }

}

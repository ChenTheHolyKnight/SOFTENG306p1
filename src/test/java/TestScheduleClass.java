import op.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestScheduleClass {
    Schedule schedule;
    Task task1;
    Task task2;
    List<Task> tasks;
    List<ScheduledTask> scheduledTasks;
    List<ScheduledTask> scheduledTasks1;
    MockTaskGraph mockTaskGraph;

    @Before
    /**
     * Initialize the graph
     */
    public void init(){
        this.schedule=new Schedule();

        //initialize the tasks
        this.task1=new Task(1,1);
        this.task2=new Task(2,1);
        this.tasks=new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);

        //initialize the scheduled tasks;
        this.scheduledTasks=new ArrayList<>();
        this.scheduledTasks1=new ArrayList<>();


        //initialize the dependencies
        Dependency dependency=new Dependency(task1,task2,1);
        List<Dependency> dependencies=new ArrayList<>();
        dependencies.add(dependency);

        //Create the mock graph
        this.mockTaskGraph=new MockTaskGraph();
        this.mockTaskGraph.setDependencies(dependencies);
        this.mockTaskGraph.setTasks(tasks);
    }

    @Test
    public void testCompleteScheduled(){
        //set up the scheduled tasks in two different lists
        ScheduledTask scheduledTask=new ScheduledTask(task1,1,1);
        ScheduledTask scheduledTask1=new ScheduledTask(task2,2,2);
        this.scheduledTasks.add(scheduledTask);
        this.scheduledTasks1.add(scheduledTask1);

        schedule.addProcessor(1,scheduledTasks);
        schedule.addProcessor(2,scheduledTasks1);

        boolean isCompleted=schedule.isComplete(mockTaskGraph);
        assertTrue(isCompleted);
    }

}

import op.algorithm.SimpleScheduler;
import op.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a class to test simple scheduler class
 */
public class TestSimpleScheduler {
    @Before
    public void setup(){
        //set up nodes
        Task task1=new Task(1,1);
        Task task2=new Task(2,1);
        Task task3=new Task(3,1);
        Task task4=new Task(4,1);
        Task task5=new Task(5,1);

        List<Task> tasks=new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);
        tasks.add(task5);


        //set up dependencies
        Dependency dependency1=new Dependency(task1,task3,1);
        Dependency dependency2=new Dependency(task1,task2,1);
        Dependency dependency3=new Dependency(task1,task4,1);
        Dependency dependency4=new Dependency(task2,task3,1);
        Dependency dependency5=new Dependency(task3,task5,1);

        List<Dependency> dependencies=new ArrayList<>();
        dependencies.add(dependency1);
        dependencies.add(dependency2);
        dependencies.add(dependency3);
        dependencies.add(dependency4);
        dependencies.add(dependency5);

        //initialise the Graph
        TaskGraph.initialize(tasks,dependencies,"SimpleGraph");
    }


    @After
    public void clean(){
        try {
            SingletonTesting.resetTaskGraph();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testSimpleGraph(){
        SimpleScheduler ss=new SimpleScheduler();
        Schedule schedule=ss.produceSchedule(1);
        List<ScheduledTask>s=schedule.getScheduledTasks(1);
        s.forEach(scheduledTask -> System.out.println(scheduledTask.task.getId()));
        //System.out.println(s);
    }
}

import op.model.Dependency;
import op.model.Schedule;
import op.model.Task;
import op.model.TaskGraph;
import org.junit.Before;

public class TestScheduleClass {
    Schedule schedule=new Schedule();

    @Before
    public void init(){
        //initialize the tasks
        Task task1=new Task(1,1);
        Task task2=new Task(2,1);
        //initialize the dependencies
        Dependency dependency=new Dependency(task1,task2,1);
    }
}

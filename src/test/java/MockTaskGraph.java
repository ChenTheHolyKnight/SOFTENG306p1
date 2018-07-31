

import op.model.Dependency;
import op.model.Task;
import op.model.TaskGraph;

import java.util.List;

/**
 * This is a mocking class for setting values to the mocked graph. This class is used for testing only
 */
public class MockTaskGraph extends TaskGraph{
    private List<Task> tasks;
    private List<Dependency> dependencies;
    private String title;


    public MockTaskGraph(){
        super(null,null,null);
    }
    /**
     * set the lists in the mock task graph class
     */
    public void setTasks(List<Task> tasks){
        this.tasks=tasks;
    }

    /**
     * set the dependency list in the mock task graph
     */
    public void setDependencies(List<Dependency> dependencies){
        this.dependencies=dependencies;
    }

    /**
     * set the title of the mock graph
     */
    public void setTitle(){
        this.setTitle();
    }

    /**
     * get all the tasks in the mock graph
     */
    public List<Task> getAllTasks() {
        return tasks;
    }
}

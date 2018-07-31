

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

    public void setTasks(List<Task> tasks){
        this.tasks=tasks;
    }

    public void setDependencies(List<Dependency> dependencies){
        this.dependencies=dependencies;
    }

    public List<Task> getAllTasks() {
        return tasks;
    }
}

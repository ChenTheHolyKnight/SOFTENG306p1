package op.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class representing information about the task graph and providing global access
 * @author Darcy Cox
 */
public class TaskGraph {

    // becomes true once TaskGraph has been initialized and becomes accessible
    private static boolean initialized = false;

    private static TaskGraph instance;

    private final List<Task> TASKS;
    private final List<Dependency> DEPENDENCIES;
    private final String TITLE;

    /**
     * Returns the TaskGraph global instance
     * @throws UninitializedException if TaskGraph has not been initialized
     * @return The global TaskGraph instance
     */
    public static TaskGraph getInstance() throws UninitializedException {

        if (!initialized) {
            throw new UninitializedException();
        }

        return instance;
    }

    /**
     * Creates the TaskGraph instance
     * @param tasks A list of all tasks in the task graph
     * @param dependencies A list of all dependencies in the task graph
     * @param title The name of the task graph
     * @throws AlreadyInitializedException if TaskGraph has already been initialized
     */
    public static void initialize(
            List<Task> tasks,
            List<Dependency> dependencies,
            String title
    ) throws AlreadyInitializedException {

        if (initialized) {
            throw new AlreadyInitializedException();
        }

        initialized = true;

        // constructs the instance with lists that cannot be added to or removed from etc. for safety
        instance = new TaskGraph(
                Collections.unmodifiableList(tasks),
                Collections.unmodifiableList(dependencies),
                title
        );
    }


    /**
     * Gets all incoming Dependencies to a specified Task.
     * @param n the Task to get the incoming Dependencies for.
     * @return the list of Dependencies incoming to that Task.
     */
    public List<Dependency> getIncomingDependencies (Task n){
        List<Dependency> incomingDependencies = new ArrayList<Dependency>();
        for (Dependency dep : DEPENDENCIES) {
            if (dep.getEndTask().equals(n)){
                incomingDependencies.add(dep);
            }
        }
        return incomingDependencies;
    }

    /**
     * Gets all outgoing Dependencys from a specified Task
     * @param n the Task to get the outgoing Dependencys for.
     * @return the list of Dependencys outgoing from that Task.
     */
    public List<Dependency> getOutgoingDependencies(Task n) {
        List<Dependency> outgoingDependencies = new ArrayList<Dependency>();
        for (Dependency dep : DEPENDENCIES) {
            if (dep.getStartTask().equals(n)){
                outgoingDependencies.add(dep);
            }
        }
        return outgoingDependencies;
    }

    /**
     * Gets the Task in the graph with the specified ID. if there is no
     * Task with that ID, this method returns null.
     * @param id The ID of the required Task.
     * @return The Task with the specified ID.
     */
    public Task getTaskById(String id) {
        for (Task task : TASKS) {
            if (task.getId().equals(id)) {
                return task;
            }
        }
        return null;
    }

    /**
	 * Gets all the tasks in the graph
	 * @return The all the tasks in the graph.
	 */
	public List<Task> getAllTasks() {
		return TASKS;
	}

    /**
     * Gets all the dependencies in the graph
     * @return
     */
    public List<Dependency> getAllDependencies(){
        return DEPENDENCIES;
    }

    /**
     * Gets the title of the TaskGraph instance
     * @return title
     */
	public String getTitle() {
	    return TITLE;
    }
	
	// the private constructor for this singleton
    private TaskGraph (List<Task> tasks, List<Dependency> dependencies, String title){
        this.TASKS = Collections.unmodifiableList(tasks);
        this.DEPENDENCIES = Collections.unmodifiableList(dependencies);
        this.TITLE = title;
    }
}
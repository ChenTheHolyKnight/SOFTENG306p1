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
    //private final List<Dependency> DEPENDENCIES;
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
     * //@param dependencies A list of all dependencies in the task graph
     * @param title The name of the task graph
     * @throws AlreadyInitializedException if TaskGraph has already been initialized
     */
    public static void initialize(
            List<Task> tasks,
            String title
    ) throws AlreadyInitializedException {

        if (initialized) {
            throw new AlreadyInitializedException();
        }

        initialized = true;

        // constructs the instance with lists that cannot be added to or removed from etc. for safety
        instance = new TaskGraph(
                Collections.unmodifiableList(tasks),
                title
        );
    }

    /**
	 * Gets all the tasks in the graph
	 * @return The all the tasks in the graph.
	 */
	public List<Task> getAllTasks() {
		return TASKS;
	}

    /**
     * Gets the title of the TaskGraph instance
     * @return title
     */
	public String getTitle() {
	    return TITLE;
    }
	
	// the private constructor for this singleton
    private TaskGraph (List<Task> tasks, String title){
        this.TASKS = Collections.unmodifiableList(tasks);
        this.TITLE = title;
    }
}
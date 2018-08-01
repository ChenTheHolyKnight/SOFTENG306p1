package op.model;

/**
 * Exception to be thrown if TaskGraph is initialized more than once
 * @author Darcy Cox
 */
public class TaskGraphAlreadyInitializedException extends RuntimeException {

    /**
     * Constructs a new TaskGraphAlreadyInitializedException with the default message
     */
    public TaskGraphAlreadyInitializedException() {
        super("TaskGraph cannot be initialized more than once");
    }
}

package op.model;

/**
 * Exception to be thrown if access to TaskGraph is attempted before it being initialized
 * @author Darcy Cox
 */
public class TaskGraphUninitializedException extends RuntimeException {

    /**
     * Creates a new TaskGraphUninitializedException with the default message.
     */
    public TaskGraphUninitializedException() {
        super("TaskGraph must be initialized before access");
    }

}

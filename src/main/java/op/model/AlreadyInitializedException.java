package op.model;

/**
 * Exception to be thrown if a singleton is initialized more than once
 * @author Darcy Cox
 */
public class AlreadyInitializedException extends RuntimeException {

    /**
     * Constructs a new AlreadyInitializedException with the default message
     */
    public AlreadyInitializedException() {
        super("TaskGraph cannot be initialized more than once");
    }
}

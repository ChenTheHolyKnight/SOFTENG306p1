package op.model;

/**
 * Exception to be thrown if access to a singleton is attempted before it being initialized
 * @author Darcy Cox
 */
public class UninitializedException extends RuntimeException {

    /**
     * Creates a new UninitializedException with the default message.
     */
    public UninitializedException() {
        super("TaskGraph must be initialized before access");
    }

}

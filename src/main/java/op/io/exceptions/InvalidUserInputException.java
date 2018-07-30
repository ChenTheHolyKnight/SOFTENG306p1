package op.io.exceptions;

/**
 * InvalidUserInputException should be thrown when a user tries to run the program with invalid input.
 *
 * @author Victoria Skeggs
 */
public class InvalidUserInputException extends Exception {

    public InvalidUserInputException(String message) {
        super(message);
    }
}

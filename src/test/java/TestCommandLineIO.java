import controller.CommandLineIO;
import model.Command;
import model.InvalidUserInputException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests that the CommandLineIO class behaves as expected.
 *
 * @author Victoria Skeggs
 */
public class TestCommandLineIO {

    /**
     * Tests that CommandLineIO returns a Command object with correct field values when given a valid user input with
     * all options defined.
     */
    @Test
    public void testValidInputWithoutDefaults() {
        CommandLineIO commandLineIO = new CommandLineIO();

        // Define user input
        String[] args = new String[7];
        args[0] = "input.dot";
        args[1] = "10";
        args[2] = "-p";
        args[3] = "2";
        args[4] = "-v";
        args[5] = "-o";
        args[6] = "output.dot";

        try {
            Command command = commandLineIO.parseArgs(args.clone());
            assertCommandIsCorrect(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[3]),
                    true, args[6], command);
        } catch (InvalidUserInputException e) {
            Assert.fail();
        }
    }

    /**
     * Tests that CommandLineIO returns a correct Command object when given a valid user input with only mandatory
     * options defined.
     */
    @Test
    public void testValidInputWithDefaults() {
        CommandLineIO commandLineIO = new CommandLineIO();

        // Define user input
        String[] args = new String[2];
        args[0] = "input.dot";
        args[1] = "10";

        try {
            Command command = commandLineIO.parseArgs(args.clone());
            assertCommandIsCorrect(args[0], Integer.parseInt(args[1]), 1, false,
                    "INPUT-output.dot", command);
        } catch (InvalidUserInputException e) {
            Assert.fail();
        }
    }

    /**
     * Tests that CommandLineIO returns a correct Command object when options are input in a new order.
     */
    @Test
    public void testValidInputNewOrder() {
        CommandLineIO commandLineIO = new CommandLineIO();

        // Define user input
        String[] args = new String[7];
        args[0] = "input.dot";
        args[1] = "10";
        args[2] = "-v";
        args[3] = "-o";
        args[4] = "output.dot";
        args[5] = "-p";
        args[6] = "90";

        try {
            Command command = commandLineIO.parseArgs(args.clone());
            assertCommandIsCorrect(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[6]),
                    true, args[4], command);
        } catch (InvalidUserInputException e) {
            Assert.fail();
        }
    }

    private void assertCommandIsCorrect(String expectedInputFilename, int expectedNumProcessors, int expectedNumCores,
                         boolean expectedToVisualise, String expectedOutputFilename, Command command) {

        Assert.assertEquals("Command object returns incorrect input filename", expectedInputFilename,
                command.getInputFilename());
        Assert.assertEquals("Command object returns incorrect number of processors",
                expectedNumProcessors, command.getNumProcessors());
        Assert.assertEquals("Command object returns incorrect number of cores",
                expectedNumCores, command.getNumCores());
        Assert.assertEquals("Command object does not return that visualisation has been turned on",
                expectedToVisualise, command.getToVisualize());
        Assert.assertEquals("Command object returns incorrect output filename", expectedOutputFilename,
                command.getOutputFilename());
    }

    /**
     * Tests that CommandLineIO throws an InvalidUserInputException when a mandatory argument is missing.
     */
    @Test
    public void testMissingMandatoryArgument() {
        CommandLineIO commandLineIO = new CommandLineIO();

        // Define user input
        String[] args = new String[3];
        args[0] = "90";
        args[1] = "-p";
        args[2] = "90";

        try {
            commandLineIO.parseArgs(args.clone());
            Assert.fail();
        } catch (InvalidUserInputException e) {

        }
    }

    /**
     * Tests that CommandLineIO throws an InvalidUserInputException when the number of processors entered is not a
     * number.
     */
    @Test
    public void testNumProcessorsNotANumber() {
        CommandLineIO commandLineIO = new CommandLineIO();

        // Define user input
        String[] args = new String[2];
        args[0] = "Input.dot";
        args[1] = "FreddieMercury";

        try {
            commandLineIO.parseArgs(args.clone());
            Assert.fail();
        } catch (InvalidUserInputException e) {

        }
    }

    /**
     * Tests that CommandLineIO throws an InvalidUserInputException when the number of cores entered is not a
     * number.
     */
    @Test
    public void testNumCoresNotANumber() {
        CommandLineIO commandLineIO = new CommandLineIO();

        // Define user input
        String[] args = new String[4];
        args[0] = "Input.dot";
        args[1] = "900";
        args[2] = "-p";
        args[3] = "BrianMay";

        try {
            commandLineIO.parseArgs(args.clone());
            Assert.fail();
        } catch (InvalidUserInputException e) {

        }
    }

    /**
     * Tests that CommandLineIO throws an InvalidUserInputException when the user enters too many arguments.
     */
    @Test
    public void testTooManyArguments() {
        CommandLineIO commandLineIO = new CommandLineIO();

        // Define user input
        String[] args = new String[5];
        args[0] = "Input.dot";
        args[1] = "900";
        args[2] = "-p";
        args[3] = "6";
        args[4] = "-x";

        try {
            commandLineIO.parseArgs(args.clone());
            Assert.fail();
        } catch (InvalidUserInputException e) {

        }
    }
}

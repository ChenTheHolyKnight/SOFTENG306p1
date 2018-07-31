package op;

import op.io.Arguments;
import op.io.CommandLineIO;
import op.io.exceptions.InvalidUserInputException;

/**
 * Entry point for the optimal scheduling program
 */
public class Application {
    public static void main(String[] args) {
        Application application = new Application();
        Arguments arguments = application.getArguments(args);

        // To test jar from command line:
        System.out.println("Here are the arguments you entered:");
        System.out.println("Input graph filename: " + arguments.getInputGraphFilename());
        System.out.println("Number of cores: " + arguments.getNumCores());
        System.out.println("Number of processors: " + arguments.getNumProcessors());
        System.out.println("Output graph filename: " + arguments.getOutputGraphFilename());
        System.out.println("Visualization on: " + arguments.getToVisualize());

        //   DotIO iotest = new DotIO("src/main/resources/sample_inputs/test.dot");
    }

    public static int returnTwo() {
        return 2;
    }

    /**
     * Retrieve the command line arguments in an easy to read format
     * @param args command line arguments
     * @return an Arguments object representing the information contained in the command line arguments
     */
    private Arguments getArguments(String[] args) {
        Arguments arguments = null;
        try {
            arguments = (new CommandLineIO()).parseArgs(args);
        } catch (InvalidUserInputException e) {
            System.exit(1);
        }
        return arguments;
    }
}
package op;


import op.visualization.Visualiser;
import op.visualization.messages.MessageAddNode;
import op.visualization.messages.MessageEliminateChildren;

import op.io.Arguments;
import op.io.CommandLineIO;
import op.io.DotIO;
import op.io.exceptions.InvalidUserInputException;
import op.model.TaskGraph;

import java.io.IOException;

/**
 * Entry point for the optimal scheduling program
 */
public class Application {
    public static void main(String[] args) {

        System.out.println("it works!");

        //   DotIO iotest = new DotIO("src/main/resources/sample_inputs/test.dot");

        Application application = new Application();
        application.initArguments(args);

        Arguments arguments = Arguments.getInstance();

        // To test jar from command line:
        System.out.println("Here are the arguments you entered:");
        System.out.println("Input graph filename: " + arguments.getInputGraphFilename());
        System.out.println("Number of cores: " + arguments.getNumCores());
        System.out.println("Number of processors: " + arguments.getNumProcessors());
        System.out.println("Output graph filename: " + arguments.getOutputGraphFilename());
        System.out.println("Visualization on: " + arguments.getToVisualize());

        DotIO iotest = new DotIO();
        try {
            iotest.dotIn(arguments.getInputGraphFilename());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static int returnTwo() {
        return 2;
    }

    /**
     * Initialize the global Arguments object
     * @param args command line arguments
     */
    private void initArguments(String[] args) {
        Arguments arguments = null;
        try {
            new CommandLineIO().parseArgs(args);
        } catch (InvalidUserInputException e) {
            System.exit(1);
        }
    }
}

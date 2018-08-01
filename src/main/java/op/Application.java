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
        Visualiser v = new Visualiser();
        v.update(new MessageAddNode("a", "b"));
        v.update(new MessageAddNode("a", "c"));
        v.update(new MessageAddNode("c", "d"));
        v.update(new MessageAddNode("d", "e"));
        v.update(new MessageAddNode("d", "f"));
        v.update(new MessageAddNode("c", "6"));
        v.update(new MessageAddNode("c", "7"));
        v.update(new MessageAddNode("c", "8"));
        v.update(new MessageAddNode("6", "9"));
        v.update(new MessageAddNode("6", "10"));
        
        v.update(new MessageAddNode("1", "a"));
        v.update(new MessageAddNode("1", "2"));
        v.update(new MessageAddNode("2", "3"));
        v.update(new MessageAddNode("2", "4"));
        v.update(new MessageAddNode("2", "5"));
        v.update(new MessageEliminateChildren("c"));

        //   DotIO iotest = new DotIO("src/main/resources/sample_inputs/test.dot");

        Application application = new Application();
        Arguments arguments = application.getArguments(args);

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

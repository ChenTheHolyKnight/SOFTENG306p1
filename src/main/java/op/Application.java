package op;

import op.algorithm.*;
import op.algorithm.bound.CostFunction;
import op.algorithm.bound.CostFunctionManager;
import op.algorithm.prune.PrunerManager;
import op.io.InvalidUserInputException;
import op.model.Schedule;
import op.io.CommandLineIO;
import op.io.DotIO;
import op.model.Arguments;
import op.visualization.Visualizer;
import op.visualization.controller.GUIController;
import scala.App;

import java.io.IOException;

/**
 * Entry point for the optimal scheduling program
 */
public class Application {
	private Arguments arguments;
	private Scheduler scheduler;
	private long startTime;
	private Visualizer visualizer;
	
    public static void main(String[] args) {
    	
        Application application = new Application();

        // Read from command line
        Arguments arg=application.initArguments(args);

        // Read dot file
        DotIO dotParser = new DotIO();
        application.readDot(dotParser);

        // Create scheduler
        //application.createScheduler();

        // Start visualization if -v is found in the arguments
        if(arg.getToVisualize())
            application.startVisualization(args,application);
        else {
            // Produce a schedule - create a new thread to do this.
            Schedule schedule = application.produceSchedule();

            // Write out the schedule
            application.writeDot(dotParser, schedule);
        }
    }

    /**
     * Initializes the global Arguments object, which provides an easy way for all parts of the program to access
     * the user's command line arguments
     * To be run as an IO_Task with ParallelIT
     * @param args command line arguments
     */
    private Arguments initArguments(String[] args) {
        try {
            arguments = new CommandLineIO().parseArgs(args);
            return arguments;
        } catch (InvalidUserInputException e) {
            fatalError(e.getMessage());
        }
        return null;
    }

    /**
     * Reads in the DOT file the user has specified
     * To be run as an IO_Task with ParallelIT
     * @param dotParser
     */
    private void readDot(DotIO dotParser) {

        try {
            dotParser.dotIn(arguments.getInputGraphFilename());
        } catch (IOException e) {
            fatalError("Could not find file: " + arguments.getInputGraphFilename());
        }
    }

    /**
     * Produces a scheduler to schedule tasks on
     */
    private Schedule produceSchedule() {
        SchedulerFactory sf = new SchedulerFactory();
        Scheduler s = sf.createScheduler(
                arguments.getAlgorithm(),
                arguments.getNumProcessors(),
                arguments.getNumCores(),
                arguments.getCostFunctions(),
                arguments.getPruners()
        );

        System.out.println("Starting " + arguments.getAlgorithm().getCmdRepresentation()
                            + " scheduler implementation...");
        System.out.println("Using cost functions: ");
        for (CostFunctionManager.Functions cf : arguments.getCostFunctions()) {
            System.out.println(cf);
        }
        System.out.println("Using pruners: ");
        for (PrunerManager.Pruners p : arguments.getPruners()) {
            System.out.println(p);
        }

        long startTime = System.currentTimeMillis();

        Schedule schedule = s.produceSchedule();

        System.out.println("Schedule length:\t" + schedule.getLength());
        System.out.println("Schedule calculated in:\t" + (System.currentTimeMillis()-startTime) + "ms");

        return schedule;
    }

    /**
     * Visualizes the search for a solution schedule
     * To be run concurrently with produceSchedule()
     */
    private void startVisualization(String[] args,Application application) {
        //if (arguments.getToVisualize()) {
            //new Thread(() -> {
                visualizer = new Visualizer();
                //scheduler.addListener(visualizer);
                visualizer.setCore(arguments.getNumCores());
                visualizer.setApplication(application);
                visualizer.startVisualization(args);
            //}).start();
        //}

    }

    /**
     * Writes out a schedule to DOT format
     * @param dotParser writes the schedule
     * @param schedule to be written
     */
    private void writeDot(DotIO dotParser, Schedule schedule){
        try {
            dotParser.dotOut(schedule, arguments.getOutputGraphFilename());
        } catch (IOException e) {
            fatalError("Could not write dot graph output.");
        }
    }

    private void fatalError(String message) {
        System.out.println(message);
        System.exit(1);
    }

}

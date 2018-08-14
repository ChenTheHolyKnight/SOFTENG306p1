package op;

import op.algorithm.*;
import op.algorithm.bound.BottomLevelFunction;
import op.algorithm.bound.CombinedCostFunction;
import op.algorithm.bound.EmptyCostFunction;
import op.algorithm.bound.IdleTimeFunction;
import op.io.InvalidUserInputException;
import op.model.Schedule;
import op.io.CommandLineIO;
import op.io.DotIO;
import op.model.Arguments;
import op.visualization.Visualizer;

import java.io.IOException;

/**
 * Entry point for the optimal scheduling program
 */
public class Application {
	private Arguments arguments;
	private Scheduler scheduler;
	
    public static void main(String[] args) {

        Application application = new Application();

        // Read from command line
        application.initArguments(args);

        // Read dot file
        DotIO dotParser = new DotIO();
        application.readDot(dotParser);

        // Produce a schedule
        Schedule schedule = application.produceSchedule(args);

        // Write out the schedule
        application.writeDot(dotParser, schedule);
    }

    /**
     * Initializes the global Arguments object, which provides an easy way for all parts of the program to access
     * the user's command line arguments
     * To be run as an IO_Task with ParallelIT
     * @param args command line arguments
     */
    private void initArguments(String[] args) {
        try {
            arguments = new CommandLineIO().parseArgs(args);
        } catch (InvalidUserInputException e) {
            fatalError(e.getMessage());
        }
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
     * Schedules tasks on processors
     * To be run concurrently with startVisualization()
     * @return a schedule
     */
    private Schedule produceSchedule(String[] args) {

        //scheduler = new DFSScheduler(arguments.getNumProcessors(), arguments.getToVisualize(), new EmptyPruner(),
        //        new IdleTimeFunction(arguments.getNumProcessors()));
        //scheduler = new DFSScheduler(arguments.getNumProcessors(), arguments.getToVisualize(), new EmptyPruner(),
        //        new BottomLevelFunction());
        //scheduler = new DFSScheduler(arguments.getNumProcessors(), arguments.getToVisualize(), new EmptyPruner(),
        //        new EmptyCostFunction());
        scheduler = new DFSScheduler(arguments.getNumProcessors(), arguments.getToVisualize(), new EmptyPruner(),
                new CombinedCostFunction(arguments.getNumProcessors()));

        long startTime = System.currentTimeMillis();

        // Start visualization
        startVisualization(args);

        Schedule schedule = scheduler.produceSchedule();

        System.out.println("Time without cost function: " + (System.currentTimeMillis() - startTime) +
                " ms       Schedule Length: " + schedule.getLength());
        //System.out.println("Time with bottom level cost function: " + (System.currentTimeMillis() - startTime) +
        //        " ms       Schedule Length: " + schedule.getLength());
        //System.out.println("Time with both cost functions: " + (System.currentTimeMillis() - startTime) +
        //        " ms       Schedule Length: " + schedule.getLength());
        //System.out.println("Time with idle time cost function: " + (System.currentTimeMillis() - startTime) +
        //        " ms       Schedule Length: " + schedule.getLength());

        return schedule;
    }

    /**
     * Visualizes the search for a solution schedule
     * To be run concurrently with produceSchedule()
     */
    private void startVisualization(String[] args) {
        if (arguments.getToVisualize()) {
            Visualizer visualizer = new Visualizer();
        	scheduler.addListener(visualizer);
        	visualizer.startVisualization(args);
        }
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

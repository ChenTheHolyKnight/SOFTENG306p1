package op;

import op.algorithm.*;
import op.io.InvalidUserInputException;
import op.model.Schedule;
import op.io.CommandLineIO;
import op.io.DotIO;
import op.model.Arguments;

import java.io.IOException;

/**
 * Entry point for the optimal scheduling program
 */
public class Application {
	private Arguments arguments;
	
    public static void main(String[] args) {
        Application application = new Application();

        // Read from command line
        application.initArguments(args);

        // Read dot file
        DotIO dotParser = new DotIO();
        application.readDot(dotParser);

        // Produce a schedule
        Schedule schedule = application.produceSchedule();

        // Start visualization
        application.startVisualization();

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
    private Schedule produceSchedule() {
      //  Scheduler fastScheduler = new DFSScheduler(arguments.getNumProcessors(), new IdleTimePruner());
        Scheduler slowScheduler = new DFSScheduler(arguments.getNumProcessors(), new EmptyPruner());
      //  long startTime = System.currentTimeMillis();
      //  Schedule fastSchedule = fastScheduler.produceSchedule();
      //  System.out.println("Time with pruning: "+(System.currentTimeMillis() - startTime)+"ms       Schedule Length: "+fastSchedule.getLength());
        long startTime = System.currentTimeMillis();
        Schedule slowSchedule = slowScheduler.produceSchedule();
        System.out.println("Time without pruning: "+(System.currentTimeMillis() - startTime)+"ms       Schedule Length: "+slowSchedule.getLength());
        return slowSchedule;
    }

    /**
     * Visualizes the search for a solution schedule
     * To be run concurrently with produceSchedule()
     */
    private void startVisualization() {
        if (arguments.getToVisualize()) {
        	//TODO: Do something here
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

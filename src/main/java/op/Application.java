package op;

import op.algorithm.*;
import op.algorithm.bound.BottomLevelFunction;
import op.algorithm.bound.CombinedCostFunction;
import op.algorithm.bound.EmptyCostFunction;
import op.algorithm.bound.IdleTimeFunction;
import op.io.InvalidUserInputException;
import op.model.Schedule;
import op.visualization.GUIApplication;
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
        application.startVisualization(args);

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
        //Scheduler idleTimeScheduler = new DFSScheduler(arguments.getNumProcessors(), new EmptyPruner(), new IdleTimeFunction(arguments.getNumProcessors()));
        //Scheduler bottomLevelScheduler = new DFSParaScheduler(arguments.getNumProcessors(), new EmptyPruner(), new BottomLevelFunction());
        //Scheduler slowScheduler = new DFSScheduler(arguments.getNumProcessors(), new EmptyPruner(), new EmptyCostFunction());
        long startTime = System.currentTimeMillis();
        //Schedule bottomLevelSchedule = bottomLevelScheduler.produceSchedule();
        //System.out.println("Time with bottom level cost function: "+(System.currentTimeMillis() - startTime)+"ms       Schedule Length: "+bottomLevelSchedule.getLength());
        Scheduler combinedScheduler = new DFSScheduler(arguments.getNumProcessors(), new EmptyPruner(), new CombinedCostFunction(arguments.getNumProcessors()));
        startTime = System.currentTimeMillis();
        Schedule combinedSchedule = combinedScheduler.produceSchedule();
        System.out.println("Time with both cost functions: "+(System.currentTimeMillis() - startTime)+"ms       Schedule Length: "+combinedSchedule.getLength());
        Scheduler combinedParaScheduler = new DFSParaScheduler(arguments.getNumProcessors(), new EmptyPruner(), new CombinedCostFunction(arguments.getNumProcessors()));
        startTime = System.currentTimeMillis();
        Schedule combinedParaSchedule = combinedParaScheduler.produceSchedule();
        System.out.println("Time with both cost function parallel: "+(System.currentTimeMillis() - startTime)+"ms       Schedule Length: "+combinedParaSchedule.getLength());
//        startTime = System.currentTimeMillis();
//        Schedule slowSchedule = slowScheduler.produceSchedule();
//        System.out.println("Time without cost function: "+(System.currentTimeMillis() - startTime)+"ms       Schedule Length: "+slowSchedule.getLength());
//        return slowSchedule;
        return combinedParaSchedule;
    }

    /**
     * Visualizes the search for a solution schedule
     * To be run concurrently with produceSchedule()
     */
    private void startVisualization(String[] args) {
        if (arguments.getToVisualize()) {
        	//TODO: Do something here
            javafx.application.Application.launch(GUIApplication.class,args);
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

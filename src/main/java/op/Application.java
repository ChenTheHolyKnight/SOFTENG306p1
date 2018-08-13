package op;

import op.algorithm.GreedyScheduler;
import op.algorithm.Scheduler;
import op.io.InvalidUserInputException;
import op.model.Schedule;
import op.visualization.GUIApplication;
import op.visualization.Visualiser;
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

        // FOR TESTING
        GUIApplication.main(args);

        //get the  start time of the program
        long startTime=System.currentTimeMillis();

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

        //get the end time of the program
        long endTime=System.currentTimeMillis();

        int scheduledLength=schedule.getLength();
        //print out the time
        long time=endTime-startTime;

        //print out in the command Line
        System.out.println("Time: "+time+"ms        Schedule Length: "+scheduledLength);


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
        Scheduler scheduler = new GreedyScheduler(arguments.getNumProcessors());
        return scheduler.produceSchedule();
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

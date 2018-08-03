package op;


import op.algorithm.GreedyScheduler;
import op.algorithm.Scheduler;
import op.model.Schedule;
import op.visualization.Visualiser;

import op.io.Arguments;
import op.io.CommandLineIO;
import op.io.DotIO;
import op.io.exceptions.InvalidUserInputException;

import java.io.IOException;

/**
 * Entry point for the optimal scheduling program
 */
public class Application {
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
            new CommandLineIO().parseArgs(args);
        } catch (InvalidUserInputException e) {
            System.exit(1);
        }
    }

    /**
     * Reads in the DOT file the user has specified
     * To be run as an IO_Task with ParallelIT
     * @param dotParser
     */
    private void readDot(DotIO dotParser) {

        try {
            dotParser.dotIn(Arguments.getInstance().getInputGraphFilename());
        } catch (IOException e) {
            System.out.println("Could not find file: " + Arguments.getInstance().getInputGraphFilename());
            System.exit(1);
        }
    }

    /**
     * Schedules tasks on processors
     * To be run concurrently with startVisualization()
     * @return a schedule
     */
    private Schedule produceSchedule() {
        Scheduler scheduler = new GreedyScheduler();
        return scheduler.produceSchedule();
    }

    /**
     * Visualizes the search for a solution schedule
     * To be run concurrently with produceSchedule()
     */
    private void startVisualization() {
        if (Arguments.getInstance().getToVisualize()) {
            Visualiser visualiser = new Visualiser();
        }
    }

    /**
     * Writes out a schedule to DOT format
     * @param dotParser writes the schedule
     * @param schedule to be written
     */
    private void writeDot(DotIO dotParser, Schedule schedule){
        dotParser.dotOut(schedule);
    }

}

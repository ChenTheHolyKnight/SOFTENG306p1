package op;

import op.algorithm.*;
import op.algorithm.bound.CostFunctionManager;
import op.algorithm.prune.PrunerManager;
import op.io.InvalidUserInputException;
import op.model.Schedule;
import op.io.CommandLineIO;
import op.io.DotIO;
import op.model.Arguments;
import op.visualization.Visualizer;
import op.visualization.controller.GUIController;

import java.io.IOException;
/**
 * Entry point for the optimal scheduling program
 */
public class Application {
	private Arguments arguments;
	private Scheduler scheduler;
	private Visualizer visualizer;
	private static DotIO dotParser;
	private static Application application;
	private static Arguments arg;


	private Application(){
	    application = this;
    }

    /**
     * @return the application singleton
     */
    public static Application getInstance(){

	    if (application == null) {
	        application = new Application();
        }
        return application;
    }

    public int getProcessNum(){
	    return arg.getNumProcessors();
    }

    public static void main(String[] args) {

        application = Application.getInstance();

        // Read from command line
        application=Application.getInstance();
        arg=application.initArguments(args);
        // Read dot file
        dotParser = new DotIO();
        application.readDot();

        // Create a scheduler
        application.createScheduler();

        if (arg.getToVisualize())

            // Start visualization if user has specified this
            application.startVisualization(args);

        else {

            // Otherwise produce a new schedule
            Schedule schedule = application.produceSchedule();

            // Write out the schedule
            application.writeDot(schedule);
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
     * Produces a scheduler based on user-specified command line arguments
     */
    private void createScheduler() {
        SchedulerFactory sf = new SchedulerFactory();
        scheduler = sf.createScheduler(
                arguments.getAlgorithm(),
                arguments.getNumProcessors(),
                arguments.getNumCores(),
                arguments.getCostFunctions(),
                arguments.getPruners()
        );
    }

    /**
     * Reads in the DOT file the user has specified
     */
    private void readDot() {

        try {
            dotParser.dotIn(arguments.getInputGraphFilename());
        } catch (IOException e) {
            fatalError("Could not find file: " + arguments.getInputGraphFilename());
        }
    }

    /**
     * Starts the algorithm running concurrently with the calling thread
     */
    public void startConcurrentAlgorithm() {
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
            private Schedule schedule;

            @Override
            protected Void call() {
                System.out.println("start");
                schedule = Application.getInstance().produceSchedule();
                System.out.println("in");
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                Application.getInstance().writeDot(schedule);
            }
        };
        new Thread(task).start();
    }

    /**
     * Produces a scheduler to schedule tasks on
     */
    private Schedule produceSchedule() {

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

        Schedule schedule = scheduler.produceSchedule();

        System.out.println("Schedule length:\t" + schedule.getLength());
        System.out.println("Schedule calculated in:\t" + (System.currentTimeMillis()-startTime) + "ms");

        return schedule;
    }

    /**
     * Lets scheduler listeners listen to events fired by a scheduler
     */
    public void addSchedulerListener(SchedulerListener listener) {
        scheduler.addListener(listener); //
    }

    /**
     * Starts the visualization
     */
    private void startVisualization(String[] args) {
        visualizer = new Visualizer();
        visualizer.startVisualization(args);
    }

    /**
     * Writes out a schedule to DOT format
     * @param schedule to be written
     */
    private void writeDot(Schedule schedule){
        try {
            dotParser.dotOut(schedule, arguments.getOutputGraphFilename());
        } catch (IOException e) {
            fatalError("Could not write dot graph output.");
        }
    }

    /**
     * Informs the user of a fatal error and stops the program
     * @param message
     */
    private void fatalError(String message) {
        System.out.println(message);
        System.exit(1);
    }

}
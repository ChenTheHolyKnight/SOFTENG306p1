package controller;

import model.Command;
import model.InvalidUserInputException;
import org.apache.commons.cli.*;

/**
 * This class parses user input to the console and writes requested messages to the console.
 *
 * @author Victoria Skeggs
 */
public class CommandLineIO {

    // User option flags
    private static final String NUM_CORES_FLAG = "p";
    private static final String TO_VISUALIZE_FLAG = "v";
    private static final String OUTPUT_FILENAME_FLAG = "o";

    // Default values for user options
    private static final int NUM_CORES_DEFAULT = 1;
    private static final String OUTPUT_FILENAME_DEFAULT = "INPUT-output.dot";

    // Order of command line arguments with no flags
    private static final short INPUT_FILENAME_POSITION = 0;
    private static final short NUM_PROCESSORS_POSITION = 1;

    // User option descriptions
    private static final String NUM_CORES_DESCRIPTION =
            "number of cores to execute program on (default is 1 core)";
    private static final String TO_VISUALIZE_DESCRIPTION = "visualise the search";
    private static final String OUTPUT_FILENAME_DESCRIPTION = "name of output file (default is INPUT-output.dot)";

    private static final String HELP_MESSAGE =
            "<INPUT GRAPH FILENAME> <NUMBER OF PROCESSORS> [OPTIONS]";

    private HelpFormatter formatter;
    private Options options;
    private CommandLineParser parser;

    public CommandLineIO() {
        options = new Options();
        parser = new DefaultParser();
        formatter = new HelpFormatter();
    }

    /**
     * Sets options for user and reads input into a Command object
     * @throws InvalidUserInputException if the user has entered invalid input
     */
    public Command parseArgs(String[] args) throws InvalidUserInputException {
        setOptions();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            printHelpAndThrowError();
        }
        return interpret(cmd);
    }

    /**
     * Sets the required options for the program:
     * 1. number of processor cores the program will run on
     * 2. whether to visualise the search
     * 3. what to name the output graph file
     */
    private void setOptions() {
        addOption(NUM_CORES_FLAG, true, NUM_CORES_DESCRIPTION, false, Integer.class);
        addOption(TO_VISUALIZE_FLAG, false, TO_VISUALIZE_DESCRIPTION, false);
        addOption(OUTPUT_FILENAME_FLAG, true, OUTPUT_FILENAME_DESCRIPTION, false, String.class);
    }

    /**
     * Adds an option to allow the user to control an aspect of how the program will behave
     *
     * @param flag the string the user inputs to use the option
     * @param hasArg specifies whether the option takes an argument
     * @param description describes the function of the option
     * @param isRequired specifies whether the option is mandatory
     * @param type the type of the expected user input
     */
    private void addOption(String flag, boolean hasArg, String description, boolean isRequired, Class type) {
        Option option = addOption(flag, hasArg, description, isRequired);
        option.setType(type);
    }

    private Option addOption(String flag, boolean hasArg, String description, boolean isRequired) {
        Option option = new Option(flag, hasArg, description);
        option.setRequired(isRequired);
        options.addOption(option);
        return option;
    }

    /**
     * Interprets user input from command line arguments
     * @param cmd represents the command line
     * @return a Command object, which represents the user's command
     * @throws InvalidUserInputException if the user has entered invalid input
     */
    private Command interpret(CommandLine cmd) throws InvalidUserInputException {

        checkCorrectNumArguments(cmd);

        int numCores = getNumCores(cmd);
        String outputFilename = getOutputFilename(cmd);
        boolean toVisualize = getToVisualize(cmd);
        String inputFilename = getInputFilename(cmd);
        int numProcessors = getNumProcessors(cmd);

        Command userCommand = new Command(inputFilename, numProcessors, numCores, toVisualize, outputFilename);
        return userCommand;
    }

    private int getNumCores(CommandLine cmd) throws InvalidUserInputException {
        String numCoresRaw = cmd.getOptionValue(NUM_CORES_FLAG);
        if (numCoresRaw == null) {
            return NUM_CORES_DEFAULT;
        }
        int numCores = NUM_CORES_DEFAULT;
        try {
            numCores = Integer.parseInt(numCoresRaw);
        } catch (NumberFormatException e) {
            printHelpAndThrowError();
        }
        return numCores;
    }

    private String getOutputFilename(CommandLine cmd) {
        String outputFilename = cmd.getOptionValue(OUTPUT_FILENAME_FLAG);
        if (outputFilename == null) {
            outputFilename = OUTPUT_FILENAME_DEFAULT;
        }
        return outputFilename;
    }

    private boolean getToVisualize(CommandLine cmd) {
        return cmd.hasOption(TO_VISUALIZE_FLAG);
    }

    private String getInputFilename(CommandLine cmd) {
        return cmd.getArgs()[INPUT_FILENAME_POSITION];
    }

    private int getNumProcessors(CommandLine cmd) throws InvalidUserInputException {

        String numProcessorsRaw = cmd.getArgs()[NUM_PROCESSORS_POSITION];

        int numProcessors = NUM_CORES_DEFAULT;
        try {
            numProcessors = Integer.parseInt(numProcessorsRaw);
        } catch (NumberFormatException e) {
            printHelpAndThrowError();
        }
        return numProcessors;
    }

    /**
     * Checks the user has entered the correct number of arguments
     * @param cmd represents the command line
     * @throws InvalidUserInputException if the user has entered the wrong number of arguments
     */
    private void checkCorrectNumArguments(CommandLine cmd) throws InvalidUserInputException {
        if (cmd.getArgs().length != NUM_PROCESSORS_POSITION + 1) {
            printHelpAndThrowError();
        }
    }

    /**
     * Designed to be called if the user inputs invalid arguments on the command line. Prints a message to the console
     * describing correct usage of the program then throws an InvalidUserInputException to notify the calling class
     * about the error.
     * @throws InvalidUserInputException tells the program the user has entered invalid input
     */
    private void printHelpAndThrowError() throws InvalidUserInputException {
        formatter.printHelp(HELP_MESSAGE, options);
        throw new InvalidUserInputException("Command line arguments missing, of wrong type or in wrong order.");
    }

}

package op.io;

import op.algorithm.Scheduler;
import op.algorithm.bound.CostFunction;
import op.algorithm.bound.CostFunctionManager;
import op.algorithm.prune.PrunerManager;

import org.apache.commons.cli.*;
import op.model.Arguments;

import java.util.ArrayList;
import java.util.List;


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
    private static final String ALGORITHM_FLAG = "a";
    private static final String COST_FUNCTION_FLAG = "f";
    private static final String PRUNER_FLAG = "P";

    // Default values for user options
    private static final int NUM_CORES_DEFAULT = 1;
    private static final String OUTPUT_FILENAME_APPENDER_DEFAULT = "-output.dot";
    private static final Scheduler.Implementation ALGORITHM_IMPLEMENTATION = Scheduler.Implementation.DFS;
    private static final List<CostFunctionManager.Functions> COST_FUNCTIONS = new ArrayList<>(); // no cost funcs is default

    // Order of command line arguments with no flags
    private static final short INPUT_FILENAME_POSITION = 0;
    private static final short NUM_PROCESSORS_POSITION = 1;

    // User option descriptions
    private static final String NUM_CORES_DESCRIPTION =
            "number of cores to execute program on (default is 1 core)";
    private static final String TO_VISUALIZE_DESCRIPTION = "visualise the search";
    private static final String OUTPUT_FILENAME_DESCRIPTION = "name of output file (default is INPUT-output.dot)";
    private static final String ALGORITHM_DESCRIPTION =
            "the algorithm implementation to use for scheduling:" +
                    System.lineSeparator() + "parallel | dfs | astar | greedy | simple";
    private static final String COST_FUNC_DESCRIPTION = "comma-separated list of cost functions to be used."
            + System.lineSeparator() + "Acceptable values: bl | it ";
    private static final String PRUNER_DESCRIPTION = "Pruner to be used."
            + System.lineSeparator() + "Acceptable values: es | ne";

    private static final String HELP_MESSAGE =
            "<INPUT GRAPH FILENAME> <NUMBER OF PROCESSORS> [OPTIONS]";

    private static final String FILENAME_EXTENSION_INDICATOR = ".";

    private HelpFormatter formatter;
    private Options options;
    private CommandLineParser parser;

    public CommandLineIO() {
        options = new Options();
        parser = new DefaultParser();
        formatter = new HelpFormatter();
    }

    /**
     * Sets options for user and reads input into the global Arguments object
     * @throws InvalidUserInputException if the user has entered invalid input
     * @return The arguments object containing all the command line arguments
     */
    public Arguments parseArgs(String[] args) throws InvalidUserInputException {
        setOptions();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            if (e.getMessage() == null || e.getMessage().equals("")) {
                printHelpAndThrowError("Could not parse command line arguments.");
            } else {
                printHelpAndThrowError(e.getMessage());
            }
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
        addOption(NUM_CORES_FLAG, true, NUM_CORES_DESCRIPTION, false);
        addOption(TO_VISUALIZE_FLAG, false, TO_VISUALIZE_DESCRIPTION, false);
        addOption(OUTPUT_FILENAME_FLAG, true, OUTPUT_FILENAME_DESCRIPTION, false);
        addOption(ALGORITHM_FLAG, true, ALGORITHM_DESCRIPTION, false);

        // the next options are more complex and must use the OptionsBuilder.
        // build and set cost functions option
        Option costFuncOption = Option.builder(COST_FUNCTION_FLAG)
                .hasArgs()
                .valueSeparator(',')
                .required(false)
                .desc(COST_FUNC_DESCRIPTION)
                .build();
        options.addOption(costFuncOption);

        // build and set the pruner option
        Option prunerOption = Option.builder(PRUNER_FLAG)
                .hasArgs()
                .valueSeparator(',')
                .required(false)
                .desc(PRUNER_DESCRIPTION)
                .build();
        options.addOption(prunerOption);
    }

    /**
     * Adds an option to allow the user to control an aspect of how the program will behave
     *
     * @param flag the string the user inputs to use the option
     * @param hasArg specifies whether the option takes an argument
     * @param description describes the function of the option
     * @param isRequired specifies whether the option is mandatory
     */
    private Option addOption(String flag, boolean hasArg, String description, boolean isRequired) {
        Option option = new Option(flag, hasArg, description);
        option.setRequired(isRequired);
        options.addOption(option);
        return option;
    }

    /**
     * Interprets user input from command line arguments and stores the input in the global arguments object
     * @param cmd represents the command line
     * @throws InvalidUserInputException if the user has entered invalid input
     */
    private Arguments interpret(CommandLine cmd) throws InvalidUserInputException {

        checkCorrectNumArguments(cmd);

        int numCores = getNumCores(cmd);
        boolean toVisualize = getToVisualize(cmd);
        String inputFilename = getInputFilename(cmd);
        String outputFilename = getOutputFilename(inputFilename, cmd);
        int numProcessors = getNumProcessors(cmd);
        Scheduler.Implementation algorithm = getAlgorithm(cmd);
        List<CostFunctionManager.Functions> costFunctions = getCostFunctions(cmd);
        List<PrunerManager.Pruners> pruners = getPruners(cmd);

        return new Arguments(inputFilename, numProcessors, numCores,
                toVisualize, outputFilename, algorithm, costFunctions, pruners);
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
            printHelpAndThrowError("Number of cores to run the program on must be a positive integer.");
        }
        if (numCores <= 0) {
            printHelpAndThrowError("Number of cores to run the program on cannot be 0 or negative.");
        }
        return numCores;
    }

    private String getOutputFilename(String inputFilename, CommandLine cmd) {
        String outputFilename = cmd.getOptionValue(OUTPUT_FILENAME_FLAG);
        if (outputFilename == null) {

            // Drop the filename extension from the input filename if one has been specified
            if (inputFilename.contains(FILENAME_EXTENSION_INDICATOR)) {
                outputFilename = inputFilename.substring(0, inputFilename.lastIndexOf(FILENAME_EXTENSION_INDICATOR))
                        + OUTPUT_FILENAME_APPENDER_DEFAULT;
            } else {
                outputFilename = inputFilename + OUTPUT_FILENAME_APPENDER_DEFAULT;
            }
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
            printHelpAndThrowError("Number of processors must be a positive integer.");
        }
        if (numProcessors <= 0) {
            printHelpAndThrowError("Number of processors cannot be 0 or negative.");
        }
        return numProcessors;
    }

    private Scheduler.Implementation getAlgorithm(CommandLine cmd) throws InvalidUserInputException {
        String alg = cmd.getOptionValue(ALGORITHM_FLAG);
        if (alg == null) {
            return ALGORITHM_IMPLEMENTATION; // return default value
        } else {
            for (Scheduler.Implementation a : Scheduler.Implementation.values()) {
                if (alg.equals(a.getCmdRepresentation())) {
                    return a;
                }
            }
            printHelpAndThrowError("Specified algorithm is not available. See available values above.");
            return null;
        }
    }

    private List<CostFunctionManager.Functions> getCostFunctions(CommandLine cmd) throws InvalidUserInputException {
        String[] values = cmd.getOptionValues(COST_FUNCTION_FLAG);
        List<CostFunctionManager.Functions> funcs = new ArrayList<>();
        if (values == null) {
            return funcs; // empty list to represent no cost functions specified
        } else {
            for (String func : values) {
                // add the appropriate cost function to the list, or throw an error if unacceptable value
                boolean validSpecifier = false;
                for (CostFunctionManager.Functions cf : CostFunctionManager.Functions.values()) {
                    if (func.equals(cf.getCmdRepresentation())) {
                        funcs.add(cf);
                        validSpecifier = true;
                        break;
                    }
                }
                if (!validSpecifier) {
                    printHelpAndThrowError("One or more of the cost function values is not supported."
                            + " See correct usage above.");
                }
            }
            return funcs;
        }
    }

    private List<PrunerManager.Pruners> getPruners(CommandLine cmd) throws InvalidUserInputException {
        String[] values = cmd.getOptionValues(PRUNER_FLAG);
        List<PrunerManager.Pruners> funcs = new ArrayList<>();
        if (values == null) {
            return funcs;
        } else {
            for (String func : values) {
                // add the appropriate cost function to the list, or throw an error if unacceptable value
                boolean validSpecifier = false;
                for (PrunerManager.Pruners pruner : PrunerManager.Pruners.values()) {
                    if (func.equals(pruner.getCmdRepresentation())) {
                        funcs.add(pruner);
                        validSpecifier = true;
                        break;
                    }
                }
                if (!validSpecifier) {
                    printHelpAndThrowError("One or more of the pruner values is not supported."
                            + " See correct usage above.");
                }
            }
            return funcs;
        }
    }

    /**
     * Checks the user has entered the correct number of arguments
     * @param cmd represents the command line
     * @throws InvalidUserInputException if the user has entered the wrong number of arguments
     */
    private void checkCorrectNumArguments(CommandLine cmd) throws InvalidUserInputException {
        if (cmd.getArgs().length != NUM_PROCESSORS_POSITION + 1) {
            printHelpAndThrowError("Not enough or too many arguments entered. See correct usage above.");
        }
    }

    /**
     * Designed to be called if the user inputs invalid arguments on the command line. Prints a message to the console
     * describing correct usage of the program then throws an InvalidUserInputException to notify the calling class
     * about the error.
     * @throws InvalidUserInputException tells the program the user has entered invalid input
     */
    private void printHelpAndThrowError(String message) throws InvalidUserInputException {
        formatter.printHelp(HELP_MESSAGE, options);
        throw new InvalidUserInputException(message);
    }

}
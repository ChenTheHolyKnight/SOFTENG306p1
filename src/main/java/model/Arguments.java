package model;

/**
 * Represents a command that the user has input to run an instance of this program
 *
 * @author Victoria Skeggs
 */
public class Command {
    private String inputFilename;
    private int numProcessors;
    private int numCores;
    private boolean toVisualize;
    private String outputFilename;

    /**
     * Creates a Command object
     *
     * TO DO: finish commenting this class
     *
     * @param inputFilename
     * @param numProcessors
     * @param numCores
     * @param toVisualize
     * @param outputFilename
     */
    public Command(String inputFilename, int numProcessors, int numCores, boolean toVisualize, String outputFilename) {
        this.inputFilename = inputFilename;
        this.numProcessors = numProcessors;
        this.numCores = numCores;
        this.toVisualize = toVisualize;
        this.outputFilename = outputFilename;
    }

    public String getInputFilename() {
        return inputFilename;
    }

    public int getNumProcessors() {
        return numProcessors;
    }

    public int getNumCores() {
        return numCores;
    }

    public boolean getToVisualize() {
        return toVisualize;
    }

    public String getOutputFilename() {
        return outputFilename;
    }
}

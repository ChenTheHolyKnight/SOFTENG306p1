package op.model;

/**
 * Class to store the arguments that the user has input to run the program with.
 * @author Victoria Skeggs, Ravid Aharon
 */
public class Arguments {

    private String inputGraphFilename;
    private int numProcessors;
    private int numCores;
    private boolean toVisualize;
    private String outputGraphFilename;

    /**
     *
     * @return the filename/path to the existing input graph
     */
    public String getInputGraphFilename() {
        return inputGraphFilename;
    }

    /**
     *
     * @return the number of processors to schedule the tasks on
     */
    public int getNumProcessors() {
        return numProcessors;
    }

    /**
     *
     * @return the number of cores to run the program on
     */
    public int getNumCores() {
        return numCores;
    }

    /**
     *
     * @return specifies whether the user has turned visualization on
     */
    public boolean getToVisualize() {
        return toVisualize;
    }

    /**
     *
     * @return the desired filename/path to the output graph
     */
    public String getOutputGraphFilename() {
        return outputGraphFilename;
    }
    
    /**
     * Creates a new Arguments object.
     * @param inputFilename
     * @param numProcessors
     * @param numCores
     * @param toVisualize
     * @param outputFilename
     */
    public Arguments(String inputFilename, int numProcessors, int numCores, boolean toVisualize, String outputFilename) {
        this.inputGraphFilename = inputFilename;
        this.numProcessors = numProcessors;
        this.numCores = numCores;
        this.toVisualize = toVisualize;
        this.outputGraphFilename = outputFilename;
    }
}

package op.model;

/**
 * Singleton to represent the arguments that the user has input to run the program with.
 * @author Victoria Skeggs
 */
public class Arguments {

    private static boolean initialized = false;
    private static Arguments instance;

    private String inputGraphFilename;
    private int numProcessors;
    private int numCores;
    private boolean toVisualize;
    private String outputGraphFilename;

    /**
     * Initializes the Arguments instance
     *
     * @param inputFilename the filename/path to the existing input graph
     * @param numProcessors the number of processors to schedule the tasks on
     * @param numCores the number of cores to run the program on
     * @param toVisualize specifies whether the user has turned visualization on
     * @param outputFilename the desired filename/path to the output graph
     */
    public static void initialize(
            String inputFilename, int numProcessors,
            int numCores, boolean toVisualize,
            String outputFilename
    ) throws AlreadyInitializedException {

        if (initialized) {
            throw new AlreadyInitializedException();
        }

        initialized = true;

        instance = new Arguments(inputFilename, numProcessors, numCores, toVisualize, outputFilename);
    }

    /**
     * Returns the Arguments global instance
     * @throws UninitializedException if instance has not been initialized
     * @return The global Arguments instance
     */
    public static Arguments getInstance() throws UninitializedException {
        if (!initialized) {
            throw new UninitializedException();
        }

        return instance;
    }


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
    
    // constructor to be called by the initialize method
    private Arguments(String inputFilename, int numProcessors, int numCores, boolean toVisualize, String outputFilename) {
        this.inputGraphFilename = inputFilename;
        this.numProcessors = numProcessors;
        this.numCores = numCores;
        this.toVisualize = toVisualize;
        this.outputGraphFilename = outputFilename;
    }
}

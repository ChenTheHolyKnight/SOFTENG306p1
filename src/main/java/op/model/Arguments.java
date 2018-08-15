package op.model;

import op.algorithm.Scheduler;
import op.algorithm.bound.CostFunction;
import op.algorithm.prune.PrunerManager;

import java.util.List;

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
    private Scheduler.Implementation algorithm;
    private List<CostFunction.Implementation> costFunctions;
    private List<PrunerManager.Pruners> pruners;

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
     *
     * @return the algorithm implementation to run
     */
    public Scheduler.Implementation getAlgorithm() { 
    	return algorithm; 
    }

    public List<CostFunction.Implementation> getCostFunctions() { 
    	return costFunctions; 
    }
    
    public List<PrunerManager.Pruners> getPruners() {
    	return pruners;
    }
    
    /**
     * Creates a new Arguments object.
     * @param inputFilename
     * @param numProcessors
     * @param numCores
     * @param toVisualize
     * @param outputFilename
     * @param algorithm the specified algorithm to run
     * @param costFunctions the specified cost functions to use
     * @param prunerManager the prunerManager to use
     */
    public Arguments(String inputFilename, int numProcessors, int numCores,
                     boolean toVisualize, String outputFilename,
                     Scheduler.Implementation algorithm, 
                     List<CostFunction.Implementation> costFunctions, 
                     List<PrunerManager.Pruners> prunerManager) {
        this.inputGraphFilename = inputFilename;
        this.numProcessors = numProcessors;
        this.numCores = numCores;
        this.toVisualize = toVisualize;
        this.outputGraphFilename = outputFilename;
        this.algorithm = algorithm;
        this.costFunctions = costFunctions;
    }
}

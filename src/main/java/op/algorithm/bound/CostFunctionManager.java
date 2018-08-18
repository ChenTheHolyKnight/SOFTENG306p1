package op.algorithm.bound;

import java.util.ArrayList;
import java.util.List;

import op.algorithm.prune.EquivalentSchedulePruner;
import op.algorithm.prune.NodeEquivalencePruner;
import op.model.Schedule;

/**
 * A manager class for the Cost Function interface. Can add as many cost functions as user 
 * chooses, then can calculate the cost functionof a schedule.
 * @author Ravid Aharon
 *
 */
public class CostFunctionManager {
    /**
     * Enum defining the different implementations of cost functions available
     */
    public enum Functions {

        BOTTOM_LEVEL("bl"),
        IDLE_TIME("it");

        private String cmdRepresentation;
        Functions(String cmdRepresentation) {
            this.cmdRepresentation = cmdRepresentation;
        }

        public String getCmdRepresentation() {
            return this.cmdRepresentation;
        }
    }

    private int numProcessors;
    private List<CostFunction> costFunctions = new ArrayList<CostFunction>();

    public CostFunctionManager(int numProcessors) {
        this.numProcessors = numProcessors;
    }

    public void addIdleTimeFunction() {
        IdleTimeFunction f =  new IdleTimeFunction(numProcessors);
        if (!costFunctions.contains(f)) costFunctions.add(f);
    }

    public void addBottomLevelFunction() {
        BottomLevelFunction f =  new BottomLevelFunction();
        if (!costFunctions.contains(f)) costFunctions.add(f);
    }

    /**
     * Calculates the cost function with the tightest lower bound
     * @return cost function with the tightest lower bound
     */
    public int calculate(Schedule s) {
        int tightestBound = 0;

        // calculate the different cost functions then take the maximum of them
        // because it will be the tightest lower bound
        for (CostFunction cf : costFunctions) {
            int currentFunc = cf.calculate(s);
            if (currentFunc > tightestBound) {
                tightestBound = currentFunc;
            }
        }

        return tightestBound;
    }
}

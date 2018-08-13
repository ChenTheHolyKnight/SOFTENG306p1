package op.algorithm.bound;

import op.model.Schedule;

/**
 * Cost function implementation that uses all cost function implementations in the hope that we will always get
 * the tightest lower bound on each partial schedule.
 * @author Darcy Cox
 */
public class CombinedCostFunction implements CostFunction {

    private BottomLevelFunction blFunc;
    private IdleTimeFunction idleTimeFunc;

    /**
     * Constructs a new combined cost function instance
     * @param processors The number of processors that tasks are being scheduled on
     */
    public CombinedCostFunction(int processors) {
        this.blFunc = new BottomLevelFunction();
        this.idleTimeFunc = new IdleTimeFunction(processors);
    }

    @Override
    public int calculate(Schedule s) {
        int blValue = blFunc.calculate(s);
        int itValue = idleTimeFunc.calculate(s);

        // we take the maximum because it is the tightest bound
        if (blValue > itValue) {
            return blValue;
        } else {
            return itValue;
        }
    }
}

package op.algorithm.bound;

import op.model.Schedule;

/**
 * Cost function implementation that won't affect the performance of the algorithm at all.
 * Used so we can compare the performance of brute force vs. using effective cost functions
 * @author Darcy Cox
 */
public class EmptyCostFunction implements CostFunction {
    @Override
    public int calculate(Schedule s) {
        return 0;
    }
}

package op.algorithm.bound;

import op.model.Schedule;

/**
 * Base interface for all cost function implementations
 * @author Darcy Cox
 */
public interface CostFunction {

    /**
     * Calculates the cost function of a given schedule instance.
     * This schedule may be a partial or a complete schedule.
     * @return the cost function of the schedule
     */
    int calculate(Schedule s);
}

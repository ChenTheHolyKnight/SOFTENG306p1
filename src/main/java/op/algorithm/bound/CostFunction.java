package op.algorithm.bound;

import op.model.Schedule;

/**
 * Base interface for all cost function implementations
 * @author Darcy Cox
 */
public interface CostFunction {

    /**
     * Enum defining the different implementations of cost functions available
     */
    public enum Implementation {

        BOTTOM_LEVEL("bl"),
        IDLE_TIME("it");

        private String cmdRepresentation;
        Implementation(String cmdRepresentation) {
            this.cmdRepresentation = cmdRepresentation;
        }

        public String getCmdRepresentation() {
            return this.cmdRepresentation;
        }
    }

    /**
     * Calculates the cost function of a given schedule instance.
     * This schedule may be a partial or a complete schedule.
     * @return the cost function of the schedule
     */
    int calculate(Schedule s);
}

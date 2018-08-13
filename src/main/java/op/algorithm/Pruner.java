package op.algorithm;

import op.model.Schedule;

import java.util.List;

/**
 * Interface to define method signature of a Pruner implementation. A pruner is used by branch and bound algorithms
 * to decide whether or not to continue searching a certain sub-tree.
 * @author Darcy Cox
 */
public interface Pruner {
    List<Schedule> prune(List<Schedule> toPrune, int bestScheduleLength, int numProcessors);
}

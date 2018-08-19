package op.algorithm.prune;

import op.model.Schedule;

import java.util.List;

/**
 * Interface to define method signature of a Pruner implementation. A pruner is used by branch and bound algorithms
 * to decide whether or not to continue searching a certain sub-tree.
 * @author Sam Broadhead
 */
public interface Pruner {
	/**
	 * Given a list of Schedules, the prune method will return a subset of this list where the subset
	 * contains only schedules which are not pruned by a specific pruner.
	 * @param toPrune The list of schedules to prune
	 * @return A subset of the list, containing the schedules which were not pruned.
	 */
	List<Schedule> prune(List<Schedule> toPrune);
}

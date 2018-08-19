package op.algorithm.prune;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import op.model.Dependency;
import op.model.Schedule;
import op.model.ScheduledTask;
import op.model.Task;

/**
 * Prunes all schedules with equivalent next scheduled tasks. 
 * two tasks are equivalent if they have the same weight, same incoming and outgoing dependency nodes, 
 * and same incoming and outgoing dependency weights, and have different IDs.
 * @author Ravid Aharon
 *
 */
public class NodeEquivalencePruner implements Pruner{

	@Override
	public List<Schedule> prune(List<Schedule> toPrune) {
		List<Schedule> pruned = new ArrayList<>(toPrune);

		int size = pruned.size();
		for (int i = 0;  i < size; i++) {
			for (int j = 0; j < size; j++) {
				// for each schedule, remove all other schedules that have just scheduled an equivalent task
				Schedule s1 = pruned.get(i);
				Schedule s2 = pruned.get(j);
				if (i != j && s1 != null && s2 != null) {
					ScheduledTask task1 = s1.getMostRecentScheduledTask();
					ScheduledTask task2 = s2.getMostRecentScheduledTask();
					if (areEquivalentTasks(task1, task2)) {
						pruned.set(j, null); // mark index i for deletion
					}
				}
			}
		}
		pruned.removeAll(Collections.singleton(null)); // remove all elements that were marked for deletion (null)
		return pruned;
	}

	/*
	 * returns true if two tasks are equivalent. two tasks are equivalent if they have the same weight,
	 * same incoming and outgoing dependency nodes, and same incoming and outgoing dependency weights.
	 * returns false if tasks are not equivalent.
	 */
	private boolean areEquivalentTasks(ScheduledTask st1, ScheduledTask st2){
		Task t1 = st1.getTask();
		Task t2 = st2.getTask();

		if (t1.getDuration() != t2.getDuration()) {
			return false;
		}

		if (t1.equals(t2)) {
			return false; // if these tasks have the same id, they are not what we define "equivalent" in this context
		}

		List<Task> t1InTasks = new ArrayList<>();
		List<Task> t2InTasks = new ArrayList<>();

		for (Dependency incoming : t1.getIncomingDependencies()) {
			t1InTasks.add(incoming.getStartTask());
		}

		for (Dependency incoming : t2.getIncomingDependencies()) {
			t2InTasks.add(incoming.getStartTask());
		}

		if (!t1InTasks.equals(t2InTasks)) {
			return false; // start tasks of incoming dependencies must be the same for the tasks to be equivalent
		}


		List<Task> t1OutTasks = new ArrayList<>();
		List<Task> t2OutTasks = new ArrayList<>();

		for (Dependency outgoing : t1.getOutgoingDependencies()) {
			t1OutTasks.add(outgoing.getEndTask());
		}

		for (Dependency outgoing : t2.getOutgoingDependencies()) {
			t2OutTasks.add(outgoing.getEndTask());
		}

		if (!t1OutTasks.equals(t2OutTasks)) {
			return false; // end tasks of outgoing dependencies must be the same for the tasks to be equivalent
		}

		return true;
	}

	@Override
	public boolean equals (Object o) {
		if (o instanceof NodeEquivalencePruner) {
			return true;
		}
		return false;
	}
}

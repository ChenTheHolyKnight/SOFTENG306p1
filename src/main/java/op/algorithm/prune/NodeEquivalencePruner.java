package op.algorithm.prune;

import java.util.ArrayList;
import java.util.List;

import op.model.Dependency;
import op.model.Schedule;
import op.model.ScheduledTask;
import op.model.Task;

/**
 * Prunes all schedules with equivalent next scheduled tasks. 
 * two tasks are equivalent if they have the same weight, same incoming and outgoing dependency nodes, 
 * and same incoming and outgoing dependency weights.
 * @author Ravid Aharon
 *
 */
public class NodeEquivalencePruner implements Pruner{

	@Override
	public List<Schedule> prune(List<Schedule> toPrune) {	
		List<Schedule> toRemove = new ArrayList<Schedule>();
		for (Schedule s1 : toPrune) {
			for (Schedule s2 : toPrune) {
				if (toPrune.indexOf(s1) != toPrune.indexOf(s2)) {
					if (areEquivalentTasks(
							s1.getMostRecentScheduledTask(), s2.getMostRecentScheduledTask())) {
						toRemove.add(s2);
					}
				}
			}
		}
		toPrune.removeAll(toRemove);
		return toPrune;
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
		
		for (Dependency t1IncomingDep : t1.getIncomingDependencies()) {
			for (Dependency t2IncomingDep : t2.getIncomingDependencies()) {
				if (t1IncomingDep.hashCode() != t2IncomingDep.hashCode()) {
					return false;
				}
			}
		}
		
		for (Dependency t1OutgoingDep : t1.getOutgoingDependencies()) {
			for (Dependency t2OutgoingDep : t2.getOutgoingDependencies()) {
				if (t1OutgoingDep.hashCode() != t2OutgoingDep.hashCode()) {
					return false;
				}
			}
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

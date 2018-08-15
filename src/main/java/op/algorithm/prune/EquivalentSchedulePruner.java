package op.algorithm.prune;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import op.model.Schedule;

/**
 * Prunes schedules which are equivalent to any other schedule in the list of schedules.
 * Two schedules are equivalent if and only if every processor in the schedule has the same
 * scheduling of scheduled tasks on any other processor in another schedule.
 * 
 * @author Ravid Aharon
 *
 */
public class EquivalentSchedulePruner implements Pruner{

	@Override
	public List<Schedule> prune(List<Schedule> toPrune, int bestScheduleLength, int numProcessors) {
		Set<Schedule> set = new LinkedHashSet<Schedule>(toPrune);
		toPrune.clear();
		toPrune.addAll(set);
		return toPrune;
	}
	
	@Override
	public boolean equals (Object o) {
		if (o instanceof EquivalentSchedulePruner) {
			return true;
		}
		return false;
	}

}

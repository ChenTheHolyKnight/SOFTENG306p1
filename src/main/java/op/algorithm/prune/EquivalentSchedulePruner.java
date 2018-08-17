package op.algorithm.prune;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
	private Set<Schedule> seenSchedules = Collections.newSetFromMap(new ConcurrentHashMap<>());

//	private Set<Schedule> seenSchedules = new HashSet<Schedule>();

	@Override
	public List<Schedule> prune(List<Schedule> toPrune) {
		Set<Schedule> set = new LinkedHashSet<Schedule>(toPrune);

		toPrune.clear();
		set.removeAll(seenSchedules);
		toPrune.addAll(set);

		seenSchedules.addAll(toPrune);

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

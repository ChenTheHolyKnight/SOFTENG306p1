package op.algorithm.prune;

import java.util.ArrayList;
import java.util.List;
import op.model.Schedule;

/**
 * A manager class for the Pruner interface. Can add as many pruners as user chooses, then can execute 
 * the pruners on a list of Schedules.
 * @author Ravid Aharon
 *
 */
public class PrunerManager {
	private List<Pruner> pruners = new ArrayList<Pruner>();
	
	public void addIdleTimePruner(){
		IdleTimePruner p = new IdleTimePruner();
		if (!pruners.contains(p)) pruners.add(p);
	}
	
	public void addEquivalentSchedulePruner() {
		EquivalentSchedulePruner p = new EquivalentSchedulePruner();
		if (!pruners.contains(p)) pruners.add(p);
	}
	
	public void addNodeEquivalencePruner() {
		NodeEquivalencePruner p =  new NodeEquivalencePruner();
		if (!pruners.contains(p)) pruners.add(p);
	}
	
	public List<Schedule> execute(List<Schedule> toPrune, int bestScheduleLength, int numProcessors) {
		System.out.println(pruners.size());
		List<Schedule> pruned = new ArrayList<Schedule>();
		pruned.addAll(toPrune);
		for (Pruner p: pruners) {
			pruned = p.prune(pruned, bestScheduleLength, numProcessors);
		}
		return pruned;
	}
}

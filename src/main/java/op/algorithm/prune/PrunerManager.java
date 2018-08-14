package op.algorithm.prune;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import op.model.Schedule;

public class PrunerManager {
	private Set<Pruner> pruners = new HashSet<Pruner>();
	
	private List<Schedule> toPrune;
	private int bestScheduleLength;
	private int numProcessors;
	
	public PrunerManager(List<Schedule> toPrune, int bestScheduleLength, int numProcessors) {
		this.toPrune = toPrune;
		this.bestScheduleLength = bestScheduleLength;
		this.numProcessors = numProcessors;
	}
	
	public void addIdleTimePruner(){
		pruners.add(new IdleTimePruner());
	}
	
	public void addEquivalentSchedulePruner() {
		pruners.add(new EquivalentSchedulePruner());
	}
	
	public void addNodeEquivalencePruner() {
		pruners.add(new NodeEquivalencePruner());
	}
	
	public List<Schedule> execute() {
		List<Schedule> pruned = new ArrayList<Schedule>();
		pruned.addAll(toPrune);
		for (Pruner p: pruners) {
			pruned = p.prune(pruned, bestScheduleLength, numProcessors);
		}
		return pruned;
	}
}

package op.algorithm.prune;

import java.util.List;

import op.model.Schedule;

public class NodeEquivalencePruner implements Pruner{

	@Override
	public List<Schedule> prune(List<Schedule> toPrune, int bestScheduleLength, int numProcessors) {
		// TODO Auto-generated method stub
		return toPrune;
	}

	@Override
	public boolean equals (Object o) {
		if (o instanceof NodeEquivalencePruner) {
			return true;
		}
		return false;
	}
}

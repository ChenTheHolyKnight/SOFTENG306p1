package op.algorithm.prune;

import op.model.Schedule;

import java.util.List;

/**
 *
 */
public class EmptyPruner implements Pruner {
    @Override
    public List<Schedule> prune(List<Schedule> toPrune, int bestScheduleLength, int numProcessors) {
        return toPrune;
    }
    
    @Override
	public boolean equals (Object o) {
		if (o instanceof EmptyPruner) {
			return true;
		}
		return false;
	}
}

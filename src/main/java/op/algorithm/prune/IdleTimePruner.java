package op.algorithm.prune;

import op.model.Schedule;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class IdleTimePruner implements Pruner {
    @Override
    public List<Schedule> prune(List<Schedule> toPrune, int bestScheduleLength, int numProcessors) {
        List<Schedule> pruned = new ArrayList<>();
        for (Schedule s :  toPrune){
            if(bestScheduleLength/numProcessors >= s.getLength()/numProcessors){
                pruned.add(s);
            }
        }
        //System.out.println(pruned.size());
        return pruned;
    }
    
    @Override
	public boolean equals (Object o) {
		if (o instanceof IdleTimePruner) {
			return true;
		}
		return false;
	}
}

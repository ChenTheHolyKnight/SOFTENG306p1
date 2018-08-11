package op.algorithm;

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
}

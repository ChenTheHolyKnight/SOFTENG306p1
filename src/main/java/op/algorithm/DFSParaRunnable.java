package op.algorithm;

import op.algorithm.bound.CostFunction;
import op.model.Schedule;

import java.util.ArrayList;
import java.util.List;

public class DFSParaRunnable extends BranchAndBoundScheduler implements Runnable {

    private Schedule schedule;
    private int bestScheduleLength;
    private List<Schedule> toAdd = new ArrayList<>();
    private int length;
    /**
     * Creates a BranchAndBoundScheduler instance with the specified Pruner implementation.
     *
     * @param numProcessors the number of processors to schedule tasks on
     * @param p             The Pruner implementation to be used in the scheduling algorithm
     * @param f             the cost function implementation to use for this scheduler
     */
    public DFSParaRunnable(int numProcessors, Pruner p, CostFunction f, Schedule s, int bestLength) {
        super(numProcessors, p, f);
        this.schedule = s;
        this.bestScheduleLength = bestLength;
        this.length = Integer.MAX_VALUE;
    }

    @Override
    public void run() {
        produceSchedule();
    }
    private boolean costFunctionIsPromising(Schedule s, int bestSoFar) {
        int costFunction = super.getCostFunction().calculate(s);
        return costFunction < bestSoFar;
    }
    @Override
    public Schedule produceSchedule() {
        if (schedule.isComplete()) {
            length = schedule.getLength();
            // check if the complete schedule is better than our best schedule so far
            if (schedule.getLength() < bestScheduleLength) {
                bestScheduleLength = (schedule.getLength());
                //System.out.println("New best: " + bestScheduleLength);
                //setBestYet(s);
            }
        } else {
            // not a complete schedule so add children to the stack to be processed later
            List<Schedule> pruned = getPruner().prune(super.getChildrenOfSchedule(schedule), bestScheduleLength, getNumProcessors());
            for (Schedule sched : pruned){
                if (costFunctionIsPromising(sched, bestScheduleLength)) {
                    toAdd.add(sched);
                }
            }
        }
        return schedule;
    }
    public Schedule getSchedule(){
        return schedule;
    }
    public int getScheduleLength(){
        return length;
    }
    public List<Schedule> getToAdd(){
        return toAdd;
    }
}

package op.algorithm;

import op.algorithm.bound.CostFunction;
import op.model.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DFSParaRunnable extends DFSScheduler implements Runnable {

    private Schedule schedule;
    private Schedule bestSchedule;
    private int bestScheduleLength;
    private List<Schedule> toAdd = new ArrayList<>();
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
    }

    @Override
    public void run() {
        produceSchedule();
    }
    public Schedule produceSchedule(){
        // variables to keep track of the best schedule so far in the search
        bestScheduleLength = Integer.MAX_VALUE;
        Pruner p = getPruner();

        // initialize stack with the empty schedule
        Stack<Schedule> scheduleStack =  new Stack<Schedule>();
        scheduleStack.push(schedule);

        // start the search, and continue until all possible schedules have been processed
        while (!scheduleStack.isEmpty()) {
            Schedule currentSchedule = scheduleStack.pop();
            DFSParaScheduler.beenChecked.add(currentSchedule.hashCode());
            if (currentSchedule.isComplete()) {
                // check if the complete schedule is better than our best schedule so far
                if (currentSchedule.getLength() < bestScheduleLength) {
                    bestSchedule = currentSchedule;
                    bestScheduleLength = currentSchedule.getLength();
                }
            } else {
                // not a complete schedule so add children to the stack to be processed later

                List<Schedule> pruned = p.prune(super.getChildrenOfSchedule(currentSchedule), bestScheduleLength, getNumProcessors());
                for (Schedule s: pruned){
                    if (costFunctionIsPromising(s, bestScheduleLength) && !DFSParaScheduler.beenChecked.contains(s.hashCode())) {
                        scheduleStack.push(s);
                    }
                }
            }
        }
        return bestSchedule;
    }
    private boolean costFunctionIsPromising(Schedule s, int bestSoFar) {
        int costFunction = super.getCostFunction().calculate(s);
        return costFunction < bestSoFar;
    }
    public Schedule getSchedule(){
        return bestSchedule;
    }
}

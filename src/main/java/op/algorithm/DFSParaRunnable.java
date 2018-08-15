package op.algorithm;

import op.algorithm.bound.CostFunction;
import op.model.Schedule;

import java.util.List;
import java.util.Stack;

public class DFSParaRunnable extends DFSScheduler implements Runnable {

    private Stack<Schedule> scheduleStack;
    private Schedule bestSchedule;
    /**
     * Creates a BranchAndBoundScheduler instance with the specified Pruner implementation.
     *
     * @param numProcessors the number of processors to schedule tasks on
     * @param p             The Pruner implementation to be used in the scheduling algorithm
     * @param f             the cost function implementation to use for this scheduler
     */
    public DFSParaRunnable(int numProcessors, Pruner p, CostFunction f, Stack<Schedule> s) {
        super(numProcessors, p , f);
        this.scheduleStack = s;
    }

    @Override
    public void run() {
        produceSchedule();
    }
    public Schedule produceSchedule(){
        // variables to keep track of the best schedule so far in the search
        int bestScheduleLength = Integer.MAX_VALUE;

        // initialize stack with the empty schedule
        //Stack<Schedule> scheduleStack =  new Stack<>();
        //scheduleStack.push(schedule);
        // start the search, and continue until all possible schedules have been processed
        while (!scheduleStack.isEmpty()) {
            Schedule currentSchedule = scheduleStack.pop();
            if (currentSchedule.isComplete()) {
                // check if the complete schedule is better than our best schedule so far
                if (currentSchedule.getLength() < bestScheduleLength) {
                    bestSchedule = currentSchedule;
                    bestScheduleLength = currentSchedule.getLength();
                }
            } else {
                // not a complete schedule so add children to the stack to be processed later

                List<Schedule> pruned = getPruner().prune(getChildrenOfSchedule(currentSchedule), bestScheduleLength, getNumProcessors());
                for (Schedule s: pruned){
                    if (costFunctionIsPromising(s, bestScheduleLength)) {
                        scheduleStack.push(s);
                    }
                }
            }
        }
        return bestSchedule;
    }
    public Schedule getSchedule(){
        return bestSchedule;
    }

}

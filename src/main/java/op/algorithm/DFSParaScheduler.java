package op.algorithm;

import op.algorithm.bound.CostFunction;
import op.model.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DFSParaScheduler extends BranchAndBoundScheduler {
    public static List<Integer> beenChecked = new ArrayList<>();
    /**
     * Instantiates a DFSScheduler with the specified params
     *
     * @param numProcessors The number of processors to schedule tasks on
     * @param p             The pruner implementation to use
     * @param f             The cost function implementation to use
     */
    public DFSParaScheduler(int numProcessors, Pruner p, CostFunction f) {
        super(numProcessors, p, f);
    }

    /**
     * @return a schedule with the optimal length
     */
    @Override
    public Schedule produceSchedule() {/*
        *   Run sequentially on 1 thread
        *   when stack in thread reaches size of number of processors
        *   create new threads (n-1, continue using existing thread)
        *   those threads run to completiong
        *   compare results of threads
        */
        Schedule bestSchedule = new Schedule();
        Stack<Schedule> scheduleStack = new Stack<Schedule>();
        scheduleStack.push(bestSchedule);
        int bestScheduleLength = Integer.MAX_VALUE;
        int threadSize = 2;
        Thread[] threads = new Thread[threadSize];
        DFSParaRunnable[] runnables = new DFSParaRunnable[threadSize];
        while(scheduleStack.size()<threadSize){
            //make one thread and run it
            Schedule currentSchedule = scheduleStack.pop();
            beenChecked.add(currentSchedule.hashCode());
            if (currentSchedule.isComplete()) {
                // check if the complete schedule is better than our best schedule so far
                if (currentSchedule.getLength() < bestScheduleLength) {
                    bestSchedule = currentSchedule;
                    bestScheduleLength = currentSchedule.getLength();
                }
            } else {
                List<Schedule> pruned = getPruner().prune(super.getChildrenOfSchedule(currentSchedule), bestScheduleLength, getNumProcessors());
                for (Schedule s: pruned){
                    if (costFunctionIsPromising(s, bestScheduleLength) && !beenChecked.contains(s.hashCode())) {
                        scheduleStack.push(s);
                    }
                }
            }
        }
        for (int i = 0; i<threadSize; i++){
            Schedule currentSchedule = scheduleStack.pop();
            beenChecked.add(currentSchedule.hashCode());
            runnables[i] = new DFSParaRunnable(getNumProcessors(), getPruner(), getCostFunction(), currentSchedule, bestScheduleLength);
            threads[i] = new Thread(runnables[i]);
            threads[i].start();
        }
        for (int i = 0; i < threadSize; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i<threadSize; i++) {
            if(runnables[i].getSchedule() != null) {
                if (runnables[i].getSchedule().getLength() < bestScheduleLength) {
                    bestScheduleLength = runnables[i].getSchedule().getLength();
                    bestSchedule = runnables[i].getSchedule();
                }
            }
        }
        // variables to keep track of the best schedule so far in the search

        System.out.println("Optimal length: " + bestSchedule.getLength());
        return bestSchedule;
    }
    private boolean costFunctionIsPromising(Schedule s, int bestSoFar) {
        int costFunction = super.getCostFunction().calculate(s);
        return costFunction < bestSoFar;
    }
}


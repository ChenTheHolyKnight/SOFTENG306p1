package op.algorithm;

import op.algorithm.bound.CostFunction;
import op.model.Schedule;

import java.util.List;
import java.util.Stack;

public class DFSParaScheduler extends BranchAndBoundScheduler {

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
    public Schedule produceSchedule() {
        Schedule bestSchedule = new Schedule();
        Stack<Schedule> scheduleStack = new Stack<Schedule>();
        Stack<Integer> beenChecked = new Stack<>();
        int bestScheduleLength = Integer.MAX_VALUE;
        int threadSize = 5;

        // variables to keep track of the best schedule so far in the search


        // initialize stack with the empty schedule
        for (Schedule s : getChildrenOfSchedule(bestSchedule)){
            scheduleStack.push(s);
        }
        // start the search, and continue until all possible schedules have been processed
        while (!scheduleStack.isEmpty()) {
            // do something here for para
            Thread[] threads = new Thread[threadSize];
            DFSParaRunnable[] runnables = new DFSParaRunnable[threadSize];
            for (int i = 0; i < threadSize; i++) {
                if (i > scheduleStack.size()) break;
                Schedule currentSchedule = scheduleStack.pop();
                beenChecked.push(currentSchedule.hashCode());
                runnables[i] = new DFSParaRunnable(getNumProcessors(), getPruner(), getCostFunction(), currentSchedule, bestScheduleLength);
                threads[i] = new Thread(runnables[i]);
                threads[i].start();
            }
            for (int i = 0; i < threadSize; i++) {
                if (i > scheduleStack.size()) break;
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < threadSize; i++) {
                if (runnables[i] == null) break;
                if (i > scheduleStack.size()) break;
                if ((runnables[i].getSchedule().getLength() < bestScheduleLength) && (runnables[i].getSchedule().isComplete())) {
                    bestScheduleLength = runnables[i].getSchedule().getLength();
                    bestSchedule = runnables[i].getSchedule();
                    beenChecked.push(bestSchedule.hashCode());
                } else {
                    for (Schedule s : runnables[i].getToAdd()) {
                        if (!beenChecked.contains(s.hashCode())) {
                            scheduleStack.push(s);
                        }
                    }
                }
            }
        }
        System.out.println("Optimal length: " + bestSchedule.getLength());
        return bestSchedule;
    }
}


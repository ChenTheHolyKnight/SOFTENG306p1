package op.algorithm;

import op.algorithm.bound.CostFunction;
import op.model.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.*;

public class DFSParaScheduler extends DFSScheduler {
    private int numThreads;
    /**
     * Instantiates a DFSScheduler with the specified params
     *
     * @param numProcessors The number of processors to schedule tasks on
     * @param p             The pruner implementation to use
     * @param f             The cost function implementation to use
     */
    public DFSParaScheduler(int numProcessors, Pruner p, CostFunction f, int numThreads) {
        super(numProcessors, p, f);
        this.numThreads = numThreads;
    }

    /**
     * @return a schedule with the optimal length
     */
    @Override
    public Schedule produceSchedule() {
        List<Integer> schedulesSeen = new ArrayList<>();
        Schedule bestSchedule = new Schedule();
        Stack<Schedule> scheduleStack = new Stack<Schedule>();
        scheduleStack.push(bestSchedule);
        int bestScheduleLength = Integer.MAX_VALUE;
        numThreads = 30;
        // run sequentially until our stack is big enough to run in parallel
        while(scheduleStack.size()< numThreads){
            //make one thread and run it
            Schedule currentSchedule = scheduleStack.pop();
            schedulesSeen.add(currentSchedule.hashCode());
            if (currentSchedule.isComplete()) { // unlikely to happen before the stack reaches thread size but have anyway
                // check if the complete schedule is better than our best schedule so far
                if (currentSchedule.getLength() < bestScheduleLength) {
                    bestSchedule = currentSchedule;
                    bestScheduleLength = currentSchedule.getLength();
                }
            } else {
                List<Schedule> pruned = getPruner().prune(super.getChildrenOfSchedule(currentSchedule), bestScheduleLength, getNumProcessors());
                for (Schedule s: pruned){
                    if (costFunctionIsPromising(s, bestScheduleLength) && !schedulesSeen.contains(s.hashCode())) {
                        scheduleStack.push(s);
                        schedulesSeen.add(s.hashCode()); // make sure each schedule going onto the stack is unique
                    }
                }
            }
        }
        Stack<Schedule>[] threadStacks = new Stack[numThreads]; // make stacks for the threads to work from
        for (int i = 0; i < numThreads; i++){
            threadStacks[i] = new Stack<>();
        }
        int scheduleSize = scheduleStack.size(); // for loop iteration safety
        for (int i = 0; i < scheduleSize; i++){
            threadStacks[i%numThreads].push(scheduleStack.pop());
        }
        // initiate threads and run them in parallel
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        DFSParaRunnable[] runnables = new DFSParaRunnable[numThreads];
        for (int i = 0; i<numThreads; i++){
            runnables[i] = new DFSParaRunnable(getNumProcessors(), getPruner(), getCostFunction(), threadStacks[i]);
            executor.execute(runnables[i]);
        }
        executor.shutdown();
        while(!executor.isTerminated()){} // wait for runnables to finish
        for (int i = 0; i<numThreads; i++) { // get the best schedule from each thread
            if(runnables[i].getSchedule() != null) { // there is a chance that thread did not reach a complete solution
                System.out.println(runnables[i].getSchedule());
                if (runnables[i].getSchedule().getLength() < bestScheduleLength) {
                    bestScheduleLength = runnables[i].getSchedule().getLength();
                    bestSchedule = runnables[i].getSchedule();
                }
            }
        }
        System.out.println("Optimal length: " + bestSchedule.getLength());
        return bestSchedule;
    }
}


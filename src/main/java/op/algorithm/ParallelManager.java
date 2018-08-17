package op.algorithm;

import op.algorithm.bound.CostFunction;
import op.algorithm.bound.CostFunctionManager;
import op.algorithm.prune.PrunerManager;
import op.model.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.*;

/**
 * Parallel DFS algorithm manager.
 * @author Sam Broadhead
 */
public class ParallelManager extends DFSScheduler {
    private int numThreads;
    /**
     * Instantiates a DFSScheduler with the specified params
     *
     * @param numProcessors The number of processors to schedule tasks on
     * @param pm             The pruner manager to use
     * @param cfm             The cost function manager to use
     */
    public ParallelManager(int numProcessors, PrunerManager pm, CostFunctionManager cfm, int numThreads) {
        super(numProcessors, pm, cfm);
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
        PrunerManager pm = getPrunerManager();
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
                List<Schedule> pruned = pm.execute(getChildrenOfSchedule(currentSchedule));
                for (Schedule s: pruned){
                    boolean costFunctionIsPromising = super.getCostFunctionManager().calculate(s) < bestScheduleLength;
                    if (costFunctionIsPromising && !schedulesSeen.contains(s.hashCode())) {
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
        ParallelRunnable[] runnables = new ParallelRunnable[numThreads];
        for (int i = 0; i<numThreads; i++){
            runnables[i] = new ParallelRunnable(getNumProcessors(), getPrunerManager(), getCostFunctionManager(), threadStacks[i]);
            executor.execute(runnables[i]);
        }
        executor.shutdown();
        while(!executor.isTerminated()){} // wait for runnables to finish
        for (int i = 0; i<numThreads; i++) { // get the best schedule from each thread
            if(runnables[i].getSchedule() != null) { // there is a chance that thread did not reach a complete solution
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

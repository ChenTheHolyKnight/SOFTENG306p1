package op.algorithm;

import op.algorithm.bound.CostFunctionManager;
import op.algorithm.prune.PrunerManager;
import op.model.Schedule;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Parallel DFS Scheduler. Run sequentially until there is enough schedules to switch to parallel. Once switched
 * each thread runs from its own stack. When a thread finishes its stack it will switch over to its neighboring threads
 * stack and these threads will work together on that stack. One best length is maintained across the threads so
 * a thread does not hold a schedule worse than another thread's best schedule.
 * @author Sam Broadhead
 */
public class DFSParallelScheduler extends BranchAndBoundScheduler {
    private int numThreads;
    Deque<Schedule> scheduleStack = new ConcurrentLinkedDeque<>();
    Deque<Schedule>[] stacks;
    private AtomicInteger globalBestScheduleLength = new AtomicInteger(Integer.MAX_VALUE);

    /**
     * Instantiates a DFSParallelScheduler with the specified params
     *
     * @param numProcessors The number of processors to schedule tasks on
     * @param pm            The pruner manager to use
     * @param cfm           The cost function manager to use
     * @param numThreads    How many threads to run
     */
    public DFSParallelScheduler(int numProcessors, PrunerManager pm, CostFunctionManager cfm, int numThreads) {
        super(numProcessors, pm, cfm);
        this.numThreads = numThreads;
    }

    /**
     * @return a schedule with the optimal length
     */
    @Override
    public Schedule produceSchedule() {

        Schedule bestSchedule = new Schedule();
        int bestScheduleLength = Integer.MAX_VALUE;
        scheduleStack.push(bestSchedule);

        while (scheduleStack.size() < numThreads) {
            for (Schedule s : getPrunerManager().execute(getChildrenOfSchedule(scheduleStack.pop()))) {
                boolean costFunctionIsPromising = super.getCostFunctionManager().calculate(s) < bestScheduleLength;
                if (costFunctionIsPromising) {
                    scheduleStack.push(s);
                }
            }
        }

        // split the stack up into equal size stacks, one stack for each thread
        stacks = new ConcurrentLinkedDeque[numThreads];
        for (int i = 0; i < numThreads; i++) {
            stacks[i] = new ConcurrentLinkedDeque<>();
        }
        int scheduleSize = scheduleStack.size(); // ensure the whole stack gets emptied
        for (int i = 0; i < scheduleSize; i++) {
            stacks[i%numThreads].push(scheduleStack.pop());
        }

        // create a fixed thread pool and a list of futures for the results to be stored in
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Schedule>> futures = new ArrayList<>();

        // submit a new callable to the thread pool and add its future to the futures list
        for (int i = 0; i < numThreads; i++) {
            // each dfs callable needs to have the same counter variable reference,
            // so we set it explicitly in the constructor
            DFSScheduler dfs = new DFSScheduler(getNumProcessors(), getPrunerManager(), getCostFunctionManager(),
                    getNodesVisited(), getPrunedTrees(),
                    stacks, i, globalBestScheduleLength);
            for (SchedulerListener sl : super.getListeners()) {
                dfs.addListener(sl);
            }
            futures.add(executor.submit(dfs));
        }
        executor.shutdown(); // shut down the executor once all tasks have finished

        List<Schedule> results = new ArrayList<>();

        for (Future<Schedule> f : futures) {
            try {
                results.add(f.get()); // wait if necessary and get result.
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                // ignore this as it can only be thrown
                // when all stacks are empty and program
                // is finished
            }
        }

        // If the thread was unable to process a complete solution, remove it from our results
        results.removeAll(Collections.singleton(null));

        for (Schedule s : results) {

            // get the best schedule from each of the threads
            if (s.getLength() < bestScheduleLength) {
                bestSchedule = s;
                bestScheduleLength = bestSchedule.getLength();
            }
        }

        return bestSchedule;
    }
}
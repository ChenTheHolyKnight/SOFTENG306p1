package op.algorithm;

import op.algorithm.bound.CostFunction;
import op.algorithm.bound.CostFunctionManager;
import op.algorithm.prune.PrunerManager;
import op.model.Schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.*;

/**
 * Parallel DFS algorithm manager.
 * @author Sam Broadhead
 */
public class DFSParallelScheduler extends BranchAndBoundScheduler {
    private int numThreads;
    static Stack<Schedule> scheduleStack = new Stack<Schedule>();

    /**
     * Instantiates a DFSScheduler with the specified params
     *
     * @param numProcessors The number of processors to schedule tasks on
     * @param pm            The pruner manager to use
     * @param cfm           The cost function manager to use
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

        // initiate threads and run them in parallel
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        //ForkJoinPool executor = new ForkJoinPool(numThreads);
        List<Future<Schedule>> futures = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            futures.add(executor.submit(new DFSScheduler(getNumProcessors(), getPrunerManager(), getCostFunctionManager().clone(), scheduleStack)));
        }
        executor.shutdown();

        List<Schedule> results = new ArrayList<>();
        for (Future<Schedule> f : futures) {
            try {
                results.add(f.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        results.removeAll(Collections.singleton(null));

        for (Schedule s : results) {
            if (s.getLength() < bestScheduleLength) {
                bestSchedule = s;
            }
            //System.out.println("Optimal length: " + bestSchedule.getLength());
        }

        return bestSchedule;
    }
}

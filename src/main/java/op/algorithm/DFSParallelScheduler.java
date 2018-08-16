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
    Stack<Schedule>[] stacks;

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
        List<Future<Schedule>> futures = new ArrayList<>();
        /*stacks = new Stack[numThreads];
        for (int i = 0; i < numThreads; i++){
            stacks[i] = new Stack<>();
        }
        int scheduleSize = scheduleStack.size(); // for loop iteration safety
        for (int i = 0; i < scheduleSize; i++){
            stacks[i%numThreads].push(scheduleStack.pop());
        }*/
        for (int i = 0; i < numThreads; i++) {
            futures.add(executor.submit(new DFSScheduler(getNumProcessors(), getPrunerManager(), getCostFunctionManager().clone(), scheduleStack)));
        }
        executor.shutdown();
        /*while(!executor.isTerminated()){
            for (int i = 0; i < numThreads; i++){
                int neighbor = (i+1)%numThreads;
                /*if((stacks[i].size()+10 < stacks[neighbor].size()) && (stacks[neighbor].size() > 4)){
                    stacks[i].push(stacks[neighbor].pop());
                }
                if(stacks[i].isEmpty() && !(stacks[neighbor].size() < 2)){
                    int splitSize = stacks[neighbor].size()/2;
                    for (int j = 0; j < splitSize; j++){
                        stacks[i].push(stacks[neighbor].pop());
                    }
                }
            }
        }*/

        List<Schedule> results = new ArrayList<>();
        for (Future<Schedule> f : futures) {
            try {
                results.add(f.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                // the stack was emptied when a thread was trying to get a new schedule
                // can ignore this because this means algorithm has already finished
                e.printStackTrace();
            }
        }
        results.removeAll(Collections.singleton(null));

        for (Schedule s : results) {

            // if the thread was unable to process any complete schedules, ignore it
            if(s.getLength() == 0){
                continue;
            }
            // get the best schedule from each of the threads
            if (s.getLength() < bestScheduleLength) {
                bestSchedule = s;
                bestScheduleLength = bestSchedule.getLength();
            }
        }

        return bestSchedule;
    }
}

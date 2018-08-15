package op.algorithm;

import op.algorithm.bound.CostFunction;
import op.model.Schedule;
import op.model.Task;
import op.model.TaskGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.*;

public class DFSParaScheduler extends BranchAndBoundScheduler {
    //public static List<Integer> beenChecked = new ArrayList<>();
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
     *   those threads run to completion
     *   compare results of threads
     */
        TaskGraph tg = TaskGraph.getInstance();
        List<Task> allTasks = tg.getAllTasks();
        List<Integer> schedulesSeen = new ArrayList<>();
        Schedule bestSchedule = new Schedule();
        Stack<Schedule> scheduleStack = new Stack<Schedule>();
        scheduleStack.push(bestSchedule);
        int bestScheduleLength = Integer.MAX_VALUE;
        int threadSize = 4;
        // run sequentially until our stack is big enough to run in parallel
        while(scheduleStack.size()< threadSize){
            //make one thread and run it
            Schedule currentSchedule = scheduleStack.pop();
            schedulesSeen.add(currentSchedule.hashCode());
            if (currentSchedule.isComplete()) {
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
                        schedulesSeen.add(s.hashCode());
                    }
                }
            }
        }
        /*Stack<Schedule>[] threadStacks = new Stack[threadSize];
        for (int i = 0; i < threadSize; i++){
            threadStacks[i] = new Stack<>();
        }
        /*System.out.println(scheduleStack.size());
        int scheduleSize = scheduleStack.size();
        for (int i = 0; i < scheduleSize; i++){
            threadStacks[i%threadSize].push(scheduleStack.pop());
        }
        for(int i=0; i<threadSize; i++){
            System.out.println(threadStacks[i].size());
        }*/
        // initiate threads and run them in parallel
        // using runnables
        ExecutorService executor = Executors.newFixedThreadPool(threadSize);
        DFSParaRunnable[] runnables = new DFSParaRunnable[threadSize];
        for (int i = 0; i<threadSize; i++){
            Schedule currentSchedule = scheduleStack.pop();
            System.out.println(currentSchedule);
            runnables[i] = new DFSParaRunnable(getNumProcessors(), getPruner(), getCostFunction(), currentSchedule, allTasks);
            executor.execute(runnables[i]);
        }
        executor.shutdown();
        while(!executor.isTerminated()){}
        for (int i = 0; i<threadSize; i++) { // get the best schedule from each thread
            if(runnables[i].getSchedule() != null) { // there is a chance that thread did not reach a complete solution
                System.out.println(runnables[i].getSchedule().getLength());
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
    private boolean costFunctionIsPromising(Schedule s, int bestSoFar) {
        int costFunction = super.getCostFunction().calculate(s);
        return costFunction < bestSoFar;
    }
}


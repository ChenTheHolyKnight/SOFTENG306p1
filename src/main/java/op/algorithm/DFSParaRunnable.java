package op.algorithm;

import op.algorithm.bound.CostFunction;
import op.model.Dependency;
import op.model.Schedule;
import op.model.ScheduledTask;
import op.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DFSParaRunnable extends Scheduler implements Runnable {

    private Schedule schedule;
    private Schedule bestSchedule;
    private Pruner p;
    private CostFunction f;
    private List<Task> allTasks;
    /**
     * Creates a BranchAndBoundScheduler instance with the specified Pruner implementation.
     *
     * @param numProcessors the number of processors to schedule tasks on
     * @param p             The Pruner implementation to be used in the scheduling algorithm
     * @param f             the cost function implementation to use for this scheduler
     */
    public DFSParaRunnable(int numProcessors, Pruner p, CostFunction f, Schedule s, List<Task> allTasks) {
        super(numProcessors);
        this.p = p;
        this.f = f;
        this.schedule = s;
        this.allTasks = allTasks;
    }

    @Override
    public void run() {
        produceSchedule();
    }
    public Schedule produceSchedule(){
        // variables to keep track of the best schedule so far in the search
        int bestScheduleLength = Integer.MAX_VALUE;

        // initialize stack with the empty schedule
        Stack<Schedule> scheduleStack =  new Stack<>();
        scheduleStack.push(schedule);
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

                List<Schedule> pruned = p.prune(getChildrenOfSchedule(currentSchedule), bestScheduleLength, getNumProcessors());
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
    private boolean costFunctionIsPromising(Schedule s, int bestSoFar) {
        int costFunction = f.calculate(s);
        return costFunction < bestSoFar;
    }
    private List<Schedule> getChildrenOfSchedule(Schedule s) {
        List<Schedule> children = new ArrayList<Schedule>();

        List<Task> freeTasks = getFreeTasks(s);

        // build a new schedule for every valid free task to processor allocation and add to the list of children
        for (Task t : freeTasks) {
            for (int i = 1; i <= super.getNumProcessors(); i++) {
                int startTime = super.getEarliestStartTime(s, t, i);
                children.add(new Schedule(s, new ScheduledTask(t, startTime, i)));
            }
        }

        return children;
    }


    // gets all free tasks based on a task graph and a (partial) schedule
    // a free task is one whose dependencies are already scheduled, and is not scheduled itself
    private List<Task> getFreeTasks(Schedule s) {

        List<Task> freeTasks = new ArrayList<Task>();
        for (Task t : allTasks) {

            if (s.getScheduledTask(t) == null) {
                // the current task is not yet scheduled, check its dependencies
                List<Dependency> deps = t.getIncomingDependencies();
                if (deps.isEmpty()) {
                    freeTasks.add(t); // no dependencies so this task must be free
                } else {
                    boolean dependenciesAllScheduled = true;
                    for (Dependency d : deps) {
                        Task startTask = d.getStartTask();
                        if (s.getScheduledTask(startTask) == null) {
                            // if even one of the tasks dependencies is not scheduled, it is not free
                            dependenciesAllScheduled = false;
                        }
                    }
                    if (dependenciesAllScheduled) {
                        freeTasks.add(t);
                    }
                }
            }
        }

        return freeTasks;
    }
}

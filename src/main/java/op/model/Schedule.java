package op.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Representation of a schedule, which is an allocation of tasks to processors over a certain time.
 */

public class Schedule {


    // to be implemented
    private HashMap<Integer,List<ScheduledTask>> processorTasksMap = new HashMap<>();
    private HashMap<Task,ScheduledTask> taskMap = new HashMap<>();


    /**
     * Tells whether or not this Schedule instance is a complete schedule (all tasks allocated)
     * @return true if this Schedule is complete, false otherwise
     */
    public boolean isComplete() {

        List<Task> tasks=TaskGraph.getInstance().getAllTasks();
        Set<Integer> scheduledProcessor=processorTasksMap.keySet();
        List<ScheduledTask> scheduledTasks=new ArrayList<>();

        for(Integer ps:scheduledProcessor){
            List<ScheduledTask> subScheduledTasks=processorTasksMap.get(ps);
            scheduledTasks.addAll(subScheduledTasks);
        }
        if(scheduledTasks.size()==tasks.size()){
            return true;
        }
        return false;
    }

    
    public ScheduledTask getScheduledTask(Task t) {
    	return taskMap.get(t);
    }

    /**
     * Add the processor to the Hashmap with a list of tasks scheduled on this processor
     * @param processNum the number indicates the processor
     * @param scheduledTasks a list of tasks scheduled on the processor
     */
    public void addProcessor(Integer processNum,List<ScheduledTask> scheduledTasks){
        this.processorTasksMap.put(processNum,scheduledTasks);
    }

    /**
     * Get the list of tasks allocated on the processor
     * @param processorNum the number indicates the processor
     * @return a list of scheduled tasks that scheduled on the processor
     */
    public List<ScheduledTask> getScheduledTasks(int processorNum){
        return processorTasksMap.get(processorNum);
    }
}

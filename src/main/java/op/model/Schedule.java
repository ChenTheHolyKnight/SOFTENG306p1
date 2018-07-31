package op.model;

import java.util.HashMap;
import java.util.List;

/**
 * Representation of a schedule, which is an allocation of tasks to processors over a certain time.
 */
public class Schedule {

    // to be implemented
        HashMap<Integer,List<ScheduledTask>> processorTasksMap=new HashMap<>();
        HashMap<ScheduledTask,Integer> taskProcessorMap=new HashMap<>();

    /**
     * Tells whether or not this Schedule instance is a complete schedule (all tasks allocated)
     * @return true if this Schedule is complete, false otherwise
     */
    public boolean isComplete() {
        return false;
    }


    public List<ScheduledTask> getScheduledTasks(int processorNum){
        return processorTasksMap.get(processorNum);
    }

    public int getProcessorNum(ScheduledTask scheduledTask){
        return taskProcessorMap.get(scheduledTask);
    }



}

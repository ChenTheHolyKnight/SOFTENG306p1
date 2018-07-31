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
    private HashMap<ScheduledTask,Integer> taskProcessorMap = new HashMap<>();


    /**
     * Tells whether or not this Schedule instance is a complete schedule (all tasks allocated)
     * @return true if this Schedule is complete, false otherwise
     */

    public boolean isComplete(TaskGraph graph) {

        List<Task> tasks=graph.getAllTasks();
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

    public void addScheduledTask(ScheduledTask task){
        this.taskProcessorMap.put(task,task.getProcessor());
    }

    public void addProcessor(Integer processNum,List<ScheduledTask> scheduledTasks){
        this.processorTasksMap.put(processNum,scheduledTasks);
    }

    public List<ScheduledTask> getScheduledTasks(int processorNum){
        return processorTasksMap.get(processorNum);
    }

    public int getProcessorNum(ScheduledTask task){
        return taskProcessorMap.get(task);
    }
}

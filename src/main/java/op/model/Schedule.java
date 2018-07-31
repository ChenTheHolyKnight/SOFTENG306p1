package op.model;

import org.graphstream.graph.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Representation of a schedule, which is an allocation of tasks to processors over a certain time.
 */
public class Schedule {

    //a HashMap to indicate the list of scheduled tasks assigned to each processor
    private HashMap<Integer,List<ScheduledTask>> processorTasksMap=new HashMap<>();

    //a HashMap to indicate which processor each scheduled task in.
    private HashMap<ScheduledTask,Integer> taskProcessorMap=new HashMap<>();

    /**
     * Tells whether or not this Schedule instance is a complete schedule (all tasks allocated)
     * @Param graph the input graph containing tasks and dependencies.
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

    /**
     * Add the scheduled task in the map
     * @param task the scheduled task needs to be added to the map*/
    public void addScheduledTask(ScheduledTask task){
        int processorNum=task.getProcessor();
        this.taskProcessorMap.put(task,processorNum);
    }

    /**
     * Store each processor with the scheduled tasks assigned to it to the map
     * @param processNum the number indicating the processor
     * @param scheduledTasks a list of scheduled tasks that are assigned to the processor
     */
    public void addProcessor(Integer processNum,List<ScheduledTask> scheduledTasks){
        this.processorTasksMap.put(processNum,scheduledTasks);
    }

    /**
     * Get the list of scheduled task that is assigned to the specific processor
     * @param processorNum the number indicating the processor
     * @return a list of scheduled tasks assigned to the processor
     */
    public List<ScheduledTask> getScheduledTasks(int processorNum){
        return processorTasksMap.get(processorNum);
    }

    /**
     * Get the processor number that indicates which processor the task is assigned to
     * @param task a scheduled task
     * @return  the processor number indicates the processor where the task is assigned to*/
    public int getProcessorNum(ScheduledTask task){
        return taskProcessorMap.get(task);
    }



}

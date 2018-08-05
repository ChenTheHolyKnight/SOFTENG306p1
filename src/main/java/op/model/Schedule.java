package op.model;

import java.util.*;

/**
 * Representation of a schedule, which is an allocation of tasks to processors over a certain time.
 */

public class Schedule {

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


    /**
     * Method to add a new scheduled task to this schedule instance
     * @param scheduledTask the scheduled task to add
     */
    public void addScheduledTask(ScheduledTask scheduledTask){
        int processorNum=scheduledTask.getProcessor();
        if(processorTasksMap.get(processorNum)!=null){
	    processorTasksMap.get(processorNum).add(scheduledTask);
        }else{
            List<ScheduledTask> tasks=new ArrayList<>();
            tasks.add(scheduledTask);
            processorTasksMap.put(scheduledTask.getProcessor(),tasks);
        }
        taskMap.put(scheduledTask.getTask(), scheduledTask);
    }

    /**
     * Gets a task's corresponding scheduled task in this schedule
     * 
     * @param task the task to retrieve the relevant ScheduledTask for
     * @return the scheduled task representing the task in this schedule
     */
    public ScheduledTask getScheduledTask(Task t) {
    	return taskMap.get(t);
    }

    /**
     * Get the list of tasks allocated on the processor
     * @param processorNum the number indicates the processor
     * @return a list of scheduled tasks that scheduled on the processor
     */
    public List<ScheduledTask> getScheduledTasks(int processorNum){
        return processorTasksMap.get(processorNum);
    }

    /**
     * Calculates the length of the schedule or partial schedule
     * @return the time it would take for all scheduled tasks to be completed
     */
	public int getLength() {
        int length = 0;
        for (List<ScheduledTask> tasks: processorTasksMap.values()) {
            ScheduledTask lastTask = tasks.get(tasks.size()-1);
            if (lastTask.getStartTime() + lastTask.getTask().getDuration() > length) {
                length = lastTask.getStartTime() + lastTask.getTask().getDuration();
            }
        }
        return length;
    }
}

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
     * Gets a task's corresponding scheduled task in this schedule
     * 
     * @param t the task 
     * @return the scheduled task representing the task in this schedule
     */
    public void addScheduledTask(ScheduledTask task){
        int processorNum=task.getProcessor();
        if(processorTasksMap.get(processorNum)!=null){
	    processorTasksMap.get(processorNum).add(task);
        }else{
            List<ScheduledTask> tasks=new ArrayList<>();
            tasks.add(task);
            processorTasksMap.put(task.getProcessor(),tasks);
        }
    }

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
	public double getLength() {
        double length = 0;
        for (List<ScheduledTask> tasks: processorTasksMap.values()) {
            ScheduledTask lastTask = tasks.get(tasks.size()-1);
            if (lastTask.getStartTime() + lastTask.getTask().getDuration() > length) {
                length = lastTask.getStartTime() + lastTask.getTask().getDuration();
            }
        }
        return length;
    }
}

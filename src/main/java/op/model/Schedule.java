package op.model;

import java.util.*;

/**
 * Representation of a schedule, which is an allocation of tasks to processors over a certain time.
 */

public class Schedule {

    private HashMap<Integer,List<ScheduledTask>> processorTasksMap;
    private HashMap<Task,ScheduledTask> taskMap;

    /**
     * Constructs an empty schedule instance
     */
    public Schedule() {
        this.processorTasksMap = new HashMap<>();
        this.taskMap = new HashMap<>();
    }

    /**
     * Constructs a new schedule by extending the provided schedule by the provided scheduled task
     * @param s The schedule to base this new schedule on
     * @param stNew The scheduled task to add
     */
    public Schedule(Schedule s, ScheduledTask stNew) {

        this.taskMap = (HashMap<Task, ScheduledTask>)((s.taskMap).clone());
        this.processorTasksMap = (HashMap<Integer, List<ScheduledTask>>)((s.processorTasksMap).clone());

        // replace the lists of scheduled tasks with an equivalent list, but with a different reference.
        // this is so that when these lists are changed in child schedules of s, it does not affect
        // the parent schedule. This is not necessary for the Task/ScheduledTask map because the values within
        // those objects cannot be changed by objects referencing them.
        for (int i : this.processorTasksMap.keySet()) {
            this.processorTasksMap.put(i, new ArrayList<ScheduledTask>(this.processorTasksMap.get(i)));
        }

        this.addScheduledTask(stNew);
    }
  
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
        if(processorTasksMap.get(processorNum)!= null){
	        processorTasksMap.get(processorNum).add(scheduledTask);
        }else{
            List<ScheduledTask> tasks=new ArrayList<>();
            tasks.add(scheduledTask);
            processorTasksMap.put(processorNum,tasks);
        }
        taskMap.put(scheduledTask.getTask(), scheduledTask);
    }

    /**
     * Gets a task's corresponding scheduled task in this schedule
     * 
     * @param t the task to retrieve the relevant ScheduledTask for
     * @return the scheduled task representing the task in this schedule, null if the task is not yet scheduled
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

package op.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Representation of a schedule, which is an allocation of tasks to processors over a certain time.
 */

public class Schedule {

    private HashMap<Integer,List<ScheduledTask>> processorMap; // stores all scheduled tasks on a given processor
    private HashMap<Task,ScheduledTask> taskMap; // stores the scheduled representation of tasks
    
    private ScheduledTask latestScheduledTask;
    /**
     * Constructs an empty schedule instance
     */
    public Schedule() {
        this.processorMap = new HashMap<>();
        this.taskMap = new HashMap<>();
    }

    /**
     * Constructs a new schedule by extending the provided schedule by the provided scheduled task
     * @param s The schedule to base this new schedule on
     * @param stNew The scheduled task to add
     */
    public Schedule(Schedule s, ScheduledTask stNew) {

        this.taskMap = (HashMap<Task, ScheduledTask>)((s.taskMap).clone());
        this.processorMap = (HashMap<Integer, List<ScheduledTask>>)((s.processorMap).clone());

        // replace the lists of scheduled tasks with an equivalent list, but with a different reference.
        // this is so that when these lists are changed in child schedules of s, it does not affect
        // the parent schedule. This is not necessary for the Task/ScheduledTask map because the values within
        // those objects cannot be changed by objects referencing them.
        for (int i : this.processorMap.keySet()) {
            this.processorMap.put(i, new ArrayList<ScheduledTask>(this.processorMap.get(i)));
        }
        
        this.latestScheduledTask = stNew;
        this.addScheduledTask(stNew);
    }
  
    /**
     * Tells whether or not this Schedule instance is a complete schedule (all tasks allocated)
     * @return true if this Schedule is complete, false otherwise
     */
    public boolean isComplete() {

        List<Task> tasks = TaskGraph.getInstance().getAllTasks();

        List<ScheduledTask> scheduledTasks = this.getAllScheduledTasks();

        if(scheduledTasks.size() == tasks.size()){
            return true;
        }
        return false;
    }


    /**
     * Adds a new scheduled task to this schedule instance
     * @param scheduledTask the scheduled task to add
     */
    public void addScheduledTask(ScheduledTask scheduledTask){
        int processorNum = scheduledTask.getProcessor();
        if(processorMap.get(processorNum)!= null){
	        processorMap.get(processorNum).add(scheduledTask);
        }else{
            List<ScheduledTask> tasks=new ArrayList<>();
            tasks.add(scheduledTask);
            processorMap.put(processorNum,tasks);
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
     * Gets the latest ScheduledTask added to this schedule
     * @return the latest ShceduledTask added to the schedule
     */
    public ScheduledTask getLatestScheduledTask(){
    	return latestScheduledTask;
    }

    /**
     * Get the list of tasks allocated on the specified processor
     * @param processorNum the number indicates the processor
     * @return a list of scheduled tasks that scheduled on the processor, null if this processor has no scheduled tasks
     */
    public List<ScheduledTask> getScheduledTasksOfProcessor(int processorNum){

        if (processorMap.get(processorNum) == null) {
            return null;
        } else {
            // create new list so code using the returned list cannot modify this schedule's
            // internal representation of the list.
            return new ArrayList<ScheduledTask>(processorMap.get(processorNum));
        }
    }

    /**
     * Calculates the length of the schedule or partial schedule
     * @return the time it would take for all scheduled tasks to be completed
     */
	public int getLength() {

	    //TODO change this to return cost function
        int length = 0;
        for (List<ScheduledTask> tasks: processorMap.values()) {
            ScheduledTask lastTask = tasks.get(tasks.size()-1);
            if (lastTask.getStartTime() + lastTask.getTask().getDuration() > length) {
                length = lastTask.getStartTime() + lastTask.getTask().getDuration();
            }
        }
        return length;
    }

    /**
     * @param processorNum The processor to get the next free time of
     * @return The earliest time that anything can be scheduled on the specified processor
     */
    public int getNextFreeTimeOfProcessor(int processorNum) {
        List<ScheduledTask> tasksOnProcessor = processorMap.get(processorNum);
        if (tasksOnProcessor == null) {
            return 0; // the processor has no tasks on it, so it is free from t = 0
        }
        ScheduledTask lastTaskOnProcessor = tasksOnProcessor.get(tasksOnProcessor.size() - 1);
        return lastTaskOnProcessor.getStartTime() + lastTaskOnProcessor.getTask().getDuration();
    }

    /**
     * @return All tasks that are scheduled in this schedule instance
     */
    public List<ScheduledTask> getAllScheduledTasks() {

        List<ScheduledTask> scheduledTasks = new ArrayList<>();

        for (int proc : processorMap.keySet()) {
            List<ScheduledTask> tasksOnCurrentProcessor = processorMap.get(proc);
            scheduledTasks.addAll(tasksOnCurrentProcessor);
        }

        return scheduledTasks;
    }

	/**
	 * Checks if this schedule is equal to another object.
	 * Two schedules are equal if and only if every processor in the schedule has the same
	 * scheduling of scheduled tasks on any other processor in another schedule.
	 */
	@Override
	public boolean equals(Object schedule){
		int numProcessors = processorMap.size();
		if (schedule instanceof Schedule){
			schedule = (Schedule) schedule;
		} else {
			return false;
		}
		for (int i=1; i<=numProcessors; i++) {
			if (!processorMap.values().contains(((Schedule) schedule).getScheduledTasksOfProcessor(i))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * gets the hashcode for this schedule.
	 * The hashcode is calculated based off all the scheduled tasks on all processors.
	 * If two Schedules have the same hashcode, they are considered equal. 
	 */
	@Override 
	public int hashCode() {
		int result = 17;
		
		for (List<ScheduledTask> scheduledTasks : processorMap.values()) {
			for (ScheduledTask st : scheduledTasks) {
				result = result + st.hashCode();
			}
		}
		return result;
	}
}

package op.model;

/**
 * Represents a task that has been scheduled.
 *
 * @author Victoria Skeggs
 */
public class ScheduledTask {

    private Task task;
    private int startTime;
    private int processor;

    public ScheduledTask (Task task, int startTime, int processor) {
        this.task = task;
        this.startTime = startTime;
        this.processor = processor;
    }

    /**
     *
     * @return the number of the processor the task has been scheduled on
     */
    public int getProcessor() {
        return processor;
    }

    /**
     * 
     * @return the start time of this scheduled task
     */
	public int getStartTime() {
		return startTime;
	}

	/**
	 * 
	 * @return the task that this ScheduledTask instance refers to
	 */
	public Task getTask() {
		return task;
	}

}

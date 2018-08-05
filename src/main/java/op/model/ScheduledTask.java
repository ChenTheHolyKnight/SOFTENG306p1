package op.model;

/**
 * Represents a task that has been scheduled.
 *
 * @author Victoria Skeggs
 */
public class ScheduledTask {

    public Task task;
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

	public double getStartTime() {
		return startTime;
	}

	public Task getTask() {
		return task;
	}

}

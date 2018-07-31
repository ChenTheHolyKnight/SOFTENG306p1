package op.model;

public class ScheduledTask extends Task{
    private int startTime;
    private int processor;

    public ScheduledTask(Task task,int startTime,int processor){
        super(task.getId(),task.getWeight());
        this.startTime=startTime;
        this.processor=processor;
    }

    public int getStartTime(){
        return this.startTime;
    }

    public int getProcessor(){
        return this.processor;
    }

}

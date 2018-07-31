package op.model;

public class ScheduledTask extends Task{
    private int startTime;
    private int processor;

    public ScheduledTask(int id, int weight,int startTime,int processor){
        super(id,weight);
        this.startTime=startTime;
        this.processor=processor;
    }

    public int getStartTime(){
        return this.startTime;
    }

    public int getProcessor(){
        return this.getProcessor();
    }

}

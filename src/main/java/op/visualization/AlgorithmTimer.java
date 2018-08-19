package op.visualization;

import eu.hansolo.tilesfx.Tile;

import java.util.TimerTask;

public class AlgorithmTimer extends TimerTask{
    private Tile timerTile;
    private long time;

    public AlgorithmTimer(Tile timerTile){
        time=System.currentTimeMillis();
        this.timerTile=timerTile;
    }

    private double getTime(){
        double currentTime=System.currentTimeMillis();
        System.out.println((currentTime-time)*1000);
        return (currentTime-time)*1000;
    }

    @Override
    public void run() {
        timerTile.setValue(getTime());
    }
}

package op.visualization;

import eu.hansolo.tilesfx.Tile;
import javafx.application.Platform;

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
        return (currentTime-time);
    }

    @Override
    public void run() {
        Platform.runLater(()->{
            timerTile.setValue(getTime());
        });

    }
}

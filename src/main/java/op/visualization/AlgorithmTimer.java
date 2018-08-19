package op.visualization;

import eu.hansolo.tilesfx.Tile;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.TimerTask;

public class AlgorithmTimer extends TimerTask{
    private Label timerTile;
    private long time;

    public AlgorithmTimer(Label timerTile){
        time=System.currentTimeMillis();
        this.timerTile=timerTile;
    }

    private double getTime(){
        double currentTime=System.currentTimeMillis();
        return (currentTime-time)/1000.0;
    }

    @Override
    public void run() {
        Platform.runLater(()->{
            //timerTile.setUnit("ms");
            timerTile.setText(Double.toString(getTime()));
        });

    }
}

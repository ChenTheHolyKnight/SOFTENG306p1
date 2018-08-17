package op.visualization;

import eu.hansolo.tilesfx.Tile;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import op.algorithm.GreedyScheduler;
import op.visualization.controller.GUIController;
import op.visualization.messages.UpdateMessage;

import java.io.IOException;
import java.util.Timer;

/**
 * Interface that any visualisation implementation must conform to.
 * Defines how other components can control the visualisation
 */
public class Visualizer extends Application{

    private GUIController controller;
    private static final int SCENE_HEIGHT = 620;
    private int coreNum;

    /**
     * Starts the visualization GUI
     */
    public void startVisualization(String[] args) {
        launch(args);
    }

    /**
     * Starts the GUI application with JavaFX
     * @param stage
     * @throws Exception if there is an issue starting the application
     */
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(Visualizer.class.getResource("view/GUI.fxml"));
        Parent root=loader.load();
        Scene scene = new Scene(root);

        controller = loader.getController();
        //System.out.println(application.toString()+" core number "+coreNum);
        //controller.setCoreNum(coreNum);

        stage.setHeight(SCENE_HEIGHT);
        stage.setResizable(false);
        stage.centerOnScreen();

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Updates the visualisation state.
     * Can only be called after startVisualization() has been called.
     * @param u the update message containing the necessary information about which state should be changed
     */
    public void update(UpdateMessage u) {
        // run on JavaFX thread
        controller.updateGraph(u);
    }




    /**
     * Stops the timer
     */
    @Override
    public void stop(){
        controller.cancelTimer();
    }
}

package op.visualization;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import op.visualization.controller.GUIController;


/**
 * Interface that any visualisation implementation must conform to.
 * Defines how other components can control the visualisation
 */
public class Visualizer extends Application{

    private GUIController controller;
    private static final int SCENE_HEIGHT = 645;
    private static final int SCENE_WIDTH = 995;
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

        stage.setHeight(SCENE_HEIGHT);
        stage.setWidth(SCENE_WIDTH);
        stage.setResizable(false);
        stage.setTitle("Optimize Prime");
        stage.centerOnScreen();

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Stops the timer
     */
    @Override
    public void stop(){
        System.exit(1);
    }
}

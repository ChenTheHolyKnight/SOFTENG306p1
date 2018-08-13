package op.visualization;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import op.visualization.controller.GUIController;
import op.visualization.messages.UpdateMessage;

/**
 * Interface that any visualisation implementation must conform to.
 * Defines how other components can control the visualisation
 */
public class Visualizer extends Application {

    private GUIController controller;

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
        controller = loader.getController();
        Parent root=loader.load();
        Scene scene = new Scene(root);

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Updates the visualisation state.
     * Can only be called after startVisualization() has been called.
     * @param u the update message containing the necessary information about which state should be changed
     */
    public void update(UpdateMessage u) {
        controller.updateGraph(u);
    }
}

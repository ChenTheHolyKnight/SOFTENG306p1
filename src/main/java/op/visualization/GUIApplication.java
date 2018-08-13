package op.visualization;

import eu.hansolo.tilesfx.Tile;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import op.visualization.controller.GUIController;

import java.io.File;
import java.net.URL;
import java.util.Timer;

public class GUIApplication extends Application{
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(GUIApplication.class.getResource("view/GUI.fxml"));
        Parent root=loader.load();
        Scene scene = new Scene(root);

        GUIController controller=loader.getController();
        controller.setScene(scene);

        stage.setHeight(620);
        stage.setResizable(false);
        stage.centerOnScreen();
        //System.out.println(new SystemInfo().OsInfo());
        //new SystemInfo().printCPU();
        Timer timer = new Timer();
        Tile cpuTile=controller.getCPUTile();
        Tile memTile=controller.getMemoryTile();
        timer.schedule(new SystemInfo(cpuTile,memTile), 0, 100);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main method for testing GUI
     */
    public static void main(String[] args) {
        launch(args);
    }
}

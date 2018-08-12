package op.visualization;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class GUIApplication extends Application{
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(GUIApplication.class.getResource("View/GUI.fxml"));
        Parent root=loader.load();
        Scene scene = new Scene(root);



        stage.setResizable(true);

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

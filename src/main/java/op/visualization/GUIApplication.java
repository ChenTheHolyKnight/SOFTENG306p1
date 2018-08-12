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
        String location="file:/"+System.getProperty("user.dir")+"\\src\\main\\java\\op\\visualization\\View\\GUI.fxml";

        //System.out.println(location);
        //System.out.println(this.getClass().getResource("."));
        //FXMLLoader loader=new FXMLLoader(new URL(location));
        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(GUIApplication.class.getResource("View/GUI.fxml"));
        Parent root=loader.load();
        Scene scene = new Scene(root);



        stage.setResizable(true);

        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

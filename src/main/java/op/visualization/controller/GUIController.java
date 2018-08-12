package op.visualization.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import org.controlsfx.control.ToggleSwitch;

/**
 * The controller class to control the GUI*/
public class GUIController {
    @FXML
    public ToggleSwitch graphSwitch;

    @FXML
    public AnchorPane graphPane;

    @FXML
    public AnchorPane schedulePane;

    @FXML
    public TabPane tabPane;

    @FXML
    public Tab statsTab;

    private Scene scene;

    private boolean selected=false;




    /**
     * method to control the switch when the toggle switch is triggered
     */
    @FXML
    public void onSwitchTriggered(){
        boolean selected=this.graphSwitch.isSelected();
        if(!selected){
            FadeTransition fadeout = new FadeTransition(Duration.millis(500), schedulePane); //fade out schedulePane
            fadeout.setFromValue(1.0);
            fadeout.setToValue(0.0);
            fadeout.setOnFinished(event -> {
                fadeout.stop();
            });
            fadeout.playFromStart();
        }else {
            FadeTransition fadeout = new FadeTransition(Duration.millis(500), schedulePane); //fade in schedulePane
            fadeout.setFromValue(0.0);
            fadeout.setToValue(1.0);
            fadeout.setOnFinished(event -> {
                fadeout.stop();
            });
            fadeout.playFromStart();
        }

    }

    @FXML
    public void onTabClicked(){
        Translate translate=new Translate();
        Tab tab=tabPane.getSelectionModel().getSelectedItem();
        AnchorPane pane=(AnchorPane) tab.getContent();
        if(!selected) {
            selected=true;
            this.scene.getWindow().setWidth(this.scene.getWindow().getWidth() + tabPane.getWidth());


            translate.setX(pane.getWidth());

        }else{
            selected=false;
            this.scene.getWindow().setWidth(this.scene.getWindow().getWidth() - tabPane.getWidth());
            translate.setX(-pane.getWidth());
        }
        tabPane.getTransforms().add(translate);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    @FXML
    public void initialize(){
        schedulePane.setOpacity(0.0);
    }


}

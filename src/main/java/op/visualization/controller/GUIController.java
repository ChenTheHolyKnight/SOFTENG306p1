package op.visualization.controller;

import eu.hansolo.tilesfx.Tile;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import op.Application;
import op.visualization.messages.UpdateMessage;
import org.controlsfx.control.ToggleSwitch;
import org.graphstream.ui.fx_viewer.FxDefaultView;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.GraphRenderer;

/**
 * The controller class to control the GUI*/
public class GUIController {
    @FXML
    private ToggleSwitch graphSwitch;

    @FXML
    private AnchorPane graphPane;

    @FXML
    private AnchorPane schedulePane;

    @FXML
    private Tile cpuTile;

    @FXML
    private Tile memoryTile;

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

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    @FXML
    public void initialize(){
        schedulePane.setOpacity(0.0);
        embedGraph();
    }

    public Tile getCPUTile(){
        this.cpuTile.setSkinType(Tile.SkinType.BAR_GAUGE);
        return cpuTile;
    }

    public Tile getMemoryTile(){
        this.memoryTile.setSkinType(Tile.SkinType.BAR_GAUGE);
        return memoryTile;
    }

    /**
     * Embeds the visualization graph in the graph anchor pane
     */
    private void embedGraph() {
        GraphicGraph graph = GraphController.getInstance().getGraph();
        FxViewer viewer = new FxViewer(graph);
        GraphRenderer renderer = viewer.newDefaultGraphRenderer();
        FxDefaultView view = new FxDefaultView(viewer, "graph", renderer);
        graphPane.getChildren().add(view);
    }

    /**
     * Tells the graph component of the GUI to update
     * @param u
     */
    public void updateGraph(UpdateMessage u) {
        GraphController.getInstance().updateGraph(u);
        graphPane.requestLayout();
        graphPane.layout();
    }
}

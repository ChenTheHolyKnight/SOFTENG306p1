package op.visualization.controller;

import eu.hansolo.tilesfx.Tile;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import op.model.Schedule;
import op.model.ScheduledTask;
import op.model.Task;
import op.model.TaskGraph;
import op.visualization.GanttChart;
import op.Application;
import op.visualization.messages.UpdateMessage;
import org.controlsfx.control.ToggleSwitch;
import org.graphstream.ui.fx_viewer.FxDefaultView;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.GraphRenderer;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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


    //axis of the Gantt chart
    final NumberAxis xAxis = new NumberAxis();
    final CategoryAxis yAxis = new CategoryAxis();

    //the customized Gantt chart
    final GanttChart<Number,String> chart = new GanttChart<Number,String>(xAxis,yAxis);



    private HashMap<Integer,XYChart.Series> seriesHashMap=new HashMap<>();


    private Scene scene;

    private boolean selected=false;

    private int coreNum=5;

    private URL path;





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

    /**
     * initialize the controller
     */
    @FXML
    public void initialize(){
        schedulePane.setOpacity(0.0);
        embedGraph();
        System.out.println(this.getClass().getResource("../view/Styles/ganttchart.css"));
        initializeGanttChart();
    }

    /**
     * Get the CPU Tile with certain skin
     */
    public Tile getCPUTile(){
        this.cpuTile.setSkinType(Tile.SkinType.BAR_GAUGE);
        return cpuTile;
    }

    /**
     * Get the Memory Tile with certain skin
     */
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


    /**
     * Set number of cores in the controller
     * @param coreNum the number of cores.
     */
    public void setCoreNum(int coreNum){
        this.coreNum=coreNum;
    }

    /**
     * This is the method to initialize the Ganchart
     */
    private void initializeGanttChart(){
        List<String> processors=new ArrayList<>();
        for(int i=0;i<coreNum;i++){
            processors.add("Processor"+(i+1));
            seriesHashMap.put(i,new XYChart.Series());
        }

        //set up Axis and Chart
        xAxis.setLabel("");
        xAxis.setTickLabelFill(Color.CHOCOLATE); //need to change later on
        xAxis.setMinorTickCount(4);

        yAxis.setLabel("");
        yAxis.setTickLabelFill(Color.CHOCOLATE); //need to change later on
        yAxis.setTickLabelGap(10);
        yAxis.setCategories(FXCollections.<String>observableArrayList(processors));


        chart.setTitle("Machine Monitoring");
        chart.setLegendVisible(false);
        chart.setBlockHeight( 50);
        chart.setPrefHeight(schedulePane.getPrefHeight());
        chart.setPrefWidth(schedulePane.getPrefWidth());
        System.out.println("NOTFOUND "+this.getClass().getResource("../view/GUI.fxml"));
        chart.getStylesheets().add(this.getClass().getResource("../view/Styles/ganttchart.css").toExternalForm());

        //add chart to the pane
        schedulePane.getChildren().add(chart);

        Task task=new Task("1",2);
        ScheduledTask task1=new ScheduledTask(task,1,2);
        addScheduledTaskToChart(task1);


    }

    /**
     * The method to add the scheduled task to the Gantt chart
     */
    public void addScheduledTaskToChart(ScheduledTask task){
        int weight=task.getTask().getDuration();
        int processorNum=task.getProcessor();
        XYChart.Series series=seriesHashMap.get(processorNum-1);
        series.getData().add(new XYChart.Data(task.getStartTime(), yAxis.getCategories().get(processorNum-1),
                new GanttChart.ExtraData( weight, "status-blue")));
        chart.getData().add(series);

    }

    /**
     * set the css path
     */
    public void setPath(URL path){
        this.path=path;
    }
}

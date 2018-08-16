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
import javafx.scene.control.Label;
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
    
    @FXML 
    private Tile percentageTile;

    @FXML
    private Button startBtn;

    @FXML
    private Button pauseBtn;

    @FXML
    private Button stopBtn;

    @FXML
    private Label bestLength;

    @FXML
    private Label prunedTrees;

    @FXML
    private Label nodesVisisted;

    @FXML
    private Label scheduledTasks;



    //axis of the Gantt chart
    final NumberAxis xAxis = new NumberAxis();
    final CategoryAxis yAxis = new CategoryAxis();

    //the customized Gantt chart
    final GanttChart<Number,String> chart = new GanttChart<Number,String>(xAxis,yAxis);

    private HashMap<Integer,XYChart.Series> seriesHashMap=new HashMap<>();

    private int coreNum=5;

    private Application application;

    private Thread uiThread;

    /**
     * Method to control the start button
     */

    @FXML
    public void onStartBtnClicked(){
        /*uiThread.start();


        if(!uiThread.isAlive()){

        }*/
        this.setPercentageTile(3,1);
        this.setStats(1,2,3,4);
        stopBtn.setDisable(false);
        pauseBtn.setDisable(false);
        startBtn.setDisable(true);
    }

    @FXML
    public void onStopBtnClicked(){
        //uiThread.interrupt();
        stopBtn.setDisable(true);
        pauseBtn.setDisable(true);
        startBtn.setDisable(false);
    }

    @FXML
    public void onPauseBtnClicked(){
        this.setPercentageTile(2,1);
        this.setStats(5,6,7,8);
        pauseBtn.setDisable(true);
        startBtn.setDisable(false);
        /*try {
            uiThread.wait();
        } catch (InterruptedException e) {
            System.out.println("Wait fails");
        }*/
    }


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



    /**
     * initialize the controller
     */
    @FXML
    public void initialize(){

        uiThread=new Thread(()->{
            //uiThread for multithreading
        });
        schedulePane.setOpacity(0.0);
        embedGraph();
        //System.out.println(this.getClass().getResource("../view/Styles/ganttchart.css"));
        initializeGanttChart();
        stopBtn.setDisable(true);
        pauseBtn.setDisable(true);
        percentageTile.setSkinType(Tile.SkinType.BAR_GAUGE);
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


        chart.setTitle("Scheduling Visualization");
        chart.setLegendVisible(false);
        chart.setBlockHeight( 50);
        chart.setPrefHeight(schedulePane.getPrefHeight());
        chart.setPrefWidth(schedulePane.getPrefWidth());
        chart.getStylesheets().add("op/visualization/view/Styles/ganttchart.css");
        seriesHashMap.keySet().forEach(key->{
            chart.getData().add(seriesHashMap.get(key));
        });

        //add chart to the pane
        schedulePane.getChildren().add(chart);

        /*Task task=new Task("1",2);
        ScheduledTask task1=new ScheduledTask(task,1,2);
        addScheduledTaskToChart(task1);*/


    }

    /**
     * The helper method to add the scheduled task to the Gantt chart
     */
    private void addScheduledTaskToChart(ScheduledTask task){
        int weight=task.getTask().getDuration();
        int processorNum=task.getProcessor();
        XYChart.Series series=seriesHashMap.get(processorNum-1);
        series.getData().add(new XYChart.Data(task.getStartTime(), yAxis.getCategories().get(processorNum-1),
                new GanttChart.ExtraData( weight, "status-blue")));

        //chart.getData().add(series);
    }

    /**
     * The method to map the entire schedule to the Gantt chart
     */
    public void mapScheduleToGanttChart(Schedule schedule){
        clearGanttChart();
        List<ScheduledTask> scheduledTasks=schedule.getAllScheduledTasks();
        scheduledTasks.forEach(scheduledTask -> {
            addScheduledTaskToChart(scheduledTask);
        });
    }

    /**
     * The helper method to clear the gantt chart after each new shedule is passed
     */
    private void clearGanttChart(){
        chart.getData().forEach(series->{
            series.getData().clear();
        });
    }


    /**
     * The method needs to be called in the scheule
     */
    public void setPercentageTile(int totalTasks,int scheduledTasks){ ;
        double percentage=(double) scheduledTasks/(double) totalTasks*100;
        percentageTile.setValue(percentage);
    }


    /**
     * The method to get the stats ps: feel free to change the params
     */
    public void setStats(int stNum,int nvNum,int blNum,int ptNum){
        scheduledTasks.setText(Integer.toString(stNum));
        nodesVisisted.setText(Integer.toString(nvNum));
        bestLength.setText(Integer.toString(blNum));
        prunedTrees.setText(Integer.toString(ptNum));
    }



    public void setApplication(Application application) {
        this.application = application;
    }


}

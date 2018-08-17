package op.visualization.controller;

import eu.hansolo.tilesfx.Tile;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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
import op.algorithm.Scheduler;
import op.algorithm.SchedulerListener;
import op.model.Schedule;
import op.model.ScheduledTask;
import op.model.Task;
import op.model.TaskGraph;
import op.visualization.GanttChart;
import op.Application;
import op.visualization.VisualizerData;
import op.visualization.messages.UpdateMessage;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.control.action.Action;
import org.graphstream.ui.fx_viewer.FxDefaultView;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.GraphRenderer;
import scala.xml.Null;

import java.net.URL;
import java.nio.file.Path;
import java.util.*;

/**
 * The controller class to control the GUI*/
public class GUIController implements SchedulerListener {
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
    private int coreNum=8;

    private Application application;
    private Thread uiThread;

    private VisualizerData visualizerData;
    // GUI should know the current best so it knows when to update (if the value is changed)
    private int bestScheduleLength;

    /**
     * Method to control the start button
     */

    @FXML
    public void onStartBtnClicked(){
        /*uiThread.start();


        if(!uiThread.isAlive()){

        }*/
        //testing
        /*Schedule schedule=new Schedule();
        schedule.addScheduledTask(new ScheduledTask(new Task("1",20),1,5));
        schedule.addScheduledTask(new ScheduledTask(new Task("1",30),2,4));
        schedule.addScheduledTask(new ScheduledTask(new Task("1",40),3,3));
        mapScheduleToGanttChart(schedule);
        this.setPercentageTile(3,1); //testing
        this.setStats(1,2,3,4); // testing*/
        stopBtn.setDisable(false);
        pauseBtn.setDisable(false);
        startBtn.setDisable(true);
    }

    @FXML
    public void onStopBtnClicked(){
        /*Schedule schedule=new Schedule();
        schedule.addScheduledTask(new ScheduledTask(new Task("1",50),1,5));
        schedule.addScheduledTask(new ScheduledTask(new Task("1",30),2,4));
        mapScheduleToGanttChart(schedule);*/


        //uiThread.interrupt();
        stopBtn.setDisable(true);
        pauseBtn.setDisable(true);
        startBtn.setDisable(false);
    }

    @FXML
    public void onPauseBtnClicked(){
        /*this.setPercentageTile(2,1);    // testing
        this.setStats(5,6,7,8);   // testing*/
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
    public void initialize() {

        /*uiThread=new Thread(()->{
            //uiThread for multithreading
        });*/
        visualizerData = new VisualizerData();
        bestScheduleLength = Integer.MAX_VALUE;


        schedulePane.setOpacity(0.0);
        embedGraph();
        //System.out.println(this.getClass().getResource("../view/Styles/ganttchart.css"));
        initializeGanttChart();
        stopBtn.setDisable(true);
        pauseBtn.setDisable(true);
        percentageTile.setSkinType(Tile.SkinType.BAR_GAUGE);

        Application app = Application.getInstance();
        Scheduler s = app.getScheduler();
        s.addListener(visualizerData); // register the visualization data as a listener

        // start running algorithm
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
            private Schedule schedule;

            @Override
            protected Void call() {
                System.out.println("start");
                schedule = app.produceSchedule();
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                Application.getInstance().writeDot(schedule);
            }
        };
        new Thread(task).start();

        // arrange for controller to query visualization data instance often and update the gui
        // based on the data it reads
        Timeline updateCounters = new Timeline(
                new KeyFrame(Duration.millis(100), (ActionEvent ae) -> {
                    long numPrunedTrees = visualizerData.getNumPrunedTrees();
                    long numNodesVisited = visualizerData.getNumNodesVisited();
                    prunedTrees.setText(Long.toString(numPrunedTrees));
                    nodesVisisted.setText(Long.toString(numNodesVisited));
                }
         ));
        updateCounters.setCycleCount(Timeline.INDEFINITE);
        updateCounters.play();


        // best schedules update far slower than the counters, so update every half second
        Timeline updateBestSchedule = new Timeline(
                new KeyFrame(Duration.millis(500), (ActionEvent e) -> {
                    int newBestScheduleLength = visualizerData.getBestScheduleLength();

                    if (newBestScheduleLength != bestScheduleLength) {
                        // only update if the best schedule is different
                        bestScheduleLength = newBestScheduleLength;
                        Schedule newSchedule = visualizerData.getNewestSchedule();
                        bestLength.setText(Integer.toString(newBestScheduleLength));
                        mapScheduleToGanttChart(newSchedule);
                    }
                }
        ));
        updateBestSchedule.setCycleCount(Timeline.INDEFINITE);
        updateBestSchedule.play();
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

    @Override
    public void newSchedule(Schedule s) {
        System.out.println("new sched!");
        Platform.runLater(() -> {
            mapScheduleToGanttChart(s);
        });

    }

    @Override
    public void updateNumPrunedTrees(int numPrunedTrees) {
        Platform.runLater(() -> {
            prunedTrees.setText(Integer.toString(numPrunedTrees));
        });
    }

    @Override
    public void updateNodesVisited(int numNodesVisited) {
        Platform.runLater(() -> {
            nodesVisisted.setText(Integer.toString(numNodesVisited));
        });
    }

    @Override
    public void updateBestScheduleLength(int scheduleLength) {
        Platform.runLater(() -> {
            bestLength.setText(Integer.toString(scheduleLength));
        });
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
        series.getData().add(new XYChart.Data<Number, String>(task.getStartTime(), yAxis.getCategories().get(processorNum-1),
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
     * The method to get the stats ps: feel free to change the params
     */
    public void setStats(int stNum,int nvNum,int blNum,int ptNum){
        scheduledTasks.setText(Integer.toString(stNum));
        nodesVisisted.setText(Integer.toString(nvNum));
        bestLength.setText(Integer.toString(blNum));
        prunedTrees.setText(Integer.toString(ptNum));
    }



    /*public void setApplication(Application application) {
        this.application = application;
    }*/


}

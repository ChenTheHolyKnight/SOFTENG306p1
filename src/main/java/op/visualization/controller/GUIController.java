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
import op.visualization.SystemInfo;
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

    // Constants
    private final String GRAPH_IDENTIFIER = "graph";
    private final int GANTT_CHART_X_AXIS_MINOR_TICK_COUNT = 4;
    private final int GANTT_CHART_Y_AXIS_TICK_LABEL_GAP = 10;
    private final int GANTT_CHART_BLOCK_HEIGHT = 50;

    // XML elements
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
    private Label bestLength;

    @FXML
    private Label prunedTrees;

    @FXML
    private Label nodesVisisted;

    @FXML
    private Label scheduledTasks;

    // Gantt chart components
    final NumberAxis xAxis = new NumberAxis();
    final CategoryAxis yAxis = new CategoryAxis();
    final GanttChart<Number,String> chart = new GanttChart<>(xAxis,yAxis);
    private HashMap<Integer,XYChart.Series> seriesHashMap = new HashMap<>();
    private int coreNum = 8;

    private Timer timer;

    private VisualizerData visualizerData;

    /**
     * GUI should know the current best so it knows when to update (if the value is changed)
      */
    private int bestScheduleLength;

    /**
     * Initialize the GUI components
     */
    @FXML
    public void initialize() {

        visualizerData = new VisualizerData();
        bestScheduleLength = Integer.MAX_VALUE;

        embedGraph();
        initializeGanttChart();
        initializeMemoryAndCPUPolling();

        schedulePane.setOpacity(0.0);
        percentageTile.setSkinType(Tile.SkinType.BAR_GAUGE);

        registerVisualizationDataAsListener();
        startAlgorithm();
        initializeVisualizationDataUpdate();
        initializeBestScheduleUpdate();

    }

    /**
     * Allow the CPU and memory tiles to poll the system to find CPU and memory usage
     */
    private void initializeMemoryAndCPUPolling() {
        timer = new Timer();
        memoryTile.setSkinType(Tile.SkinType.BAR_GAUGE);
        cpuTile.setSkinType(Tile.SkinType.BAR_GAUGE);
        timer.schedule(new SystemInfo(cpuTile, memoryTile), 0, 100);
    }

    /**
     * Register the visualization data as a listener to the scheduler's events
     */
    private void registerVisualizationDataAsListener() {
        Application.getInstance().addSchedulerListener(visualizerData);
    }

    /**
     * Starts running the algorithm concurrently with the visualization
     */
    private void startAlgorithm() {
        Application.getInstance().startConcurrentAlgorithm();
    }

    /**
     * Arrange for controller to query visualization data instance often and update the gui based on the data it reads.
      */
    private void initializeVisualizationDataUpdate() {
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
    }

    /**
     * Best schedules update far slower than the counters, so update every half second.
     */
    private void initializeBestScheduleUpdate() {
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
     * When the toggle switch is triggered, switch display from the graph to the Gantt chart
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
        } else {
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
     * Embeds the visualization graph in the graph anchor pane
     */
    private void embedGraph() {
        GraphicGraph graph = GraphController.getInstance().getGraph();
        // TODO: Add the stuff to the graph
        FxViewer viewer = new FxViewer(graph);
        GraphRenderer renderer = viewer.newDefaultGraphRenderer();
        FxDefaultView view = new FxDefaultView(viewer, GRAPH_IDENTIFIER, renderer);
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
     * When there's a new schedule, map it to the Gantt chart
     * @param s the new schedule
     */
    @Override
    public void newSchedule(Schedule s) {
        Platform.runLater(() -> {
            mapScheduleToGanttChart(s);
        });

    }

    /**
     * Display the new number of possible schedules that spawn from a partial schedule that the algorithm is no longer
     * considering
     * @param numPrunedTrees number of partial schedules whose children will no longer be considered
     */
    @Override
    public void updateNumPrunedTrees(int numPrunedTrees) {
        Platform.runLater(() -> {
            prunedTrees.setText(Integer.toString(numPrunedTrees));
        });
    }

    /**
     * Display the new number of schedules considered
     * @param numNodesVisited number of schedules visited so far
     */
    @Override
    public void updateNodesVisited(int numNodesVisited) {
        Platform.runLater(() -> {
            nodesVisisted.setText(Integer.toString(numNodesVisited));
        });
    }

    /**
     * Update the best schedule length found so far
     * @param scheduleLength best schedule length so far
     */
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
     * Set up the Gantt chart display
     */
    private void initializeGanttChart(){

        // Set up Axis and Chart
        initializeGanttChartXAxis();
        initializeGanttChartYAxis();
        initializeGanttChartSettings();

        // Map data to the chart
        seriesHashMap.keySet().forEach(key->{
            chart.getData().add(seriesHashMap.get(key));
        });

        // Add chart to the pane
        schedulePane.getChildren().add(chart);
    }

    private void initializeGanttChartXAxis() {
        xAxis.setLabel("");
        xAxis.setTickLabelFill(Color.CHOCOLATE); //need to change later on
        xAxis.setMinorTickCount(GANTT_CHART_X_AXIS_MINOR_TICK_COUNT);
    }

    private void initializeGanttChartYAxis() {
        yAxis.setLabel("");
        yAxis.setTickLabelFill(Color.CHOCOLATE); //need to change later on
        yAxis.setTickLabelGap(GANTT_CHART_Y_AXIS_TICK_LABEL_GAP);

        // Set the y-axis categories
        List<String> processors = new ArrayList<>();
        for(int i = 0; i < coreNum; i++){
            processors.add("Processor" + (i+1));
            seriesHashMap.put(i, new XYChart.Series());
        }
        yAxis.setCategories(FXCollections.<String>observableArrayList(processors));
    }

    private void initializeGanttChartSettings() {
        chart.setTitle("Scheduling Visualization");
        chart.setLegendVisible(false);
        chart.setBlockHeight(GANTT_CHART_BLOCK_HEIGHT);
        chart.setPrefHeight(schedulePane.getPrefHeight());
        chart.setPrefWidth(schedulePane.getPrefWidth());
        chart.getStylesheets().add("op/visualization/view/Styles/ganttchart.css");
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

    /**
     * Stops the timer
     */
    public void cancelTimer() {
        timer.cancel();
    }


}

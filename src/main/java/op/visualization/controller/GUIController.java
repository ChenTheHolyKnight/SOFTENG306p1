package op.visualization.controller;

import com.sun.management.OperatingSystemMXBean;
import eu.hansolo.tilesfx.Tile;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import op.model.Schedule;
import op.model.ScheduledTask;
import op.visualization.GanttChart;
import op.Application;
import op.visualization.VisualizerData;
import org.controlsfx.control.ToggleSwitch;
import org.graphstream.ui.fx_viewer.FxDefaultView;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.GraphRenderer;

import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * The controller class to control the GUI*/
public class GUIController {

    // Constants
    private final String GRAPH_IDENTIFIER = "graph";
    private final int GANTT_CHART_X_AXIS_MINOR_TICK_COUNT = 4;
    private final int GANTT_CHART_Y_AXIS_TICK_LABEL_GAP = 10;
    private final int GANTT_CHART_GAP = 200;

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
    private Label percentageTile;

    @FXML
    private Label bestLength;

    @FXML
    private Label prunedTrees;

    @FXML
    private Label nodesVisited;

    @FXML
    private Button startButton;

    @FXML
    private  Label graphView;

    // Gantt chart components
    private final NumberAxis xAxis = new NumberAxis();
    private final CategoryAxis yAxis = new CategoryAxis();
    private final GanttChart<Number,String> chart = new GanttChart<>(xAxis,yAxis);
    private HashMap<Integer,XYChart.Series> seriesHashMap = new HashMap<>();
    private int coreNum = 1;

    // Time tile components
    private long time;
    private Timeline updateCounters;

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
        coreNum=Application.getInstance().getProcessNum();

        graphPane.setPrefSize(661,455);
        graphPane.setMaxSize(graphPane.getPrefWidth(),graphPane.getPrefHeight());
        graphPane.setMinSize(graphPane.getPrefWidth(),graphPane.getPrefHeight());
        embedGraph();
        initializeGanttChart();
        initializeMemoryAndCPUPolling();

        schedulePane.setOpacity(0.0);
        graphPane.setOpacity(1.0);

        registerVisualizationDataAsListener();
        initializeVisualizationDataUpdate();
        initializeBestScheduleUpdate();

    }

    /**
     * Allow the CPU and memory tiles to poll the system to find CPU and memory usage
     */
    private void initializeMemoryAndCPUPolling() {

        // Style the memory and CPU tiles
        memoryTile.setSkinType(Tile.SkinType.BAR_GAUGE);
        cpuTile.setSkinType(Tile.SkinType.BAR_GAUGE);

        // Poll memory and CPU usage and display on the tiles
        Timeline updateCounters = new Timeline(
                new KeyFrame(Duration.millis(100), (ActionEvent ae) -> {
                    cpuTile.setUnit("%");
                    cpuTile.setValue(this.getCPU());
                    if(memoryTile.getUnit()!=null)
                        memoryTile.setUnit("%");
                    memoryTile.setValue(this.getMemory());
                }
                ));
        updateCounters.setCycleCount(Timeline.INDEFINITE);
        updateCounters.play();
    }

    /**
     * A helper method for initializeMemoryAndCPUPolling().
     * @return the current memory usage of the system
     */
    private double getMemory() {
        OperatingSystemMXBean operatingSystemMXBean=
                (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double free=(double)operatingSystemMXBean.getFreePhysicalMemorySize();
        double total=(double) operatingSystemMXBean.getTotalPhysicalMemorySize();
        return (total-free)/total*100;
    }

    /**
     * A helper method for initializeMemoryAndCPUPolling().
     * @return the current CPU usage of the system
     */
    private double getCPU(){
        OperatingSystemMXBean operatingSystemMXBean =
                (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        return operatingSystemMXBean.getProcessCpuLoad()*100;
    }

    /**
     * Register the visualization data as a listener to the scheduler's events
     */
    private void registerVisualizationDataAsListener() {
        Application.getInstance().addSchedulerListener(visualizerData);
    }

    /**
     * Starts running the algorithm concurrently with the visualization. The timer tile keeps record of time since
     * the algorithm started running.
     */
    @FXML
    private void initializePercentageTile() {
        time=System.currentTimeMillis();
        updateCounters = new Timeline(
                new KeyFrame(Duration.millis(100), (ActionEvent ae) -> percentageTile.setText(Double.toString(getTime()) + " s")
                ));
        updateCounters.setCycleCount(Timeline.INDEFINITE);
        updateCounters.play();
        Application.getInstance().startConcurrentAlgorithm();
    }

    /**
     * A helper method for the timer tile.
     * @return time in seconds since the algorithm started running
     */
    private double getTime(){
        double currentTime=System.currentTimeMillis()-time;
        double timeInTenthSecs = Math.round(currentTime/10.0);
        return timeInTenthSecs/100.0;
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
                    nodesVisited.setText(Long.toString(numNodesVisited));
                    if (visualizerData.getOptimalScheduleFound()) {
                        optimalScheduleFound();
                    }
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

    @FXML
    public void hideGraphLabel(){
        FadeTransition fade = new FadeTransition(Duration.millis(500), graphView);
        fade.setFromValue(1.0);
        fade.setToValue( 0.0);
        Platform.runLater(() -> {
            fade.setOnFinished(event -> fade.stop());
            fade.playFromStart();
        });
        graphView.setVisible(false);
    }

    /**
     * When the toggle switch is triggered, switch display from the graph to the Gantt chart
     */
    @FXML
    public void onSwitchTriggered(){
        boolean selected=this.graphSwitch.isSelected();
        if(!selected){
            // Fade out schedule pane and fade in graph pane
            fadeTransition(false, schedulePane);
            fadeTransition(true, graphPane);
        } else {
            // Fade in schedule pane and fade out graph pane
            fadeTransition(true, schedulePane);
            fadeTransition(false, graphPane);
        }

    }

    /**
     * When the 'start algorithm' button is clicked, the algorithm is started.
     */
    @FXML
    public void startButtonClicked() {
        startButton.setDisable(true);
        initializePercentageTile();
    }

    /**
     * Fades a pane in or out
     * @param toFadeIn true to fade the pane in, false otherwise
     * @param pane the pane to fade
     */
    private void fadeTransition(boolean toFadeIn, Pane pane) {
        FadeTransition fade = new FadeTransition(Duration.millis(500), pane);
        fade.setFromValue(toFadeIn? 0.0: 1.0);
        fade.setToValue(toFadeIn? 1.0: 0.0);
        Platform.runLater(() -> {
            fade.setOnFinished(event -> fade.stop());
            fade.playFromStart();
        });
    }

    /**
     * Embeds the visualization graph in the graph anchor pane
     */
    private void embedGraph(){
        GraphController.getInstance().initializeGraph();
        GraphicGraph graph = GraphController.getInstance().getGraph();
        FxViewer viewer = new FxViewer(graph);
        viewer.enableAutoLayout();
        GraphRenderer renderer = viewer.newDefaultGraphRenderer();
        FxViewPanel view = new FxDefaultView(viewer, GRAPH_IDENTIFIER, renderer);
        view.setMaxSize(graphPane.getMaxWidth(),graphPane.getMaxHeight());
        view.setMinSize(graphPane.getMaxWidth(),graphPane.getMaxHeight());
        view.setPrefSize(graphPane.getMaxWidth(),graphPane.getMaxHeight());
        graphPane.getChildren().add(view);
        graphView = new Label("Click to show graph");
        graphPane.getChildren().add(graphView);
        graphView.setFont(new Font(24));

    }

    /**
     * Stop the schedule timer
     */
    public void optimalScheduleFound() {
        Platform.runLater(() ->
                updateCounters.stop());
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
        seriesHashMap.keySet().forEach(key-> chart.getData().add(seriesHashMap.get(key)));

        // Add chart to the pane
        schedulePane.getChildren().add(chart);
    }

    private void initializeGanttChartXAxis() {
        xAxis.setLabel("Time");
        xAxis.setMinorTickCount(GANTT_CHART_X_AXIS_MINOR_TICK_COUNT);
    }

    private void initializeGanttChartYAxis() {
        yAxis.setTickLabelGap(GANTT_CHART_Y_AXIS_TICK_LABEL_GAP);

        // Set the y-axis categories
        List<String> processors = new ArrayList<>();
        for(int i = 0; i < coreNum; i++){
            processors.add("Processor " + (i+1));
            seriesHashMap.put(i, new XYChart.Series());
        }
        yAxis.setCategories(FXCollections.observableArrayList(processors));
    }

    private void initializeGanttChartSettings() {
        chart.setTitle("Gantt Chart");
        chart.setLegendVisible(false);
        chart.setBlockHeight((schedulePane.getPrefHeight()-GANTT_CHART_GAP)/coreNum);
        chart.setPrefHeight(schedulePane.getPrefHeight());
        chart.setPrefWidth(schedulePane.getPrefWidth());
        chart.getStylesheets().add("op/visualization/controller/ganttchart.css");
    }

    /**
     * The helper method to add the scheduled task to the Gantt chart
     */
    private void addScheduledTaskToChart(ScheduledTask task){
        int weight=task.getTask().getDuration();
        String id = task.getTask().getId();
        int processorNum=task.getProcessor();
        XYChart.Series series=seriesHashMap.get(processorNum-1);
        series.getData().add(new XYChart.Data<Number, String>(task.getStartTime(), yAxis.getCategories().get(processorNum-1),
                new GanttChart.ExtraData(id, weight, "status-blue")));
    }

    /**
     * The method to map the entire schedule to the Gantt chart
     */
    public void mapScheduleToGanttChart(Schedule schedule){
        clearGanttChart();
        List<ScheduledTask> scheduledTasks=schedule.getAllScheduledTasks();
        scheduledTasks.forEach(this::addScheduledTaskToChart);
    }

    /**
     * The helper method to clear the gantt chart after each new shedule is passed
     */
    private void clearGanttChart(){
        chart.getData().forEach(series-> series.getData().clear());
    }
}

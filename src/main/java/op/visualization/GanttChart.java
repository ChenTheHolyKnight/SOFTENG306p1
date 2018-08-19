package op.visualization;

import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @param <X>
 * @param <Y>
 */
public class GanttChart<X, Y> extends XYChart<X,Y> {
    /**
     * Nested class representing extra data contained in a GanttChart
     */
    public static class ExtraData {

        private String id;
        private long length;
        private String styleClass;

        public ExtraData(String id, long lengthMs, String styleClass) {
            this.id = id;
            this.length = lengthMs;
            this.styleClass = styleClass;
        }
        public long getLength() {
            return length;
        }
        public String getStyleClass() {
            return styleClass;
        }
        public String getId() {return id; }

    }

    private double blockHeight = 10;

    /**
     * Constructor for a new Gantt chart.
     * @param xAxis What will go on horizontal axis (e.g. length)
     * @param yAxis What will go on vertical axis (e.g. a category such as processors)
     */
    public GanttChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis) {
        this(xAxis, yAxis, FXCollections.<Series<X, Y>>observableArrayList());
    }

    /**
     * Constructor for a new Gantt chart including already specified d
     * @param xAxis What will go on horizontal axis (e.g. length)
     * @param yAxis What will go on vertical axis (e.g. a category such as processors)
     * @param data What will go on the Gantt chart itself.
     */
    public GanttChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis, @NamedArg("data")
            ObservableList<Series<X,Y>> data) {
        super(xAxis, yAxis);
        if (!(xAxis instanceof ValueAxis && yAxis instanceof CategoryAxis)) {
            throw new IllegalArgumentException("Axis type incorrect, X and Y should both be NumberAxis");
        }
        setData(data);
    }

    /**
     * Helper method that gets the style class of a given object
     * @param obj object to get style class of
     * @return A string representing the style class
     */
    private static String getStyleClass( Object obj) {
        return ((ExtraData) obj).getStyleClass();
    }

    /**
     * Helper method that gets the style class of a given object
     * @param data object to get style class of
     * @return A double representing the length
     */
    private static double getLength( ExtraData data) {
        return data.getLength();
    }

    /**
     * Helper method to return the ID of given data
     * @param data of which the ID is to come from
     * @return String representing the ID
     */
    private static String getId(ExtraData data) {
        return data.getId();
    }

    /**
     * The main method of the Gantt Chart class. Lays out all plot children (Tasks)
     * onto the Gantt Chart.
     */
    @Override
    protected void layoutPlotChildren() {

        for (int seriesIndex=0; seriesIndex < getData().size(); seriesIndex++) {

            Series<X,Y> series = getData().get(seriesIndex);

            Iterator<Data<X,Y>> iter = getDisplayedDataIterator(series);
            while(iter.hasNext()) {

                Data<X,Y> item = iter.next(); // get the next item to plot

                double x = getXAxis().getDisplayPosition(item.getXValue()); // get the X and Y coordinates
                double y = getYAxis().getDisplayPosition(item.getYValue()); // of the item

                if (Double.isNaN(x) || Double.isNaN(y)) {
                    continue; // not a valid item, skip it
                }

                Node block = item.getNode();

                Rectangle rectangle;
                double width = getLength((ExtraData) item.getExtraValue()) * ((getXAxis() instanceof NumberAxis)
                        ? Math.abs(((NumberAxis)getXAxis()).getScale()) : 1);

                if (block != null) { // check the block is a valid block
                    if (block instanceof StackPane) {

                        StackPane region = (StackPane)item.getNode();

                        if (region.getShape() == null) { // if there is not yet a shape in the StackPane
                            rectangle = new Rectangle( getLength((ExtraData) item.getExtraValue()), getBlockHeight());
                        }
                        else if (region.getShape() instanceof Rectangle) {
                            rectangle = (Rectangle)region.getShape(); // there is a shape, get it
                        }
                        else {
                            return;
                        }

                        if (region.getChildren().isEmpty()) {
                            // add the Task ID to the region
                            Text id = new Text(" " + getId((ExtraData) item.getExtraValue()));
                            region.getChildren().add(id);
                            region.setAlignment(Pos.TOP_LEFT);
                        }

                        rectangle.setWidth(width); // set width
                        rectangle.setHeight(getBlockHeight() * ((getYAxis() instanceof NumberAxis) ? // calculate height
                                Math.abs(((NumberAxis)getYAxis()).getScale()) : 1)); // based on number of processors.
                        y -= getBlockHeight() / 2.0;

                        setRegionParameters(region, rectangle);

                        block.setLayoutX(x);
                        block.setLayoutY(y);
                    }
                }
            }
        }
    }

    /**
     * helper method to get block height
     * @return Block height
     */
    public double getBlockHeight() {
        return blockHeight;
    }

    /**
     * helper method to set block height
     * @param blockHeight new block height
     */
    public void setBlockHeight( double blockHeight) {
        this.blockHeight = blockHeight;
    }

    /**
     * Add a new node to the plot
     * @param series Series
     * @param itemIndex Index of item to be added
     * @param item Data to be added to node
     */
    @Override
    protected void dataItemAdded(Series<X,Y> series, int itemIndex, Data<X,Y> item) {
        Node block = createContainer(series, getData().indexOf(series), item, itemIndex);
        getPlotChildren().add(block);
    }

    /**
     * Remove the given Data item from a given Series.
     * @param item Data to be removed
     * @param series Series to remove Item from
     */
    @Override
    protected  void dataItemRemoved(final Data<X,Y> item, final Series<X,Y> series) {
        final Node block = item.getNode();
        getPlotChildren().remove(block);
        removeDataItemFromDisplay(series, item);
    }

    /**
     * Override existing method to prevent unwanted behaviour.
     * @param item item to be change
     */
    @Override
    protected void dataItemChanged(Data<X, Y> item) {}

    /**
     * add a Series to the display
     * @param series Series to be added
     * @param seriesIndex Index of the Series
     */
    @Override
    protected  void seriesAdded(Series<X,Y> series, int seriesIndex) {
        for (int j=0; j<series.getData().size(); j++) {
            Data<X,Y> item = series.getData().get(j);
            Node container = createContainer(series, seriesIndex, item, j);
            getPlotChildren().add(container);
        }
    }

    /**
     * Remove a Series from the Display
     * @param series Series to be removed
     */
    @Override
    protected  void seriesRemoved(final Series<X,Y> series) {
        for (Data<X,Y> d : series.getData()) {
            final Node container = d.getNode();
            getPlotChildren().remove(container);
        }
        removeSeriesFromDisplay(series);

    }

    /**
     * Create a new container
     * @param series a given series
     * @param seriesIndex index of series
     * @param item item to place node into
     * @param itemIndex position of the item
     * @return a new container
     */
    private Node createContainer(Series<X, Y> series, int seriesIndex, final Data<X,Y> item, int itemIndex) {

        Node container = item.getNode();

        if (container == null) {
            container = new StackPane();
            item.setNode(container);
        }

        container.getStyleClass().add( getStyleClass( item.getExtraValue()));

        return container;
    }

    /**
     * Update the axis range of the Gantt chart if the schedule length exceeds what is shown.
     */
    @Override
    protected void updateAxisRange() {
        final Axis<X> xa = getXAxis();
        final Axis<Y> ya = getYAxis();

        List<X> xData = null;
        List<Y> yData = null;
        if(xa.isAutoRanging()) xData = new ArrayList<>();
        if(ya.isAutoRanging()) yData = new ArrayList<>();

        if(xData != null || yData != null) {
            for(Series<X,Y> series : getData()) {
                for(Data<X,Y> data: series.getData()) {
                    if(xData != null) {
                        xData.add(data.getXValue());
                        xData.add(xa.toRealValue(xa.toNumericValue(data.getXValue()) +
                                getLength((ExtraData)data.getExtraValue())));
                    }
                    if(yData != null){
                        yData.add(data.getYValue());
                    }
                }
            }

            if(xData != null) xa.invalidateRange(xData);
            if(yData != null) ya.invalidateRange(yData);
        }
    }

    /**
     * helper function to set the parameters of a StackPane
     * @param region the StackPane
     * @param ellipse shape to be placed in StackPane
     */
    private void setRegionParameters(StackPane region, Rectangle ellipse){
        region.setShape(null);
        region.setShape(ellipse);
        region.setScaleShape(false);
        region.setCenterShape(false);
        region.setCacheShape(false);
    }
}

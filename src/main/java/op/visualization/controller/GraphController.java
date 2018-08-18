package op.visualization.controller;

import op.model.TaskGraphToStringConverter;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphicGraph;

import java.util.*;

/**
 * A singleton class representing the display for the Graphstream graph.
 * 
 * @author Ravid
 *
 */
public class GraphController {
	
	private static final String LABEL = "ui.label";
	private static final int X_MIN = -100;
	private static final int X_MAX = 100;
    private static final int Y_START = -50;
    private static final int Y_INCREMENT = 15;
    private int y_count = 0;

	private static GraphController instance = new GraphController();
	private GraphicGraph graph;
	private Random random;
	
	private GraphController () {
		graph =  new GraphicGraph("graph");
		graph.setAttribute("ui.stylesheet", GRAPH_DISPLAY_STYLESHEET);
		random = new Random();
	}

	/**
	 * Gets the singleton instance of GraphController
	 * 
	 * @return the singleton instance of GraphController
	 */
	public static GraphController getInstance(){
		return instance;
	}

    /**
     * Sets up the graph to display the input task graph
     */
	public void initializeGraph() {
        TaskGraphToStringConverter converter = new TaskGraphToStringConverter();
        addNodes(converter.createNodes());
        addEdges(converter.createEdges());
    }

    /**
     * Adds nodes to the graph
     * @param nodeIds IDs of nodes to add
     */
    private void addNodes(Set<String> nodeIds) {
        for (String nodeId: nodeIds) {
            // Add the child node if it does not exist
            if (graph.getNode(nodeId) == null) {
                placeNode(nodeId);
            }
        }
    }

    /**
     * Adds edges to the graph
     * @param edges a map of nodes to a list of their child nodes
     */
    private void addEdges(Map<String, List<String>> edges) {
        for (Map.Entry<String, List<String>> edge: edges.entrySet()) {
            for (String child: edge.getValue()) {
                // Add the edge if it does not exist
                if (graph.getEdge(createEdgeId(edge.getKey(), child)) == null) {
                    graph.addEdge(createEdgeId(edge.getKey(), child), edge.getKey(), child, true);
                }
            }
        }
    }

    /**
     * Produces an ID for an edge between two nodes
     * @param sourceNodeId ID of the source node
     * @param targetNodeId ID of the target node
     * @return the edge ID
     */
    private String createEdgeId(String sourceNodeId, String targetNodeId) {
	    return sourceNodeId + ":" + targetNodeId;
    }

    /**
     * Adds a new node to the graph by choosing co-ordinates for the node
     * @param nodeId
     */
    private void placeNode(String nodeId) {
        graph.addNode(nodeId);
        graph.getNode(nodeId).setAttribute(LABEL, nodeId);
        graph.getNode(nodeId).setAttribute("x", newNodeXPosition());
        graph.getNode(nodeId).setAttribute("y", newNodeYPosition());
    }

    /**
     * Finds an x co-ordinate to place a new node on the graph
     * @return x co-ordinate
     */
    private double newNodeXPosition() {

        return random.nextDouble()*(X_MAX - X_MIN) + X_MIN;
    }

    /**
     * Finds a y co-ordinate to place a new node on the graph
     * @return y co-ordinate
     */
    private double newNodeYPosition() {

        y_count++;
        return Y_START + Y_INCREMENT*y_count;
    }

    /**
     * @return a reference to the visualization graph
     */
	public GraphicGraph getGraph() {
	    return graph;
    }
	
	private static final String GRAPH_DISPLAY_STYLESHEET =
            "graph {"
                + "fill-color: #00003E, #000000;"
                    + "fill-mode: gradient-vertical;"
            + "}"

            + "node {"
                + "fill-color: #EEEEEE, #404070;"
                    + "fill-mode: gradient-radial;"
                    + "shadow-color: #5F5F87, rgba(0, 0, 0, 0);"
                    + "shadow-mode: gradient-radial;"
                    + "shadow-width: 8;"
                    + "shadow-offset: 0;"
                    + "text-alignment: left; "
                    + "text-color: white;"
                    + "text-style: bold;"
                    + "text-background-mode: rounded-box; "
                    + "text-background-color: rgba(80, 80, 80, 255);"
                    + "text-padding: 5px, 4px; "
                    + "text-offset: -12px, 0px;"
                    + "text-size: 16;"
            + "}"

                    + "edge {"
                        + "fill-color: rgb(64, 64, 112);"
                        + "fill-mode: plain;"
                        + "shadow-color: rgba(64, 64, 112, 128), rgba(0, 0, 0, 0);"
                        + "shadow-mode: gradient-horizontal;"
                        + "shadow-width: 1;"
                        + "shadow-offset: 0;"
                        + "arrow-shape: arrow;"
                        + "arrow-size: 10px, 10px;"
                    + "}"

            + "node.eliminated { "
			    + "shape: cross; "
                    + "fill-color: #CCCCCC, #704040;"
                    + "shadow-color: #875F5F, rgba(0, 0, 0, 0);"
		    + "}"
				
		    + "edge.eliminated { "
			    + "fill-color: rgba(112, 64, 64, 128);"
                    + "shadow-color: rgba(112, 64, 64, 128), rgba(0, 0, 0, 0);"
		    + " }"

            + "node.optimal {"
                    + "fill-color: #CCCCCC, #407040;"
                    + "shadow-color: #5F875F, rgba(0, 0, 0, 0);"
                    + "size: 15px, 15px;"
            + "}"

            + "edge.optimal {"
                    + "fill-color: rgba(64, 112, 64, 128);"
                    + "shadow-color: rgba(64, 112, 64, 128), rgba(0, 0, 0, 0);"
            + "}";
	
}

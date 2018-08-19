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
        //graph.getNode(nodeId).setAttribute("layout.frozen");
        //graph.getNode(nodeId).setAttribute("x", newNodeXPosition());
        //graph.getNode(nodeId).setAttribute("y", newNodeYPosition());
    }

    /**
     * @return a reference to the visualization graph
     */
	public GraphicGraph getGraph() {
	    return graph;
    }
	
	private static final String GRAPH_DISPLAY_STYLESHEET =
            "graph {"
                + "fill-color: #FFFFFF;"
            + "}"

            + "node {"
                + "fill-color: #37b3fc;"
                    + "text-color: #FFFFFF;"
                    + "text-style: bold;"
                    + "text-size: 16;"
                    + "size: 25px, 25px;"
            + "}"
                    + "edge {"
                        + "fill-color: #3A3A3A;"
                        + "arrow-shape: arrow;"
                        + "arrow-size: 10px, 6px;"
                    + "}";
}

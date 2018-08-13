package op.visualization.controller;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.Viewer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A singleton class representing the display for the Graphstream graph.
 * 
 * @author Ravid
 *
 */
public class GraphController {
	
	private static final String STYLE_CLASS = "ui.class";
	private static GraphController instance = new GraphController();
	private GraphicGraph graph;
	
	private GraphController () {
		graph =  new GraphicGraph("graph");
		graph.setAttribute("ui.stylesheet", GRAPH_DISPLAY_STYLESHEET);
	}
	
	/**
	 * Gets the singleton instance of GraphDisplay
	 * 
	 * @return the singleton instance of GraphDisplay
	 */
	public static GraphController getInstance(){
		return instance;
	}
	
	/**
	 * Adds a node to the graph visualisation
	 * 
	 * @param newNodeId the Id of the node to add
	 */
	public void addNode(String parentNodeId, String newNodeId) {
		if (graph.getNode(parentNodeId) == null) {
			graph.addNode(parentNodeId);
		}
		if (graph.getNode(newNodeId) == null) {
			graph.addNode(newNodeId);
		}
		
		graph.addEdge(parentNodeId + newNodeId, parentNodeId, newNodeId, true);
	}
 
	public void eliminateChildren (String nodeId) {
		Node n = graph.getNode(nodeId);
		n.setAttribute(STYLE_CLASS, "eliminated");
		for (Edge e : n.leavingEdges().collect(Collectors.toList())){
			e.setAttribute(STYLE_CLASS, "eliminated");
			eliminateChildren(e.getTargetNode().getId());
		}
	}
	
	public void update() {
		
	}

    /**
     * @return a reference to the visualization graph
     */
	public GraphicGraph getGraph() {
	    return graph;
    }
	
	private static final String GRAPH_DISPLAY_STYLESHEET =
		"node.eliminated { "
			+ "shape: cross; "
			+ "fill-color: red; "
		+ "}"
				
		+ "edge.eliminated { "
			+ "fill-color: red;"
		+ " }";
	
}

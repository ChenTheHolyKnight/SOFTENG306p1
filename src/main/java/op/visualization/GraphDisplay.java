package op.visualization;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

/**
 * A singleton class representing the display for the Graphstream graph.
 * 
 * @author Ravid
 *
 */
public class GraphDisplay {
	
	private static final String STYLE_CLASS = "ui.class";
	
	private static GraphDisplay instance = new GraphDisplay();
	
	private Graph graph; 
	
	private GraphDisplay () {
		graph =  new SingleGraph("graph");
		graph.addAttribute("ui.stylesheet", GRAPH_DISPLAY_STYLESHEET);
		Viewer viewer = graph.display();
	}
	
	/**
	 * Get's the singleton instance of GraphDisplay
	 * 
	 * @return the singleton instance of GraphDisplay
	 */
	public static GraphDisplay getInstance(){
		return instance;
	}
	
	/**
	 * Adds a node to the graph visualisation
	 * 
	 * @param nodeId the Id of the node to add
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
		n.addAttribute(STYLE_CLASS, "eliminated");		
		for (Edge e : n.getEachLeavingEdge()){
			e.addAttribute(STYLE_CLASS, "eliminated");
			eliminateChildren(e.getTargetNode().getId());
		}
	}
	
	public void update() {
		
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

package op.visualization.controller;

import op.visualization.messages.MessageAddNodes;
import op.visualization.messages.MessageEliminateNodes;
import op.visualization.messages.MessageSetOptimalSolution;
import op.visualization.messages.UpdateMessage;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A singleton class representing the display for the Graphstream graph.
 * 
 * @author Ravid
 *
 */
public class GraphController {
	
	private static final String STYLE_CLASS = "ui.class";
	private static int Y_BOUND = 90;
	private static int X_BOUND = 90;

	private static GraphController instance = new GraphController();
	private GraphicGraph graph;
	private Random random;
	
	private GraphController () {
		graph =  new GraphicGraph("graph");
		graph.setAttribute("ui.stylesheet", GRAPH_DISPLAY_STYLESHEET);
		random = new Random();
		//addNodes();
	}

    /**
     * Test method. To be moved to test class.
     */
	/*private void addNodes() {

	    for (int i = 0; i < 20; i++){
            placeNode(""+i);
        }
        List<String> children = new ArrayList<>();
	    children.add("2");
        children.add("3");
        children.add("4");
        children.add("5");
        children.add("6");
        addNodes("1", children);

        List<String> children2 = new ArrayList<>();
        children2.add("7");
        children2.add("8");
        children2.add("9");
        children2.add("10");
        children2.add("11");
        addNodes("6", children2);

        List<String> children3 = new ArrayList<>();
        children3.add("12");
        children3.add("13");
        children3.add("14");
        children3.add("15");
        children3.add("16");
        addNodes("11", children3);

        eliminateChildren("11");
    }*/
	
	/**
	 * Gets the singleton instance of GraphController
	 * 
	 * @return the singleton instance of GraphController
	 */
	public static GraphController getInstance(){
		return instance;
	}

    /**
     * Reads an update message to determine how to update the graph
     * @param u
     */
    public void updateGraph(UpdateMessage u) {
        if (u instanceof MessageAddNodes) {
            addNodes(((MessageAddNodes) u).getParentNodeId(), ((MessageAddNodes) u).getChildNodeIds());
        }
        if (u instanceof MessageEliminateNodes) {
            eliminateNodes(((MessageEliminateNodes) u).getNodeIds());
        }
        if (u instanceof MessageSetOptimalSolution) {
            setOptimalSolution(((MessageSetOptimalSolution) u).getOptimalSolution());
        }
    }
	
	/**
	 * Adds new nodes to the graph visualisation
	 *
     * @param parentNodeId the ID of the parent node to add
	 * @param childNodeIds the Id of the children nodes to add; can be null if the node has no children
	 */
	private void addNodes(String parentNodeId, Set<String> childNodeIds) {
		if (graph.getNode(parentNodeId) == null) {
		    // Add the parent node if it does not exist
            placeNode(parentNodeId);
		}
		if (childNodeIds != null) {
            for (String childNodeId: childNodeIds) {
                // Add the child node if it does not exist
                if (graph.getNode(childNodeId) == null) {
                    placeNode(childNodeId);
                }
                // Add the edge if it does not exist
                if (graph.getEdge(createEdgeId(parentNodeId, childNodeId)) == null) {
                    graph.addEdge(createEdgeId(parentNodeId, childNodeId), parentNodeId, childNodeId, true);
                }
            }
        }
	}

    /**
     * Removes the given nodes from the solution tree
     * @param nodeIds nodes to remove
     */
    private void eliminateNodes (Set<String> nodeIds) {
        for (String nodeId: nodeIds) {
            Node n = graph.getNode(nodeId);
            n.setAttribute(STYLE_CLASS, "eliminated");
            for (Edge e : n.leavingEdges().collect(Collectors.toList())){
                if (nodeIds.contains(e.getTargetNode().getId())) {
                    e.setAttribute(STYLE_CLASS, "eliminated");
                }
            }
        }
    }

    /**
     * Marks the given node and all its subschedules as the optimal solution
     * @param nodeId the nodes that represents the optimal solution
     */
	private void setOptimalSolution(String nodeId) {
        Node n = graph.getNode(nodeId);
        n.setAttribute(STYLE_CLASS, "optimal");
        for (Edge e : n.enteringEdges().collect(Collectors.toList())){
            e.setAttribute(STYLE_CLASS, "optimal");
            setOptimalSolution(e.getTargetNode().getId());
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
        graph.getNode(nodeId).setAttribute("x", newNodeXPosition());
        graph.getNode(nodeId).setAttribute("y", newNodeYPosition());
    }

    /**
     * Finds an x co-ordinate to place a new node on the graph
     * @return x co-ordinate
     */
    private int newNodeXPosition() {
        return random.nextInt(X_BOUND);
    }

    /**
     * Finds a y co-ordinate to place a new node on the graph
     * @return y co-ordinate
     */
    private int newNodeYPosition() {
        return random.nextInt(Y_BOUND);
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
                + "fill-color: #CCCCCC, #404070;"
                    + "fill-mode: gradient-radial;"
                    + "shadow-color: #5F5F87, rgba(0, 0, 0, 0);"
                    + "shadow-mode: gradient-radial;"
                    + "shadow-width: 8;"
                    + "shadow-offset: 0;"
            + "}"

                    + "edge {"
                        + "fill-color: rgba(64, 64, 112, 128);"
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

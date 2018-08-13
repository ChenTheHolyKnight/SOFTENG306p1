package op.visualization.controller;

import op.visualization.messages.MessageAddNodes;
import op.visualization.messages.MessageEliminateChildren;
import op.visualization.messages.MessageSetOptimalSolution;
import op.visualization.messages.UpdateMessage;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.view.Viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
		addNodes();
	}

    /**
     * Test method. To be moved to test class.
     */
	private void addNodes() {
	    Random random = new Random();

	    for (int i = 0; i < 20; i++){
            graph.addNode(""+i);
            graph.getNode(""+i).setAttribute("x", random.nextInt(90));
            graph.getNode(""+i).setAttribute("y", 100-5*i);
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
     * Reads an update message to determine how to update the graph
     * @param u
     */
    public void updateGraph(UpdateMessage u) {
        if (u instanceof MessageAddNodes) {
            addNodes(((MessageAddNodes) u).getParentNodeId(), ((MessageAddNodes) u).getChildNodeIds());
        }
        if (u instanceof MessageEliminateChildren) {
            eliminateChildren(((MessageEliminateChildren) u).getParentNodeId());
        }
        if (u instanceof MessageSetOptimalSolution) {
            setOptimalSolution(((MessageSetOptimalSolution) u).getOptimalDescendantLine());
        }
    }
	
	/**
	 * Adds new nodes to the graph visualisation
	 *
     * @param parentNodeId the ID of the parent node to add
	 * @param childNodeIds the Id of the children nodes to add
	 */
	private void addNodes(String parentNodeId, List<String> childNodeIds) {
		if (graph.getNode(parentNodeId) == null) {
			graph.addNode(parentNodeId);
		}
        for (String nodeId: childNodeIds) {
            // Add the child node if it does not exist
            if (graph.getNode(nodeId) == null) {
                graph.addNode(nodeId);
            }
            // Add the edge if it does not exist
            if (graph.getEdge(parentNodeId + ":" + nodeId) == null) {
                graph.addEdge(parentNodeId + nodeId, parentNodeId, nodeId, true);
            }
        }
	}

    /**
     * Removes the descendants of a given node from the solution tree
     * @param nodeId
     */
	private void eliminateChildren (String nodeId) {
		Node n = graph.getNode(nodeId);
		n.setAttribute(STYLE_CLASS, "eliminated");
		for (Edge e : n.leavingEdges().collect(Collectors.toList())){
			e.setAttribute(STYLE_CLASS, "eliminated");
			eliminateChildren(e.getTargetNode().getId());
		}
	}

    /**
     * Marks the given nodes as the optimal solution
     * @param nodeIds the nodes that represent the optimal solution
     */
	private void setOptimalSolution(List<String> nodeIds) {
	    for (String nodeId : nodeIds) {
            Node n = graph.getNode(nodeId);
            n.setAttribute(STYLE_CLASS, "optimal");
            for (Edge e : n.leavingEdges().collect(Collectors.toList())){
                if (nodeIds.contains(e.getTargetNode().getId())) {
                    e.setAttribute(STYLE_CLASS, "optimal");
                    eliminateChildren(e.getTargetNode().getId());
                }

            }
        }
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
                    + "fill-mode: gradient-radial;"
                    + "shadow-color: #875F5F, rgba(0, 0, 0, 0);"
		    + "}"
				
		    + "edge.eliminated { "
			    + "fill-color: rgba(112, 64, 64, 128);"
                    + "shadow-color: rgba(112, 64, 64, 128), rgba(0, 0, 0, 0);"
		    + " }"

            + "node.optimal {"
                + "fill-color: red;"
            + "}"

            + "edge.optimal {"
                + "fill-color: red;"
            + "}";
	
}

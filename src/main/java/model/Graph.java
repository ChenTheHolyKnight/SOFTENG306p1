package model;

import java.util.ArrayList;
import java.util.List;

/**
 * An adapter class used to communicate with the GraphDisplay class to display a
 * real time updating graph of our algorithm.
 */
public class Graph {
	private List<Node> nodes;
	private List<Edge> edges;
	
	public Graph (){
	}
	
	/**
	 * Gets all incoming edges to a specified node.
	 * @param n the node to get the incoming edges for.
	 * @return the list of edges incoming to that node.
	 */
	public List<Edge> getIncomingEdges (Node n){
		List<Edge> incomingEdges = new ArrayList<Edge>();
		for (Edge edge : edges) {
			if (edge.getEndNode().equals(n)){
				incomingEdges.add(edge);
			}
		}
		return incomingEdges;
	}
	
	/**
	 * Gets all outgoing edges from a specified node
	 * @param n the node to get the outgoing edges for.
	 * @return the list of edges outgoing from that node.
	 */
	public List<Edge> getOutgoingEdges(Node n) {
		List<Edge> outgoingEdges = new ArrayList<Edge>();
		for (Edge edge : edges) {
			if (edge.getStartNode().equals(n)){
				outgoingEdges.add(edge);
			}
		}
		return outgoingEdges;
	}
	
	/**
	 * Gets the node in the graph with the specified ID. if there is no 
	 * node with that ID, this method returns null.
	 * @param id The ID of the required node.
	 * @return The node with the specified ID.
	 */
	public Node getNodeById(int id) {
		for (Node node : nodes) {
			if (node.getId() == id) {
				return node;
			}
		}
		return null;		
	}

	/**
	 * Adds specified node to the graph
	 * @param node the node to add
	 */
	public void addNode(Node node) {
		nodes.add(node);
	}

	/**
	 * adds specified edge to the graph.
	 * @param edge the edge to add.
	 */
	public void addEdge(Edge edge) {
		edges.add(edge);
	}

	
}

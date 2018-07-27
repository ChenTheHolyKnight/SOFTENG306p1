package model;
/**
 * 
 * @author Ravid
 *
 * A class representing an edge on a graph
 */
public class Edge {
	private Node startNode;
	private Node endNode;
	
	private int weight;
	private String id;
	
	public Edge (Node startNode, Node endNode, int weight){
		this.startNode = startNode;
		this.endNode = endNode;
		this.weight = weight;
	}

	public Node getStartNode() {
		return startNode;
	}

	public Node getEndNode() {
		return endNode;
	}

	public int getWeight() {
		return weight;
	}	
}

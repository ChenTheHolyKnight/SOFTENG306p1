package model;

/**
 * 
 * @author Ravid
 * Class used to represent Node of a graph for graph visualization.
 */
public class Task {
	private int id;
	private int weight;
	
	public Task (int id, int weight) {
		this.id = id;
		this.weight = weight;
	}

	public int getId() {
		return id;
	}

	public int getWeight() {
		return weight;
	}
	
	@Override
	public boolean equals(Object task) {
		return ((Task) task).getId() == id;
	}
}

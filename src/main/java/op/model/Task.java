package op.model;

/**
 * 
 * @author Ravid
 * Class used to represent Node of a graph for graph visualization.
 */
public class Task {
	private String id;
	private int weight;
	
	public Task (String id, int weight) {
		this.id = id;
		this.weight = weight;
	}

	public String getId() {
		return id;
	}

	public int getDuration() {
		return weight;
	}
	
	@Override
	public boolean equals(Object task) {
		return ((Task) task).getId().equals(id);
	}
}

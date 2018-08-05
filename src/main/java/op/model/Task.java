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

	/**
	 * 
	 * @return The id of this task
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @return The duration of this task (time it takes to complete the task)
	 */
	public int getDuration() {
		return weight;
	}
	
	@Override
	public boolean equals(Object task) {
		return ((Task) task).getId().equals(id);
	}
}

package op.model;

/**
 * 
 * @author Ravid
 * Class used to represent an input task.
 */
public class Task {
	private String id;
	private int weight;
	
	/**
	 * Create a new task
	 * @param id Task ID as String
	 * @param weight weight of the task as an int
	 */
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
	
	/**
	 * Checks if this task object is equal to another object.
	 * Two task objects are equal if they have identical id strings.
	 */
	@Override
	public boolean equals(Object task) {
		return ((Task) task).getId().equals(id);
	}
	
	/**
	 * calculate the hashcode of this task based off of it's Id.
	 */
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + id.hashCode();
		return result;
	}
}

package op.model;

import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Ravid, Sam
 * Class used to represent an input task.
 */
public class Task {
	private final String id;
	private final int weight;
	private int bottomLevel;

	private List<Dependency> incomingDependencies;
	private List<Dependency> outgoingDependencies;


	/**
	 * Create a new task
	 *
	 * @param id     Task ID as String
	 * @param weight weight of the task as an int
	 */
	public Task(String id, int weight) {
		this.id = id;
		this.weight = weight;
		this.bottomLevel = 0;
	}

	private int calculateBottomLevel(Task t) {
		int currentMax = 0;
		if(t.getOutgoingDependencies().size() == 0){
			currentMax = t.getDuration();
		}
		for (Dependency d : t.getOutgoingDependencies()) {
			int bottom = t.getDuration() + calculateBottomLevel(d.getEndTask());
			if (bottom > currentMax) { // if a task has multiple children, take the shortest path from them
				currentMax = bottom;
			}
		}
		return currentMax;

	}

	public void setBottomLevel() {
		bottomLevel = calculateBottomLevel(this);
	}

	public int getBottomLevel() {
		return bottomLevel;
	}
	public void addDependencies(List<Dependency> in, List<Dependency> out){
		this.incomingDependencies = Collections.unmodifiableList(in);
		this.outgoingDependencies = Collections.unmodifiableList(out);
	}
	public List<Dependency> getIncomingDependencies() {
		return incomingDependencies;
	}

	public List<Dependency> getOutgoingDependencies() {
		return outgoingDependencies;
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

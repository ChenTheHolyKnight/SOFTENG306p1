package op.model;

import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Ravid
 * Class used to represent Node of a graph for graph visualization.
 */
public class Task {
	private final String id;
	private final int weight;

	private List<Dependency> incomingDependencies;
	private List<Dependency> outgoingDependencies;

	public Task (String id, int weight) {
		this.id = id;
		this.weight = weight;
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
	
	@Override
	public boolean equals(Object task) {
		return ((Task) task).getId().equals(id);
	}
}

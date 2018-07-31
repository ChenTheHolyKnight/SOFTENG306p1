package op.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * An adapter class used to communicate with the GraphDisplay class to display a
 * real time updating graph of our algorithm.
 */
public class TaskGraph {
	private List<Task> tasks;
	private List<Dependency> dependencies;
	private String title;

	public TaskGraph (List<Task> tasks, List<Dependency> dependencies, String title){
		this.title = title;
		this.tasks = tasks;
		this.dependencies = dependencies;
	}
	
	/**
	 * Gets all incoming Dependenceis to a specified Task.
	 * @param n the Task to get the incoming Dependencies for.
	 * @return the list of Dependencies incoming to that Task.
	 */
	public List<Dependency> getIncomingDependencies (Task n){
		List<Dependency> incomingDependencies = new ArrayList<Dependency>();
		for (Dependency dep : dependencies) {
			if (Dependency.getEndTask().equals(n)){
				incomingDependencies.add(dep);
			}
		}
		return incomingDependencies;
	}
	
	/**
	 * Gets all outgoing Dependencys from a specified Task
	 * @param n the Task to get the outgoing Dependencys for.
	 * @return the list of Dependencys outgoing from that Task.
	 */
	public List<Dependency> getOutgoingDependencys(Task n) {
		List<Dependency> outgoingDependencys = new ArrayList<Dependency>();
		for (Dependency Dependency : Dependencies) {
			if (Dependency.getStartTask().equals(n)){
				outgoingDependencys.add(Dependency);
			}
		}
		return outgoingDependencys;
	}
	
	/**
	 * Gets the Task in the graph with the specified ID. if there is no 
	 * Task with that ID, this method returns null.
	 * @param id The ID of the required Task.
	 * @return The Task with the specified ID.
	 */
	public Task getTaskById(int id) {
		for (Task Task : Tasks) {
			if (Task.getId() == id) {
				return Task;
			}
		}
		return null;		
	}

	/**
	 * Adds specified Task to the graph
	 * @param Task the Task to add
	 *
	public void addTask(Task Task) {
		Tasks.add(Task);
	}

	/**
	 * adds specified Dependency to the graph.
	 * @param Dependency the Dependency to add.
	 *
	public void addDependency(Dependency Dependency) {
		Dependencies.add(Dependency);
	}
	*/

}

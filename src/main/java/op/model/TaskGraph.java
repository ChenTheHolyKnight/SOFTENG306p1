package op.model;

import java.util.ArrayList;
import java.util.List;

/**
 * An adapter class used to communicate with the GraphDisplay class to display a
 * real time updating graph of our algorithm.
 */
public class TaskGraph {
	private List<Task> Tasks;
	private List<Dependency> Dependencies;
	
	public TaskGraph (){
	}
	
	/**
	 * Gets all incoming Dependencys to a specified Task.
	 * @param n the Task to get the incoming Dependencys for.
	 * @return the list of Dependencys incoming to that Task.
	 */
	public List<Dependency> getIncomingDependencys (Task n) {
		List<Dependency> incomingDependencys = new ArrayList<Dependency>();
		for (Dependency Dependency : Dependencies) {
			if (Dependency.getEndTask().equals(n)){
				incomingDependencys.add(Dependency);
			}
		}
		return incomingDependencys;
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
	 */
	public void addTask(Task Task) {
		Tasks.add(Task);
	}

	/**
	 * adds specified Dependency to the graph.
	 * @param Dependency the Dependency to add.
	 */
	public void addDependency(Dependency Dependency) {
		Dependencies.add(Dependency);
	}

	
}

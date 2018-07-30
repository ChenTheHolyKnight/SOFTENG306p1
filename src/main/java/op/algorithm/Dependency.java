package op.algorithm;
/**
 * 
 * @author Ravid
 *
 * A class representing an edge on a graph
 */
public class Dependency {
	private Task startTask;
	private Task endTask;
	
	private int weight;
	private String id;
	
	public Dependency (Task startTask, Task endTask, int weight){
		this.startTask = startTask;
		this.endTask = endTask;
		this.weight = weight;
	}

	public Task getStartTask() {
		return startTask;
	}

	public Task getEndTask() {
		return endTask;
	}

	public int getWeight() {
		return weight;
	}	
}

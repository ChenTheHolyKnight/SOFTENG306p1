package op.model;

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

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Dependency)) {
			return false;
		}

		// dependencies are only equal if they have the same weight, start task, and end task
		Dependency other = (Dependency)obj;
		if (!(this.startTask.getId().equals(other.startTask.getId()))) {
			return false;
		}
		if (!(this.endTask.getId().equals(other.endTask.getId()))) {
			return false;
		}
		if (this.weight != other.weight) {
			return  false;
		}

		return true; // we made it past all checks so the dependencies are equal
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		
		result = 31 * result + startTask.getId().hashCode();
		result = 31 * result + endTask.getId().hashCode();
		result = 31 * result + startTask.getDuration();
		result = 31 * result + endTask.getDuration();
		result = 31 * result + weight;
		
		return result;
	}
}

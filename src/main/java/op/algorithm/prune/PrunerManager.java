package op.algorithm.prune;

import java.util.ArrayList;
import java.util.List;
import op.model.Schedule;

/**
 * A manager class for the Pruner interface. Can add as many pruners as user chooses, then can execute 
 * the pruners on a list of Schedules.
 * @author Ravid Aharon
 *
 */
public class PrunerManager {
    
	/**
     * Enum defining the different implementations of pruners available
     */
    public enum Pruners {

        EQUIVALENT_SCHEDULE("es"),
        NODE_EQUIVALENCE("ne");

        private String cmdRepresentation;
        Pruners(String cmdRepresentation) {
            this.cmdRepresentation = cmdRepresentation;
        }

        public String getCmdRepresentation() {
            return this.cmdRepresentation;
        }
    }
	
	private List<Pruner> pruners = new ArrayList<Pruner>();
	
	public void addEquivalentSchedulePruner() {
		EquivalentSchedulePruner p = new EquivalentSchedulePruner();
		if (!pruners.contains(p)) pruners.add(p);
	}
	
	public void addNodeEquivalencePruner() {
		NodeEquivalencePruner p =  new NodeEquivalencePruner();
		if (!pruners.contains(p)) pruners.add(p);
	}

	/**
	 * Executes the prune method of every added pruner to the pruner manager on the list of Schedules passed in.
	 * @param toPrune The list of schedules to prune
	 * @return The newly pruned List of schedules
	 */
	public List<Schedule> execute(List<Schedule> toPrune) {
		if (pruners.size() != 0){
			List<Schedule> pruned = new ArrayList<Schedule>();
			pruned.addAll(toPrune);
			for (Pruner p: pruners) {
				pruned = p.prune(pruned);
			}
			return pruned;
		}
		return toPrune;
	}
}

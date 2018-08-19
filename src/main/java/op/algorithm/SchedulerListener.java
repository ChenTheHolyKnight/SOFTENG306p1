package op.algorithm;

import op.model.Schedule;

/**
 * Defines the methods that an observer of any scheduler is interested in
 * @author Darcy Cox
 */
public interface SchedulerListener {

    /**
     * Tells the listener that a new schedule has been created
     * @param s The new schedule
     */
    void newSchedule(Schedule s);

    /**
     * Tells the listener how many trees have been pruned
     * @param prunedTrees
     */
    void updateNumPrunedTrees(int prunedTrees);

    /**
     * Tells the listener how many nodes in the search space have been visited
     * @param nodesVisited
     */
    void updateNodesVisited(int nodesVisited);

    /**
     * Tells the listener what the new best schedule length is
     * @param scheduleLength
     */
    void updateBestScheduleLength(int scheduleLength);

    /**
     * Tells the listener what the optimal schedule has been found
     */
    void optimalScheduleFound();

}

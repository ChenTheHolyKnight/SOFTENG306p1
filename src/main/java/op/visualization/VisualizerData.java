package op.visualization;

import op.algorithm.SchedulerListener;
import op.model.Schedule;

/**
 * Class that stores information needed by the visualization. The GUI controller will query this class
 * every so often in order to update it's data.
 * @author Darcy Cox
 */
public class VisualizerData implements SchedulerListener {

    private Schedule newestSchedule = new Schedule();
    private long numPrunedTrees = 0;
    private long numNodesVisited = 0;
    private int bestScheduleLength = 0;

    @Override
    public void newSchedule(Schedule s) {
        newestSchedule = s;
    }

    @Override
    public void updateNumPrunedTrees(int prunedTrees) {
        numPrunedTrees = prunedTrees;
    }

    @Override
    public void updateNodesVisited(int nodesVisited) {
        numNodesVisited = nodesVisited;
    }

    @Override
    public void updateBestScheduleLength(int scheduleLength) {
        bestScheduleLength = scheduleLength;
    }

    public Schedule getNewestSchedule() {
        return newestSchedule;
    }

    public long getNumPrunedTrees() {
        return numPrunedTrees;
    }

    public long getNumNodesVisited() {
        return numNodesVisited;
    }

    public int getBestScheduleLength() {
        return bestScheduleLength;
    }
}

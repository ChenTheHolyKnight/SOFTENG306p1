import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import op.algorithm.GreedyScheduler;
import op.algorithm.SimpleScheduler;
import org.junit.After;
import org.junit.Test;

import op.io.DotIO;
import op.model.Arguments;
import op.model.Dependency;
import op.model.Schedule;
import op.model.ScheduledTask;
import op.model.Task;
import op.model.TaskGraph;

public class TestScheduler {
	
	private Schedule s;

	private final String PATH_TO_TEST = "./src/main/resources/sample_inputs/test.dot";
    private final String PATH_TO_NODES_7 = "./src/main/resources/sample_inputs/Nodes_7_OutTree.dot";
    private final String PATH_TO_NODES_8 = "./src/main/resources/sample_inputs/Nodes_8_Random.dot";
    private final String PATH_TO_NODES_9 = "./src/main/resources/sample_inputs/Nodes_9_SeriesParallel.dot";
    private final String PATH_TO_NODES_10 = "./src/main/resources/sample_inputs/Nodes_10_Random.dot";
    private final String PATH_TO_NODES_11 = "./src/main/resources/sample_inputs/Nodes_11_OutTree.dot";

	@After
	public void reset() {
		try {
			TestSingletonUtil.resetTaskGraph();
			TestSingletonUtil.resetArguments();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}


    /**
     * Tests if the schedule produced by SimpleScheduler is valid.
     */
	@Test
    public void testSimpleScheduler() {
	    try {

            setup(PATH_TO_TEST);
            s = (new SimpleScheduler()).produceSchedule();
            checkScheduleIsValid();
        } catch (IOException e) {
	        e.printStackTrace();
        }
    }


    /**
     * Tests GreedyScheduler is valid with test.dot as the input graph.
     */
    @Test
    public void testGreedySchedulerWithTestGraph() {
        checkGreedyScheduler(PATH_TO_TEST);
    }

    /**
     * Tests GreedyScheduler is valid with Nodes_7_OutTree.dot as the input graph.
     */
    @Test
    public void testGreedySchedulerWithNodes7Graph() {
        checkGreedyScheduler(PATH_TO_NODES_7);
    }

    /**
     * Tests GreedyScheduler is valid with Nodes_8_Random.dot as the input graph.
     */
    @Test
    public void testGreedySchedulerWithNodes8Graph() {
        checkGreedyScheduler(PATH_TO_NODES_8);
    }

    /**
     * Tests GreedyScheduler is valid with Nodes_9_SeriesParallel.dot as the input graph.
     */
    @Test
    public void testGreedySchedulerWithNodes9Graph() {
        checkGreedyScheduler(PATH_TO_NODES_9);
    }

    /**
     * Tests GreedyScheduler is valid with Nodes_10_Random.dot as the input graph.
     */
    @Test
    public void testGreedySchedulerWithNodes10Graph() {
        checkGreedyScheduler(PATH_TO_NODES_10);
    }

    /**
     * Tests GreedyScheduler is valid with Nodes_11_OutTree.dot as the input graph.
     */
    @Test
    public void testGreedySchedulerWithNodes11Graph() {
        checkGreedyScheduler(PATH_TO_NODES_11);
    }
    
    /**
     * Sets up the program input.
     * @throws IOException
     */
    private void setup(String inputFilePath) throws IOException {
        Arguments.initialize(inputFilePath,10,1,false,"testOutput.dot");
        new DotIO().dotIn(inputFilePath);
    }
    
    /**
     * Checks that the schedule produced by GreedyScheduler is valid and at least as good as the schedule produced by
     * SimpleScheduler.
     */
    private void checkGreedyScheduler(String path) {
        try {
            setup(path);
            s = (new GreedyScheduler()).produceSchedule();
            checkScheduleIsValid();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * Checks if a schedule is valid. A schedule is valid if and only if there is no overlap between
	 * tasks and all dependencies are respected.
	 */
	private void checkScheduleIsValid() {
		checkNoOverlap();
		checkDependenciesAreRespected();
	}

	/**
	 * Checks there is no overlap between any two scheduled tasks.
	 * There is no overlap if and only if for every pair of tasks (A,B), A's start time is greater than B's end time OR
	 * B's start time is greater than A's end time.
	 */
	private void checkNoOverlap() {

		for (int processor = 1; processor <= Arguments.getInstance().getNumProcessors(); processor++) {
			if (s.getScheduledTasks(processor) != null) {
				for (ScheduledTask t1 : s.getScheduledTasks(processor)) {
					for (ScheduledTask t2 : s.getScheduledTasks(processor)) {
						if (!t1.equals(t2)) {
							assertTrue(t1.getStartTime() + t1.getTask().getDuration() <= t2.getStartTime()
									|| t2.getStartTime() + t2.getTask().getDuration() <= t1.getStartTime());
						}
					}
				}
			}
		}
	}

	/**
	 * Checks all dependencies are respected.
	 * Dependencies are respected if and only if, for all tasks' dependencies, the start time of the end dependency
	 * is no less than the task's start time (assuming there is no overlap) AND, if the tasks are on
	 * different processors, the start time of the end task is no less than than the end time of the
	 * task plus the weight of the dependency.
	 */
	private void checkDependenciesAreRespected() {

		for (Task task : TaskGraph.getInstance().getAllTasks()) {
			for (Dependency d : TaskGraph.getInstance().getOutgoingDependencies(task)) {

				if (s.getScheduledTask(task).getProcessor() != s.getScheduledTask(d.getEndTask()).getProcessor()){
					assertTrue(s.getScheduledTask(task).getStartTime() + task.getDuration() + d.getWeight()
						<= s.getScheduledTask(d.getEndTask()).getStartTime());
				} else {
					assertTrue(s.getScheduledTask(task).getStartTime() < s.getScheduledTask(d.getEndTask()).
                            getStartTime());
				}
			}
		}
		
	}
}
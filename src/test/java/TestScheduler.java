import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import op.algorithm.GreedyScheduler;
import op.algorithm.Scheduler;
import op.algorithm.SimpleScheduler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import op.io.Arguments;
import op.io.DotIO;
import op.model.Dependency;
import op.model.Schedule;
import op.model.ScheduledTask;
import op.model.Task;
import op.model.TaskGraph;

public class TestScheduler {
	
	private Schedule s;

	private final String PATH_TO_DOT = "./src/main/resources/sample_inputs/test.dot";
	
	/**
	 * reads in a .DOT file as input for a task graph
	 * @throws IOException
	 */
	@Before
	public void setup() throws IOException {
			//TaskGraph tg=TaskGraph.getInstance();
			Arguments.initialize(PATH_TO_DOT,10,1,false,"test.dot");
			new DotIO().dotIn(PATH_TO_DOT);
	}

	@After
	public void reset() {
		try {
			SingletonTesting.resetTaskGraph();
			SingletonTesting.resetArguments();
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
    public void testSimpleSchedulerSchedule() {
        s = (new SimpleScheduler()).produceSchedule();
        checkScheduleIsValid();
    }

    /**
     * Tests if the schedule produced by GreedyScheduler is valid and at least as good as the schedule produced by
     *  SimpleScheduler.
     */
    @Test
    public void testGreedySchedulerSchedule() {
        s = (new GreedyScheduler()).produceSchedule();
        checkScheduleIsValid();

        // Ensure schedule is at least as good as schedule produced by simple scheduler
        if (s.getLength() > (new SimpleScheduler()).produceSchedule().getLength()){
            fail();
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

		for (int processor = 1; processor < Arguments.getInstance().getNumProcessors(); processor++) {
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
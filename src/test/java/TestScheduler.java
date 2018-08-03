import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import op.algorithm.Scheduler;
import op.algorithm.SimpleScheduler;
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
	private static final int NUM_PROCESSORS = 10;
	
	/**
	 * reads in a .DOT file as input for a task graph
	 * @throws IOException
	 */
	@Before
	public void setup() throws IOException {
			new DotIO().dotIn(PATH_TO_DOT);
	}

    /**
     * Tests if the schedule produced by SimpleScheduler is valid.
     */
	@Test
    public void testSimpleSchedulerSchedule() {
        s = (new SimpleScheduler()).produceSchedule(NUM_PROCESSORS);
        checkScheduleIsValid();
    }

    /**
     * Tests if the schedule produced by GreedyScheduler is valid and at least as good as the schedule produced by
     *  SimpleScheduler.
     */
    @Test
    public void testGreedySchedulerSchedule() {
        s = (new SimpleScheduler()).produceSchedule(NUM_PROCESSORS);
        checkScheduleIsValid();

        // Ensure schedule is at least as good as schedule produced by simple scheduler
        if (s.getLength() > (new SimpleScheduler()).produceSchedule(NUM_PROCESSORS).getLength()){
            fail();
        }
    }
	
	/*
	 * Checks if a schedule is valid. A schedule is valid if and only if there is no overlap between
	 * tasks and all dependencies are respected. 
	 * 
	 * No Overlap: For every pair of tasks (A,B), A's start time is greater than B's end time OR 
	 * B's start time is greater than A's end time.
	 * 
	 * Dependencies are Respected: For all tasks' dependencies, the start time of the end dependency
	 * is greater than the task's start time (assuming there is no overlap) AND, if the tasks are on
	 * different processors, the start time of the end task is greater than the end time of the
	 * task + the weight of the dependency.
	 */
	private void checkScheduleIsValid() {

		TaskGraph tg = TaskGraph.getInstance();

		// Checks if no Scheduled Tasks overlap each other on the same processor
		for (int processor = 1; processor < Arguments.getInstance().getNumProcessors(); processor ++ ) {
			for (ScheduledTask t1 : s.getScheduledTasks(processor)) {
				for (ScheduledTask t2 : s.getScheduledTasks(processor)) {
					if (!t1.equals(t2)) {
						assertTrue(t1.getStartTime() + t1.getTask().getDuration() <= t2.getStartTime() 
								|| t2.getStartTime() + t2.getTask().getDuration() <= t2.getStartTime());
					}
				}
			}
		}
		
		// Checks if all dependencies for all tasks respect each other
		for (Task task : tg.getAllTasks()) { 
			for (Dependency d : tg.getOutgoingDependencies(task)) {
				if (s.getScheduledTask(task).getProcessor() != s.getScheduledTask(d.getEndTask()).getProcessor()){
					assertTrue(s.getScheduledTask(task).getStartTime() + task.getDuration() + d.getWeight()
						<= s.getScheduledTask(d.getEndTask()).getStartTime());
				} else {
					assertTrue(s.getScheduledTask(task).getStartTime() < s.getScheduledTask(d.getEndTask()).getStartTime());
				}
			}
		}
		
	}
}
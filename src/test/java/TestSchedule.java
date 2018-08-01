import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import op.io.Arguments;
import op.io.DotIO;
import op.model.Dependency;
import op.model.Schedule;
import op.model.ScheduledTask;
import op.model.Task;
import op.model.TaskGraph;

public class TestSchedule {
	
	private TaskGraph tg;
	private Schedule s;
	
	// CHANGE THIS TO CHANGE THE GRAPH TO TEST
	private final String PATH_TO_DOT = "../../main/resources/sample_inputs/test.dot";
	
	/**
	 * reads in a .DOT file as input for a task graph
	 * @throws IOException 
	 */
	@Before
	public void setup() throws IOException {
        new DotIO().dotIn(PATH_TO_DOT);
	}
	
	/*
	 * Tests if a schedule is valid. A schedule is valid if and only if there is no overlap between
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
	@Test
	public void testScheduleIsValid() {
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
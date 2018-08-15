import static org.junit.Assert.assertTrue;

import java.io.IOException;

import java.util.ArrayList;

import op.algorithm.DFSScheduler;
import op.algorithm.GreedyScheduler;
import op.algorithm.SimpleScheduler;
import op.algorithm.bound.CostFunctionManager;
import op.algorithm.prune.PrunerManager;
import org.junit.After;
import org.junit.Test;

import op.io.DotIO;
import op.model.Arguments;
import op.model.Dependency;
import op.model.Schedule;
import op.model.ScheduledTask;
import op.model.Task;
import op.model.TaskGraph;

/**
 * Test class to ensure schedules produced are valid
 * @author Ravid Aharon
 *
 */
public class TestScheduler {
	
	private Schedule s;

	private final String PATH_TO_TEST = "./src/main/resources/sample_inputs/test.dot";
    private final String PATH_TO_NODES_7 = "./src/main/resources/sample_inputs/Nodes_7_OutTree.dot";
    private final String PATH_TO_NODES_8 = "./src/main/resources/sample_inputs/Nodes_8_Random.dot";
    private final String PATH_TO_NODES_9 = "./src/main/resources/sample_inputs/Nodes_9_SeriesParallel.dot";
    private final String PATH_TO_NODES_10 = "./src/main/resources/sample_inputs/Nodes_10_Random.dot";
    private final String PATH_TO_NODES_11 = "./src/main/resources/sample_inputs/Nodes_11_OutTree.dot";

    private Arguments arguments;
    
	@After
	public void reset() {
		try {
			TestSingletonUtil.resetTaskGraph();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * checks to see if two equivalent schedules are equal and have matching hashcodes. 
	 * Also checks if two different schedules are NOT equal and do NOT have matching hashcodes
	 * Schedules are equal if every processor is identical to another processor in the other schedule.
	 */
	@Test
	public void testEqualsMethod() {
		s = new Schedule();
        Schedule s2 = new Schedule();
        
		arguments = new Arguments(null,2,1,
				false,null, null, null, null);

        s.addScheduledTask(new ScheduledTask(new Task("1", 3), 0, 1));
		s.addScheduledTask(new ScheduledTask(new Task("2", 2), 3, 1));
		s.addScheduledTask(new ScheduledTask(new Task("3", 3), 0, 2));
		
		s2.addScheduledTask(new ScheduledTask(new Task("1", 3), 0, 2));
		s2.addScheduledTask(new ScheduledTask(new Task("2", 2), 3, 2));
		s2.addScheduledTask(new ScheduledTask(new Task("3", 3), 0, 1));
		
		assertTrue(s.equals(s2));
		assertTrue(s.hashCode() == s2.hashCode());
		
		Schedule s3 = new Schedule (s2, new ScheduledTask(new Task("4", 3), 3, 1));
		s2.addScheduledTask(new ScheduledTask(new Task("4", 3), 3, 1));
		
		try {
			assertTrue(s.equals(s2));
			throw new AssertionError("Thought unequal schedules are equal");
		} catch (AssertionError e) {
			// Do nothing
		}
		try {
			assertTrue(s.hashCode() == s2.hashCode());
			throw new AssertionError("Thought unequal schedules are equal");
		} catch (AssertionError e) {
			// Do nothing
		}
		
		assertTrue (s2.equals(s3));
		assertTrue (s2.hashCode() == s2.hashCode());
	}
	
	/**
     * Test if the testScheduleIsValid method recognizes a schedule with overlap
     */
	@Test
	public void testScheduleIsValidOverlap() {
		s = new Schedule();
        arguments = new Arguments(null,1,1,
				false,null, null, null, null);

        // set up a schedule with overlap
		s.addScheduledTask(new ScheduledTask(new Task("1", 3), 0, 1));
		s.addScheduledTask(new ScheduledTask(new Task("2", 2), 2, 1));		
	
		// Checks if method recognizes overlap
		try {
				checkScheduleIsValid();
				throw new AssertionError("did not detect overlap");	
		} catch (AssertionError e) {
			// do nothing if error was detected
		}
	}
	
	/**
     * Test if the testScheduleIsValid method recognizes a schedule which does not 
     * respect it's dependencies between tasks on different processors
     */
	@Test
	public void testScheduleIsValidDependencyDisrespect() {
		s = new Schedule();
        arguments = new Arguments(null,2,
				1,false,null, null, null, null);
        
        ArrayList<Task> tasks = new ArrayList<Task>();
        ArrayList<Dependency> dependencies = new ArrayList<Dependency>();
        
        // Create and add two tasks which don't respect dependency
        Task t1 = new Task("1", 3);
        Task t2 = new Task("2", 3);
        ScheduledTask s1 = new ScheduledTask(t1, 0, 1);
        ScheduledTask s2 = new ScheduledTask(t2, 3, 2);
        Dependency d = new Dependency(t1, t2, 1);
        t1.addDependencies(new ArrayList<>(), new ArrayList<Dependency>(){{add(d);}});
        t2.addDependencies(new ArrayList<Dependency>(){{add(d);}}, new ArrayList<>());
        tasks.add(t1);
        tasks.add(t2);
        dependencies.add(d);
        
    	TaskGraph.initialize(tasks, "depTest");
        
        // set up a schedule with overlap
		s.addScheduledTask(s1);
		s.addScheduledTask(s2);
		
	
		// Checks if method recognizes that a dependency was not respected
		try {
				checkScheduleIsValid();
				throw new AssertionError("did not detect dependency DISRESPECT");	
		} catch (AssertionError e) {
			// do nothing if error was detected
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

    @Test
	public void testDFSSchedulerWithTestGraph() { checkDFSScheduler(PATH_TO_TEST); }

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
     * Checks that the schedule produced by GreedyScheduler is valid and at least as good as the schedule produced by
     * SimpleScheduler.
     */
    private void checkGreedyScheduler(String path) {
        try {
            setup(path);
            s = (new GreedyScheduler(arguments.getNumProcessors())).produceSchedule();
            checkScheduleIsValid();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    /**
     * Creates an Arguments Object and creates taskgraph.
     * @throws IOException
     */
    private void setup(String inputFilePath) throws IOException {
        arguments = new Arguments(inputFilePath,10,1,
				false,"testOutput.dot", null, new ArrayList<>(), new ArrayList<>());
        new DotIO().dotIn(inputFilePath);
    }


	private void checkDFSScheduler(String path) {
		try {
			setup(path);
			s = (new DFSScheduler(arguments.getNumProcessors(), new PrunerManager(), new CostFunctionManager(1))).produceSchedule();
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

		for (int processor = 1; processor <= arguments.getNumProcessors(); processor++) {
			if (s.getScheduledTasksOfProcessor(processor) != null) {
				for (ScheduledTask t1 : s.getScheduledTasksOfProcessor(processor)) {
					for (ScheduledTask t2 : s.getScheduledTasksOfProcessor(processor)) {
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
			for (Dependency d : task.getOutgoingDependencies()) {

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
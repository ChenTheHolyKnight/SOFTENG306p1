import op.algorithm.prune.EquivalentSchedulePruner;
import op.algorithm.prune.NodeEquivalencePruner;
import op.model.Dependency;
import op.model.Schedule;
import op.model.ScheduledTask;
import op.model.Task;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests if pruners prune correctly
 * @Author Ravid Aharon
 */
public class TestPruners {
	
	/**
	 * Checks if EquivalentSchedulePruner correctly prunes equivalent schedules
	 */
	@Test
	public void TestEquivalentSchedulePruner(){
		List<Schedule> schedules = new ArrayList<Schedule>();
		
		Schedule s1 = new Schedule();
		Schedule s2 = new Schedule();
		Schedule s3 = new Schedule();
		Schedule s4 = new Schedule();
		
		ScheduledTask st1 = new ScheduledTask(new Task("a", 3), 0, 1);
		ScheduledTask st2 = new ScheduledTask(new Task("b", 3), 3, 1);
		
		s1.addScheduledTask(st1);
		s1.addScheduledTask(st2);
		s2.addScheduledTask(st1);
		s2.addScheduledTask(st2);
		s3.addScheduledTask(st1);
		s3.addScheduledTask(st2);
		
		s4.addScheduledTask(st1);
		
		System.out.println("s1: " + s1.hashCode());
		System.out.println("s4: " + s4.hashCode());
		
		schedules.add(s1);
		schedules.add(s2);
		schedules.add(s3);
		
		EquivalentSchedulePruner pruner = new EquivalentSchedulePruner();
		
		System.out.println("Pre Equiv Schedule Prune: " + schedules.size());
		
		// checks if pruner prunes equivalent schedules
		schedules =  pruner.prune(schedules);
		assertEquals(1, schedules.size());
		
		
		// checks if pruner does not prune non equivalent schedules
		List<Schedule> schedules2 = new ArrayList<Schedule>();
		schedules2.add(s1);
		schedules2.add(s4);
		System.out.println("Pre Equiv Schedule Prune2: " + schedules2.size());
		
		schedules =  pruner.prune(schedules2);
		System.out.println("Post Equiv Schedule Prune2: " + schedules2.size());
		
		assertEquals(2, schedules2.size());
	}
	
	/**
 	 * Checks if NodeEquivalencePruner correctly prunes Schedules with Equivalent next Nodes
	 */
	@Test
	public void TestNodeEquivalencePruner() {
		List<Schedule> schedules = new ArrayList<Schedule>();
		
		Schedule s1 = new Schedule();
		Schedule s2;
		Schedule s3;
		
		Task t1 = new Task("a", 3);
		Task t2 = new Task("b", 3);
		Task t3 = new Task("c", 3);
		
		Dependency d_t1Tot2 = new Dependency(t1, t2, 2);
		Dependency d_t1Tot3 = new Dependency(t1, t3, 2);
		
		List<Dependency> t2_ins = new ArrayList<>();
		t2_ins.add(d_t1Tot2);
		List<Dependency> t3_ins = new ArrayList<>();
		t3_ins.add(d_t1Tot3);
		List<Dependency> t1_outs = new ArrayList<>();
		t1_outs.add(d_t1Tot2);
		t1_outs.add(d_t1Tot3);
		
		t1.addDependencies(new ArrayList<>(), t1_outs);
		t2.addDependencies(t2_ins, new ArrayList<>());
		t3.addDependencies(t3_ins, new ArrayList<>());
		
		ScheduledTask st1 = new ScheduledTask(t1, 0, 1);
		ScheduledTask st2 = new ScheduledTask(t2 , 3, 1);
		ScheduledTask st3 = new ScheduledTask(t3, 3, 1);
		
		s1.addScheduledTask(st1);
		s2 = new Schedule(s1, st2);
		s3 = new Schedule(s1, st3);
		
		schedules.add(s1);
		schedules.add(s2);
		schedules.add(s3);
		
		System.out.println("Pre NodeEquivalencePrune: " + schedules.size());
		NodeEquivalencePruner pruner = new NodeEquivalencePruner();
		schedules =  pruner.prune(schedules);
		
		assertEquals(2, schedules.size());
	}
}
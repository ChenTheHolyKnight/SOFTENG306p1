package op.algorithm;

import op.model.Dependency;
import op.model.Task;
import op.model.TaskGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A util class can be used by all the schedulers
 */
public class SchedulerUtil {
    /**
     * A method to create a topological order for a given graph
     * @param tasks all the tasks in the input graph
     * @return a list of tasks that sorted in topological order
     */
    public static List<Task> createTopologicalOrder(List<Task> tasks){
        TaskGraph tg= TaskGraph.getInstance();
        List<Dependency> dependencies=new ArrayList<>();
        tasks.forEach(task -> dependencies.addAll(tg.getIncomingDependencies(task)));
        List<Task> tasks1=new ArrayList<>(tasks);
        Collections.sort(tasks1,((t1, t2) ->{
                int num=0;
                for(Dependency dependency:dependencies){
                    if(dependency.getStartTask()==t1 &&dependency.getEndTask()==t2){
                        num=1;
                    }
                }
                return num;
        } ));

        return tasks;
    }
}

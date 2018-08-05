package op.algorithm;

import op.model.Dependency;
import op.model.Task;
import op.model.TaskGraph;

import java.util.*;

/**
 * A util class can be used by all the schedulers
 */
public class SchedulerUtil {
    /**
     * A method to create a topological order for a given graph
     *
     * @param tasks all the tasks in the input graph
     * @return a list of tasks that sorted in topological order
     */
    public static List<Task> createTopologicalOrder(List<Task> tasks) {
        TaskGraph tg=TaskGraph.getInstance();
        List<Task> tasks1=new ArrayList<>(tasks);
        List<Dependency> dependencies=new ArrayList<>();
        tasks1.forEach(task -> dependencies.addAll(tg.getOutgoingDependencies(task)));
        List<Task> removedTasks=new ArrayList<>();
        List<Dependency> removedDep=new ArrayList<>();

        List<Task> outputList=new ArrayList<>();
        while (!tasks1.isEmpty()){
            removedTasks=findEntryPoints(tasks1,dependencies);
            //System.out.println(removedTasks.size());
            removedTasks.forEach(task -> {
                removedDep.addAll(tg.getOutgoingDependencies(task));
            });
            outputList.addAll(removedTasks);
            tasks1.removeAll(removedTasks);
            dependencies.removeAll(removedDep);
            removedDep.clear();
            removedTasks.clear();
            //System.out.println(tasks1.size());
        }
        //outputList.forEach(task -> System.out.print(task.getId()));

        return outputList;
    }

    /**
     * This is a method to find the Entry task of the task graph.
     * */
    private static List<Task> findEntryPoints(List<Task> tasks,List<Dependency> dependencies){
        List<Task> starts=new ArrayList<>();
        //List<Task> tasks=TaskGraph.getInstance().getAllTasks();

        tasks.forEach(task -> {
            //System.out.println(TaskGraph.getInstance().getIncomingDependencies(task).size());
            List<Dependency> incoming=new ArrayList<>();
            for(Dependency dependency:dependencies){
                if(dependency.getEndTask().equals(task)){
                    incoming.add(dependency);
                }
            }
            if(incoming.isEmpty()){
                starts.add(task);
            }
        });
        return starts;
    }
}
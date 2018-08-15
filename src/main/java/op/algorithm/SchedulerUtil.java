package op.algorithm;

import op.algorithm.bound.CostFunction;
import op.model.Dependency;
import op.model.Schedule;
import op.model.Task;

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
        List<Task> tasks1=new ArrayList<>(tasks);
        List<Dependency> dependencies=new ArrayList<>();
        tasks1.forEach(task -> dependencies.addAll(task.getOutgoingDependencies()));
        List<Task> removedTasks;
        List<Dependency> removedDep=new ArrayList<>();

        List<Task> outputList=new ArrayList<>();
        while (!tasks1.isEmpty()){
            removedTasks=findEntryPoints(tasks1,dependencies);
            removedTasks.forEach(task -> removedDep.addAll(task.getOutgoingDependencies()));
            outputList.addAll(removedTasks);
            tasks1.removeAll(removedTasks);
            dependencies.removeAll(removedDep);
            removedDep.clear();
            removedTasks.clear();
        }
        return outputList;
    }

    /**
     * This is a method to find the Entry task of the task graph.
     * */
    private static List<Task> findEntryPoints(List<Task> tasks,List<Dependency> dependencies){
        List<Task> starts=new ArrayList<>();

        tasks.forEach(task -> {
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

    /**
     * Finds the tightest bound for the length of a schedule, given specified cost functions
     * @param s the schedule
     * @param costFunctions a list of cost functions
     * @return the tightest bound
     */
    public static int getTightestBound(Schedule s, List<CostFunction> costFunctions) {

        // calculate the cost functions using each implementation then take the maximum of them
        // because it will be the tightest lower bound
        int tightestBound = 0;
        for (CostFunction cf : costFunctions) {
            int currentFunc = cf.calculate(s);
            if (currentFunc > tightestBound) {
                tightestBound = currentFunc;
            }
        }
        return tightestBound;
    }
}
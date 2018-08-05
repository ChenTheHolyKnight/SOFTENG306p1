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
        TaskGraph tg = TaskGraph.getInstance();
        List<Dependency> dependencies = new ArrayList<>();
        tasks.forEach(task -> {
            dependencies.addAll(tg.getIncomingDependencies(task));
        });

        List<Dependency> dependencies1 = new ArrayList<>(dependencies);
        List<Dependency> dependencies2 = new ArrayList<>(dependencies);
        while (true) {
            int num_deps = dependencies1.size();
            for (Dependency dependency : dependencies1) {
                for (Dependency dependency1 : dependencies1) {
                    if (dependency.getEndTask().getId().equals(dependency1.getStartTask().getId())) {
                        boolean hasAdded = false;
                        Dependency new_dep = new Dependency(dependency.getStartTask(), dependency1.getEndTask(), -1);
                        for (Dependency d : dependencies1) {
                            if (d.getStartTask().equals(new_dep.getStartTask()) && d.getEndTask().equals(new_dep.getEndTask())) {
                                hasAdded = true;
                            }
                        }
                        if (!hasAdded)
                            dependencies2.add(new Dependency(dependency.getStartTask(), dependency1.getEndTask(), -1));
                    }
                }
            }

            if (num_deps == dependencies2.size()) {
                break;
            }
            dependencies1.clear();
            dependencies1.addAll(dependencies2);
        }


        List<Task> tasks1 = new ArrayList<>(tasks);
        Collections.sort(tasks1, (t1, t2) -> {
            int num = 0;
            for (Dependency dependency : dependencies2) {
                if (dependency.getStartTask().equals(t1) && dependency.getEndTask().equals(t2)) {
                    num = -1;
                }
                if (dependency.getStartTask().equals(t2) && dependency.getEndTask().equals(t1)) {
                    num = 1;
                }
            }
            return num;
        });

        return tasks1;
    }
}
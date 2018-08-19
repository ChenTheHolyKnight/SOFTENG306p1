package op.model;

import java.util.*;

/**
 * This class creates string representations of the edges and nodes of a TaskGraph
 *
 * @author Victoria Skeggs
 */
public class TaskGraphToStringConverter {

    private List<Task> tasks;

    public TaskGraphToStringConverter() {
        tasks = TaskGraph.getInstance().getAllTasks();
    }

    /**
     * @return a set of IDs of each node in the input graph
     */
    public Set<String> createNodes() {
        Set<String> taskIds = new HashSet<>();
        for (Task task: tasks) {
            taskIds.add(task.getId());
        }
        return taskIds;
    }

    /**
     * @return a string abstraction of each edge in the input graph
     */
    public Map<String, List<String>> createEdges() {
        Map<String, List<String>> edges = new HashMap<>();
        for (Task task : tasks) {
            List<String> children = new ArrayList<>();
            for (Dependency dependency : task.getOutgoingDependencies()) {
                children.add(dependency.getEndTask().getId());
            }
            edges.put(task.getId(), children);
        }
        return edges;
    }
}

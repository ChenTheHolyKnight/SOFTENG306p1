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
     * @param tasks all the tasks in the input graph
     * @return a list of tasks that sorted in topological order
     */
    public static List<Task> createTopologicalOrder(List<Task> tasks){
        /*TaskGraph tg= TaskGraph.getInstance();
        List<Dependency> dependencies=new ArrayList<>();
        tasks.forEach(task -> {
            dependencies.addAll(tg.getIncomingDependencies(task));
        });
        //dependencies.forEach(dependency -> System.out.println(dependency.getStartTask().getId()+"&"+dependency.getEndTask().getId()));

        List<Dependency> dependencies1=new ArrayList<>(dependencies);

        while(true){
            int num_deps=dependencies1.size();
            for(Dependency dependency:dependencies1){
                for(Dependency dependency1:dependencies1){
                    if(dependency.getEndTask().equals(dependency1.getStartTask())){
                        dependencies1.add(new Dependency(dependency.getStartTask(),dependency1.getEndTask(),-1));
                    }
                }
            }
            if(num_deps==dependencies1.size()){
                break;
            }

        }

        List<Task> tasks1=new ArrayList<>(tasks);
        tasks.forEach(task -> System.out.println(task.getId()));



        //System.out.println("After");
        /*Collections.sort(tasks1,(t1,t2)->{
            int num=0;
            for(Dependency dependency : dependencies) {
                //System.out.println(dependency.getStartTask().getId() +" & "+ t1.getId());
                if (dependency.getStartTask().getId() == t1.getId() && dependency.getEndTask().getId() == t2.getId()) {
                    num = -1;
                }
                if (dependency.getStartTask().getId() == t2.getId() && dependency.getEndTask().getId() == t1.getId()) {
                    num = 1;
                }

            }

            //System.out.println("break");
            return num;
        });
        for(Task task1:tasks){
            for(Task task2:tasks){
                for(Dependency dependency:dependencies){
                    Task start=dependency.getStartTask();
                    Task end=dependency.getEndTask();
                    if((start.equals(task1)&&end.equals(task2))){
                        Collections.swap(tasks1,tasks.indexOf(task1),tasks1.indexOf(task2));
                    }
                }
            }
        }


        tasks1.forEach(task1 -> System.out.println(task1.getId()));
        return tasks;*/
        List<Task> tasks1=TaskGraph.getInstance().getAllTasks();
        tasks1.forEach(task -> System.out.print(task.getId()));
        System.out.println("");
        topologicalSort();
        return null;
    }


    private static void topologicalUtil(Task v, boolean visited[],Stack stack){

        TaskGraph tg=TaskGraph.getInstance();
        // Mark the current node as visited.
        List<Task> tasks=tg.getAllTasks();
        visited[tasks.indexOf(v)] = true;
        Task i;

        // Recur for all the vertices adjacent to this
        // vertex

        Iterator<Dependency> it = tg.getIncomingDependencies(v).iterator();
        while (it.hasNext())
        {
            i = it.next().getEndTask();
            if (!visited[tasks.indexOf(i)])
                topologicalUtil(i, visited, stack);
        }

        // Push current vertex to stack which stores result
        stack.push(v);
    }

    private static void topologicalSort() {
        Stack<Task> stack = new Stack();
        List<Task> tasks=TaskGraph.getInstance().getAllTasks();
        int V=tasks.size();
        // Mark all the vertices as not visited
        boolean visited[] = new boolean[V];
        for (int i = 0; i < V; i++)
            visited[i] = false;

        // Call the recursive helper function to store
        // Topological Sort starting from all vertices
        // one by one
        for (int i = 0; i < V; i++)
            if (visited[i] == false)
                topologicalUtil(tasks.get(i), visited, stack);

        // Print contents of stack
        while (stack.empty()==false)
            System.out.print(stack.pop().getId() + " ");

    }
}

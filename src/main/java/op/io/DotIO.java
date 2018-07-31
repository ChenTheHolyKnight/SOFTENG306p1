package op.io;

import op.algorithm.Dependency;
import op.algorithm.Task;
import op.algorithm.TaskGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The beginnings of a DotIO class that will read in a .dot file and create a Graph object that can be used in the
 * algorithm. This class will also produce a dot file from the resultant graph after the algorithm has been run.
 * (obviously) still in very early stages so subject to change.
 *
 * @author Sam Broadhead
 */
public class DotIO {
    private String file;
    private String title;
    private BufferedReader br;
    private List<Task> taskList;
    private List<Dependency> depList;
    private Map<Integer, Task> taskMap;

    /**
     * Constructor for a new DotIO.
     * @param path the path to the Dot file to be read in.
     */
    public DotIO(String path){
        this.file = path;
    }

    /**
     * method that reads the dot file in
     * @return g - a graph that represents the dot file.
     * @throws IOException if the file can't be found
     */
    public TaskGraph dotIn() throws IOException {
        title = "";
        depList = new ArrayList<Dependency>();
        taskMap = new HashMap<Integer, Task>();
        br = new BufferedReader(new FileReader(file));
        String line = "";
        while((line = br.readLine()) != null){
            if(line.contains("{")) {
                //beginning of graph
                String [] parts = line.split("\"");
                System.out.println(parts[1]);
                title = parts[1];
            } else if (line.contains("}")){
                //end of graph
                continue;
            }else if(line.contains("−>")){
                //Dependency
                line = line.replaceAll("\\s","");
                String [] parts = line.split("\\[");
                String [] tasks = parts[0].split("−>");
                parts[1] = parts[1].replaceAll("[^0-9]+","");
                int startTask = Integer.parseInt(tasks[0]);
                int endTask = Integer.parseInt(tasks[1]);
                int depWeight = Integer.parseInt(parts[1]);
                System.out.println("New dependency with Start Task: " + tasks[0] + " End Task: " + tasks[1] + " Weight: " + parts[1]);
                depList.add(new Dependency(taskMap.get(startTask), taskMap.get(endTask), depWeight));
            }else{
                //Task
                line = line.replaceAll("\\s","");
                String [] parts = line.split("\\[");
                parts[1] = parts[1].replaceAll("[^0-9]+","");
                System.out.println("New task with id: "+parts[0]+" and weight: "+parts[1]);
                int taskID = Integer.parseInt(parts[0]);
                int taskWeight = Integer.parseInt(parts[1]);
                taskMap.put(taskID,new Task(taskID, taskWeight));
                //taskList.add(new Task(taskID, taskWeight)); if we opt for a list implementation for tasks
            }
        }
        taskList = new ArrayList<Task>(taskMap.values());
        TaskGraph tg = new TaskGraph();
        return tg;
    }

    /**
     * method that writes out the scheduled graph to a dot file
     */
    public void dotOut(){

    }
}

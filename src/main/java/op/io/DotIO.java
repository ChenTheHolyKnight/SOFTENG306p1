package op.io;

import op.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The beginnings of a DotIO class that will read in a .dot file and create a Graph object that can be used in the
 * algorithm. This class will also produce a dot file from the resultant graph after the algorithm has been run.
 * (obviously) still in very early stages so subject to change.
 *
 * @author Sam Broadhead (dotIn()) Darcy Cox (dotOut())
 */
public class DotIO {

    // regex + string constants commonly used in DOT files
    private static final String WHITE_SPACE = "\\s";
    private static final String ID_WEIGHT_SPLIT = "\\[";
    private static final String NON_NUMBERS = "[^0-9]+";
    private static final String QUOTES = "\"";
    private static final String GRAPH_START = "{";
    private static final String GRAPH_END = "}";
    private static final String INDENT = "\t";
    private static final String ATTR_START = "[";
    private static final String ATTR_END = "]";
    private static final String ATTR_SEPARATOR = ",";
    private static final String WEIGHT_ATTR_SPECIFIER = "Weight=";
    private static final String START_ATTR_SPECIFIER = "Start=";
    private static final String PROC_ATTR_SPECIFIER = "Processor=";
    private static final String STATEMENT_END = ";";
    private static final String DEP_SPECIFIER = "−>";


    /**
     * Constructor for a new DotIO.
     */
    public DotIO(){    }

    /**
     * method that reads the dot file in and initializes the TaskGraph instance
     * @throws IOException if the file can't be found
     */
    public void dotIn(String path) throws IOException {

        String file = path;
        String title = "";
        List<Dependency> depList = new ArrayList<Dependency>();
        List<Task> taskList;
        Map<Integer, Task> taskMap = new HashMap<Integer, Task>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while((line = br.readLine()) != null){
            if(line.contains(GRAPH_START)) {
                //beginning of graph
                String [] parts = line.split(QUOTES);
                System.out.println(parts[1]);
                title = parts[1];
            } else if (line.contains(GRAPH_END)){
                //end of graph
                continue;
            }else if(line.contains(DEP_SPECIFIER)){
                //Dependency
                line = line.replaceAll(WHITE_SPACE,""); // get rid of spaces and tabs
                String [] parts = line.split(ID_WEIGHT_SPLIT); // split between the tasks and the weight
                String [] tasks = parts[0].split(DEP_SPECIFIER); // split the two tasks
                parts[1] = parts[1].replaceAll(NON_NUMBERS,""); //strip everything not a number from the
                // weight string to get weight
                int startTask = Integer.parseInt(tasks[0]); // parse strings to int for Dependency object
                int endTask = Integer.parseInt(tasks[1]);
                int depWeight = Integer.parseInt(parts[1]);
                System.out.println("New dependency with Start Task: " + tasks[0] + " End Task: " + tasks[1] +
                        " Weight: " + parts[1]);
                depList.add(new Dependency(taskMap.get(startTask), taskMap.get(endTask), depWeight)); // create a
                // dependency object and add it to the last of dependencies
            }else{
                //Task
                line = line.replaceAll(WHITE_SPACE,"");
                String [] parts = line.split(ID_WEIGHT_SPLIT);
                parts[1] = parts[1].replaceAll(NON_NUMBERS,"");
                System.out.println("New task with id: " + parts[0] + " and weight: "+ parts[1]);
                int taskID = Integer.parseInt(parts[0]);
                int taskWeight = Integer.parseInt(parts[1]);
                taskMap.put(taskID,new Task(taskID, taskWeight));
                //taskList.add(new Task(taskID, taskWeight)); if we opt for a list implementation for tasks
            }
        }
        taskList = new ArrayList<Task>(taskMap.values());

        TaskGraph.initialize(taskList, depList, title);
    }

    /**
     * Writes out a dot file of a complete schedule based on the task graph
     * @param path The path of the file to be written to
     * @throws IOException if anything with File IO goes wrong
     */
    public void dotOut(String path, Schedule s) throws IOException {

        BufferedWriter bw = new BufferedWriter(new FileWriter(path, false));

        TaskGraph tg = TaskGraph.getInstance();
        String title = tg.getTitle();
        List<Task> tasks = tg.getAllTasks();

        // write the graph title
        bw.write(constructTitleLine(title));

        // we don't need to preserve input DOT file order so we first write out all tasks
        for (Task t : tasks) {
            bw.write(constructTaskLine(s.getScheduledTask(t)));
        }

        // write dependencies
        for (Task t : tasks) {
            // consider outgoing dependencies of all nodes
            // this will cover all existing dependencies as every dependency must have an origin node
            List<Dependency> outgoingDeps = tg.getOutgoingDependencies(t);
            for (Dependency d: outgoingDeps) {
                bw.write(constructDependencyLine(d));
            }
        }

        // close up dot file
        System.out.println(GRAPH_END);
        bw.write(GRAPH_END);
        bw.close();
    }

    // Helpers to construct lines of the dot file
    private String constructDependencyLine(Dependency dep) {
        StringBuilder sb = new StringBuilder();
        sb.append(INDENT + dep.getStartTask().getId() + " " + DEP_SPECIFIER + " " + dep.getEndTask().getId());
        sb.append(INDENT + ATTR_START + WEIGHT_ATTR_SPECIFIER + dep.getWeight() + ATTR_END + STATEMENT_END);
        sb.append(System.getProperty("line.separator"));

        System.out.print(sb.toString());
        return sb.toString();
    }

    private String constructTaskLine(ScheduledTask st) {
        Task t = st.getTask();
        StringBuilder sb = new StringBuilder();

        // write task id
        sb.append(INDENT + t.getId());
        // write task attributes
        sb.append(INDENT + ATTR_START + WEIGHT_ATTR_SPECIFIER + t.getDuration())
                .append(ATTR_SEPARATOR)
                .append(START_ATTR_SPECIFIER + st.getStartTime())
                .append(ATTR_SEPARATOR)
                .append(PROC_ATTR_SPECIFIER + st.getProcessor())
                .append(ATTR_END + STATEMENT_END);
        sb.append(System.getProperty("line.separator"));

        System.out.print(sb.toString());
        return sb.toString();
    }

    private String constructTitleLine(String title) {
        String line = "digraph " + QUOTES + title + QUOTES + " " + GRAPH_START + System.getProperty("line.separator");
        System.out.print(line);
        return line;
    }

}

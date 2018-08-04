package op.io;

import op.model.Dependency;
import op.model.Schedule;
import op.model.Task;
import op.model.TaskGraph;
import op.model.ScheduledTask;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that will read in a .dot file and create a Graph object that can be used in the
 * algorithm. This class will also produce a dot file from the resultant graph after the algorithm has been run.
 */
public class DotIO {

    // regex + string constants commonly used in DOT files
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
    private static final String DEP_SPECIFIER = "->";

    /*
        things to look for are as follows:
        - name of graph
        - task (id(String) and weight(int))
        - dependency (src task, dest task, weight(int))
        - end of graph ( } )
        use Strings for line matches and patterns where data needs to be extracted from the line.
        based on my interpretation of what a valid .dot file is.
     */
    private static final String GRAPH_NAME_LINE_MATCH = "[\\s]*digraph[\\s]*\".*\"[\\s]*\\{[\\s]*";
    private static final String TASK_LINE_MATCH = "[\\s]*[\\p{Alnum}]*[\\s]*\\[[\\s]*[Ww]eight[\\s]*[=]*[\\p{Digit}]*[\\s]*][\\s]*;";
    private static final String DEP_LINE_MATCH = "[\\s]*[\\p{Alnum}]*[\\s]*.>[\\s]*[\\p{Alnum}]*[\\s]*\\[[\\s]*[Ww]eight[\\s]*[=][\\s]*[\\p{Digit}]*[\\s]*][\\s]*;";
    private static final String END_OF_GRAPH_MATCH = "[\\s]*}[\\s]*";

    private static final Pattern GRAPH_NAME_MATCH = Pattern.compile("[\\s]*digraph[\\s]*\"(.*)\"[\\s]*\\{[\\s]*");
    private static final Pattern TASK_NAME_MATCH = Pattern.compile("[\\s]*([\\p{Alnum}]*)[\\s]*\\[[\\s]*[Ww]eight[\\s]*[=][\\s]*[\\p{Digit}]*[\\s]*][\\s]*;");
    private static final Pattern TASK_WEIGHT_MATCH = Pattern.compile("[\\s]*[\\p{Alnum}]*[\\s]*\\[[\\s]*[Ww]eight[\\s]*[=][\\s]*([\\p{Digit}]*)[\\s]*][\\s]*;");
    private static final Pattern DEP_SRC_TASK_MATCH = Pattern.compile("[\\s]*([\\p{Alnum}]*)[\\s]*.>[\\s]*[\\p{Alnum}]*[\\s]*\\[[\\s]*[Ww]eight[\\s]*[=][\\s]*[\\p{Digit}]*[\\s]*][\\s]*;");
    private static final Pattern DEP_DST_TASK_MATCH = Pattern.compile("[\\s]*[\\p{Alnum}]*[\\s]*.>[\\s]*([\\p{Alnum}]*)[\\s]*\\[[\\s]*[Ww]eight[\\s]*[=][\\s]*[\\p{Digit}]*[\\s]*][\\s]*;");
    private static final Pattern DEP_WEIGHT_MATCH = Pattern.compile("[\\s]*[\\p{Alnum}]*[\\s]*.>[\\s]*[\\p{Alnum}]*[\\s]*\\[[\\s]*[Ww]eight[\\s]*[=][\\s]*([\\p{Digit}]*)[\\s]*][\\s]*;");

    private static final int MATCHER_GROUP = 1;
    private static final int DEFAULT_WEIGHT = 0;

    /**
     * Constructor for a new DotIO.
     */
    public DotIO(){    }

    /**
     * New revised dotIn method that will replace previous implementation. Major upgrades for robustness.
     * @param path The path of the dot file to read.
     * @throws IOException If there is a problem reading the file
     * @author Sam Broadhead
     */
    public void dotIn(String path) throws IOException {
        String line;
        String title = "";
        List<Dependency> depList = new ArrayList<>(); // lists to initialize the task graph
        List<Task> taskList = new ArrayList<>();
        Map<String, Integer> taskMap = new HashMap<>(); // keep a map of unbuilt tasks to allow updates
        Map<String[], Integer> depMap = new HashMap<>(); // keep a map of unbuilt deps to allow updates
        BufferedReader br = new BufferedReader(new FileReader(path));
        while (((line = br.readLine()) != null) && (!line.contains(END_OF_GRAPH_MATCH))) {
            if (line.matches(GRAPH_NAME_LINE_MATCH)) { // the line is the graph name line
                title = getStringMatch(GRAPH_NAME_MATCH, line);
            } else if (line.matches(TASK_LINE_MATCH)) { // the line is a task line
                String  name = getStringMatch(TASK_NAME_MATCH, line);
                int weight = Integer.parseInt(getStringMatch(TASK_WEIGHT_MATCH, line));
                taskMap.put(name, weight);
            } else if (line.matches(DEP_LINE_MATCH)) { // the line is a dependency line
                String src = getStringMatch(DEP_SRC_TASK_MATCH, line);
                String dst = getStringMatch(DEP_DST_TASK_MATCH, line);
                if (!taskMap.containsKey(src)) { // if there are unknown tasks in the dependency, add them
                    taskMap.put(src, DEFAULT_WEIGHT);
                }
                if (!taskMap.containsKey(dst)) {
                    taskMap.put(dst, DEFAULT_WEIGHT);
                }
                String [] tasks = {src,dst}; // store the two task ids as the key
                int weight = Integer.parseInt(getStringMatch(DEP_WEIGHT_MATCH, line));
                depMap.put(tasks, weight);
            }
        }
        for (String[] keys: depMap.keySet()){ // build task and dependency objects.
            Task srcTask = new Task(keys[0],taskMap.get(keys[0]));
            if (!taskList.contains(srcTask)){ // if the task is not in the list, add it
                taskList.add(srcTask);
            }
            Task dstTask = new Task(keys[1],taskMap.get(keys[1]));
            if (!taskList.contains(dstTask)){
                taskList.add(dstTask);
            }
            depList.add(new Dependency(srcTask,dstTask,depMap.get(keys)));
        }
        for (String key : taskMap.keySet()){ // build any tasks that have no ingoing or outgoing dependencies
            Task t = new Task(key,taskMap.get(key));
            if (!taskList.contains(t)){ // if they aren't in the task list, add them
                taskList.add(t);
            }
        }
        /* check dotIn here
        for (Dependency d: depList){
            System.out.println("Dependency with src Task: "+d.getStartTask().getId()+" and dst Task: "
                    +d.getEndTask().getId()+" and weight: "+d.getWeight());
        }
        for (Task t : taskList){
            System.out.println("Task with id: "+t.getId()+" and weight: "+t.getDuration());
        }*/
        TaskGraph.initialize(taskList, depList, title);
    }

    /**
     * Helper method to find matches
     * @param toMatch A Pattern for the Matcher to match to.
     * @param str Sequence for Matcher to match against the Pattern.
     * @return the String that is found.
     * @throws IOException If no match is found, meaning the DOT file is invalid.
     * @author Sam Broadhead
     */
    private String getStringMatch(Pattern toMatch, String str) throws IOException {
        Matcher m = toMatch.matcher(str);
        if(m.find()){
            return m.group(MATCHER_GROUP);
        } else {
            throw new IOException();
        }
    }

    /**
     * Writes out a dot file of a complete schedule based on the task graph
     * @throws IOException if anything with File IO goes wrong
     * @author Darcy Cox
     */

    public void dotOut(Schedule s) throws IOException {

        BufferedWriter bw = new BufferedWriter(new FileWriter(Arguments.getInstance().getOutputGraphFilename(),
                false));

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

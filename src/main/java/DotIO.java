import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * The beginnings of a DotIO class that will read in a .dot file and create a Graph object that can be used in the
 * algorithm. This class will also produce a dot file from the resultant graph after the algorithm has been run.
 * (obviously) still in very early stages so subject to change.
 *
 * @author Sam Broadhead
 */
public class DotIO {
    private String file;
    private BufferedReader br;

    /**
     * Constructor for a new DotIO.
     * @param path the path to the Dot file to be read in.
     */
    public DotIO(String path){
        this.file = path;
        try {
            Graph g = DotIn();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * method that reads the dot file in
     * @return g - a graph that represents the dot file.
     * @throws IOException if the file can't be found
     */
    public Graph DotIn() throws IOException {
        Graph g = new SingleGraph("graph"); //"stub" so a graph is returned
        br = new BufferedReader(new FileReader(file));
        String line = "";
        while((line = br.readLine()) != null){
            if(line.contains("{")||line.contains("}")){
                //end or beginning of graph
                continue;
            }else if(line.contains("−>")){
                //do stuff for a vertice here
            }else{
                //edge
                line = line.replaceAll("\\s","");
                String [] parts = line.split("\\[");
                parts[1] = parts[1].replaceAll("[^0-9]+","");
                System.out.println("New edge with id: "+parts[0]+" and weight: "+parts[1]);
            }
        }
        return g;
    }

    /**
     * method that writes out the scheduled graph to a dot file
     * @param name optional name flag of the dot file
     * @param g the graph to be written out
     */
    public void DotOut(Graph g, String name){

    }
}

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DotIO {
    private String file;
    private BufferedReader br;
    public DotIO(String path){
        this.file = path;
        try {
            Graph g = DotIn();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
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
}

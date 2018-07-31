package op;

import op.visualization.Visualiser;
import op.visualization.messages.MessageAddNode;
import op.visualization.messages.MessageEliminateChildren;

/**
 * Entry point for the optimal scheduling program
 */
public class Application {
    public static void main(String[] args) {
        System.out.println("it works!");
        Visualiser v = new Visualiser();
        v.update(new MessageAddNode("a", "b"));
        v.update(new MessageAddNode("a", "c"));
        v.update(new MessageAddNode("c", "d"));
        v.update(new MessageAddNode("d", "e"));
        v.update(new MessageAddNode("d", "f"));
        v.update(new MessageAddNode("c", "6"));
        v.update(new MessageAddNode("c", "7"));
        v.update(new MessageAddNode("c", "8"));
        v.update(new MessageAddNode("6", "9"));
        v.update(new MessageAddNode("6", "10"));
        
        v.update(new MessageAddNode("1", "a"));
        v.update(new MessageAddNode("1", "2"));
        v.update(new MessageAddNode("2", "3"));
        v.update(new MessageAddNode("2", "4"));
        v.update(new MessageAddNode("2", "5"));
        v.update(new MessageEliminateChildren("c"));

        //   DotIO iotest = new DotIO("src/main/resources/sample_inputs/test.dot");
    }

    public static int returnTwo() {
        return 2;
    }
}

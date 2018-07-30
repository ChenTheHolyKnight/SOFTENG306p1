package op;

import op.algorithm.TaskGraph;
import op.io.DotIO;

import java.io.IOException;

/**
 * Entry point for the optimal scheduling program
 */
public class Application {
    public static void main(String[] args) {
        System.out.println("it works!");
        DotIO iotest = new DotIO("src/main/resources/sample_inputs/test.dot");
        try {
            TaskGraph tg = iotest.dotIn();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int returnTwo() {
        return 2;
    }
}

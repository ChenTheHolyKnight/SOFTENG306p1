import op.algorithm.SimpleScheduler;
import op.io.DotIO;
import op.model.TaskGraph;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.Assert.fail;

public class TestDotIO {


    @After
    public void resetSingleton() {
        try {
            SingletonTesting.resetTaskGraph();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddTask(){
        DotIO testIo = new DotIO();
        try {
            testIo.dotIn("src/main/resources/sample_inputs/test.dot");

            // this is commented because we don't want the repo/travis to write files. need to figure out
            // a good way of testing File output operations. Feel free to uncomment and check the output file but
            // make sure it's not committed to the repo.
            //testIo.dotOut("out.dot", new SimpleScheduler().produceSchedule(1));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAddDependency(){
        DotIO testio = new DotIO();
        try {
            testio.dotIn("src/main/resources/sample_inputs/test.dot");
        } catch (IOException e) {
            fail();
        }
    }
}

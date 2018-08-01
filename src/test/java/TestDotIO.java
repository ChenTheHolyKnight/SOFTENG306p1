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
            SingletonTesting.resetSingleton();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddTask(){
        DotIO testio = new DotIO();
        try {
            testio.dotIn("src/main/resources/sample_inputs/test.dot");
        } catch (IOException e) {
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

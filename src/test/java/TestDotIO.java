import op.io.DotIO;
import op.model.TaskGraph;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.fail;

public class TestDotIO {
    @Test
    public void testAddTask(){
        DotIO testio = new DotIO();
        try {
            TaskGraph tg = testio.dotIn("src/main/resources/sample_inputs/test.dot");
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testAddDependency(){
        DotIO testio = new DotIO();
        try {
            TaskGraph tg = testio.dotIn("src/main/resources/sample_inputs/test.dot");
        } catch (IOException e) {
            fail();
        }
    }
}

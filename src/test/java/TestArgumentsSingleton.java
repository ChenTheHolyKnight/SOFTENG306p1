import op.io.Arguments;
import op.model.AlreadyInitializedException;
import op.model.UninitializedException;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * Test class to ensure the Arguments singleton behaves as expected
 * @author Darcy Cox
 */
public class TestArgumentsSingleton {

    @After
    public void resetSingleton() {
        try {
            SingletonTesting.resetArguments();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testExceptions() {
        try {
            Arguments.getInstance();
            fail();
        } catch(UninitializedException e) {}

        try {

            Arguments.initialize("yo", 2, 3, true, "yes");
            Arguments.getInstance();
            Arguments.initialize("yo", 2, 3, true, "yes");
            fail();
        } catch (AlreadyInitializedException e) {}
    }
}

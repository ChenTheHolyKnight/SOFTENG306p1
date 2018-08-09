import op.model.Arguments;
import op.model.TaskGraph;

import java.lang.reflect.Field;

/**
 * Provides convenience methods to reset a singleton instance to avoid errors when multiple tests use it.
 */
public class TestSingletonUtil {

    public static void resetTaskGraph()
            throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {

        Field instance = TaskGraph.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
        instance.setAccessible(false);

        Field init = TaskGraph.class.getDeclaredField("initialized");
        init.setAccessible(true);
        init.setBoolean(null, false);
        init.setAccessible(false);
    }
}

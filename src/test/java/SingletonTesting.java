import op.io.Arguments;
import op.model.TaskGraph;

import java.lang.reflect.Field;

/**
 * Provides convenience method to reset the TaskGraph instance to avoid errors when multiple tests use it.
 */
public class SingletonTesting {

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

    public static void resetArguments()
            throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {

        Field instance = Arguments.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
        instance.setAccessible(false);

        Field init = Arguments.class.getDeclaredField("initialized");
        init.setAccessible(true);
        init.setBoolean(null, false);
        init.setAccessible(false);
    }
}

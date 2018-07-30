package op.algorithm;

/**
 * Class containing logic to produce the simplest valid schedule. Used as a fall-back option.
 */
public class SimpleScheduler extends Scheduler {

    @Override
    public Schedule produceSchedule(TaskGraph tg, int numProcessors) {
        return null;
    }
}

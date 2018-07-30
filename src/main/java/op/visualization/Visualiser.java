package op.visualization;

/**
 * Interface that any visualisation implementation must conform to.
 * Defines how other components can control the visualisation
 */
public interface Visualiser {

    /**
     * Updates the visualisation state
     * @param u the update message. contains the necessary information about which state should be changed.
     */
    public void update(VisualisationUpdate u);

    public void start();
}

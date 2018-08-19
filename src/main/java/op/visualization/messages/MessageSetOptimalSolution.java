package op.visualization.messages;

/**
 * This message is to be used when
 */
public class MessageSetOptimalSolution implements UpdateMessage {
    private String optimalSolution;

    public MessageSetOptimalSolution(String nodeId) {
        this.optimalSolution = nodeId;
    }

    public String getOptimalSolution() {
        return optimalSolution;
    }
}

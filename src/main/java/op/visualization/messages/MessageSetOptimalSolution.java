package op.visualization.messages;

import java.util.List;

/**
 * This message is to be used when
 */
public class MessageSetOptimalSolution implements UpdateMessage {
    private List<String> optimalDescendantLine;

    public MessageSetOptimalSolution(List<String> nodesIds) {
        this.optimalDescendantLine = nodesIds;
    }

    public List<String> getOptimalDescendantLine() {
        return optimalDescendantLine;
    }
}

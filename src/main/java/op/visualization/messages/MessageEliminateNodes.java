package op.visualization.messages;
import java.util.Set;

public class MessageEliminateNodes implements UpdateMessage{
	private Set<String> nodeIds;

	/**
	 *
	 * @param nodeIds The Id of the parent node of our new node
	 */
	public MessageEliminateNodes(Set<String> nodeIds) {
		this.nodeIds = nodeIds;
	}

	public Set<String> getNodeIds() {
		return nodeIds;
	}
}



package op.visualization.messages;

import java.util.Set;

/**
 * Message to add new nodes to the graph display
 *
 * @author Ravid
 *
 */
public class MessageAddNodes implements UpdateMessage {
	private String parentNodeId;
	private Set<String> childNodeIds;

	/**
	 * @param parentNodeId the ID of the existing parent node
	 * @param childNodeIds the IDs of the children nodes to add
	 */
	public MessageAddNodes(String parentNodeId, Set<String> childNodeIds) {
		this.parentNodeId = parentNodeId;
		this.childNodeIds = childNodeIds;
	}

	public String getParentNodeId() {
		return parentNodeId;
	}

	public Set<String> getChildNodeIds() {
		return childNodeIds;
	}
}

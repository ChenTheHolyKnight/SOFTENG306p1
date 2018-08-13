package op.visualization.messages;

import java.util.List;

/**
 * Message to add new nodes to the graph display
 * 
 * @author Ravid
 *
 */
public class MessageAddNodes implements UpdateMessage {
	private String parentNodeId;
	private List<String> childNodeIds;

	/**
	 * @param parentNodeId the ID of the existing parent node
	 * @param childNodeIds the IDs of the children nodes to add
	 */
	public MessageAddNodes(String parentNodeId, List<String> childNodeIds) {
		this.parentNodeId = parentNodeId;
		this.childNodeIds = childNodeIds;
	}
	
	public String getParentNodeId() {
		return parentNodeId;
	}
	
	public List<String> getChildNodeIds() {
		return childNodeIds;
	}
}

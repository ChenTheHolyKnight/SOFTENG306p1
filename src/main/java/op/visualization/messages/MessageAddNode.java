package op.visualization.messages;

/**
 * Message to add a new node in the graphDisplay
 * 
 * @author Ravid
 *
 */
public class MessageAddNode implements UpdateMessage {
	private String parentNodeId;
	private String newNodeId;

	/**
	 * @param parentNodeId The Id of the parent node of our new node
	 * @param endNodeId The Id of our new node
	 */
	public MessageAddNode (String parentNodeId, String newNodeId) {
		this.parentNodeId = parentNodeId;
		this.newNodeId = newNodeId;
	}
	
	public String getParentNodeId() {
		return parentNodeId;
	}
	
	public String getNewNodeId() {
		return newNodeId;
	}
}

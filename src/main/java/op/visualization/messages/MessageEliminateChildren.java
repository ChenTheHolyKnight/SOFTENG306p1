package op.visualization.messages;


public class MessageEliminateChildren implements UpdateMessage{
	private String parentNodeId;
	
	/**
	 * 
	 * @param parentNodeId The Id of the parent node of our new node
	 */
	public MessageEliminateChildren (String parentNodeId) {
		this.parentNodeId = parentNodeId;
	}
	
	public String getParentNodeId() {
		return parentNodeId;
	}
}



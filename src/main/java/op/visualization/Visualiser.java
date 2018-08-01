package op.visualization;

import op.visualization.messages.MessageAddNode;
import op.visualization.messages.MessageEliminateChildren;
import op.visualization.messages.UpdateMessage;

/**
 * Interface that any visualisation implementation must conform to.
 * Defines how other components can control the visualisation
 */
public class Visualiser {
	
	private GraphDisplay graphDisplay = GraphDisplay.getInstance();


    /**
     * Updates the visualisation state
     * @param u the update message. contains the necessary information about which state should be changed.
     */
    public void update(UpdateMessage u) {
    	if (u instanceof MessageAddNode) {
    		graphDisplay.addNode(((MessageAddNode) u).getParentNodeId(), ((MessageAddNode) u).getNewNodeId());
    	}
    	if (u instanceof MessageEliminateChildren) {
    		graphDisplay.eliminateChildren(((MessageEliminateChildren) u).getParentNodeId());
    	}
    }
}

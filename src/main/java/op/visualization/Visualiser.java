package op.visualization;

import op.visualization.messages.MessageAddNode;
import op.visualization.messages.MessageEliminateChildren;
import op.visualization.messages.UpdateMessage;
import op.visualization.controller.GraphController;

/**
 * Interface that any visualisation implementation must conform to.
 * Defines how other components can control the visualisation
 */
public class Visualiser {
	
	private GraphController graphController = GraphController.getInstance();

    /**
     * Updates the visualisation state
     * @param u the update message. contains the necessary information about which state should be changed.
     */
    public void update(UpdateMessage u) {
    	if (u instanceof MessageAddNode) {
    		graphController.addNode(((MessageAddNode) u).getParentNodeId(), ((MessageAddNode) u).getNewNodeId());
    	}
    	if (u instanceof MessageEliminateChildren) {
    		graphController.eliminateChildren(((MessageEliminateChildren) u).getParentNodeId());
    	}
    }
}

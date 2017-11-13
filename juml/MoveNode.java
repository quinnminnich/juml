package juml;
import javafx.scene.Node;
import umlobject.*;

public class MoveNode extends CommandObject{

	
	double startX;
	double startY;
	double endX;
	double endY;
	UMLNode node;
	
	public MoveNode(UMLNode node, double startX, double startY) {
		this (node, startX, startY, true);
	}
	
	public MoveNode(UMLNode node, double startX, double startY, Boolean addToStack) {
		super(addToStack);
		this.startX = startX;
		this.startY = startY;
		this.endX = node.getOriginX();
		this.endY = node.getOriginY();
		this.node = node;
		doAction();
	}
	
	// move from "start" to "end"
	public void doAction() {
		node.move(endX, endY);
	}

	// move from "end" to "start"
	public void undoAction() {
		node.move(startX, startY);
	}
	
	
	
}

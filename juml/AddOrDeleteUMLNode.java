package juml;

import java.util.Vector;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import juml.Controller.Mode;
import umlobject.*;



public class AddOrDeleteUMLNode extends CommandObject {

	private UMLNode object;
	private boolean isCreationObject;
	private Vector<UMLConnector> connections;
	private Vector<CommandObject> deletedConnections;
	
	
	public AddOrDeleteUMLNode(UMLNode n, boolean isCreator) {
		this(n, isCreator, true);
	}

	
	@SuppressWarnings("unchecked")
	public AddOrDeleteUMLNode(UMLNode n, boolean isCreator, boolean addToStack) {
		super(addToStack);
		object = n;
		connections = (Vector<UMLConnector>) object.getConnections().clone();
		//System.out.println("Delete object created. Number of connectors is: "+connections.size());
		deletedConnections = new Vector<CommandObject>();
		isCreationObject = isCreator;
		doAction();
	}

	
	public void doAction() {
		if (isCreationObject)
			create();
		else {
			delete();
		}
	}
	
	public void undoAction() {
		if (isCreationObject)
			delete();
		else {
			create();
		}
	}
	
	private void create	() {
		Node model = object.getModel();
		pane.getChildren().add(model);
		NODES.put(model, object);
		//this is for undoing if this is a delete object
		for (CommandObject c : deletedConnections) {
			c.undoAction();
			//System.out.println("Rebuilt a connector");
		}
	}
	
	private void delete() {
		deletedConnections.clear();
		for (UMLConnector c : connections) {
			deletedConnections.addElement(new AddUMLConnector(c, false, false));
			//System.out.println("Deleted a connector");
		}
		NODES.remove(object.getModel());
		pane.getChildren().remove(object.getModel());
	}
	
	


}

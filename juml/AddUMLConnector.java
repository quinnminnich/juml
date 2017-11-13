package juml;
import javafx.scene.Node;
import umlobject.*;

public class AddUMLConnector extends CommandObject {

	private UMLConnector connector;
	private boolean isCreationObject;
	
	
	public AddUMLConnector(UMLConnector c, boolean isCreator) {
		this (c, isCreator, true);
	}
	
	public AddUMLConnector(UMLConnector c, boolean isCreator, boolean addToStack) {
		super(addToStack);
		connector = c;
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
		Node model = connector.getModel();
		pane.getChildren().add(model);
		CONNECTORS.put(model, connector);
		connector.getStart().connections.add(connector);
		connector.getStop().connections.add(connector);
	}
	
	private void delete() {
		CONNECTORS.remove(connector.getModel());
		connector.disconnect();
		if (pane == null) {
			System.out.println("no Pane");
		}
		if (pane.getChildren() == null) {
			System.out.println("no children to pane");
		}
		pane.getChildren().remove(connector.getModel());
	}
	
	
}

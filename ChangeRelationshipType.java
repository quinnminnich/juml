package umlaction;

import java.util.Scanner;
import java.util.Vector;

import juml.*;
import umlobject.*;

public class ChangeRelationshipType extends UMLAction {

	
	// RelationshipType is defined in the UMLAction class

	DeleteUMLConnector deleteOldLine;
	AddUMLConnector addNewLine;
	Controller controller;
	UMLObject oldConnector;
	UMLObject newConnector;
	
	public ChangeRelationshipType(Relationship original, RelationshipType newRelationship, Controller c) {
		controller = c;
		controller.deselectAll();
		oldConnector = original;
		Vector<UMLNode> allNodes = new Vector<UMLNode>();
		allNodes.add(original.start);
		allNodes.add(original.stop);
		String newRelationshipSaveString = ((UMLConnector) original).saveAsString(allNodes);
		newRelationshipSaveString += original.saveAsString();
		deleteOldLine = new DeleteUMLConnector(original, c);
		UMLConnector newConnector = null;
		Scanner input = new Scanner(newRelationshipSaveString);
		if (newRelationship == RelationshipType.AGGREGATION) {
			newConnector = new Aggregation(input, allNodes, c);
		} else if (newRelationship == RelationshipType.ASSOCIATION) {
			newConnector = new Association(input, allNodes, c);
		} else if (newRelationship == RelationshipType.COMPOSITION) {
			newConnector = new Composition(input, allNodes, c);
		} else if (newRelationship == RelationshipType.DEPENDENCY) {
			newConnector = new Dependency(input, allNodes, c);
		} else if (newRelationship == RelationshipType.GENERALIZATION) {
			newConnector = new Generalization(input, allNodes, c);
		}
		addNewLine = new AddUMLConnector(newConnector, c);
		this.newConnector = newConnector; 
		controller.selectObject(newConnector);
	}

	public void doAction() {
		controller.deselectAll();
		deleteOldLine.doAction();
		addNewLine.doAction();
		controller.selectObject(newConnector);
	}

	public void undoAction() {
		controller.deselectAll();
		addNewLine.undoAction();
		deleteOldLine.undoAction();
		controller.selectObject(oldConnector);
	}

}
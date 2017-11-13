package juml;

import java.util.Vector;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import juml.Controller.Mode;
import umlobject.*;

public class AddUMLNode extends AddOrDeleteUMLNode {

	public AddUMLNode(UMLNode n) {
		super(n, true, true);
	}
}

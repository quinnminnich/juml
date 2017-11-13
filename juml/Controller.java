package juml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.Stack;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import juml.Controller.Mode;
import umlobject.*;

// ---------------------------------------------------------------------------------------------- \\

/*
 * MVC styled controller between model and view.
 * @author Samuel Carroll
 * @author Torrance Graham
 * @author Quinn Minnich
 * @author Thomas Russoniello
 * @version 0.2
 * @since 0.1
 */
public class Controller {

	// Draw modes
	public enum Mode {
		SELECT, LINE, POINT, CLASSBOX, DELETE
	}

	/*
	 * All UMLNodes currently on the pane.
	 */
	Map<Node, UMLNode> NODES ;
	/*
	 * All UMLConnectors currently on the pane
	 */
	Map<Node, UMLConnector> CONNECTORS;

	// Main node that contains all drawn nodes as children.
	@FXML
	private Pane pane;
	@FXML
	private AnchorPane inspectorObject;
	@FXML
	private TextField circleOriginX;

	/*
	 * Current mode. Defaulted to SELECT.
	 */
	Mode MODE = Mode.SELECT;
	/*
	 * Collection of all "selected" UMLNodes
	 */
	Deque<UMLNode> SELECTED = new LinkedList<>();
	FileChooser fileChooser = new FileChooser();
	//Stack<CommandObject> actions = new Stack<>();
	//Stack<CommandObject> undoneActions = new Stack<>();
	static Stage window;

	// ----------------------------------------------------------------------------------------------
	// \\

	/*
	 * Sets the Primary Stage. Called from Main.
	 * 
	 * @param primaryStage Primary stage of application instance.
	 */
	public static void setPrimaryStage(Stage primaryStage) {
		window = primaryStage;
	}

	/*
	 * Returns pane.
	 * 
	 * @return Main pane being used for application instance.
	 */
	public Pane getPane() {
		return pane;
	}

	/*
	 * Sets pane.
	 * 
	 * @return Main pane being used for application instance.
	 */
	public void setPane(Pane p) {
		pane = p;
	}
	
	public Controller () {
		//CommandObject.setState(pane);
		NODES = CommandObject.getNodes();
		CONNECTORS = CommandObject.getConnectors();
		System.out.println("Set up ran");
	}

	/*
	 * Changes mode according to event source.
	 * 
	 * @precondition Called by event on object builder button.
	 * 
	 * @param event Event triggering this method.
	 * 
	 * @postcondition MODE is updated according to button that received event
	 * triggering this method.
	 */
	public void modeClick(ActionEvent event) {
		// fix this later
		CommandObject.setState(pane);
		
		String newMode = ((Button) event.getSource()).getId();
		newMode = newMode.substring(0, newMode.length() - 4).toUpperCase();
		MODE = Mode.valueOf(newMode);
		SELECTED.clear();
		System.out.println("Draw mode changed to \"" + MODE + "\"");
	}

	/*
	 * Performs draw action relative to current draw mode.
	 * 
	 * @precondition Called by a MouseEvent on the pane.
	 * 
	 * @param event Event triggering this method.
	 * 
	 * @postcondition Mode-defined operation is performed on coordinate of event.
	 */
	public void paneClick(MouseEvent event) throws IOException {
		System.out.println("Pane clicked at " + event.getX() + " " + event.getY());
		if (event.getButton() == MouseButton.SECONDARY) {
			CommandObject.undo();
			return;
		}
		if (event.getButton() == MouseButton.MIDDLE) {
			CommandObject.redo();
			return;
		}
		switch (MODE) {
		// Adds Point UMLNode to pane coordinates that were clicked on.
		case POINT:
			Point p = new Point(event.getX(), event.getY());
			makeDraggable(p);
			new AddUMLNode(p);
			//actions.push(new AddUMLNode(p, true));
			break;

		// Adds any UMLNode clicked to SELECTED
		// If theres a UMLNode already in SELECTED, draws a line from it to currently
		// clicked UMLNode.
		case LINE:
			UMLNode node = (UMLNode) getObject(event.getTarget());
			// If relevant node was selected.
			if (node != null) {
				if (SELECTED.isEmpty()) {
					SELECTED.addLast(node);
				}
				// If selected node wasn't just clicked on again.
				else if (node != SELECTED.getLast()) {
					UMLConnector c = new UMLConnector(SELECTED.getLast(), node);
					new AddUMLConnector(c, true);
					//actions.push(new AddUMLConnector(c, true));
					SELECTED.clear();
				}
			}

			break;

		// Adds ClassBox UMLNode to pane coordinates that were clicked on.
		case CLASSBOX:
			ClassBox c = new ClassBox(event.getX(), event.getY());
			makeDraggable(c);
			new AddUMLNode(c);
			//actions.push(new AddUMLNode(c, true));
			System.out.println("this is x " + event.getX() + "this is y " + event.getY());
			break;

		// Displays information of any UMLObject clicked on in the Inspector.
		case SELECT:
			UMLObject test = getObject(event.getTarget());

			if (test instanceof UMLConnector) {
				inspectorObject.getChildren().clear();
				inspectorObject.getChildren().add(FXMLLoader.load(getClass().getResource("Line.fxml")));
			} else if (test instanceof Point) {
				inspectorObject.getChildren().clear();
				inspectorObject.getChildren().add(FXMLLoader.load(getClass().getResource("Circle.fxml")));
				// circleOriginX(getObject(event.getTarget()));

			} else if (test instanceof ClassBox) {
				inspectorObject.getChildren().clear();
				inspectorObject.getChildren().add(FXMLLoader.load(getClass().getResource("ClassBox.fxml")));
			} else {
				inspectorObject.getChildren().clear();
			}
			// TODO:
			// line.getStrokeDashArray().addAll(25d, 10d);
			// event.getTarget().toString().startsWith("T") ||
			// event.getTarget().toString().startsWith("R")
			break;

		// Remove any UMLObject clicked on.
		case DELETE:
			UMLObject target = getObject(event.getTarget());
			if (target != null) {
				if (target instanceof UMLConnector) {
					UMLConnector connector = (UMLConnector) target;
					new AddUMLConnector(connector, false);
					//actions.push(new AddUMLConnector(connector, false));
				} else {
					new DeleteUMLNode ((UMLNode)target);
					//new AddUMLNode((UMLNode)target, false);
					//actions.push(new AddUMLNode((UMLNode)target, false));
					//deleteObject(target);
				}
			}

			break;

		default:

			break;
		}
	}

	// ----------------------------------------------------------------------------------------------
	// \\

	

	
	// The code for making an object draggable was originally written by jewelsea and copied from
	// https://stackoverflow.com/questions/17312734/how-to-make-a-draggable-node-in-javafx-2-0
	// have since modified parts of it to use in out project
	public void makeDraggable(UMLNode node) {
		Node model = node.getModel();
		class DragSource {
			double x, y;
		}
		final DragSource dragSource = new DragSource();
		final DragSource oldPosition = new DragSource();
		// Records dragging coordinate information
		model.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				// Record mouse click coordinates if UMLObject is a Parent.
				if (MODE == Mode.SELECT) {
					if (model instanceof Parent) {
						dragSource.x = mouseEvent.getX();
						dragSource.y = mouseEvent.getY();
						System.out.println("Changed old position ");
						oldPosition.x = node.getOriginX();
						oldPosition.y = node.getOriginY();
					}
					// Records delta of mouse click coordinates and UMLObject's origin if UMLObject
					// is not
					// * a Parent.
					else {
						dragSource.x = node.getOriginX() - mouseEvent.getX();
						dragSource.y = node.getOriginY() - mouseEvent.getY();
						System.out.println("Changed old position ");
						oldPosition.x = node.getOriginX();
						oldPosition.y = node.getOriginY();
					}
					model.getScene().setCursor(Cursor.MOVE);
					System.out.println("begin moving Node");
				}
			}
		});

		// Moves UMLObject to new position it was dragged to.
		model.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (MODE == Mode.SELECT) {
					if (model instanceof Parent) {
						node.move(node.getOriginX() + (mouseEvent.getX() - dragSource.x),
								node.getOriginY() + (mouseEvent.getY() - dragSource.y));
					} else {
						node.move(mouseEvent.getX() + dragSource.x, mouseEvent.getY() + dragSource.y);
					}
					node.update();
					//System.out.println("moved Node");
				}
			}
		});

		// Sets cursor back to default when mouse is released on the UMLObject.
		model.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (MODE == Mode.SELECT) {
					model.getScene().setCursor(Cursor.HAND);
					System.out.print("Moved from ("+oldPosition.x+", "+oldPosition.y+") to ("+node.getOriginX()+", "+node.getOriginY()+")");
					new MoveNode(node, oldPosition.x, oldPosition.y);
				}
			}
		});

		// Sets cursor to HAND when mouse enters the UMLObject.
		model.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (MODE == Mode.SELECT) {
					if (!mouseEvent.isPrimaryButtonDown()) {
						model.getScene().setCursor(Cursor.HAND);
					}
				}
			}
		});

		// Sets cursor back to default when mouse exits the UMLObject.
		model.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (MODE == Mode.SELECT) {
					if (!mouseEvent.isPrimaryButtonDown()) {
						model.getScene().setCursor(Cursor.DEFAULT);
					}
				}
			}
		});
	}

	/*
	 * Removes given UMLObject from pane.
	 * 
	 * @precondition target is recognized UMLObject that is a child of the pane.
	 * 
	 * @param target UMLObject to be removed.
	 * 
	 * @postcondition target is removed from pane and relevant map.
	 * 
	 * @postcondition If target is a UMLNode, any UMLConnectors connected to it are
	 * also removed.
	 * 
	 * @postcondition If target is a UMLConnector, it is removed from the list of
	 * connectors for the * UMLNodes that it connects.
	 */
	
	/*
	public void deleteObject(UMLObject target) {
		// If UMLConnector is given, disconnect it on both ends and remove it.
		if (target instanceof UMLConnector) {
			UMLConnector connector = (UMLConnector) target;
			CONNECTORS.remove(connector.getModel());
			connector.disconnect();
			pane.getChildren().remove(connector.getModel());
		}
		// If UMLNode is given, removes it and any UMLConnectors connected to it.
		else {
			UMLNode node = (UMLNode) target;
			while (!node.getConnections().isEmpty()) {
				UMLConnector connector = node.getConnections().lastElement();
				CONNECTORS.remove(connector.getModel());
				pane.getChildren().remove(connector.getModel());
				connector.disconnect();
			}
			NODES.remove(node.getModel());
			pane.getChildren().remove(node.getModel());
		}
	}
*/
	/*
	 * Adds already instantiated UMLObject to pane.
	 * 
	 * @precondition obj is instantiated UMLObject.
	 * 
	 * @param obj UMLObject to be added.
	 * 
	 * @postcondition obj is added to pane and relevant map.
	 * 
	 * @postcondition Any relevant handlers are defined on obj.
	 */

	/*
	 * public void addObjects(UMLObject... objects) { for (UMLObject object :
	 * objects) { Node model = object.getModel(); pane.getChildren().add(model); //
	 * If UMLConnector is being added, add it to CONNECTOR map. if (object
	 * instanceof UMLConnector) { UMLConnector connector = (UMLConnector) object;
	 * CONNECTORS.put(model, connector); }
	 * 
	 * // If UMLNode is being added, add it to NODES map and define relevant
	 * handlers. else { UMLNode node = (UMLNode) object; NODES.put(model, node); //
	 * Records mouse drag source coordinates accross handlers. class DragSource {
	 * double x, y; } final DragSource dragSource = new DragSource(); // Records
	 * dragging coordinate information model.setOnMousePressed(new
	 * EventHandler<MouseEvent>() {
	 * 
	 * @Override public void handle(MouseEvent mouseEvent) { // Record mouse click
	 * coordinates if UMLObject is a Parent. if (MODE == Mode.SELECT) { if (model
	 * instanceof Parent) { dragSource.x = mouseEvent.getX(); dragSource.y =
	 * mouseEvent.getY(); } // Records delta of mouse click coordinates and
	 * UMLObject's origin if UMLObject // is not // * a Parent. else { dragSource.x
	 * = node.getOriginX() - mouseEvent.getX(); dragSource.y = node.getOriginY() -
	 * mouseEvent.getY(); } model.getScene().setCursor(Cursor.MOVE);
	 * System.out.println("begin moving Node"); } } });
	 * 
	 * // Moves UMLObject to new position it was dragged to.
	 * model.setOnMouseDragged(new EventHandler<MouseEvent>() {
	 * 
	 * @Override public void handle(MouseEvent mouseEvent) { if (MODE ==
	 * Mode.SELECT) { if (model instanceof Parent) { node.move(node.getOriginX() +
	 * (mouseEvent.getX() - dragSource.x), node.getOriginY() + (mouseEvent.getY() -
	 * dragSource.y)); } else { node.move(mouseEvent.getX() + dragSource.x,
	 * mouseEvent.getY() + dragSource.y); } node.update();
	 * System.out.println("moved Node"); } } });
	 * 
	 * // Sets cursor back to default when mouse is released on the UMLObject.
	 * model.setOnMouseReleased(new EventHandler<MouseEvent>() {
	 * 
	 * @Override public void handle(MouseEvent mouseEvent) { if (MODE ==
	 * Mode.SELECT) { model.getScene().setCursor(Cursor.HAND); } } });
	 * 
	 * // Sets cursor to HAND when mouse enters the UMLObject.
	 * model.setOnMouseEntered(new EventHandler<MouseEvent>() {
	 * 
	 * @Override public void handle(MouseEvent mouseEvent) { if (MODE ==
	 * Mode.SELECT) { if (!mouseEvent.isPrimaryButtonDown()) {
	 * model.getScene().setCursor(Cursor.HAND); } } } });
	 * 
	 * // Sets cursor back to default when mouse exits the UMLObject.
	 * model.setOnMouseExited(new EventHandler<MouseEvent>() {
	 * 
	 * @Override public void handle(MouseEvent mouseEvent) { if (MODE ==
	 * Mode.SELECT) { if (!mouseEvent.isPrimaryButtonDown()) {
	 * model.getScene().setCursor(Cursor.DEFAULT); } } } }); } } }
	 * 
	 */

	/*
	 * Returns recognized UMLObject for given Object (typically one's underlying
	 * model).
	 * 
	 * @param inModel Object that will have its UMLObject (if it has one) searched
	 * for.
	 * 
	 * @return UMLObject that contains inModel as part of its underlying model. If
	 * given Object is * not recognized, but has an ancestor that is, that ancestor
	 * is the return.
	 */
	public UMLObject getObject(Object inModel) {
		Node model = (Node) inModel;
		UMLObject returnNode = NODES.get(model);
		// If model is not a key in NODES (returns null to returnNode), it's UMLObject
		// must be a
		// * UMLConnector
		if (returnNode == null) {
			returnNode = CONNECTORS.get(model);
		}
		// Follow ancestry line until recognized UMLObject is found or until pane is
		// reached or until
		// * end of ancestry line is reached.
		while (returnNode == null) {
			if (model == pane || model == null) {
				break;
			}
			model = model.getParent();
			returnNode = NODES.get(model);
			if (returnNode == null) {
				returnNode = CONNECTORS.get(model);
			}
		}
		return returnNode;
	}

	// ----------------------------------------------------------------------------------------------
	// \\
	// MenuBar Action Controller

	/*
	 * Action used to Close the program: assigned to Exit.
	 * 
	 * @postcondition Application is exited.
	 */
	public void menuExitClicked() {
		window.close();
	}

	/*
	 * Opens new window with blank file.
	 * 
	 * @postcondition all maintained variables are reset.
	 */
	public void menuNewClicked() {
		pane.getChildren().clear();
		inspectorObject.getChildren().clear();
		CommandObject.reset();
	}

	/*
	 * Action to Open file WIP.
	 */
	public void menuOpenClicked() {
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JUML txt files", "*.txt"));
		fileChooser.setTitle("Open Resource File");
		File file = fileChooser.showOpenDialog(window);
		window.setTitle("Team Rocket UML Editor: " + file.toString());
		try {
			// Clears current pane contents NODES = new HashMap<>(file);
			pane.getChildren().clear();
			BufferedReader input = new BufferedReader(new FileReader(new File(file.toString())));
			String hashy = input.readLine();
			System.out.println(hashy);
			input.close();
			// TODO: Load file text as pane children.

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

	}

	/*
	 * Action to Save File WIP.
	 */
	public void menuSaveClicked() throws IOException {
		String hashMap = NODES.toString();
		String fxmlContent = pane.getChildren().toString();

		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JUML txt files", "*.txt"));
		File file = fileChooser.showSaveDialog(window);
		try {
			FileWriter fileWriter = null;
			fileWriter = new FileWriter(file);
			fileWriter.write(hashMap);
			fileWriter.close();
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	// WIP Menu Bar Actions
	public void menuCloseClicked() {
	}

	public void menuCopyClicked() {
	}

	public void menuPasteClicked() {
	}

	public void menuDeleteClicked() {
	}

	public void menuSelectAllClicked() {
	}

	public void menuSpecificationsClicked() {
		File file = new File("https://github.com/tommy-russoniello/juml/Iteration1Specifications.docx");
		// HostServices hostServices = Main.getHostServices();
		// hostServices.showDocument(file.getAbsolutePath());
	}

	/*
	 * Function to Save a Hashmap to a txt file WIP.
	 */
	public void saveFile(Map<Node, UMLNode> NODES) throws IOException {
		ObjectOutputStream outputStream = new ObjectOutputStream(
				new FileOutputStream("C:/Users/xTmil_000/Desktop/eclipse/Workspace/juml-master/Hashmap.txt"));
		outputStream.writeObject(NODES);
		outputStream.close();
	}

	/*
	 * Inspector Properties WIP.
	 */
	public void circleOriginX(UMLObject target) {
		circleOriginX.setText(String.valueOf(target.originX));
	}

}
// ----------------------------------------------------------------------------------------------
// \\

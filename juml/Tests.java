package juml;

import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import java.util.Vector;
import java.util.Arrays;
import java.util.Map;
import umlobject.*;
import static org.junit.Assert.*;
import javafx.collections.*;
import org.junit.*;

public class Tests {
	Pane pane;
	Controller juml;
	Point p1, p2, p3;
	ClassBox cb1, cb2, cb3;
	UMLConnector c1, c2, c3, c4, c5, c6;

	
	@Before
	public void setUp() {
		//Mimics controller
		pane = new Pane();
		CommandObject.setState(pane);
		CommandObject.reset();
		juml = new Controller();
		juml.setPane(pane);
		
		p1 = new Point (0,0);
		p2 = new Point (0,0);
		p3 = new Point (3,4);
		cb1 = new ClassBox(4,5);
		cb2 = new ClassBox(5,6);
		cb3 = new ClassBox(6,7);
	}

	
	 
	@Test
	public void addPoints(){
		System.out.println("AddPoints Test Running");
		addNodes(p1, p2, p3);
		assertEquals(3, pane.getChildren().size());
		assertMap(juml.NODES, p1, p2, p3);
	}

	

	@Test
	public void addClassBoxes(){
		System.out.println("AddClasBox Test Running");
		addNodes(cb1, cb2, cb3);
		assertEquals(3, pane.getChildren().size());
		assertMap(juml.NODES, cb1, cb2, cb3);
	}

	@Test
	public void addConnectorsBetweenPoints() {
		addNodes(p1, p2, p3);
		c1 = new UMLConnector(p1, p2);
		c2 = new UMLConnector(p2, p3);
		c3 = new UMLConnector(p1, p3);
		addConnectors(c1, c2, c3);
		assertEquals(6, pane.getChildren().size());
		assertMap(juml.CONNECTORS, c1, c2, c3);
		assertConnections(p1, c1, c3);
		assertConnections(p2, c1, c2);
		assertConnections(p3, c2, c3);
	}

	
	
	@Test
  public void addConnectorsBetweenClassBoxes() {
		addNodes(cb1, cb2, cb3);

		c1 = new UMLConnector(cb1, cb2);
		c2 = new UMLConnector(cb2, cb3);
		c3 = new UMLConnector(cb1, cb3);

		addConnectors(c1, c2, c3);

		assertMap(juml.CONNECTORS, c1, c2, c3);

		assertEquals(6, pane.getChildren().size());
		assertConnections(cb1, c1, c3);
		assertConnections(cb2, c1, c2);
		assertConnections(cb3, c2, c3);
  }

	@Test
	public void addConnectorsBetweenPointsAndClassBoxes() {
		addNodes(p1, p2, cb1, cb2);

		c1 = new UMLConnector (p1, p2);
		c2 = new UMLConnector (p1, cb1);
		c3 = new UMLConnector (p1, cb2);
		c4 = new UMLConnector (p2, cb1);
		c5 = new UMLConnector (p2, cb2);
		c6 = new UMLConnector (cb1, cb2);

		addConnectors(c1, c2, c3, c4, c5, c6);

		assertEquals(10, pane.getChildren().size());

		assertMap(juml.CONNECTORS, c1, c2, c3, c4, c5, c6);

		assertConnections(p1, c1, c2, c3);
		assertConnections(p2, c1, c4, c5);
		assertConnections(cb1, c2, c4, c6);
		assertConnections(cb2, c3, c5, c6);
	}

	
  @Test
  public void deletePoints(){
		addNodes(p1, p2);

		assertEquals(2, pane.getChildren().size());
		assertMap(juml.NODES, p1, p2);

		new DeleteUMLNode(p1);

		assertEquals(1, pane.getChildren().size());
		assertNull(juml.NODES.get(p1.getModel()));
		assertMap(juml.NODES, p2);
  }

 	@Test
 	public void deleteClassBoxes(){
 		addNodes(cb1, cb2);

		assertEquals(2, pane.getChildren().size());
		assertMap(juml.NODES, cb1, cb2);

		new DeleteUMLNode(cb1);

 		assertEquals(1, pane.getChildren().size());
		assertNull(juml.NODES.get(cb1.getModel()));
		assertMap(juml.NODES, cb2);

 	}

	@Test
	public void deleteConnectors() {
		addNodes(p1, p2);

		c1 = new UMLConnector(p2, p1);
		addConnectors(c1);
		assertEquals(3, pane.getChildren().size());
		// test that each point has added one connection
		assertConnections(p1, c1);
		assertConnections(p2, c1);

		assertMap(juml.CONNECTORS, c1);

		new AddUMLConnector(c1, false);

		assertEquals(2, pane.getChildren().size());
		// see if points removed connection
		assertConnections(p1);
		assertConnections(p2);

		assertNull(juml.CONNECTORS.get(c1.getModel()));
		assertMap(juml.CONNECTORS);
	}

	@Test
	public void deletePointsWithConnectors() {
		assertTrue(pane.getChildren().isEmpty());

		//add points to the pane/hashmap
		addNodes(p1, p2, p3);

		c1 = new UMLConnector(p1, p2);
		c2 = new UMLConnector(p2, p3);

		addConnectors(c1, c2);

		assertMap(juml.CONNECTORS, c1, c2);
		assertEquals(5, pane.getChildren().size());
		assertConnections(p1, c1);
		assertConnections(p2, c1, c2);
		assertConnections(p3, c2);

		//this should delete the connector2 too
		new DeleteUMLNode(p2);

		// see if both point removed the connectors
		assertMap(juml.CONNECTORS);
		assertNull(juml.CONNECTORS.get(c1.getModel()));
		assertNull(juml.CONNECTORS.get(c2.getModel()));
		assertEquals(2, pane.getChildren().size());
		assertConnections(p1);
		assertConnections(p2);
		assertConnections(p3);
	}

	@Test
	public void getPoints() {
		//set up points and connectors
		addNodes(p1, p2, p3);

		c1 = new UMLConnector(p1, p2);
		c2 = new UMLConnector(p2, p3);
		c3 = new UMLConnector(p1, p3);

		addConnectors(c1, c2, c3);

		assertEquals(p1, juml.getObject(p1.getModel()));
		assertEquals(p2, juml.getObject(p2.getModel()));
		assertEquals(p3, juml.getObject(p3.getModel()));
	}

	@Test
	public void getConnectors() {
		//set up points and connectors
		addNodes(p1, p2, p3);

		c1 = new UMLConnector(p1, p2);
		c2 = new UMLConnector(p2, p3);
		c3 = new UMLConnector(p1, p3);

		addConnectors(c1, c2, c3);

		assertEquals(c1, juml.getObject(c1.getModel()));
		assertEquals(c2, juml.getObject(c2.getModel()));
		assertEquals(c3, juml.getObject(c3.getModel()));
	}

	@Test
	public void getClassBoxes() {
		//set up points and connectors
		addNodes(cb1, cb2, cb3);

		c1 = new UMLConnector(cb1, cb2);
		c2 = new UMLConnector(cb2, cb3);
		c3 = new UMLConnector(cb1, cb3);

		addConnectors(c1, c2, c3);

		ObservableList<Node> children = ((VBox) cb2.getModel()).getChildren();

		for (Node child : children) {
			assertEquals(cb2, juml.getObject(child));
		}

		assertEquals(cb2, juml.getObject(cb2.getModel()));
	}
	
	@Test
	public void TestRedoUndoesUndo() {
		// this test just assures that redo undoes undo
		
		// test for adding points	
		addNodes(p1, p2, p3);
		assertEquals(3, pane.getChildren().size());
		assertMap(juml.NODES, p1, p2, p3);

		CommandObject.undo();
		CommandObject.redo();

		assertEquals(3, pane.getChildren().size());
		assertMap(juml.NODES, p1, p2, p3);
		
		// test undoing more times than necessary
		CommandObject.undo();
		CommandObject.undo();
		CommandObject.undo();
		CommandObject.undo();
		CommandObject.redo();
		CommandObject.redo();
		CommandObject.redo();
		CommandObject.redo();
		
		assertEquals(3, pane.getChildren().size());
		assertMap(juml.NODES, p1, p2, p3);

		// test repeated undoing
		for (int i=0; i<10; i++) {
			CommandObject.undo();
			CommandObject.redo();
		}		
		
		assertEquals(3, pane.getChildren().size());
		assertMap(juml.NODES, p1, p2, p3);
		
		
		// test for adding Connectors	
		c1 = new UMLConnector(p1, p2);
		c2 = new UMLConnector(p2, p3);
		addConnectors(c1, c2);
		
		assertMap(juml.CONNECTORS, c1, c2);
		assertEquals(5, pane.getChildren().size());
		assertConnections(p1, c1);
		assertConnections(p2, c1, c2);
		assertConnections(p3, c2);
		
		// test repeated undoing
		for (int i=0; i<10; i++) {
			CommandObject.undo();
			CommandObject.redo();
		}
		
		assertMap(juml.CONNECTORS, c1, c2);
		assertEquals(5, pane.getChildren().size());
		assertConnections(p1, c1);
		assertConnections(p2, c1, c2);
		assertConnections(p3, c2);


		
		// test for deleting a node with a connector
		new DeleteUMLNode(p2);

		// see if both point removed the connectors
		assertMap(juml.CONNECTORS);
		assertNull(juml.CONNECTORS.get(c1.getModel()));
		assertNull(juml.CONNECTORS.get(c2.getModel()));
		assertEquals(2, pane.getChildren().size());
		assertConnections(p1);
		assertConnections(p2);
		assertConnections(p3);
		
		// test repeated undoing
		for (int i=0; i<10; i++) {
			CommandObject.undo();
			CommandObject.redo();
		}
		
		assertMap(juml.CONNECTORS);
		assertNull(juml.CONNECTORS.get(c1.getModel()));
		assertNull(juml.CONNECTORS.get(c2.getModel()));
		assertEquals(2, pane.getChildren().size());
		assertConnections(p1);
		assertConnections(p2);
		assertConnections(p3);
	}

	@Test
	public void TestUndoAndRedo() {
		
		// test for adding points	
		addNodes(p1, p2, p3);
		assertEquals(3, pane.getChildren().size());
		assertMap(juml.NODES, p1, p2, p3);

		CommandObject.undo();
		assertEquals(2, pane.getChildren().size());
		assertMap(juml.NODES, p1, p2);
		
		CommandObject.undo();
		assertEquals(1, pane.getChildren().size());
		assertMap(juml.NODES, p1);
		
		CommandObject.redo();
		CommandObject.redo();		

		assertEquals(3, pane.getChildren().size());
		assertMap(juml.NODES, p1, p2, p3);
		
		
		// test for adding Connectors	
		c1 = new UMLConnector(p1, p2);
		c2 = new UMLConnector(p2, p3);
		addConnectors(c1, c2);
	
		assertMap(juml.CONNECTORS, c1, c2);
		assertEquals(5, pane.getChildren().size());
		assertConnections(p1, c1);
		assertConnections(p2, c1, c2);
		assertConnections(p3, c2);
		
		CommandObject.undo();
		assertMap(juml.CONNECTORS, c1);
		assertEquals(4, pane.getChildren().size());
		assertConnections(p1, c1);
		assertConnections(p2, c1);
		assertConnections(p3);
		
		CommandObject.undo();
		assertMap(juml.CONNECTORS);
		assertEquals(3, pane.getChildren().size());
		assertConnections(p1);
		assertConnections(p2);
		assertConnections(p3);
		
		CommandObject.redo();
		assertMap(juml.CONNECTORS, c1);
		assertEquals(4, pane.getChildren().size());
		assertConnections(p1, c1);
		assertConnections(p2, c1);
		assertConnections(p3);
		
		CommandObject.redo();
		assertMap(juml.CONNECTORS, c1, c2);
		assertEquals(5, pane.getChildren().size());
		assertConnections(p1, c1);
		assertConnections(p2, c1, c2);
		assertConnections(p3, c2);
				
		//test for deleting just a connector
		
		new AddUMLConnector(c1, false);
		assertMap(juml.CONNECTORS, c2);
		assertEquals(4, pane.getChildren().size());
		assertConnections(p1);
		assertConnections(p2, c2);
		assertConnections(p3, c2);
		
		CommandObject.undo();
		assertMap(juml.CONNECTORS, c1, c2);
		assertEquals(5, pane.getChildren().size());
		assertConnections(p1, c1);
		assertConnections(p2, c1, c2);
		assertConnections(p3, c2);
		
		CommandObject.redo();
		assertMap(juml.CONNECTORS, c2);
		assertEquals(4, pane.getChildren().size());
		assertConnections(p1);
		assertConnections(p2, c2);
		assertConnections(p3, c2);
		
		CommandObject.undo();
				
		// test for deleting a node with a connector
		new DeleteUMLNode(p2);

		// see if both point removed the connectors
		assertMap(juml.CONNECTORS);
		assertNull(juml.CONNECTORS.get(c1.getModel()));
		assertNull(juml.CONNECTORS.get(c2.getModel()));
		assertEquals(2, pane.getChildren().size());
		assertConnections(p1);
		assertConnections(p2);
		assertConnections(p3);
		
		
		CommandObject.undo();
		assertMap(juml.CONNECTORS, c1, c2);
		assertEquals(5, pane.getChildren().size());
		assertConnections(p1, c1);
		assertConnections(p2, c1, c2);
		assertConnections(p3, c2);
		
		CommandObject.redo();
		assertMap(juml.CONNECTORS);
		assertNull(juml.CONNECTORS.get(c1.getModel()));
		assertNull(juml.CONNECTORS.get(c2.getModel()));
		assertEquals(2, pane.getChildren().size());
		assertConnections(p1);
		assertConnections(p2);
		assertConnections(p3);
		
	}
	
	
	@Test
	public void undoAndRedoMoveNode(){
		addNodes(p1, p2);
		assertTrue(p1.getOriginX() == 0);
		assertTrue(p1.getOriginY() == 0);
		assertTrue(p2.getOriginX() == 0);
		assertTrue(p2.getOriginY() == 0);
		
		// artificially generates movement command 
		// (normally created by drag listeners)
		p1.move(42,  7);
		new MoveNode(p1,0,0);
		
		assertTrue(p1.getOriginX() == 42);
		assertTrue(p1.getOriginY() == 7);
		assertTrue(p2.getOriginX() == 0);
		assertTrue(p2.getOriginY() == 0);
		
		CommandObject.undo();
		
		assertTrue(p1.getOriginX() == 0);
		assertTrue(p1.getOriginY() == 0);
		assertTrue(p2.getOriginX() == 0);
		assertTrue(p2.getOriginY() == 0);
		
		CommandObject.redo();
		assertTrue(p1.getOriginX() == 42);
		assertTrue(p1.getOriginY() == 7);
		assertTrue(p2.getOriginX() == 0);
		assertTrue(p2.getOriginY() == 0);
		
		p1.move(1,  2);
		new MoveNode(p1, 42, 7);
		p1.move(2,  3);
		new MoveNode(p1, 1, 2);
		p2.move(50,  51);
		new MoveNode(p2, 0, 0);
		
		assertTrue(p1.getOriginX() == 2);
		assertTrue(p1.getOriginY() == 3);
		assertTrue(p2.getOriginX() == 50);
		assertTrue(p2.getOriginY() == 51);
		
		CommandObject.undo();
		assertTrue(p1.getOriginX() == 2);
		assertTrue(p1.getOriginY() == 3);
		assertTrue(p2.getOriginX() == 0);
		assertTrue(p2.getOriginY() == 0);
		
		CommandObject.undo();
		assertTrue(p1.getOriginX() == 1);
		assertTrue(p1.getOriginY() == 2);
		assertTrue(p2.getOriginX() == 0);
		assertTrue(p2.getOriginY() == 0);
		
		CommandObject.undo();
		CommandObject.undo();
		assertTrue(p1.getOriginX() == 0);
		assertTrue(p1.getOriginY() == 0);
		assertTrue(p2.getOriginX() == 0);
		assertTrue(p2.getOriginY() == 0);
		
		CommandObject.redo();
		CommandObject.redo();
		assertTrue(p1.getOriginX() == 1);
		assertTrue(p1.getOriginY() == 2);
		assertTrue(p2.getOriginX() == 0);
		assertTrue(p2.getOriginY() == 0);
		
		CommandObject.redo();
		assertTrue(p1.getOriginX() == 2);
		assertTrue(p1.getOriginY() == 3);
		assertTrue(p2.getOriginX() == 0);
		assertTrue(p2.getOriginY() == 0);
		
		CommandObject.redo();
		assertTrue(p1.getOriginX() == 2);
		assertTrue(p1.getOriginY() == 3);
		assertTrue(p2.getOriginX() == 50);
		assertTrue(p2.getOriginY() == 51);
	}
	
	
// ---------------------------------------------------------------------------------------------- \\

	@SafeVarargs
	public static <T> void assertMap (Map<Node, T> map, T... objects) {
		assertEquals(objects.length, map.size());
		for (T object : objects) {
			assertEquals(object, map.get(((UMLObject) object).getModel()));
		}
	}

	public static void assertConnections (UMLNode node, UMLConnector... actual) {
		Vector<UMLConnector> expected = node.getConnections();
		assertEquals(expected.size(), actual.length);
		for (UMLConnector connector : actual) {
			assertTrue(expected.contains(connector));
		}
	}
	
	public static void addNodes (UMLNode... objects) {
		for (UMLNode node : objects) {
			new AddUMLNode(node);
		}
	}
	
	public static void addConnectors (UMLConnector... objects) {
		for (UMLConnector c : objects) {
			new AddUMLConnector(c, true);
		}
	}

	
}

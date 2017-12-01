package umlobject;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import juml.Controller;

/*
 * UML composition representation.
 * @author Samuel Carroll
 * @author Torrance Graham
 * @author Quinn Minnich
 * @author Thomas Russoniello
 * @version 0.3
 * @since 0.3
 */
public class Composition extends Relationship {
	
	public Composition(Scanner input, Vector<UMLNode> allNodes, Controller controller) {
		super(input, allNodes);
		setUp();
		readInPivots(input, controller);
	}
	
	/*
	 * Explicit Constructor
	 * 
	 * @param inStart starting UMLNode for Segment to be drawn between.
	 * 
	 * @param inStop stopping UMLNode for Segment to be drawn between.
	 * 
	 * @postcondition Composition instance with given starting and stopping UMLNodes
	 * is created.
	 */
	public Composition(UMLNode inStart, UMLNode inStop) {
		super(inStart, inStop);
		setUp();
	}

	private void setUp() {
		// Initial Segment and Shape drawing.
		segments.add(new Segment(start, stop, false, true));
		startLine = endLine = (Line) segments.get(0).getModel();

		shape = new Polygon();
		reset();
		shape.setStroke(Color.BLACK);
		shape.setFill(Color.BLACK);

		group = new Group();
		group.getChildren().addAll(endLine, shape);
		// Move segment to proper starting position.
		update();
	}

	/*
	 * Resets start and end Lines to be in default position so shape and note
	 * positions can be reset. * Used to line all shapes back up with each other
	 * after a bad movement (NaN delta).
	 * 
	 * @postcondition Positions of first and last segment (may be the same one),
	 * notes, and shape are * reset to default.
	 */
	public void reset() {
		super.reset();
		shape.getPoints()
				.addAll(new Double[] { endLine.getEndX() - .5, endLine.getEndY(), endLine.getEndX() - 8.75,
						endLine.getEndY() - 5, endLine.getEndX() - 17.5, endLine.getEndY(), endLine.getEndX() - 8.75,
						endLine.getEndY() + 5 });
	}

	/*
	 * "Redraws" underlying Group model's Segments to be between starting and
	 * stopping UMLNodes, * its shape to be at the end of the Segment on the
	 * stopping side at the same angle as the * last Segment, and the notes to be on
	 * the starting of the first segment and the ending of the * last segment. Used
	 * when the starting, stopping, or pivot UMLNodes have been moved, or when *
	 * initially setting position.
	 * 
	 * @postcondition Underlying Group model's Segments are reassigned to current
	 * coordinates of * starting, stopping, and pivot UMLNodes' anchor points,
	 * underlying Group model's shape is * reassigned to end at stopping UMLNode's
	 * anchor point (rotated to match the last Segment's * angle), and start/endText
	 * Notes are reassigned to be at start of first segment and end of * last
	 * segment, respectively.
	 */
	public void update() {
		super.update(8.5, false);
	}

	public void update(boolean isReset) {
		super.update(8.5, isReset);
	}

	/*
	 * Changes color of underlying Group model's line and shape to make the object
	 * appear highlighted.
	 * 
	 * @postcondition Color of underlying Group model's line and shape changed to
	 * blue.
	 */
	public void highlight() {
		super.highlight();
		shape.setFill(Color.BLUE);
	}

	/*
	 * Changes color of underlying Group model's line and shape to make the object
	 * appear unhighlighted.
	 * 
	 * @postcondition Color of underlying Group model's line changed to black.
	 */
	public void unhighlight() {
		super.unhighlight();
		shape.setFill(Color.BLACK);
	}
}

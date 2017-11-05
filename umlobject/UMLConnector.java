package umlobject;

import javafx.scene.shape.Line;
import javafx.scene.Node;

/*
 * UML relationship representation.
 * @author Samuel Carroll
 * @author Torrance Graham
 * @author Quinn Minnich
 * @author Thomas Russoniello
 * @version 0.2
 * @since 0.1
 */
public class UMLConnector extends UMLObject {
  /*
   * 2 objects that this UMLConnector connects.
   */
  private UMLNode start, stop;
  /*
   * underlying model.
   */
  private Line line;

  /*
   * Basic Constructor
   * @param inStart UMLNode whose origin coordinates this's underlying line will start at.
   * @param inStop UMLNode whose origin coordinates this's underlying line will stop at.
   * @postcondition UMLConnector instance with given starting and stopping UMLNodes is created.
   */
  public UMLConnector(UMLNode inStart, UMLNode inStop) {
    start = inStart;
    stop = inStop;
    originX = start.getOriginX();
    originY = start.getOriginY();
    // Line between the 2 UMLNodes.
    line = new Line(start.getOriginX(), start.getOriginY(), stop.getOriginX(), stop.getOriginY());
    inStart.connections.add(this);
    inStop.connections.add(this);
  }

  /*
   * Return underlying model.
   * @return Underlying line model.
   */
  public Node getModel() {
    return line;
  }

  /*
   * Return the UMLNode that the underlying Line model starts at.
   * @return UMLNode that the underlying Line model starts at.
   */
  public UMLNode getStart() {
    return start;
  }

  /*
   * Return the UMLNode that the underlying Line model stops at.
   * @return UMLNode that the underlying Line model stops at.
   */
  public UMLNode getStop() {
    return stop;
  }

  /*
   * "Redraws" underlying Line model to be between starting and stopping UMLNodes. Used when the
   * * starting and or the stopping UMLNode has been moved.
   * @postcondition Underlying line model is reassigned to current coordinates of starting and
   * * stopping UMLNodes.
   */
  public void update() {
    System.out.println("moving line");
	  line.setStartX(start.getOriginX());
	  line.setStartY(start.getOriginY());
	  line.setEndX(stop.getOriginX());
	  line.setEndY(stop.getOriginY());
  }

  /*
   * Removes this from starting and stopping UMLNodes' list of UMLConnectors.
   * @postcondition This is removed from starting and stopping UMLNodes' list of UMLConnectors.
   */
  public void disconnect() {
    start.connections.remove(this);
    stop.connections.remove(this);
    System.out.println("connector has removed self from endpoints");
  }
}
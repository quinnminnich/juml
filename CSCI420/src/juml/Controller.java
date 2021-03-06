package juml;

import java.awt.geom.Point2D.Double;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.naming.spi.InitialContextFactory;


import javafx.scene.layout.Pane;
import javafx.event.*;



import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import umlnode.*;


//Logic
public class Controller{
    
	
	
	
	public enum Mode {
      LINE, POINT, SELECT, CLASSBOX
    }

    @FXML private Pane pane;

    Mode MODE = Mode.SELECT;
    Deque<UMLNode> POINTS = new LinkedList<UMLNode>();
    FileChooser fileChooser = new FileChooser();
    static Stage window;

    //Sets the Primary Stage
    public static void setPrimaryStage(Stage primaryStage) {
        window = primaryStage;
    }

    public Pane getPane () {
    	return pane;
    }

    public void modeClick (ActionEvent event) {
      String newMode = ((Button)event.getSource()).getId();
      newMode = newMode.substring(0, newMode.length() - 4).toUpperCase();
      MODE = Mode.valueOf(newMode);
      System.out.println ("Draw mode changed to \"" + MODE + "\"");
    }

    
    public void paneClick (MouseEvent event) {
      //Point point = new Point(event.getX(), event.getY());
      //POINTS.addLast(point);
      //System.out.println ("Pane clicked at " + point.getOriginX() + " " + point.getOriginY());
      //System.out.println(POINTS.size());

      switch (MODE) {
        case POINT:
          Point point = new Point(event.getX(), event.getY(), 10);
          pane.getChildren().add(point);
          POINTS.clear();
          System.out.println ("Pane clicked at " + point.getOriginX() + " " + point.getOriginY());
          System.out.println(POINTS.size());
          break;
          
        case CLASSBOX:
        	System.out.println("Adding class box");
        	ClassBox box = new ClassBox();
            pane.getChildren().add(box.getModel());
            break;

        case LINE:
          	if (event.getTarget() instanceof UMLNode) {
        	  System.out.println("point clicked on");
        	  UMLNode umltarget = (UMLNode)event.getTarget();
        	  //Node target = umltarget.getModel();
        	  System.out.println("so far so good");
        	  if (POINTS.size() > 0) {
            	System.out.println("creating line");
            	Connector c = (new Connector(POINTS.getFirst(), umltarget));
            	POINTS.getFirst().addConnnector(c);
            	umltarget.addConnnector(c);
            	pane.getChildren().add(c.getModel());
              POINTS.clear();
            } else {
            	System.out.println("adding first point");
            	POINTS.addLast(umltarget);
            }
          } else {
        	  System.out.println("Clearing queue");
        	  POINTS.clear();
          }
          

          break;

        case SELECT:
        	
        if (event.getTarget() instanceof UMLNode) {
        	System.out.println("Node clicked");
        	pane.setCursor(Cursor.HAND);        	
        } else {
        	pane.setCursor(Cursor.DEFAULT);       
        }
          // inspect(POINTS.getLast());
          POINTS.clear();
          
          break;

        default:
        
          break;
      }
    }


   
   
    
    
    
//MenuBar Action Controller --------------------------------------------------------------------------------------------
    //Action used to Close the program: assigned to Exit
    public void menuExitClicked(){
        window.close();
    }

    //Action to Open file
    public void menuOpenClicked(){
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All UML Diagrams", "*.UML"));
        fileChooser.setTitle("Open Resource File");
        fileChooser.showOpenDialog(window);
        window.setTitle("Team Rocket UML Editor: " + fileChooser.getTitle());
    }

    public void menuSaveClicked() {
        //temp
        String content = new String("test");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ALL UML Diagrams", "*.UML"));
        File file = fileChooser.showSaveDialog(window);
        try {
            FileWriter fileWriter = null;
            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    //WIP Menu Bar Actions
    public void menuNewClicked(){}
    public void menuCloseClicked(){}
    public void menuCopyClicked(){}
    public void menuPasteClicked(){}
    public void menuDeleteClicked(){}
    public void menuSelectAllClicked(){}
    public void menuSpecificationsClicked(){
        File file = new File("https://github.com/tommy-russoniello/juml/Iteration1Specifications.docx");
        //HostServices hostServices = Main.getHostServices();
        //hostServices.showDocument(file.getAbsolutePath());
    }

}//---------------------------------------------------------------------------------------------------------------------

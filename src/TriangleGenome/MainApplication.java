package TriangleGenome;

import java.io.File;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/** 
 * @author Christian Seely, 
 * Main class which spurs the start of the flow of the program
 * and creates and sets up the GUI framework (currently really ugly). 
 * 
 * PLEASE NOTE: You must actually upload a image before clicking the
 * initial population button or nothing will happen. 
 */
public class MainApplication extends Application
{
  private static final int WINDOW_WIDTH = 600;
  private static final int WINDOW_HEIGHT = 600;
  
  //Layouts
  private VBox horizontalLayout;
  private HBox fileContainer;
  private HBox[] buttonContainers;
  
  //Buttons
  private Button chooseFile;
  private Button viewInitialPopulation;
  
  //Filechooser
  private FileChooser fileChooser;
  
  //Image
  private Image image;
  
  @Override
  public void start(Stage stage) throws Exception
  {
    stage.setTitle("Triangle Genome");
    fileChooser = new FileChooser();
    fileChooser.setSelectedExtensionFilter(
        new FileChooser.ExtensionFilter("Image files", 
        new String[] {".png", ".bmp", ".jpg", ".gif"}));
    fileChooser.setTitle("Image Selector");
    
    horizontalLayout = new VBox();
    fileContainer = new HBox();
    buttonContainers = new HBox[1];
    
    for (int i = 0; i < 1; i++){
      buttonContainers[i] = new HBox();
    }
    
    chooseFile = new Button("Choose File");
    viewInitialPopulation =  new Button("View Initial Population");
    
    setButtonActions(stage);
    
    //Create GUI architecture. 
    fileContainer.getChildren().add(chooseFile);
    buttonContainers[0].getChildren().add(viewInitialPopulation);  
    horizontalLayout.getChildren().add(fileContainer);
    horizontalLayout.getChildren().add(buttonContainers[0]);   
    Scene scene = new Scene(horizontalLayout, WINDOW_WIDTH, WINDOW_HEIGHT);
    stage.setScene(scene);
    stage.show();
    
  }
  
  /**
   * Adds action listeners to buttons and the actions performed upon
   * the respective listener being triggered. 
   * @param stage
   */
  private void setButtonActions(Stage stage){
    chooseFile.setOnAction(new EventHandler<ActionEvent>(){
      @Override
      /**
       * If valid path/file type then create image object based
       * on user selected image. 
       */
      public void handle(ActionEvent e)
      {
        File file = fileChooser.showOpenDialog(stage);
        if (file != null){
          String path = file.toURI().toString();
          image = new Image(path,700,700,true,true);
          System.out.println("Loaded Image: " + path);
        }
      }
    });

  viewInitialPopulation.setOnAction(new EventHandler<ActionEvent>(){
      @Override
      /**
       * If valid image uploaded send it over to the InitialPopulation class
       * to calculate and display the initial population. 
       */
      public void handle(ActionEvent e)
      {
        if (image != null){
         InitialPopulation viewInitialPopulation = new InitialPopulation(image);
         viewInitialPopulation.show();
        }
      }
    });
  
  
  }


  /**
   * The main method (fall through should not be called)
   * 
   * @param args
   */
  public static void main(String[] args){
    launch();
  }
}


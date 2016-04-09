//
//package Application;
//
//import java.io.File;
//import java.io.IOException;
//
//import TriangleGenome.InitialPopulation;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.image.Image;
//import javafx.stage.Stage;
//
///**
// * Controller class for the startup popup window. Handles two buttons, the file
// * chooser button, and the button that starts the main application
// * 
// * The controller class is sort of like a class with all the event handlers in a
// * certain scene
// */
//public class startupController
//{
//
//  @FXML
//  private Button chooseFileButton;
//
//  @FXML
//  private Button startButton;
//
//  private NewMain main;
//
//  /**
//   * Uses the file chooser found in the main class to load a valid image.
//   * 
//   * @param event
//   */
//  @FXML
//  void fileButtonHandler(ActionEvent event)
//  {
//    File file = main.fileChooser.showOpenDialog(null);
//    if (file != null)
//    {
//      String path = file.toURI().toString();
//      main.originalImage = new Image(path, 500, 500, true, true);
//      System.out.println("Loaded Image: " + path);
//    }
//  }
//
//  /**
//   * If a valid image has been selected: Closes the current/startup window and
//   * starts up the GA in a new stage.
//   * 
//   * @param event
//   *          the event that triggers when the startButton is pressed
//   */
//  @FXML
//  void startButtonHandler(ActionEvent event) throws IOException
//  {
//    if (main.originalImage != null)
//    {
//      Stage stage = (Stage) startButton.getScene().getWindow();
//      stage.close();
//      main.createMainWindow();
//    }
//  }
//
//  /**
//   * Given an instance of main to be able to reference the filechooser and the
//   * method to create the main window
//   */
//  public void initController(NewMain main)
//  {
//    this.main = main;
//  }
//
//}

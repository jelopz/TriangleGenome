package Application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

/**
 * The controller class for the main window/scene. Has an ImageView object which
 * is the picture that is currently displayed on the screen. startButton and
 * stopButton handle the runtime of the algorithm.
 * 
 * Basically an object containing all the event handlers in the main window.
 */
public class mainController
{

  @FXML
  private ImageView myImageViewer; // the single imageview that gets updated
                                   // over and over again with a new Image in
                                   // updateDisplay method

  @FXML
  private Button startButton;

  @FXML
  private Button stopButton;

  @FXML
  private Text fitnessText;

  private NewMain main;

  @FXML
  void startButtonHandler(ActionEvent event)
  {
    main.startLoop();
  }

  @FXML
  void stopButtonHandler(ActionEvent event)
  {
    main.stopLoop();
  }

  /**
   * Gives the controller a reference to main to be able to communicate when
   * buttons are pressed
   * 
   * @param main
   */
  public void initController(NewMain main)
  {
    this.main = main;
  }

  /**
   * Where the actual updating to the screen of the picture and fitness value
   * happens. Called by main's application loop.
   * 
   * @param img
   * @param fitness
   */
  public void updateDisplay(Image img, double fitness)
  {
    myImageViewer.setImage(img);
    fitnessText.setText("Fitness: " + fitness);
  }
}

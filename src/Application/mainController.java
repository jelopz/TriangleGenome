package Application;

import java.io.File;

import TriangleGenome.InitialPopulation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import java.util.concurrent.TimeUnit;

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
  private ImageView targetImage;

  @FXML
  private Button chooseFileButton;

  @FXML
  private Button startButton;

  @FXML
  private Button initNewButton;

  @FXML
  private Button stopButton;

  @FXML
  private Text fitnessText;

  @FXML
  private Text elapsedTimeText;

  @FXML
  private ComboBox<String> myCB;

  @FXML
  private ComboBox<String> tribeBox;

  @FXML
  private Button printGenomes;

  private NewMain main;
  private long startTime;
  private long lastTime;
  private long stashedTime; // Time from the last time we started then stopped
                            // the GA. Without this the timer would restart to
                            // 00:00:00 everytime the GA is stopped then
                            // restarted using GUI
  private String s;

  private boolean preGA;

  @FXML
  void startButtonHandler(ActionEvent event)
  {
    preGA = false;
    if (!main.isRunning)
    {
      main.startLoop();
      startTime = System.nanoTime();
      lastTime = startTime;
    }
  }

  @FXML
  void stopButtonHandler(ActionEvent event)
  {
    if (main.isRunning)
    {
      stashedTime += System.nanoTime() - startTime;
      main.stopLoop();
    }
  }

  /**
   * Uses the file chooser found in the main class to load a valid image.
   * 
   * @param event
   */
  @FXML
  void fileButtonHandler(ActionEvent event)
  {
    File file = main.fileChooser.showOpenDialog(null);
    if (file != null)
    {
      String path = file.toURI().toString();
      main.originalImage = new Image(path, 500, 500, true, true);
      System.out.println("Loaded Image: " + path);
    }

    targetImage.setImage(main.originalImage);
  }

  /**
   * After uploading a valid image file, pressing this button finds the initial
   * population, which preps the GA.
   * 
   * @param event
   */
  @FXML
  void findInitialPopulation(ActionEvent event)
  {
    InitialPopulation viewInitialPopulation = new InitialPopulation(main.originalImage, main, main.NUM_OF_THREADS);

    main.tribes = viewInitialPopulation.getTribes();
    main.tribesGA = viewInitialPopulation.getTribesGAs();

    myImageViewer.setImage(viewInitialPopulation.getInitImage());
    fitnessText.setText("Current Best Fitness: " + String.valueOf(viewInitialPopulation.getInitFitness()));
  }

  @FXML
  void printButtonHandler(ActionEvent event)
  {
    main.printAllGenomeFitness();
  }

  /**
   * First, stops the loop. Then, calls main to tell the GA what type of
   * mutation to run
   * 
   * @param event
   */
  @FXML
  void comboBoxHandler(ActionEvent event)
  {
    main.stopLoop();
    String selection = myCB.getValue();

    if (selection == "Soft Mutate")
    {
      main.setMutationType(false);
    }
    else if (selection == "Hard Mutate")
    {
      main.setMutationType(true);
    }
  }

  @FXML
  void tribeBoxHandler(ActionEvent event)
  {
    String selection = tribeBox.getValue();
    main.toggleView(true);
    if (selection == "Best Fit Tribe")
    {
      main.toggleView(true);
      System.out.println("best fit");
      if (preGA)
      {
        main.updateDisplayPREGA();
      }
      else
      {
        main.updateDisplay();
      }
    }
    else
    {
      main.toggleView(false);

      if (selection == "Tribe 0")
      {
        System.out.println("0");
        main.setTribeDisplayed(0);
      }
      else
      {
        System.out.println("1");
        main.setTribeDisplayed(1);
      }

      if (preGA)
      {
        main.updateDisplayPREGA();
      }
      else
      {
        main.updateDisplay();
      }
    }
  }

  public void setElapsedTime(long thisTime)
  {
    long t = thisTime - lastTime;
    if (t / 1E9 >= 1) // if it's been a second and it's time to update timer
    {
      t = thisTime - startTime + stashedTime; // elapsed time in nanoseconds
      String s = String.format("%02d:%02d:%02d", TimeUnit.NANOSECONDS.toHours(t), TimeUnit.NANOSECONDS.toMinutes(t) % TimeUnit.HOURS.toMinutes(1), TimeUnit.NANOSECONDS.toSeconds(t) % TimeUnit.MINUTES.toSeconds(1));
      elapsedTimeText.setText("Elapsed Time: " + s);
      lastTime = thisTime;
    }
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

    // Populates the combo box
    myCB.getItems().addAll("Soft Mutate", "Hard Mutate");
    tribeBox.getItems().addAll("Best Fit Tribe", "Tribe 0", "Tribe 1");

    stashedTime = 0;
    preGA = true;
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
    fitnessText.setText("Current Best Fitness: " + fitness);
  }
}

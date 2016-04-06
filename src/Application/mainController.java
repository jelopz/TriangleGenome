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
  private Text totalPopulationText;

  @FXML
  private Text totalGenerations;

  @FXML
  private Text hillclimbChildren;

  @FXML
  private Text crossoverChildren;

  @FXML
  private Text currentAvgGPS;

  @FXML
  private Text totalAvgGPS;

  @FXML
  private Text bestGenomesFitPerSec;

  @FXML
  private ComboBox<String> myCB;

  @FXML
  private ComboBox<String> tribeBox;

  @FXML
  private ComboBox<String> genomeViewerBox;

  @FXML
  private Button printGenomes;

  private NewMain main;
  private long startTime;
  private long lastTime;
  private long stashedTime; // Time from the last time we started then stopped
                            // the GA. Without this the timer would restart to
                            // 00:00:00 everytime the GA is stopped then
                            // restarted using GUI

  private int totalPopulation;

  private long elapsedNanoTime;

  @FXML
      void startButtonHandler(ActionEvent event)
  {
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
      initNewButton.setDisable(false);
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
    InitialPopulation viewInitialPopulation = new InitialPopulation(main.originalImage, main,
        main.NUM_OF_THREADS);

    main.tribes = viewInitialPopulation.getTribes();
    main.tribesGA = viewInitialPopulation.getTribesGAs();

    System.out.println("SIZE: " + main.tribesGA.size());
    myImageViewer.setImage(viewInitialPopulation.getInitImage());
    fitnessText.setText("Current Best Fitness: " + String.valueOf(viewInitialPopulation
        .getInitFitness()));
    main.setTotalPopulation();
    chooseFileButton.setDisable(true);
    initNewButton.setDisable(true);
    startButton.setDisable(false);
    stopButton.setDisable(false);
    tribeBox.setDisable(false);
    printGenomes.setDisable(false);
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
      void genomeViewerBoxHandler(ActionEvent event)
  {
    main.setGenomeDisplayed(Integer.parseInt(genomeViewerBox.getValue()));
    main.updateDisplay();
  }

  @FXML
      void tribeBoxHandler(ActionEvent event)
  {
    String selection = tribeBox.getValue();
    main.toggleView(true);
    if (selection == "Best Fit From All Tribes")
    {
      genomeViewerBox.setDisable(true);
      main.toggleView(true);
      main.updateDisplay();
    }
    else
    {
      main.toggleView(false);

      if (selection == "Best Fit From Tribe 0")
      {
        genomeViewerBox.setDisable(true);
        main.setTribeDisplayed(0);
        main.setGenomeViewer(false);
      }
      else if (selection == "Best Fit From Tribe 1")
      {
        genomeViewerBox.setDisable(true);
        main.setTribeDisplayed(1);
        main.setGenomeViewer(false);
      }
      else if (selection == "Specific Genome From Tribe 0")
      {
        genomeViewerBox.setDisable(false);
        main.setTribeDisplayed(0);
        main.setGenomeViewer(true);
      }
      else // specific genome from tribe 1
      {
        genomeViewerBox.setDisable(false);
        main.setTribeDisplayed(1);
        main.setGenomeViewer(true);
      }

      main.updateDisplay();
    }
  }

  public void setElapsedTime(long thisTime)
  {
    long t = thisTime - lastTime;
    if (t / 1E9 >= 1) // if it's been a second and it's time to update timer
    {
      elapsedNanoTime = thisTime - startTime + stashedTime;
      String s = String.format("%02d:%02d:%02d", TimeUnit.NANOSECONDS.toHours(elapsedNanoTime),
          TimeUnit.NANOSECONDS.toMinutes(elapsedNanoTime) % TimeUnit.HOURS.toMinutes(1),
          TimeUnit.NANOSECONDS.toSeconds(elapsedNanoTime) % TimeUnit.MINUTES.toSeconds(1));
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
    tribeBox.getItems().addAll("Best Fit From All Tribes", "Best Fit From Tribe 0",
        "Best Fit From Tribe 1", "Specific Genome From Tribe 0", "Specific Genome From Tribe 1");

    stashedTime = 0;

    totalPopulation = 0;

    for (int i = 0; i < 100; i++)
    {
      genomeViewerBox.getItems().add(String.valueOf(i));
    }

    // totalGenerations = 0;
    // hillclimbChildren = 0;
    // crossoverChildren = 0;
    elapsedNanoTime = 0;
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

  public void setTotalPopulation(int i)
  {
    totalPopulation = i;
    totalPopulationText.setText("Total Population: " + String.valueOf(totalPopulation));
  }

  public void updateStatistics(int totalGenerations, int hillclimbChildren, int crossoverChildren,
      int currentGenerationsPerSecond, int totalGenerationsPerSecond, double deltaFitnessPerSecond)
  {
    this.totalGenerations.setText("Total Generations: " + totalGenerations);
    this.hillclimbChildren.setText("Total Hill-Climb Children: " + hillclimbChildren);
    this.crossoverChildren.setText("Total Cross-Over Children: " + crossoverChildren);
    currentAvgGPS.setText("Current Average Generations per Second: " + currentGenerationsPerSecond);
    totalAvgGPS.setText("Total Average Generations per Second: " + totalGenerationsPerSecond);
    bestGenomesFitPerSec.setText("Most Fit Genome's change in fitness/second: " + deltaFitnessPerSecond);
  }

  public long getElapsedNanoTime()
  {
    return elapsedNanoTime;
  }
}

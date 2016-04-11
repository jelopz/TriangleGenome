package Application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import TriangleGenome.InitialPopulation;
import TriangleGenome.Triangle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.util.ArrayList;
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
  private Button resetButton;

  @FXML
  private Button saveGenomeButton;

  @FXML
  private Button uploadButton;

  @FXML
  private Button saveStatsButton;

  @FXML
  private Text fitnessText;

  @FXML
  private Text elapsedTimeText;

  @FXML
  private Text totalPopulationText;

  @FXML
  private Text totalGenerations;

  @FXML
  private Text totalGPS; // Generations Per Second

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
  private ComboBox<String> tribeBox;

  @FXML
  private ComboBox<String> genomeViewerBox;

  @FXML
  private ComboBox<String> geneSelectorBox;

  @FXML
  private ComboBox<String> threadSelectorBox;

  @FXML
  private Button printGenomes;

  private NewMain main;
  private long startTime;
  private long lastTime;
  private long lastSaveTime;
  private long lastGenomeSaveTime;
  private long stashedTime; // Time from the last time we started then stopped
                            // the GA. Without this the timer would restart to
                            // 00:00:00 everytime the GA is stopped then
                            // restarted using GUI

  private int totalPopulation;

  private long elapsedNanoTime;
  private String elapsedFormattedTime;

  @FXML
      void startButtonHandler(ActionEvent event)
  {
    if (!main.isRunning)
    {
      main.startLoop();
      startTime = System.nanoTime();
      lastTime = startTime;
      lastSaveTime = startTime;
      lastGenomeSaveTime = startTime;
      saveStatsButton.setDisable(true);
      saveGenomeButton.setDisable(true);
    }
  }

  @FXML
      void stopButtonHandler(ActionEvent event)
  {
    if (main.isRunning)
    {
      stashedTime += System.nanoTime() - startTime;
      main.stopLoop();
      saveStatsButton.setDisable(false);
      saveGenomeButton.setDisable(false);
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
    InitialPopulation initPop = new InitialPopulation(main.originalImage, main, main
        .getNumThreads());

    main.tribes = initPop.getTribes();
    main.tribesGA = initPop.getTribesGAs();

    System.out.println("SIZE: " + main.tribesGA.size());
    myImageViewer.setImage(initPop.getInitImage());
    fitnessText.setText("Current Best Fitness: " + String.valueOf(initPop.getInitFitness()));
    main.setTotalPopulation();
    chooseFileButton.setDisable(true);
    initNewButton.setDisable(true);
    startButton.setDisable(false);
    stopButton.setDisable(false);
    tribeBox.setDisable(false);
    printGenomes.setDisable(false);
    threadSelectorBox.setDisable(true);
    saveGenomeButton.setDisable(false);

    if (!main.startThreads)
    {
      main.updateThreads();
    }
  }

  @FXML
      void printButtonHandler(ActionEvent event)
  {
    main.printAllGenomeFitness();
  }

  @FXML
      void saveGenomeButtonHandler(ActionEvent event)
  {
    main.saveCurrentGenomeDisplayed();
  }

  @FXML
      void uploadButtonHandler(ActionEvent event)
  {
	  File file = main.genomeChooser.showOpenDialog(null);
	  
	    if (file != null)
	    {
	      String path = file.toURI().toString();
	      System.out.println("Loaded Genome: " + path);
	      main.makeNewGenome(file);
	    }
  }

  @FXML
      void resetButtonHandler(ActionEvent event)
  {
    stashedTime = 0;
    elapsedNanoTime = 0;

    if (main.isRunning)
    {
      main.stopLoop();
    }

    fitnessText.setText("Current Best Fitness: N/A");
    elapsedTimeText.setText("Elapsed Time: N/A");
    totalPopulationText.setText("Total Population: N/A");
    totalGenerations.setText("Total Generations: N/A");
    hillclimbChildren.setText("Total Hill-Climb Children: N/A");
    crossoverChildren.setText("Total Cross-Over Children: N/A");
    currentAvgGPS.setText("Current Average Generations per Second: N/A");
    totalAvgGPS.setText("Total Average Generations per Second: N/A");
    bestGenomesFitPerSec.setText("Most Fit Genome's change in fitness/second: N/A");

    saveStatsButton.setDisable(true);
    chooseFileButton.setDisable(false);
    initNewButton.setDisable(false);
    startButton.setDisable(true);
    stopButton.setDisable(true);
    genomeViewerBox.setDisable(true);
    threadSelectorBox.setDisable(false);
    printGenomes.setDisable(true);
    geneSelectorBox.setDisable(true);
    tribeBox.setDisable(true);
    saveGenomeButton.setDisable(true);
  }

  @FXML
      void genomeViewerBoxHandler(ActionEvent event)
  {
    main.setGenomeDisplayed(Integer.parseInt(genomeViewerBox.getValue()));
    main.updateDisplay();
  }

  @FXML
      void geneSelectorBoxHandler(ActionEvent event)
  {
    if (geneSelectorBox.getValue() == "Show Complete Genome")
    {
      main.toggleShowWholeGenome(true);
    }
    else
    {
      main.toggleShowWholeGenome(false);
      main.setGeneDisplayed(Integer.parseInt(geneSelectorBox.getValue()));
    }

    main.updateDisplay();
  }

  @FXML
      void threadSelectorBoxHandler(ActionEvent event)
  {
    int newNumThreads = Integer.parseInt(threadSelectorBox.getValue());
    main.setNumThreads(newNumThreads);

    tribeBox.getItems().setAll();

    tribeBox.getItems().addAll("Best Fit From All Tribes");

    for (int i = 0; i < newNumThreads; i++)
    {
      tribeBox.getItems().addAll(("Best Fit From Tribe " + i), ("Specific Genome From Tribe " + i));
    }
  }

  @FXML
      void tribeBoxHandler(ActionEvent event)
  {
    String selection = tribeBox.getValue();
    main.toggleView(true);
    if (selection == "Best Fit From All Tribes")
    {
      genomeViewerBox.setDisable(true);
      geneSelectorBox.setDisable(true);
      uploadButton.setDisable(true);
      main.toggleView(true);
      main.updateDisplay();
    }
    else
    {
      main.toggleView(false);
      String s;
      String z;

      for (int i = 0; i < main.getNumThreads(); i++)
      {
        s = ("Best Fit From Tribe " + i);
        z = ("Specific Genome From Tribe " + i);

        if (selection.equals(s))
        {
          System.out.println("1");
          genomeViewerBox.setDisable(true);
          geneSelectorBox.setDisable(true);
          uploadButton.setDisable(true);
          main.setTribeDisplayed(i);
          main.setGenomeViewer(false);
        }
        else if (selection.equals(z))
        {
          System.out.println("2");
          genomeViewerBox.setDisable(false);
          geneSelectorBox.setDisable(false);
          uploadButton.setDisable(false);
          main.setTribeDisplayed(i);
          main.setGenomeViewer(true);
        }
      }

      main.updateDisplay();
    }
  }

  @FXML
      void saveStatsButtonHandler()
  {
    main.saveStatistics();
  }

  public void setElapsedTime(long thisTime)
  {
    long t = thisTime - lastTime;
    long l = thisTime - lastSaveTime;
    long g = thisTime - lastGenomeSaveTime;

    if (t / 1E9 >= 1) // if it's been a second and it's time to update timer
    {
      elapsedNanoTime = thisTime - startTime + stashedTime;
      elapsedFormattedTime = String.format("%02d:%02d:%02d", TimeUnit.NANOSECONDS.toHours(
          elapsedNanoTime), TimeUnit.NANOSECONDS.toMinutes(elapsedNanoTime) % TimeUnit.HOURS
              .toMinutes(1), TimeUnit.NANOSECONDS.toSeconds(elapsedNanoTime) % TimeUnit.MINUTES
                  .toSeconds(1));
      elapsedTimeText.setText("Elapsed Time: " + elapsedFormattedTime);
      lastTime = thisTime;
    }
    if (l / 1E9 >= 600)
    {
      main.updateStatSaver(elapsedFormattedTime);
      lastSaveTime = thisTime;
    }
    if (g / 1E9 >= 600)
    {
      lastGenomeSaveTime = thisTime;
      saveGenomeButtonHandler(null);
    }
    if (main.HEADLESS)
    {
      System.out.println(elapsedNanoTime / 1E9);
      if (elapsedNanoTime / 1E9 > 540) // run for 1.5 hours
      {
        main.stopLoop();
        main.saveStatistics();
      }
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
    tribeBox.getItems().addAll("Best Fit From All Tribes");// , "Best Fit From
                                                           // Tribe 0",
    // "Best Fit From Tribe 1", "Specific Genome From Tribe 0", "Specific Genome
    // From Tribe 1");

    for (int i = 0; i < main.getNumThreads(); i++)
    {
      tribeBox.getItems().addAll(("Best Fit From Tribe " + i), ("Specific Genome From Tribe " + i));
    }

    stashedTime = 0;

    totalPopulation = 0;

    geneSelectorBox.getItems().add("Show Complete Genome");

    for (int i = 0; i < 100; i++)
    {
      threadSelectorBox.getItems().add(String.valueOf(i + 1));
      geneSelectorBox.getItems().add(String.valueOf(i));
    }
    for (int i = 100; i < 200; i++)
    {
      geneSelectorBox.getItems().add(String.valueOf(i));
    }

    // totalGenerations = 0;
    // hillclimbChildren = 0;
    // crossoverChildren = 0;
    elapsedNanoTime = 0;
    initNewButton.setDisable(false); // headless
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

  public void setTargetImage(Image img)
  {
    targetImage.setImage(img);
  }

  public void setTotalPopulation(int i, int numTribes)
  {
    totalPopulation = i;
    totalPopulationText.setText("Total Population: " + String.valueOf(totalPopulation));

    // i/numTribes = population per tribe
    for (int j = 0; j < (i / numTribes); j++)
    {
      genomeViewerBox.getItems().add(String.valueOf(j));
    }
  }

  public void updateStatistics(int totalGenerations, int hillclimbChildren, int crossoverChildren,
      double totalGPS, double avgCurrentGenerationsPerSecond, double avgTotalGenerationsPerSecond,
      double deltaFitnessPerSecond)
  {
    this.totalGenerations.setText("Total Generations: " + totalGenerations);
    this.hillclimbChildren.setText("Total Hill-Climb Children: " + hillclimbChildren);
    this.crossoverChildren.setText("Total Cross-Over Children: " + crossoverChildren);

    this.totalGPS.setText("Total Generations per Second:\n" + totalGPS);

    currentAvgGPS.setText("Current Average Generations per Second:\n"
        + avgCurrentGenerationsPerSecond);
    totalAvgGPS.setText("Total Average Generations per Second:\n" + avgTotalGenerationsPerSecond);
    bestGenomesFitPerSec.setText("Most Fit Genome's change in fitness/second:\n"
        + deltaFitnessPerSecond);
  }

  public long getElapsedNanoTime()
  {
    return elapsedNanoTime;
  }
}

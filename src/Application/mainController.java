package Application;


import java.io.File;
import TriangleGenome.InitialPopulation;
import TriangleGenome.Triangle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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

  /**
   * the single imageview that gets updated over and over again with a new image
   * in updateDisplay method
   */
  @FXML
  private ImageView myImageViewer;

  @FXML
  private ImageView targetImage;

  @FXML
  private TextField editTextField;

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
  private Button editButton;

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
  private ComboBox<String> defaultImageSelectorBox;

  @FXML
  private ComboBox<String> tribeBox;

  @FXML
  private ComboBox<String> genomeViewerBox;

  @FXML
  private ComboBox<String> geneSelectorBox;

  @FXML
  private ComboBox<String> threadSelectorBox;

  @FXML
  private ComboBox<String> editGeneSelectorBox;

  @FXML
  private ComboBox<String> editTriangleSelectorBox;

  @FXML
  private Button printGenomes;

  @FXML
  private TableView<Triangle> tableID;

  @FXML
  private TableColumn<Triangle, Integer> triIDCol;

  @FXML
  private TableColumn<Triangle, Integer> p1xCol;

  @FXML
  private TableColumn<Triangle, Integer> p1yCol;

  @FXML
  private TableColumn<Triangle, Integer> p2xCol;

  @FXML
  private TableColumn<Triangle, Integer> p2yCol;

  @FXML
  private TableColumn<Triangle, Integer> p3xCol;

  @FXML
  private TableColumn<Triangle, Integer> p3yCol;

  @FXML
  private TableColumn<Triangle, Integer> rCol;

  @FXML
  private TableColumn<Triangle, Integer> gCol;

  @FXML
  private TableColumn<Triangle, Integer> bCol;

  @FXML
  private TableColumn<Triangle, Integer> aCol;

  private NewMain main;

  /** Time when GA was started */
  private long startTime;

  /** Used when determining how long ago we updated screen */
  private long lastTime;

  /** Used when determine how long ago we last made a timestamp of statistics */
  private long lastSaveTime;

  /**
   * Used when determining how long ago we last took a snapshot of the most fit
   * genome. Used for testing
   */
  private long lastGenomeSaveTime;

  /**
   * Time from the last time we started then stopped the GA. without this the
   * timer would restart to 00:00:00 everytime the GA is stopped then restarted
   * using the GUI
   */
  private long stashedTime;

  /** Total population from all tribes */
  private int totalPopulation;

  private long elapsedNanoTime;
  private String elapsedFormattedTime;

  /** ObservableList used to populate the table */
  @SuppressWarnings("rawtypes")
  private ObservableList tableList;

  /**
   * Starts the pathfinding loop
   * 
   * @param event
   */
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
      editGeneSelectorBox.setDisable(true);
      editTextField.setDisable(true);
      editButton.setDisable(true);
      uploadButton.setDisable(true);
      editTriangleSelectorBox.setDisable(true);
    }
  }

  /**
   * Stops the pathfinding loop
   * 
   * @param event
   */
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
   * After uploading a valid image file, pressing this button finds the initial
   * population, which preps the GA.
   * 
   * @param event
   */
  @SuppressWarnings("unchecked")
  @FXML
      void findInitialPopulation(ActionEvent event)
  {
    InitialPopulation initPop = new InitialPopulation(main.originalImage, main, main
        .getNumThreads());

    main.tribes = initPop.getTribes();
    main.tribesGA = initPop.getTribesGAs();

    myImageViewer.setImage(initPop.getInitImage());
    fitnessText.setText("Current Best Fitness: " + String.valueOf(initPop.getInitFitness()));
    main.setTotalPopulation();
    defaultImageSelectorBox.setDisable(true);
    initNewButton.setDisable(true);
    startButton.setDisable(false);
    stopButton.setDisable(false);
    tribeBox.setDisable(false);
    printGenomes.setDisable(false);
    threadSelectorBox.setDisable(true);
    saveGenomeButton.setDisable(false);
    editGeneSelectorBox.setDisable(true);
    editTextField.setDisable(true);
    editButton.setDisable(true);
    editTriangleSelectorBox.setDisable(true);

    int bestFitTribe = initPop.getBestFitTribe();

    p1xCol.setCellValueFactory(new PropertyValueFactory<Triangle, Integer>("p1x"));
    p1yCol.setCellValueFactory(new PropertyValueFactory<Triangle, Integer>("p1y"));
    p2xCol.setCellValueFactory(new PropertyValueFactory<Triangle, Integer>("p2x"));
    p2yCol.setCellValueFactory(new PropertyValueFactory<Triangle, Integer>("p2y"));
    p3xCol.setCellValueFactory(new PropertyValueFactory<Triangle, Integer>("p3x"));
    p3yCol.setCellValueFactory(new PropertyValueFactory<Triangle, Integer>("p3y"));
    rCol.setCellValueFactory(new PropertyValueFactory<Triangle, Integer>("red"));
    gCol.setCellValueFactory(new PropertyValueFactory<Triangle, Integer>("green"));
    bCol.setCellValueFactory(new PropertyValueFactory<Triangle, Integer>("blue"));
    aCol.setCellValueFactory(new PropertyValueFactory<Triangle, Integer>("alpha"));

    tableList = FXCollections.observableArrayList(initPop.getTribes().get(bestFitTribe)
        .getGenomesInTribe().get(0).getDNA());

    tableID.setItems(tableList);

    if (!main.startThreads)
    {
      main.updateThreads();
    }
  }

  /**
   * Called when user presses the edit button and successfully edits a gene
   * 
   * @param dna
   *          the new DNA to display on the table
   */
  @SuppressWarnings("unchecked")
  private void updateTable(ArrayList<Triangle> dna)
  {
    tableList.removeAll(tableList);
    tableList.addAll(FXCollections.observableArrayList(dna));
  }

  /**
   * For testing purposes. Prints the values of all the genomes to the console
   * 
   * @param event
   */
  @FXML
      void printButtonHandler(ActionEvent event)
  {
    main.printAllGenomeFitness();
  }

  /**
   * Handles what the target image is. Choose from presets or upload your own.
   * 
   * @param event
   */
  @FXML
      void defaultImageBoxHandler(ActionEvent event)
  {
    String s = defaultImageSelectorBox.getValue();
    if (s == "Upload..")
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
    else if (s == "Mona Lisa")
    {
      String path = "mona-lisa-cropted-512x413.png";
      main.originalImage = new Image(path, 500, 500, true, true);
      targetImage.setImage(main.originalImage);
    }
    else if (s == "Poppyfield")
    {
      String path = "poppyfields-512x384.png";
      main.originalImage = new Image(path, 500, 500, true, true);
      targetImage.setImage(main.originalImage);
    }
    else if (s == "Great Wave")
    {
      String path = "the_great_wave_off_kanagawa-512x352.png";
      main.originalImage = new Image(path, 500, 500, true, true);
      targetImage.setImage(main.originalImage);
    }
    else if (s == "Baby")
    {
      String path = "baby.png";
      main.originalImage = new Image(path, 500, 500, true, true);
      targetImage.setImage(main.originalImage);
    }
    else if (s == "Star")
    {
      String path = "dallascowboys.jpg";
      main.originalImage = new Image(path, 500, 500, true, true);
      targetImage.setImage(main.originalImage);
    }
  }

  /**
   * Saves the current genome displayed to the screen to a text file
   * 
   * @param event
   */
  @FXML
      void saveGenomeButtonHandler(ActionEvent event)
  {
    main.saveCurrentGenomeDisplayed();
  }

  /**
   * Uploads a genome to the current tribe selected.
   * 
   * @param event
   */
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

  /**
   * Resets the pathfinding loop. Loses all previous data in preparation for the
   * new one.
   * 
   * @param event
   */
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
    defaultImageSelectorBox.setDisable(false);
    initNewButton.setDisable(false);
    startButton.setDisable(true);
    stopButton.setDisable(true);
    genomeViewerBox.setDisable(true);
    threadSelectorBox.setDisable(false);
    printGenomes.setDisable(true);
    geneSelectorBox.setDisable(true);
    tribeBox.setDisable(true);
    saveGenomeButton.setDisable(true);
    editGeneSelectorBox.setDisable(true);
    editTextField.setDisable(true);
    editButton.setDisable(true);
    editTriangleSelectorBox.setDisable(true);
  }

  /**
   * Called when user wants to browse through a list of genomes from a selected
   * tribe.
   * 
   * @param event
   */
  @FXML
      void genomeViewerBoxHandler(ActionEvent event)
  {
    main.setGenomeDisplayed(Integer.parseInt(genomeViewerBox.getValue()));
    main.updateDisplay();
  }

  /**
   * Called when a user wnats to browse through a list of triangles from a
   * selected genome.
   * 
   * @param event
   */
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

  /**
   * Only possible to be used when reset. Chooses the amount of threads to use
   * in the next execution of the GA.
   * 
   * @param event
   */
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

  /**
   * Checks the values in the editTriangleSelectorBox, editTextField and
   * editgeneSelectorBox to figure out what specific gene is being edited
   * 
   * @param event
   */
  @FXML
      void editButtonHandler(ActionEvent event)
  {
    int tri = Integer.parseInt(editTriangleSelectorBox.getValue());
    int value = Integer.parseInt(editTextField.getText());
    String s = editGeneSelectorBox.getValue();
    if ((s == "r") || (s == "g") || (s == "b") || (s == "a"))
    {
      if (value >= 0 && value <= 255)
      {
        main.editGenome(tri, s, value);
      }
    }
    else
    {
      main.editGenome(tri, s, value);
    }
  }

  /**
   * Choose between looking at the most fit from all tribe, the most fit from
   * specific tribes, or specific genomes from specific tribes
   * 
   * @param event
   */
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
      editGeneSelectorBox.setDisable(true);
      editTextField.setDisable(true);
      editButton.setDisable(true);
      editTriangleSelectorBox.setDisable(true);
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
          genomeViewerBox.setDisable(true);
          geneSelectorBox.setDisable(true);
          editGeneSelectorBox.setDisable(true);
          editTextField.setDisable(true);
          editButton.setDisable(true);
          uploadButton.setDisable(true);
          editTriangleSelectorBox.setDisable(true);
          main.setTribeDisplayed(i);
          main.setGenomeViewer(false);
        }
        else if (selection.equals(z))
        {
          genomeViewerBox.setDisable(false);
          geneSelectorBox.setDisable(false);
          uploadButton.setDisable(false);
          editGeneSelectorBox.setDisable(false);
          editTextField.setDisable(false);
          editButton.setDisable(false);
          editTriangleSelectorBox.setDisable(false);
          main.setTribeDisplayed(i);
          main.setGenomeViewer(true);
        }
      }

      main.updateDisplay();
    }
  }

  /**
   * Saves all the timestamps to a text file. For testing purposes.
   */
  @FXML
      void saveStatsButtonHandler()
  {
    main.saveStatistics();
  }

  /**
   * Called approximately once every second to update the elapsed time of the
   * GA.
   * 
   * @param thisTime
   *          the current wall clock time
   */
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
      if (elapsedNanoTime / 1E9 > 540) // run for 1.5 hours
      {
        main.stopLoop();
        main.saveStatistics();
      }
    }
  }

  /**
   * Gives the controller a reference to main to be able to communicate when
   * buttons are pressed and populates all the ComboBoxs
   * 
   * @param main
   */
  public void initController(NewMain main)
  {
    this.main = main;

    // Populates the combo box
    tribeBox.getItems().addAll("Best Fit From All Tribes");

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
      editTriangleSelectorBox.getItems().add(String.valueOf(i));
    }
    for (int i = 100; i < 200; i++)
    {
      geneSelectorBox.getItems().add(String.valueOf(i));
      editTriangleSelectorBox.getItems().add(String.valueOf(i));
    }

    editGeneSelectorBox.getItems().addAll("p1x", "p1y", "p2x", "p2y", "p3x", "p3y", "r", "g", "b",
        "a");

    editTriangleSelectorBox.setValue("0");
    editGeneSelectorBox.setValue("p1x");

    // totalGenerations = 0;
    // hillclimbChildren = 0;
    // crossoverChildren = 0;
    elapsedNanoTime = 0;
    initNewButton.setDisable(false); // headless

    defaultImageSelectorBox.getItems().addAll("Mona Lisa", "Poppyfield", "Great Wave", "Baby",
        "Star", "Upload..");
  }

  /**
   * Where the actual updating to the screen of the picture and fitness value
   * happens. Called by main's application loop.
   * 
   * @param img
   * @param fitness
   */
  public void updateDisplay(Image img, double fitness, ArrayList<Triangle> dna)
  {
    myImageViewer.setImage(img);
    fitnessText.setText("Current Best Fitness: " + fitness);
    updateTable(dna);
  }

  /**
   * Called when user selects a new target image to display to screen.
   * 
   * @param img
   *          new target image
   */
  public void setTargetImage(Image img)
  {
    targetImage.setImage(img);
  }

  /**
   * Called when the population changes.
   * 
   * @param i
   *          total population from all tribes
   * @param numTribes
   *          number of tribes
   */
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

  /**
   * Called by main when needed to update statistics to GUI. All these
   * calculations are done in main with thorough explanations in
   * main.updateStatistics method
   * 
   * @param totalGenerations
   * @param hillclimbChildren
   * @param crossoverChildren
   * @param totalGPS
   * @param avgCurrentGenerationsPerSecond
   * @param avgTotalGenerationsPerSecond
   * @param deltaFitnessPerSecond
   */
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

  /**
   * @return the current elapsed time in nanoseconds
   */
  public long getElapsedNanoTime()
  {
    return elapsedNanoTime;
  }
}

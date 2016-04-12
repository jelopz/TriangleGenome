package Application;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import Application.NewMain.WorkerThread;
import TriangleGenome.FitnessFunction;
import TriangleGenome.GA;
import TriangleGenome.Genome;
import TriangleGenome.InitialPopulation;
import TriangleGenome.Renderer;
import TriangleGenome.Triangle;
import TriangleGenome.Tribe;
import TriangleGenome.UtilityClass;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * The CPU of the application. The controllers handle the GUI and the GA handles
 * the pathfinding algorithm. Anytime the controllers or the GA needs something
 * done outside of it's own respective realm, they need to go through this main
 * class to do it.
 * 
 * As of now, the animationloop tells the mainController to update the image and
 * fitness value every half a second.
 */
public class NewMain extends Application
{
  boolean HEADLESS = false;

  // Filechooser
  FileChooser fileChooser;
  FileChooser genomeChooser;

  Image originalImage;
  // int NUM_OF_THREADS = 2;
  private int numThreads;
  GA ga;
  ArrayList<Tribe> tribes;
  ArrayList<GA> tribesGA;
  ArrayList<Triangle> specificGene;
  private Random random = new Random();
  ArrayList<WorkerThread> threads;

  // Each thread will have a global pool where some number of genomes
  // from the other tribes will be collected after some number of iterations
  // and then sent over to that threads GA so it can perform cross over.
  private ArrayList<ArrayList<Genome>> globalPools = new ArrayList<>();

  boolean startThreads = true;
  boolean crossOverMode = false;
  boolean crossOverModeStarted = false;
  boolean isRunning; // used to let the ApplicationLoop know when to run
  private mainController mainController;
  private UtilityClass util;
  private Image displayedPop;
  private ArrayList<Triangle> displayedDNA;

  private int countTillCrossOver;
  private double displayedFitness;
  private boolean viewToggle; // Show the best fit genome or the user selected
  // one? true = best fit, false = user selection

  private int tribeDisplayed; // If (!viewtoggle), we display the
  // tribesGA.get(tribeDisplayed) image

  private int genomeDisplayed;
  private int geneDisplayed;

  private boolean genomeViewer;
  private boolean showWholeGenome;

  private GA currentGenome;
  private int numGenerations;

  private ArrayList<String> statSaver;

  private int totalGenerations;
  private int hillclimbChildren;
  private int crossoverChildren;
  private int stuckCount;
  private double totalGenerationsPerSecond;
  private double avgCurrentGenerationsPerSecond;
  private double avgTotalGenerationsPerSecond;
  private double deltaFitnessPerSecond;
  private double[] tribesDeltaT;
  private Renderer render;

  /**
   * The creation of the initial window, a popup that asks you to load an image.
   * Once loaded, the user will press start and then the main window is created.
   */
  public void start(Stage primaryStage) throws Exception
  {
    fileChooser = new FileChooser();
    fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Image files",
        new String[]
    { ".png", ".bmp", ".jpg", ".gif" }));
    fileChooser.setTitle("Image Selector");

    genomeChooser = new FileChooser();
    genomeChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Genome Files",
        new String(".txt")));
    genomeChooser.setTitle("Genome Selector");

    isRunning = false;
    stuckCount = 0;
    countTillCrossOver = 0;
    specificGene = new ArrayList<>();
    util = new UtilityClass();
    statSaver = new ArrayList<>();
    displayedDNA = new ArrayList<>();

    viewToggle = true;
    genomeViewer = false;
    showWholeGenome = true;

    createMainWindow();
  }

  /**
   * Used when user selected a specific genome from a specific tribe.
   * 
   * @param b
   *          true if showing the whole genome, false if showing a single
   *          triangle from the genome
   */
  public void toggleShowWholeGenome(boolean b)
  {
    showWholeGenome = b;
  }

  /**
   * Overriding the stop method as the application wasn't turning off when the
   * user closes
   */
  @Override
  public void stop()
  {
    System.exit(0);
  }

  /**
   * Iniitalizes a renderer for the main class. Used when uploading or editing
   * genomes
   * 
   * @param w
   *          width of image
   * @param h
   *          height of image
   * @param c
   *          color of background
   */
  public void initRenderer(int w, int h, Color c)
  {
    render = new Renderer(w, h, c);
  }

  /**
   * Given the genome txt file, reads the data and creates the genome
   * accordingly
   * 
   * This is called when a genome is uploaded to the application. The user can
   * only press the upload button whenever the user is looking at a specific
   * genome from a specific tribe and the application is paused.
   * 
   * The genome that is uploaded will replace the least fit genome from the
   * tribe that the user has already selected
   * 
   * @param f
   *          the genome .txt file uploaded by the user
   */
  public void makeNewGenome(File f)
  {
    ArrayList<Triangle> newDNA = new ArrayList<>();

    try
    {
      FileReader reader = new FileReader(f);
      BufferedReader bufferedReader = new BufferedReader(reader);
      Triangle t;

      String line;
      String[] s;
      int[] dnaValues = new int[10];

      while ((line = bufferedReader.readLine()) != null)
      {
        s = line.split(" ");
        for (int i = 0; i < 10; i++)
        {
          dnaValues[i] = Integer.parseInt(s[i]);
        }
        t = new Triangle();
        t.setP1x(dnaValues[0]);
        t.setP1y(dnaValues[1]);
        t.setP2x(dnaValues[2]);
        t.setP2y(dnaValues[3]);
        t.setP3x(dnaValues[4]);
        t.setP3y(dnaValues[5]);
        t.setRed(dnaValues[6]);
        t.setGreen(dnaValues[7]);
        t.setBlue(dnaValues[8]);
        t.setAlpha(dnaValues[9]);
        t.updateTriangle();
        newDNA.add(t);
      }

      reader.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    Genome g = new Genome(newDNA);
    render.render(g.getDNA());
    System.out.println(tribeDisplayed);
    FitnessFunction ff = tribesGA.get(tribeDisplayed).getFitObj();
    ff.calculateFitness(render.getBuff());
    g.setFitness(ff.getFitness());

    System.out.println(tribes.get(tribeDisplayed).getTribePopulation());
    tribes.get(tribeDisplayed).removeLeastFit();
    util.insertSorted(g, tribes.get(tribeDisplayed).getGenomesInTribe());
    System.out.println(tribes.get(tribeDisplayed).getTribePopulation());

    updateInfo(SwingFXUtils.toFXImage(render.getBuff(), null), ff.getFitness(), g.getDNA());
    mainController.updateDisplay(displayedPop, displayedFitness, displayedDNA);
  }

  /**
   * Called when user uses the GUI to try and edit a value.
   * 
   * Determines which genome is being edited, what triangle of the genome is
   * being edited, and the new value.
   * 
   * @param triangle
   * @param gene
   * @param value
   */
  public void editGenome(int triangle, String gene, int value)
  {
    Genome g = tribes.get(tribeDisplayed).getGenomesInTribe().get(genomeDisplayed);
    tribes.get(tribeDisplayed).getGenomesInTribe().remove(genomeDisplayed);
    if (gene.equals("p1x"))
    {
      g.getDNA().get(triangle).setP1x(value);
    }
    else if (gene.equals("p1y"))
    {
      g.getDNA().get(triangle).setP1y(value);
    }
    else if (gene.equals("p2x"))
    {
      g.getDNA().get(triangle).setP2x(value);
    }
    else if (gene.equals("p2y"))
    {
      g.getDNA().get(triangle).setP2y(value);
    }
    else if (gene.equals("p3x"))
    {
      g.getDNA().get(triangle).setP3x(value);
    }
    else if (gene.equals("p3y"))
    {
      g.getDNA().get(triangle).setP3y(value);
    }
    else if (gene.equals("r"))
    {
      g.getDNA().get(triangle).setRed(value);
    }
    else if (gene.equals("g"))
    {
      g.getDNA().get(triangle).setGreen(value);
    }
    else if (gene.equals("b"))
    {
      g.getDNA().get(triangle).setBlue(value);
    }
    else if (gene.equals("a"))
    {
      g.getDNA().get(triangle).setAlpha(value);
    }

    render.render(g.getDNA());

    FitnessFunction ff = tribesGA.get(tribeDisplayed).getFitObj();
    ff.calculateFitness(render.getBuff());
    g.setFitness(ff.getFitness());

    util.insertSorted(g, tribes.get(tribeDisplayed).getGenomesInTribe());

    updateInfo(SwingFXUtils.toFXImage(render.getBuff(), null), ff.getFitness(), g.getDNA());
    mainController.updateDisplay(displayedPop, displayedFitness, displayedDNA);
  }

  /**
   * First creates the initialPopulation, which then proceeds to initialize the
   * GA. A reference to the GA is passed back to main immediately (Happens in
   * the InitialPopulation constructor) The initial population is immediately
   * displayed on the main window pending the start of the pathfinding loop.
   * 
   * The main application window. Currently has two buttons, start or stop,
   * which controls the pathfinding algorithm. If you press start, the algorithm
   * begins and one of the most fit images found is displayed to the screen
   * 
   * @param initFitness
   *          The fitness for the initial population, to display onto screen
   * @throws IOException
   *           To make the compiler happy
   */
  public void createMainWindow() throws IOException
  {

    // InitialPopulation viewInitialPopulation = new
    // InitialPopulation(originalImage, this,NUM_OF_THREADS);
    // viewInitialPopulation.show();
    //
    // tribes = viewInitialPopulation.getTribes();
    // tribesGA = viewInitialPopulation.getTribesGAs();

    FXMLLoader loader = new FXMLLoader(getClass().getResource("GAFXML.fxml"));
    Parent root = loader.load();

    numThreads = 2;

    mainController = loader.getController();
    mainController.initController(this);
    // mainController.updateDisplay(displayedPop, displayedFitness);

    Stage primaryStage = new Stage();
    primaryStage.setTitle("Genetic Algorithm");
    primaryStage.setScene(new Scene(root));
    primaryStage.setResizable(false);

    String path = "mona-lisa-cropted-512x413.png";
    originalImage = new Image(path, 500, 500, true, true);
    System.out.println("Loaded Image: " + path);
    mainController.setTargetImage(originalImage);
    mainController.findInitialPopulation(null);

    if (!HEADLESS)
    {
      primaryStage.show();
    }
    else
    {
      mainController.startButtonHandler(null);
    }

    threads = new ArrayList<>();
    tribesDeltaT = new double[numThreads];
    AnimationTimer loop = new ApplicationLoop();
    loop.start();
  }

  /**
   * Only used during testing.
   * 
   * Every x amount of time (time is set in the GUI), add a timestamp of all the
   * current values and place it in the ArrayList to later be saved to a txt
   * file
   */
  public void updateStatSaver(String elapsedTime)
  {
    statSaver.add(statisticsToString(elapsedTime));
  }

  /**
   * Only used during testing.
   * 
   * Writes all the contents of the arraylist and saves it to a text file.
   */
  public void saveStatistics()
  {
    try
    {
      FileOutputStream outputStream = new FileOutputStream(System.currentTimeMillis()
          + "Stats.txt");
      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-16");
      BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

      bufferedWriter.write(statSaver.get(0));
      for (int i = 1; i < statSaver.size(); i++)
      {
        bufferedWriter.newLine();
        bufferedWriter.write(statSaver.get(i));
      }

      bufferedWriter.close();
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    if (HEADLESS)
    {
      System.exit(0);
    }
  }

  /**
   * Saves the data of the genome displayed to the screen into a txt file format
   */
  public void saveCurrentGenomeDisplayed()
  {
    int bestTribe = 0;
    double bestFit = tribesGA.get(0).getFit();
    if (viewToggle)
    {
      // best fit genome from all tribes
      double f;
      for (int i = 1; i < numThreads; i++) // find best fit genome
      {
        f = tribesGA.get(i).getFit();
        if (f > bestFit) // if better than current best
        {
          bestTribe = i;
        }
      }
      tribes.get(bestTribe).getGenomesInTribe().get(0).saveGenome();
    }
    else
    {
      if (!genomeViewer) // saves most fit from user selected tribe
      {
        tribes.get(tribeDisplayed).getGenomesInTribe().get(0).saveGenome();
      }
      else // saves the user selected genome from the user selected
      // tribe
      {
        Genome g = tribes.get(tribeDisplayed).getGenomesInTribe().get(genomeDisplayed);
        g.saveGenome();
      }
    }
  }

  /**
   * Called by the GA when an improved fitness is found.
   * 
   * @param img
   * @param d
   * @param arrayList
   */
  public void updateInfo(Image img, double d, ArrayList<Triangle> dna)
  {
    displayedPop = img;
    displayedFitness = d;
    displayedDNA = dna;
  }

  /**
   * Called by the Application loop every half second to update the screen with
   * the image with the highest fitness
   */
  public void updateDisplay()
  {
    // Find the fitest genome from all the tribes (should be at the front of
    // the
    // list at all times... and display that tribes genome.
    int bestTribe = 0; // Start with saying best is the first one
    double bestFit = tribesGA.get(0).getFit(); // start with first tribe
    // fit.
    // It seems that the tribe that starts with the best initial fitness
    // most of the time keeps the best fitness during hill climbing.
    // Sometimes one tribe over takes the other for the best genome.

    if (viewToggle) // selects best fit genome from all tribes
    {
      double f;
      for (int i = 1; i < numThreads; i++)
      {
        f = tribesGA.get(i).getFit();
        if (f > bestFit)
        {
          bestTribe = i;
          bestFit = f;
        }
      }

      System.out.println("Tribe with fitest member, tribe: " + bestTribe);
      System.out.println(bestFit);
      updateInfo(tribesGA.get(bestTribe).getGenome(), bestFit, tribesGA.get(bestTribe).getDNA());
    }
    else
    {
      if (!genomeViewer) // selects the best fit genome from user selected
      // tribe
      {
        bestFit = tribesGA.get(tribeDisplayed).getFit();
        updateInfo(tribesGA.get(tribeDisplayed).getGenome(), bestFit, tribesGA.get(tribeDisplayed)
            .getDNA());

      }
      else // selects the user selected genome from the user selected
      // tribe
      {
        Genome g = tribes.get(tribeDisplayed).getGenomesInTribe().get(genomeDisplayed);
        bestFit = g.getFitness();
        if (showWholeGenome)
        {
          // Get one of the GA's render object (doesn't matter which
          // one)
          render.render(g.getDNA());
          updateInfo(SwingFXUtils.toFXImage(render.getBuff(), null), bestFit, g.getDNA());
        }
        else // choose which gene/triangle to display
        {
          specificGene.add(g.getDNA().get(geneDisplayed));
          render.render(specificGene);
          updateInfo(SwingFXUtils.toFXImage(render.getBuff(), null), bestFit, g.getDNA());
        }
      }
    }

    mainController.updateDisplay(displayedPop, displayedFitness, displayedDNA);
  }

  /**
   * This is a method to get the algorithm unstuck. First the best genome from
   * the tribe is gotten to store temporarly and then it is deleted out of the
   * tribe. Next there is a random injection to alter the genes of it, and then
   * it is re-inserted into the tribe (this forces the best genome to be pushed
   * back in the list since its fitness goes down. Then the next best fit genome
   * in the tribe (which should be very similar from cross over goes into the
   * best genomes place and we start hill climbing on it, and ideally this will
   * break us out of the hill we were stuck on.
   * 
   * @param index
   */
  public void getUnStuck(int index)
  {
    System.out.println("\n GET UNSTUCK @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    ArrayList<Triangle> temp = tribes.get(index).getGenomesInTribe().get(0).getDNA();
    tribes.get(index).getGenomesInTribe().remove(0);
    int IMAGE_HEIGHT = tribesGA.get(index).getImgHeight();
    int IMAGE_WIDTH = tribesGA.get(index).getImgWidth();
    for (int i = 0; i < 100; i++)
    {
      int rand1 = random.nextInt(200);
      int rand2 = random.nextInt(10);

      Triangle triangle = temp.get(rand1);
      switch (rand2)
      {
        case 1:
          triangle.setAlpha(random.nextInt(255));
          break;
        case 2:

          triangle.setRed(random.nextInt(255));
          break;
        case 3:

          triangle.setGreen(random.nextInt(255));
          break;
        case 4:

          triangle.setBlue(random.nextInt(255));
          break;
        case 5:

          triangle.setP1x(random.nextInt(IMAGE_WIDTH));
          break;
        case 6:

          triangle.setP1y(random.nextInt(IMAGE_HEIGHT));
          break;
        case 7:

          triangle.setP2x(random.nextInt(IMAGE_WIDTH));
          break;
        case 8:

          triangle.setP2y(random.nextInt(IMAGE_HEIGHT));
          break;
        case 9:

          triangle.setP3x(random.nextInt(IMAGE_WIDTH));
          break;
        case 10:

          triangle.setP3y(random.nextInt(IMAGE_HEIGHT));
          break;
        default:
          break; // Should never reach here.
      }
      triangle.updateTriangle();
    }

    FitnessFunction checkFit = tribesGA.get(index).getFitObj();
    render.render(temp);
    checkFit.calculateFitness(render.getBuff());
    Genome afterInjection = new Genome(temp);
    afterInjection.setFitness(checkFit.getFitness());
    System.out.println("new fitness: " + checkFit.getFitness());
    util.insertSorted(afterInjection, tribes.get(index).getGenomesInTribe());
    tribesGA.get(index).updateBestDNA();
    tribesGA.get(index).updateBestGenome();
  }

  /**
   * Called by mainController when the start button is pressed. This starts the
   * execution of the algorithm if it's not already currently running
   */
  public void startLoop()
  {
    isRunning = true;
  }

  /**
   * Called by mainController when the stop button is pressed. This stops the
   * execution of the algorithm if it's not already stopped.
   */
  public void stopLoop()
  {
    isRunning = false;
  }

  /**
   * Called by the GUI when deciding if to show the most fit genome from all
   * tribes or not
   * 
   * @param b
   */
  public void toggleView(boolean b)
  {
    viewToggle = b;
  }

  /**
   * Called by the GUI to inform main which tribe is currently displayed on the
   * screen.
   * 
   * @param i
   *          the position of the tribe in the tribe list
   */
  public void setTribeDisplayed(int i)
  {
    tribeDisplayed = i;
  }

  /**
   * Called by the GUI to inform main which genome from a specific tribe is
   * being displayed to the screen.
   * 
   * @param i
   *          the position of the genome in the list
   */
  public void setGenomeDisplayed(int i)
  {
    genomeDisplayed = i;
  }

  /**
   * Called by the GUI to inform main which gene from a specific genome is being
   * displayed to the screen
   * 
   * @param i
   *          the position of the triangle in the ArrayList<Triangle> object
   */
  public void setGeneDisplayed(int i)
  {
    geneDisplayed = i;
    specificGene.clear();
  }

  /**
   * Set by the GUI to inform main that we are currently looking through genomes
   * from a specific tribe
   * 
   * @param b
   */
  public void setGenomeViewer(boolean b)
  {
    genomeViewer = b;
  }

  /**
   * Called by the mainController to tell the GA what kind of mutation to apply
   * 
   * @param type
   *          true if Hard Mutation, false if Soft Mutation
   */
  public void setMutationType(boolean type)
  {
    ga.setMutateType(type);
  }

  /**
   * Updates the information in the ArrayList tribes of the genome being acted
   * on by the GA. As of now since we're only using the GA on the initial best
   * fit genome from each tribe so we only change the first genome in the tribe.
   * 
   * @param DNA
   * @param fit
   * @param img
   * @param g
   *          The GA object that called it. Since we can have multiple ones, we
   *          use this to compare with tribesGA to make sure we edit the correct
   *          index in the tribes ArrayList
   */
  public void updateTribesList(ArrayList<Triangle> DNA, double fit, Image img, GA g)
  {
    Genome gen = null;
    if (isRunning)
    {
      for (int i = 0; i < numThreads; i++)
      {
        if (tribesGA.get(i).equals(g))
        {
          gen = tribes.get(i).getGenomesInTribe().get(0);
          gen.setDNA(DNA);
          gen.setFitness(fit);
        }
      }
    }
  }

  /**
   * Only for testing purposes. These values are essentially the same values on
   * the table now.
   */
  public void printAllGenomeFitness()
  {
    int g = 0;
    int f = 0;
    Genome bestGenome = tribes.get(0).getGenomesInTribe().get(0);
    Genome potential;
    double bestFit = bestGenome.getFitness();
    System.out.println("First genome fitness " + bestFit);
    for (int i = 0; i < numThreads; i++)
    {
      bestGenome = tribes.get(i).getGenomesInTribe().get(0);
      bestFit = bestGenome.getFitness();
      System.out.println("tribe " + i + " initial genome fitness:  " + bestFit);

      for (int j = 0; j < 100; j++)
      {
        potential = tribes.get(i).getGenomesInTribe().get(j);
        if (potential.getFitness() > bestFit)
        {
          g = i;
          f = j;
          bestGenome = potential;
          bestFit = bestGenome.getFitness();
          System.out.println("next best fitness in tribe:" + i + " genome:" + j + "  with fit: "
              + bestFit);
        }
        System.out.println("genome: " + j + " - fitness: " + potential.getFitness());
      }
    }
  }

  /**
   * Called by the GUI after creating the initial population. We do not allow
   * the population to ever change, so this number remains the same per
   * execution
   */
  public void setTotalPopulation()
  {
    int pop = 0;
    for (int i = 0; i < tribes.size(); i++)
    {
      pop += tribes.get(0).getTribePopulation();
    }
    mainController.setTotalPopulation(pop, tribes.size());
  }

  /**
   * Called by the GUI when a new GA is being initialized and we want to reuse
   * old threads
   */
  public void updateThreads()
  {
    for (int i = 0; i < numThreads; i++)
    {
      threads.get(i).setNewTribe(tribes.get(i), tribesGA.get(i));
    }
  }

  /**
   * @return The number of threads being used in this execution
   */
  public int getNumThreads()
  {
    return numThreads;
  }

  /**
   * Called by the GUI when we change the number of threads.
   * 
   * @param i
   *          the number of threads to be used in the next execution
   */
  public void setNumThreads(int i)
  {
    isRunning = false;

    tribesDeltaT = new double[i];

    if (!threads.isEmpty())
    {
      for (int j = 0; j < numThreads; j++)
      {
        threads.get(0).terminateThread();
      }
      threads.clear();
    }

    startThreads = true;
    numThreads = i;
  }

  /**
   * For cross tribal cross over after triggering the cross over mode a global
   * pool of genoems gathered from every tribe is made for each thread and then
   * it is sent to the GA to perform cross over with.
   */
  private void crossOverMode()
  {
    crossOverMode = false;

    // To increase the probability of the tribes best genomes
    // being crossed over in the GA put more instances in the pool.
    // (This is a number which can be changed also for testing)
    for (int i = 0; i < globalPools.size(); i++)
    {
      for (int j = 0; j < numThreads; j++)
      {
        for (int k = 0; k < 5; k++)
        {
          if (j != i) // Don't copy own best genome into pool..
          {
            globalPools.get(i).add(tribes.get(j).getGenomesInTribe().get(0));
          }

        }
      }
    }

    // Pick a random tribes genome and continually fill the global pools
    // (This number can also be changed for testing)
    for (int i = 0; i < 100; i++)
    {
      for (ArrayList<Genome> globalPool : globalPools)
      {
        Tribe randTribe = tribes.get(random.nextInt(numThreads));
        globalPool.add(randTribe.getGenomesInTribe().get(randTribe.getGenomesInTribe().size() - 1));
      }
    }
    // Set the global pools in each of the threads GA's, and turn on their
    // cross over modes.
    for (int i = 0; i < numThreads; i++)
    {
      tribesGA.get(i).setGlobalPool(globalPools.get(i));
      tribesGA.get(i).isCrossOverMode = true;
    }

    // Reset the pools.
    for (int i = 0; i < numThreads; i++)
    {
      globalPools.set(i, new ArrayList<>());
    }
    crossOverModeStarted = false;

  }

  /**
   * Converts all the statistics displayed to the GUI into a single line String.
   * 
   * @param elapsedTime
   * @return single line string of all the statistics
   */
  private String statisticsToString(String elapsedTime)
  {
    return (elapsedTime + " " + displayedFitness + " " + totalGenerations + " " + hillclimbChildren
        + " " + crossoverChildren + " " + totalGenerationsPerSecond + " "
        + avgCurrentGenerationsPerSecond + " " + avgTotalGenerationsPerSecond + " "
        + deltaFitnessPerSecond);
  }

  /**
   * Calculates all the statistics to display on the gui and then sends the
   * values to mainController to display
   */
  private void updateStatistics()
  {
    totalGenerations = 0;
    hillclimbChildren = 0;
    crossoverChildren = 0;
    totalGenerationsPerSecond = 0;
    avgCurrentGenerationsPerSecond = 0;
    avgTotalGenerationsPerSecond = 0;
    deltaFitnessPerSecond = 0;
    int genomeWithBestFit = 0;
    double bestFit = tribesGA.get(0).getFit();

    for (int i = 0; i < tribesGA.size(); i++)
    {
      currentGenome = tribesGA.get(i);
      numGenerations = currentGenome.getNumGenerations();

      // The total numbers of generations calculated by all the tribes
      // get generations value from each GA
      // send this value to mainController
      totalGenerations += numGenerations;

      // the total hill climb children
      hillclimbChildren += currentGenome.getNumHillclimb();

      // the total cross over children
      crossoverChildren += currentGenome.getNumCrossover();

      // current generations per second averaged over the past second
      //
      // my interpretation: the difference between total generations from
      // the
      // previous second to the current second
      //
      // take last second's generation count and this second's generation
      // count
      // subtract 1st from 2nd
      // average these values from all tribes

      avgCurrentGenerationsPerSecond += numGenerations - currentGenome.getPreviousNumGenerations();
      currentGenome.updatePreviousGeneration(numGenerations);

      // current generations per second averaged over all time since most
      // recent
      // pop init
      //
      // my interpretation: the total generations per second
      //
      // take the current number of total generations divided by time
      // elapsed in
      // seconds. average these values from all tribes
      if (mainController.getElapsedNanoTime() < (1 * 1E9))
      {
        avgTotalGenerationsPerSecond = 0;
        totalGenerationsPerSecond = 0;
      }
      else
      {
        avgTotalGenerationsPerSecond += (numGenerations / (mainController.getElapsedNanoTime()
            / 1E9));
      }

      // determine which GA has the highest current fitness, so we know
      // which
      // delta Fitness/sec to display
      if (i > 0)
      {
        if (bestFit < currentGenome.getFit())
        {
          genomeWithBestFit = i;
          bestFit = currentGenome.getFit();
        }
      }
      tribesDeltaT[i] = ((currentGenome.getFit() - tribesGA.get(i).getPreviousBestFit()) * 2);
    }

    // Since this method gets called every .5 seconds, we need to multiply
    // currentGenerationsPerSecond by 2 to get the proper value. Is actually
    // currentGenerationsPerHalfSecond until the multiplication by 2.
    avgCurrentGenerationsPerSecond = (avgCurrentGenerationsPerSecond / tribesGA.size()) * 2;

    totalGenerationsPerSecond = avgTotalGenerationsPerSecond;
    avgTotalGenerationsPerSecond = avgTotalGenerationsPerSecond / tribesGA.size();

    // the change in fitness per second of the most fit genome in the
    // population.
    //
    // my interpretation: the difference between the bestFitness right now
    // and
    // that same GA's bestFitness from a second ago
    //
    // take the current best fitness and subtract it from the previous best
    // fitness. since this method is called every half a second this
    // initially
    // gives us the change in fitness per half second, thus we multiply it
    // by
    // two for change in fitness per second.

    deltaFitnessPerSecond = (bestFit - tribesGA.get(genomeWithBestFit).getPreviousBestFit()) * 2;

    for (int i = 0; i < numThreads; i++)
    {
      tribesGA.get(i).updatePreviousFitness(tribesGA.get(i).getFit());
    }

    // tribesGA.get(genomeWithBestFit).updatePreviousFitness(bestFit);

    System.out.println("One: " + tribesDeltaT[genomeWithBestFit] + " two: "
        + deltaFitnessPerSecond);
    for (int i = 0; i < numThreads; i++)
    {
      System.out.println("Thread: " + i + " " + tribesDeltaT[i]);
      if (tribesDeltaT[i] == 0.0 && !tribesGA.get(i).isCrossOverMode)
      {
        tribesGA.get(i).stuckCount++;
        System.out.println("Thread: " + i + " stuckcount: " + tribesGA.get(i).stuckCount);
      }
      else
      {
        if (!tribesGA.get(i).isCrossOverMode)
        {
          tribesGA.get(i).stuckCount = 0;
        }
      }
      System.out.println("Thread: " + i + " stuckcount: " + tribesGA.get(i).stuckCount);
      if (tribesGA.get(i).stuckCount > 200)
      {
        isRunning = false;
        getUnStuck(i);
        tribesGA.get(i).stuckCount = 0;
        isRunning = true;
      }
    }
    mainController.updateStatistics(totalGenerations, hillclimbChildren, crossoverChildren,
        totalGenerationsPerSecond, avgCurrentGenerationsPerSecond, avgTotalGenerationsPerSecond,
        deltaFitnessPerSecond);
  }

  public static void main(String[] args)
  {
    launch();
  }

  /**
   * Loop for the application. We make a mutation if isRunning is true. If it's
   * been over half a second, we draw the most fit image we have to screen and
   * update the value
   */

  class ApplicationLoop extends AnimationTimer
  {
    private long lastTime;
    private long thisTime;

    public ApplicationLoop()
    {
      lastTime = 0;
    }

    public void handle(long now)
    {
      if (isRunning)
      {
        thisTime = System.nanoTime();

        if ((thisTime - lastTime) / 1E9 > .5)
        {
          lastTime = thisTime;
          // Updates display with the most fit overall genome from all
          // of the tribes.
          updateDisplay();
          updateStatistics();
        }
        // This is what triggers the cross over mode, its after 1500
        // loops
        // in the first threads run method, we only need to keep count
        // in the
        // first thread, this will make sure regardless of the number of
        // threads the cross over is triggered at the same time.
        // Again for testing you can alter the number its triggered at
        // We could very possibly make the number larger.
        if (countTillCrossOver > 1500)
        {

          System.out.println("Trigger crossover mode");
          crossOverMode = true;
        }

        if (crossOverMode)
        {
          // pause threads;
          System.out.println(crossOverModeStarted);
          if (!crossOverModeStarted)
          {
            isRunning = false; // Pause threads.
            crossOverMode(); // Create global pools for cross tribal
            // cross over.
            countTillCrossOver = 0;
            isRunning = true;
          }

        }
        mainController.setElapsedTime(thisTime);
        // Start the threads (this is done once, and there is currently
        // only one thread).
        if (startThreads)
        {
          startThreads = false;

          for (int i = 0; i < numThreads; i++)
          {
            WorkerThread thread = new WorkerThread(tribes.get(i), tribesGA.get(i), i);
            threads.add(thread);
            thread.start();
            globalPools.add(new ArrayList<>());// Each thread will
            // have a pool
            // of genomes which come from all the other tribes, this
            // will be
            // updated prior to cross over each time.
          }

        }
      }
    }
  }

  /**
   * 
   * Each worker thread handles a single tribe. Currently there is only one
   * tribe but once we create more threads and subsequent tribes we probably
   * will either have to have more than one instantiation of GA or put some of
   * the methodology inside of the worker threads run method.
   *
   */
  public class WorkerThread extends Thread
  {

    private Tribe tribe;
    private GA ga;
    int tribeNum;
    private volatile boolean isAlive;

    WorkerThread(Tribe tribe, GA ga, int tribeNum)
    {
      this.tribe = tribe;
      this.ga = ga;
      this.tribeNum = tribeNum;
      isAlive = true;
    }

    public void terminateThread()
    {
      isAlive = false;
    }

    public void setNewTribe(Tribe t, GA g)
    {
      tribe = t;
      ga = g;
    }

    public void run()
    {
      while (isAlive)
      {
        if (isRunning)
        {
          if (tribeNum == 0) // Only increment in first thread.
          {
            ++countTillCrossOver;
          }
          ga.Mutate();
        }
        else
        {
          try
          {
            Thread.sleep(500);
          }
          catch (InterruptedException e)
          {
            e.printStackTrace();
          }
        }
      }
    }
  }
}

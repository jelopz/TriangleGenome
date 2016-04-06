package Application;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import TriangleGenome.GA;
import TriangleGenome.Genome;
import TriangleGenome.InitialPopulation;
import TriangleGenome.Triangle;
import TriangleGenome.Tribe;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
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

  // Filechooser
  FileChooser fileChooser;
  Image originalImage;
  int NUM_OF_THREADS = 2;;
  GA ga;
  ArrayList<Tribe> tribes;
  ArrayList<GA> tribesGA;

  boolean isRunning; // used to let the ApplicationLoop know when to run
  private mainController mainController;
  private Image displayedPop;
  private double displayedFitness;
  private boolean viewToggle; // Show the best fit genome or the user selected
                              // one? true = best fit, false = user selection

  private int tribeDisplayed; // If (!viewtoggle), we display the
  // tribesGA.get(tribeDisplayed) image

  private int genomeDisplayed;
  private boolean genomeViewer;

  private GA currentGenome;
  private int numGenerations;

  private int totalGenerations;
  private int hillclimbChildren;
  private int crossoverChildren;
  private int currentGenerationsPerSecond;
  private int totalGenerationsPerSecond;
  private double deltaFitnessPerSecond;

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

    isRunning = false;

    viewToggle = true;
    genomeViewer = false;

    createMainWindow();
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

    mainController = loader.getController();
    mainController.initController(this);
    mainController.updateDisplay(displayedPop, displayedFitness);

    Stage primaryStage = new Stage();
    primaryStage.setTitle("Genetic Algorithm");
    primaryStage.setScene(new Scene(root));
    primaryStage.show();

    AnimationTimer loop = new ApplicationLoop();
    loop.start();
  }

  /**
   * Called immediately after the creation of the GA in the InitialPopulation
   * class.
   * 
   * The main class is given a reference to the GA class, and the initial
   * population and fitness, so the ApplicationLoop can tell the GA when to
   * mutate.
   * 
   * @param g
   *          The pathfinding object
   * @param img
   *          The initial population
   * @param imgFitness
   *          the initial fitness
   */
  // public void setGA(GA g, Image img, double imgFitness)
  // {
  // ga = g;
  // displayedPop = img;
  // displayedFitness = imgFitness;
  // }

  /**
   * Called by the GA when an improved fitness is found.
   * 
   * @param img
   * @param d
   */
  public void updateInfo(Image img, double d)
  {
    displayedPop = img;
    displayedFitness = d;
  }

  /**
   * Called by the Application loop every half second to update the screen with
   * the image with the highest fitness
   */
  public void updateDisplay()
  {
    // Find the fitest genome from all the tribes (should be at the front of the
    // list at all times... and display that tribes genome.
    int bestTribe = 0; // Start with saying best is the first one
    double bestFit = tribesGA.get(0).getFit(); // start with first tribe fit.
    // It seems that the tribe that starts with the best initial fitness
    // most of the time keeps the best fitness during hill climbing.
    // Sometimes one tribe over takes the other for the best genome.

    if (viewToggle)
    {
      double f;
      for (int i = 1; i < NUM_OF_THREADS; i++)
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
      updateInfo(tribesGA.get(bestTribe).getGenome(), bestFit);
    }
    else
    {
      if (!genomeViewer)
      {
        bestFit = tribesGA.get(tribeDisplayed).getFit();
        updateInfo(tribesGA.get(tribeDisplayed).getGenome(), bestFit);
      }
      else
      {
        Genome g = tribes.get(tribeDisplayed).getGenomesInTribe().get(genomeDisplayed);
        bestFit = g.getFitness();
        updateInfo(g.getImg().getImage(), bestFit);
      }
    }

    mainController.updateDisplay(displayedPop, displayedFitness);
  }

  private void updateStatistics()
  {
    totalGenerations = 0;
    hillclimbChildren = 0;
    crossoverChildren = 0;
    currentGenerationsPerSecond = 0;
    totalGenerationsPerSecond = 0;
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
      // my interpretation: the difference between total generations from the
      // previous second to the current second
      //
      // take last second's generation count and this second's generation count
      // subtract 1st from 2nd
      // average these values from all tribes

      currentGenerationsPerSecond += numGenerations - currentGenome.getPreviousNumGenerations();
      currentGenome.updatePreviousGeneration(numGenerations);

      // current generations per second averaged over all time since most recent
      // pop init
      //
      // my interpretation: the total generations per second
      //
      // take the current number of total generations divided by time elapsed in
      // seconds. average these values from all tribes
      if (mainController.getElapsedNanoTime() < (1 / 1E9))
      {
        totalGenerationsPerSecond = 0;
      }
      else
      {
        totalGenerationsPerSecond += (numGenerations / (mainController.getElapsedNanoTime() / 1E9));
      }

      // determine which GA has the highest current fitness, so we know which
      // delta Fitness/sec to display
      if (i > 0)
      {
        if (bestFit < currentGenome.getFit())
        {
          genomeWithBestFit = i;
          bestFit = currentGenome.getFit();
        }
      }
    }

    // Since this method gets called every .5 seconds, we need to multiply
    // currentGenerationsPerSecond by 2 to get the proper value. Is actually
    // currentGenerationsPerHalfSecond until the multiplication by 2.
    currentGenerationsPerSecond = (currentGenerationsPerSecond / tribesGA.size()) * 2;

    totalGenerationsPerSecond = totalGenerationsPerSecond / tribesGA.size();

    // the change in fitness per second of the most fit genome in the
    // population.
    //
    // my interpretation: the difference between the bestFitness right now and
    // that same GA's bestFitness from a second ago
    //
    // take the current best fitness and subtract it from the previous best
    // fitness. since this method is called every half a second this initially
    // gives us the change in fitness per half second, thus we multiply it by
    // two for change in fitness per second.

    deltaFitnessPerSecond = (bestFit - tribesGA.get(genomeWithBestFit).getPreviousBestFit()) * 2;
    tribesGA.get(genomeWithBestFit).updatePreviousFitness(bestFit);

    mainController.updateStatistics(totalGenerations, hillclimbChildren, crossoverChildren,
        currentGenerationsPerSecond, totalGenerationsPerSecond, deltaFitnessPerSecond);
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

  public void toggleView(boolean b)
  {
    viewToggle = b;
  }

  public void setTribeDisplayed(int i)
  {
    tribeDisplayed = i;
  }

  public void setGenomeDisplayed(int i)
  {
    System.out.println(i);
    genomeDisplayed = i;
  }

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
    for (int i = 0; i < 2; i++)
    {
      if (tribesGA.get(i).equals(g))
      {
        gen = tribes.get(i).getGenomesInTribe().get(0);
        gen.setDNA(DNA);
        gen.setImg((WritableImage) img);
        gen.setFitness(fit);
      }
    }
  }

  public void printAllGenomeFitness()
  {
    int g = 0;
    int f = 0;
    Genome bestGenome = tribes.get(0).getGenomesInTribe().get(0);
    Genome potential;
    double bestFit = bestGenome.getFitness();
    System.out.println("First genome fitness " + bestFit);
    for (int i = 0; i < 2; i++)
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

  public void setTotalPopulation()
  {
    int pop = 0;
    for (int i = 0; i < tribes.size(); i++)
    {
      pop += tribes.get(0).getTribePopulation();
    }
    mainController.setTotalPopulation(pop);
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
  boolean startThreads = true;

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

        mainController.setElapsedTime(thisTime);
        // Start the threads (this is done once, and there is currently
        // only one thread).
        if (startThreads)
        {

          startThreads = false;
          for (int i = 0; i < NUM_OF_THREADS; i++)
          {
            WorkerThread thread = new WorkerThread(tribes.get(i), tribesGA.get(i), i);
            thread.start();
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

    Tribe tribe;
    GA ga;
    int tribeNum;

    WorkerThread(Tribe tribe, GA ga, int tribeNum)
    {
      this.tribe = tribe;
      this.ga = ga;
      this.tribeNum = tribeNum;
    }

    public void run()
    {
      while (true)
      {
        if (isRunning)
        {
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

package Application;

import java.awt.image.BufferedImage;
import java.io.IOException;

import TriangleGenome.GA;
import TriangleGenome.InitialPopulation;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * The CPU of the application. The controllers handle the GUI and the GA handles
 * the pathfinding algorithm. Anytime the controllers or the GA needs something
 * done outside of it's own respective realm, they need to go through this main
 * class to do it.
 * 
 * Current flow of the whole application:
 * 
 * Begin in the start class in NewMain. This creates the initial popup window
 * which asks you to upload an image and then start. These buttons are handled
 * in the startupController class. This acts very similarly to the original GUI.
 * Upon pressing the start button: The popup window is closed then the main
 * window is created.
 * 
 * At this point the startupController is irrelevent.
 * 
 * The initialPopulation is created, the GA is initialized and passed to this
 * Main class. (This happens in initialPopulations constructor by calling
 * main.setGA method after the creation of the GA)
 * 
 * Now we're shown a new window with the initial population's image and fitness
 * displayed, and buttons. All these things are handled in the mainController
 * class. Upon creation of the main window we also initialize the
 * ApplicationLoop, which is a part of main responsible for iterating the GA.
 * When a button is pressed on the GUI, the mainController handles what to do.
 * 
 * As of now, the animationloop tells the mainController to update the image and
 * fitness value every half a second.
 */
public class NewMain extends Application
{

  // Filechooser
  FileChooser fileChooser;
  Image originalImage;

  private mainController mainController;
  private startupController startupController;
  private Image displayedPop;
  private double displayedFitness;
  boolean isRunning; // used to let the ApplicationLoop know when to run
  GA ga;

  /**
   * The creation of the initial window, a popup that asks you to load an image.
   * Once loaded, the user will press start and then the main window is created.
   */
  public void start(Stage primaryStage) throws Exception
  {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("startupFXML.fxml"));
    Parent root = loader.load();

    fileChooser = new FileChooser();
    fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Image files", new String[]
    { ".png", ".bmp", ".jpg", ".gif" }));
    fileChooser.setTitle("Image Selector");

    startupController = loader.getController();
    startupController.initController(this);

    isRunning = false;

    primaryStage.setTitle("Choose File");
    primaryStage.setScene(new Scene(root));
    primaryStage.show();
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

    InitialPopulation viewInitialPopulation = new InitialPopulation(originalImage, this);
    viewInitialPopulation.show();

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
  public void setGA(GA g, Image img, double imgFitness)
  {
    ga = g;
    displayedPop = img;
    displayedFitness = imgFitness;
  }

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
    mainController.updateDisplay(displayedPop, displayedFitness);
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
   * Called by the mainController to tell the GA what kind of mutation to apply
   * 
   * @param type
   *          true if Hard Mutation, false if Soft Mutation
   */
  public void setMutationType(boolean type)
  {
    ga.setMutateType(type);
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
          updateDisplay();
        }
        //Start the threads (this is done once, and there is currently
        //only one thread). 
        if(startThreads)
        {
        	startThreads = false;
            WorkerThread  thread = new WorkerThread();
        	thread.start();
        	
        }
      }
    }
  }
  /**
   * 
   * Each worker thread handles a single tribe. 
   * Currently there is only one tribe but once we create
   * more threads and subsequent tribes we probably
   * will either have to have more than one instantiation
   * of GA or put some of the methodology inside of the
   * worker threads run method. 
   *
   */
  public class WorkerThread extends Thread{



	  public void run()	  
	  {
 
		  while(true)
		  {
			  ga.Mutate();
		  }
	  }
  }
}

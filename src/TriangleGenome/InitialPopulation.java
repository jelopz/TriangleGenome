package TriangleGenome;

import java.awt.Graphics;

import java.awt.Point;
import java.awt.TextField;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import Application.NewMain;
import TriangleGenome.FitnessFunction;
import TriangleGenome.GA;
import TriangleGenome.Genome;
import TriangleGenome.Triangle;
import TriangleGenome.Tribe;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.Group;

import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
//import javafx.scene.paint.Color;
import java.awt.Color;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;

/**
 * This is a class for creating the initial population. Please note this is just
 * a possibility for the initial population. Methodology for this potential
 * initial population: The picture is divided into a 10x10 grid consisting of
 * 100 rectangular regions. Next each rectangular region is split along the main
 * diagonal to create 200 triangles. Each triangle color in the new image is the
 * average RGB color of all the pixels contained in the a triangle at the same
 * position on the original image.
 * 
 * The initialization of this class also creates the beginning of a GA class.
 * This GA class is immediately passed over to the main class.
 * 
 * Note the initial population is currently a random genome from the tribe. We
 * are going to have to calculate the fitness of each member of the tribe and
 * select the fittest member most likely but I have not included this yet since
 * it would drastically slow down the program which is not helpful for testing.
 */
public class InitialPopulation extends Stage
{

  Image image;
  private Group root;
  private BorderPane bp;
  private ImageView originalImage;
  private ImageView perspectiveImage;
  private BufferedImage writableImage;
  private Random random = new Random();
  private double initialFitness;
  int IMAGE_WIDTH;
  int IMAGE_HEIGHT;
  // For this initial population all triangles have the
  // same height/width
  int triangleWidth;
  int triangleHeight;
  // There should be one tribe per thread, but to start out we can just
  // work with one tribe (single threaded)
  private int NUM_TRIBES = 1;

  // The tribes population can range form 2000 to 10000, we will have to test
  // to see what the best initial population would be.
  private int INITIAL_TRIBE_POPULATIONS = 2000;

  // Due to the fact that it is very unlikely to have a picture consisting
  // of a pixel height/width both divisible by 10 its most likely that
  // there will be extra pixels, to fix this some of the triangles will
  // just be a pixel larger in width/height.
  double leftOverPixelsWidth;
  double leftOverPixelsHeight;
  private PixelReader reader;
  // Just something to hold to triangles so they can be kept track of.
  // All 200 triangles make up a solution or genome/DNA.
  private ArrayList<Triangle> DNA = new ArrayList<>();
  // Array List to hold all the tribes.
  private ArrayList<Tribe> tribes = new ArrayList<>();

  private NewMain main;

  /**
   * Initialize fields and trigger the calculation and display of the initial
   * population.
   * 
   * @param main
   * @param Start
   *          Image.
   */
  public InitialPopulation(Image image, NewMain main)
  {
    this.main = main;
    this.image = image;
    // The images should have the same height/width.
    this.IMAGE_HEIGHT = (int) image.getHeight();
    this.IMAGE_WIDTH = (int) image.getWidth();

    for (int i = 0; i < NUM_TRIBES; i++)
    {
      tribes.add(createTribe());
    }

    // Make sure the type includes alpha since we have to take account for it,
    // ARGB is alpha, red, green, blue. (Currently set to RGB for testing)
    this.writableImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
    // this.pixelWriter = writableImage.getPixelWriter();
    this.reader = image.getPixelReader();
    // Using grid method we know there will be 10 rows.
    triangleWidth = IMAGE_WIDTH / 10;
    // Calculate left over pixels in the width.
    leftOverPixelsWidth = IMAGE_WIDTH - triangleWidth * 10;
    // Using grid method we know there will be 10 columns.
    triangleHeight = IMAGE_HEIGHT / 10;
    // Calculate left over pixels in the height.
    leftOverPixelsHeight = IMAGE_HEIGHT - triangleHeight * 10;

    originalImage = new ImageView(image);


    // Convert the initial image into a buffered image.
    BufferedImage temp = SwingFXUtils.fromFXImage(image, null);
    // Initialize the fitness function object with the bufferedImage
    // version of the original image.
    FitnessFunction startFitness = new FitnessFunction(temp);

    // Create 5 perspective images with different colored background
    // The 5 images are the exact same with exception of the background color
    // White and black are probably the best two choices to choose from because
    // other colors often have elements of white or black (which can be 
    // usefully when things are transparent). 
    Color[] colors = new Color[]
    { Color.BLACK, Color.WHITE};
    ImageView[] candidates = new ImageView[2];
    BufferedImage[] candidateTemps = new BufferedImage[2];
    double[] fitnessValues = new double[2];
    int currentBest = 0;

    for (int i = 0; i < 2; i++)
    {
      // Create the initial population with the correct colored background
      candidates[i] = createNewPerspectiveImage(colors[i]);

      // Convert the perspective image to a bufferd image
      candidateTemps[i] = SwingFXUtils.fromFXImage(candidates[i].getImage(), null);

      // Calculates the fitness of the perspective image
      startFitness.calculateFitness(candidateTemps[i]);
      fitnessValues[i] = startFitness.getFitness();

      // Keep track of which one has the highest fitness throughout the loop.
      if (fitnessValues[i] > fitnessValues[currentBest])
      {
        currentBest = i;
      }

      System.out.println(i + "  :  " + fitnessValues[i]);
    }

    System.out.println("currentBest" + currentBest);

    // Sets the image with the most fit background and its respective initial
    // fitness
    perspectiveImage = candidates[currentBest];
    initialFitness = fitnessValues[currentBest];

    // Initialize the GA with the initial population and give main a reference
    // to it and the initial population and fitness
    GA g = new GA(test, initialFitness, image, IMAGE_WIDTH, IMAGE_HEIGHT, main);
    main.setGA(g, perspectiveImage.getImage(), initialFitness);

    Pane fitnessFunctionDisplay = new Pane();
    Text fitnessDisplay = new Text("Fitness: " + initialFitness);
    fitnessDisplay.setFont(Font.font("Verdana", FontWeight.BOLD, 70));
    fitnessFunctionDisplay.getChildren().add(fitnessDisplay);

    bp = new BorderPane();
    // Right side of border pane is the original image.
    bp.setRight(originalImage);
    // Left side of border pane is the initial population image.
    bp.setLeft(perspectiveImage);
    // Display fitness of initial population.
    bp.setBottom(fitnessDisplay);
    root = new Group();
    root.getChildren().add(bp);
    Scene scene = new Scene(root);
    this.setScene(scene);
  }

  private Tribe createTribe()
  {
    ArrayList<Genome> genomes = new ArrayList<>();
    for (int i = 0; i < INITIAL_TRIBE_POPULATIONS; i++)
    {
      genomes.add(createGenome());
    }
    return new Tribe(genomes);
  }

  private Genome createGenome()
  {
    // 200 triangles per genome.

    ArrayList<Triangle> DNA = new ArrayList<>();
    for (int i = 0; i < 200; i++)
    {
      // Make the genome.
      Triangle triangle = new Triangle();
      triangle.setAlpha(random.nextInt(255));
      triangle.setRed(random.nextInt(255));
      triangle.setBlue(random.nextInt(255));
      triangle.setGreen(random.nextInt(255));
      triangle.setP1(new Point(random.nextInt(IMAGE_WIDTH), random.nextInt(IMAGE_HEIGHT)));
      triangle.setP2(new Point(random.nextInt(IMAGE_WIDTH), random.nextInt(IMAGE_HEIGHT)));
      triangle.setP3(new Point(random.nextInt(IMAGE_WIDTH), random.nextInt(IMAGE_HEIGHT)));
      triangle.updateTriangle();
      DNA.add(triangle);

    }

    return new Genome(DNA);
  }

 

  
  ArrayList<Triangle> test;

  private ImageView createNewPerspectiveImage(Color c)
  {
    // Get first genome from the tribe to be the start photo.
    //
    // This genome is no longer randomly selected as we need to start on the
    // same one for each background color. If this genome was random, then there
    // would be more things different than just the background color.
    test = tribes.get(0).getGenomesInTribe().get(0).getDNA();
    // System.out.println(test.size());
    Graphics genome = writableImage.createGraphics();
    // This might really effect anything or it might
    // have a big effect, what ever we set the background color
    // to e.g black or white it will play as the base of the image.
    genome.setColor(c);
    genome.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
    // Draw each triangle.
    for (Triangle triangle : test)
    {
      genome.setColor(triangle.getColor());
      genome.fillPolygon(triangle.getTriangle());
    }
    // Convert buffered image to FXImage, set in a image view and return it.
    ImageView imgView = new ImageView();
    imgView.setImage(SwingFXUtils.toFXImage(writableImage, null));

    return imgView;
  }

  public double getInitFitness()
  {
    return initialFitness;
  }

}
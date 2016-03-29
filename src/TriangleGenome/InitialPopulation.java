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
    // ARGB is alpha, red, green, blue.
    this.writableImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
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

    // Calculate the initial population
    CalculateInitialPopulation(image);

    // Convert the initial image into a buffered image.
    BufferedImage temp = SwingFXUtils.fromFXImage(image, null);
    // Initialize the fitness function object with the bufferedImage
    // version of the original image.
    FitnessFunction startFitness = new FitnessFunction(temp);

    // Create 5 perspective images with different colored background
    // The 5 images are the exact same with exception of the background color
    Color[] colors = new Color[]
    { Color.BLACK, Color.WHITE, Color.RED, Color.GREEN, Color.BLUE };
    ImageView[] candidates = new ImageView[5];
    BufferedImage[] candidateTemps = new BufferedImage[5];
    double[] fitnessValues = new double[5];
    int currentBest = 0;

    for (int i = 0; i < 5; i++)
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

  /**
   * Method to calculate the initial population.
   * 
   * @param Start
   *          Image
   */
  private void CalculateInitialPopulation(Image originalImage)
  {
    Triangle triangle1;
    Triangle triangle2;
    // It seems all the pictures only have extra height pixels
    // if they end up having any extra pixels.
    int offset = 0;
    // Iterate through grid creating two triangle objects at a time
    // (The two triangles you would see when splitting a rectangle down
    // the diagonal).
    for (int i = 0; i < 10; i++)
    {
      for (int j = 0; j < 10; j++)
      {
        // Add extra pixels to the last row, if we want we could
        // more evenly distribute them over the rows but I'm not sure
        // that will make much of a difference.
        if (i == 9 && leftOverPixelsHeight > 0)
        {
          offset = (int) leftOverPixelsHeight;
        }

        // Calculate the vertices of each new triangle and set them to their
        // respective triangle objects, the triangles are created in pairs of
        // two
        // because we are iterating over the squares of the board and each
        // square
        // consists of two triangles.
        triangle1 = new Triangle();
        triangle1.setP1(new Point(j * triangleWidth, i * triangleHeight));
        triangle1.setP2(new Point(j * triangleWidth, (i + 1) * triangleHeight + offset));
        triangle1.setP3(new Point((j + 1) * triangleWidth, (i + 1) * triangleHeight + offset));
        triangle1.setAlpha(150);// Set to semi-transparent , this is something
                                // we can play around with.
        triangle2 = new Triangle();
        triangle2.setP1(new Point(j * triangleWidth, i * triangleHeight));
        triangle2.setP2(new Point((j + 1) * triangleWidth, i * triangleHeight));
        triangle2.setP3(new Point((j + 1) * triangleWidth, (i + 1) * triangleHeight + offset));
        triangle2.setAlpha(150);// Set to semi-transparent , this is something
                                // we can play around with.
        // Update the triangles with the new data that was set.
        triangle1.updateTriangle();
        triangle2.updateTriangle();
        // Get the start x/y location for the triangles for calculating
        // their average RGB.
        int startX = j * triangleWidth;
        int startY = i * triangleHeight;
        // Calculate the average RGB of pixels contained in each of the
        // triangles
        // and set that average RGB color as the color of the triangle.
        calculateAverageRGB(triangle1, triangleWidth, triangleHeight + offset, startX, startY);
        calculateAverageRGB(triangle2, triangleWidth, triangleHeight + offset, startX, startY);

        // Add the two triangle objects to the list of triangles.
        DNA.add(triangle1);
        DNA.add(triangle2);

      }
    }
  }

  /**
   * @param Triangle
   *          object, with height and width of the triangle (which is also the
   *          height and width of the bounding box), and the start x/y.
   * 
   *          Method for iteration of triangles is using bounding rectangular
   *          boxes and checking if each pixel in the bounding box, if its
   *          inside the triangle then the RGB values of the pixel are used for
   *          calculation. Another method that could be done which we might want
   *          to look into later is triangle rasterization which allows for
   *          accessing all pixels in a bounding area defined by 3 vertices
   *          (this method is usually used for drawling triangles pixel by pixel
   *          or line by line).
   */
  private void calculateAverageRGB(Triangle triangle, int width, int height, int startX, int startY)
  {

    int boundingBoxWidth = width;
    int boundingBoxHeight = height;
    int pixelCount = 0;
    double blueCount = 0;
    double redCount = 0;
    double greenCount = 0;
    int newRedValue = 0;
    int newBlueValue = 0;
    int newGreenValue = 0;
    // Iterate through the bounding box, if the pixel is in the triangle
    // then increment the pixel count and the counts for red, blue and green.
    for (int i = startX; i < boundingBoxWidth + startX; i++)
    {
      for (int j = startY; j < boundingBoxHeight + startY; j++)
      {
        if (triangle.getTriangle().contains(i, j))
        {
          ++pixelCount;
          // Note the getRed/blue/green methods return a value
          // from 0.0 to 1.0 not 0 to 255.
          redCount += reader.getColor(i, j).getRed();
          greenCount += reader.getColor(i, j).getGreen();
          blueCount += reader.getColor(i, j).getBlue();
        }
      }
    }

    // Average the average RGB value for the triangular region and set
    // that as the RGB value of the new triangle.
    newRedValue = (int) ((redCount / pixelCount) * (255));
    newGreenValue = (int) ((greenCount / pixelCount) * (255));
    newBlueValue = (int) ((blueCount / pixelCount) * (255));
    triangle.setRed(newRedValue);
    triangle.setGreen(newGreenValue);
    triangle.setBlue(newBlueValue);
    // Update the triangle with its new color values.
    triangle.updateTriangle();
  }

  /**
   * This method draws all of the triangles (java.awt polygons) to the buffered
   * image and then the buffered image is converted to a FXImage which is set
   * inside of a image view and returned.
   * 
   * @return ImageView of the initialPopulation.
   */
  // private ImageView createNewPerspectiveImage()
  // {
  // Graphics genome = writableImage.createGraphics();
  // //This might really effect anything or it might
  // //have a big effect, what ever we set the background color
  // //to e.g black or white it will play as the base of the image.
  // genome.setColor(java.awt.Color.BLACK);
  // genome.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
  // //Draw each triangle.
  // for(Triangle triangle: DNA)
  // {
  // genome.setColor(triangle.getColor());
  // genome.drawPolygon(triangle.getTriangle());
  // genome.fillPolygon(triangle.getTriangle());
  // }
  // //Convert buffered image to FXImage, set in a image view and return it.
  // ImageView imgView = new ImageView();
  // imgView.setImage(SwingFXUtils.toFXImage(writableImage, null));
  //
  // return imgView;
  // }
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
      // System.out.println(triangle.getRed() + " " + triangle.getGreen() + " "
      // + triangle.getBlue());
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
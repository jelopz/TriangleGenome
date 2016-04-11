package TriangleGenome;

import java.awt.Graphics;

import java.awt.Point;
import java.awt.Polygon;
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
  private Renderer imageRenderer;
  private Random random = new Random();
  private double initialFitness;
  private UtilityClass util;
  private Color backGroundColor;
  FitnessFunction startFitness;
  int IMAGE_WIDTH;
  int IMAGE_HEIGHT;
  // For this initial population all triangles have the
  // same height/width
  int triangleWidth;
  int triangleHeight;
  // There should be one tribe per thread, but to start out we can just
  // work with one tribe (single threaded)
  private int NUM_TRIBES;

  private int[] reds;
  private int[] greens;
  private int[] blues;
  
  private boolean completedBackGroundTest;
  
  // The tribes population can range form 2000 to 10000, we will have to test
  // to see what the best initial population would be.
  // (Set to 100 for better performance when testing).
  private int INITIAL_TRIBE_POPULATIONS = 100;

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
  // Each tribe has the genetic algorithm operating soley for
  // itself so each one needs an object.
  private ArrayList<GA> tribesGA = new ArrayList<>();
  private NewMain main;

  private int initBestFitTribe;

  /**
   * Initialize fields and trigger the calculation and display of the initial
   * population.
   * 
   * @param main
   * @param Start
   *          Image.
   */
  public InitialPopulation(Image image, NewMain main, int numThreads)
  {
    this.NUM_TRIBES = numThreads;
    this.main = main;
    this.image = image;
    
    this.reds = new int[200];
    this.blues = new int[200];
    this.greens = new int[200];
    this.completedBackGroundTest = false;
    this.util = new UtilityClass();
    // The images should have the same height/width.
    this.IMAGE_HEIGHT = (int) image.getHeight();
    this.IMAGE_WIDTH = (int) image.getWidth();
    // Start imageRenderer with black as default.
    this.imageRenderer = new Renderer(IMAGE_WIDTH, IMAGE_HEIGHT, Color.BLACK);

//    // Make sure the type includes alpha since we have to take account for it,
//    // ARGB is alpha, red, green, blue. (Currently set to RGB for testing)
//    this.writableImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
    // this.pixelWriter = writableImage.getPixelWriter();
    this.reader = image.getPixelReader();
    // Using grid method we know there will be 10 rows.
    this.triangleWidth = IMAGE_WIDTH / 10;
    // Using grid method we know there will be 10 columns.
    this.triangleHeight = IMAGE_HEIGHT / 10;
    // Calculate left over pixels in the height.

    CalculateInitialPopulation(image);

    originalImage = new ImageView(image);

    perspectiveImage = new ImageView();

    // Convert the initial image into a buffered image.
    BufferedImage temp = SwingFXUtils.fromFXImage(image, null);
    // Initialize the fitness function object with the bufferedImage
    // version of the original image.
    startFitness = new FitnessFunction(temp);
    for (int i = 0; i < NUM_TRIBES; i++)
    {
      tribes.add(createTribe());
    }

    // Now to find the very first genome to display:
    // First find the fittest genome out of all the tribes
    // (All we have to check is the tribes first genome in the list
    // as the list is sorted and should have the fittest tribal member.)
    // Now that we have the fittest overall genome we need to see if we
    // want to use white or black as a background. Also with the new
    // renderer we have to pass the background color to it.
    initBestFitTribe = 0;
    Genome bestGenome = tribes.get(initBestFitTribe).getGenomesInTribe().get(0);
    double bestFit = bestGenome.getFitness();
    for (int i = 1; i < NUM_TRIBES; i++)
    {
      if (tribes.get(i).getGenomesInTribe().get(0).getFitness() > bestFit)
      {
        initBestFitTribe = i;
        bestFit = tribes.get(i).getGenomesInTribe().get(0).getFitness();
        bestGenome = tribes.get(i).getGenomesInTribe().get(0);
      }
    }

    initialFitness = bestFit;
    imageRenderer.render(bestGenome.getDNA());
    perspectiveImage.setImage(SwingFXUtils.toFXImage(imageRenderer.getBuff(), null));
    
    
   

    // Initialize the GA with the initial population and give main a reference
    // to it and the initial population and fitness
    for (int i = 0; i < NUM_TRIBES; i++)
    {
      Genome gen = tribes.get(i).getGenomesInTribe().get(0);
      tribesGA.add(new GA(tribes.get(i), gen.getFitness(), image, IMAGE_WIDTH, IMAGE_HEIGHT, main, backGroundColor));
    }

    // For now just use the first tribes GA.
//    main.setGA(tribesGA.get(0), perspectiveImage.getImage(), initialFitness);
  }
  
  /**
   * Now to figure out what background color to use this method is
   * called once upon the creation of the very first genome and with
   * the first genome the background color is decided. 
   * @param DNA
   */
  private void backGroundColorTest(ArrayList<Triangle>DNA)
  {
	   imageRenderer.setBackGroundColor(Color.BLACK);
	    imageRenderer.render(DNA);
	    BufferedImage blackTest = imageRenderer.getBuff();
	    startFitness.calculateFitness(blackTest);
	    double blackFit = startFitness.getFitness();
	    imageRenderer.setBackGroundColor(Color.WHITE);
	    imageRenderer.render(DNA);
	    BufferedImage whiteTest = imageRenderer.getBuff();
	    startFitness.calculateFitness(whiteTest);
	    double whiteFit = startFitness.getFitness();

	    if (blackFit > whiteFit)
	    {
	      System.out.println("Best Color is Black.");
	      backGroundColor = Color.BLACK;
	      imageRenderer.setBackGroundColor(backGroundColor);
	    }
	    else
	    {
	      System.out.println("Best Color is White.");
	      backGroundColor = Color.WHITE;
	      imageRenderer.setBackGroundColor(backGroundColor);
	    }
  }
  
  
  
  /**
   * Grab colors from triangular bounding boxes to be used to color
   * sample part of the initial populaiton. 
   * @param originalImage
   */
  private void CalculateInitialPopulation(Image originalImage)
  {
	Polygon triangle1;
    Polygon triangle2;
    int index = -1;

    for (int i = 0; i < 10; i++)
    {
      for (int j = 0; j < 10; j++)
      {

          int P1x = (j * triangleWidth);
          int P2x = (j * triangleWidth);
          int P3x = (((j + 1) * triangleWidth));
          int P1y = (i * triangleHeight);
          int P2y = ((i + 1) * triangleHeight);
          int P3y=((i + 1) * triangleHeight);
          triangle1 = new Polygon(new int[]{P1x,P2x,P3x},new int[]{P1y,P2y,P3y},3);

          int P1xt2 = (j * triangleWidth);
          int P2xt2 = (((j + 1) * triangleWidth));
          int P3xt2 = (((j + 1) * triangleWidth));
          int P1yt2 = (i * triangleHeight);
          int P2yt2 = (i * triangleHeight);
          int P3yt2 = ((i + 1) * triangleHeight);

          triangle2 = new Polygon(new int[]{P1xt2,P2xt2,P3xt2},new int[]{P1yt2,P2yt2,P3yt2},3);
          

          // Get the start x/y location for the triangles for calculating
          // their average RGB.
          int startX = j * triangleWidth;
          int startY = i * triangleHeight;
          // Calculate the average RGB of pixels contained in each of the
          // triangles
          // and set that average RGB color as the color of the triangle.
          calculateAverageRGB(triangle1, triangleWidth, triangleHeight , startX, startY,++index);
          calculateAverageRGB(triangle2, triangleWidth, triangleHeight , startX, startY,++index);

      }
    }
  }

  /**
   * Calculate the RGB values to be used when creating part of the initial
   * population. 
   * @param triangle
   * @param width
   * @param height
   * @param startX
   * @param startY
   * @param index
   */
  private void calculateAverageRGB(Polygon triangle, int width, int height, int startX, int startY,int index)
  {

	//System.out.println("index: " + index);
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
        if (triangle.contains(i, j))
        {
        	//System.out.println("here");
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
    //System.out.println(newRedValue + " " +newGreenValue + " " + newBlueValue);
    reds[index] = newRedValue;
    greens[index] = newGreenValue;
    blues[index] = newBlueValue;
  }
  
  
  public ArrayList<Tribe> getTribes()
  {
    return tribes;
  }

  public ArrayList<GA> getTribesGAs()
  {
    return tribesGA;
  }

  public Color getBackGroundColor()
  {
    return backGroundColor;
  }

  // Create the members of the tribe in a sorted manor.
  private Tribe createTribe()
  {
    ArrayList<Genome> genomes = new ArrayList<>();
    Genome genome;
    for (int i = 0; i < INITIAL_TRIBE_POPULATIONS; i++)
    {

      genome = createGenome();
      imageRenderer.render(genome.getDNA());
      // Check new fitness.

      startFitness.calculateFitness(imageRenderer.getBuff());

      imageRenderer.render(genome.getDNA());
      genome.setFitness(startFitness.getFitness());
      // Insert the genome into the tribe sorted so that the genomes
      // in the tribe go from the most fit to the least fit as the
      // index increases.
      util.insertSorted(genome, genomes);

    }

    return new Tribe(genomes);
  }

  /**
   * Updates all the ArrayList<Genome> values with the new white background.
   * This updates the new fitness values and sorts accordingly.
   * 
   * Changing the color of the background can change the fitness value
   * drastically. The genomes will not always retain the correct order when
   * changing a background from black to white
   */
//  private void updateGenomeBackgrounds()
//  {
//    for (int i = 0; i < NUM_TRIBES; i++)
//    {
//      ArrayList<Genome> newGenomes = new ArrayList<>();
//      Genome genome;
//      for (int j = 0; j < tribes.get(i).getTribePopulation(); j++)
//      {
//        genome = tribes.get(i).getGenomesInTribe().get(j);
//        imageRenderer.setBackGroundColor(Color.WHITE);
//        imageRenderer.render(genome.getDNA());
//        startFitness.calculateFitness(imageRenderer.getBuff());
//        genome.setFitness(startFitness.getFitness());
//        genome.setFitness(startFitness.getFitness());
//
//        util.insertSorted(genome, newGenomes);
//      }
//
//      tribes.get(i).updateGenome(newGenomes);
//    }
//  }

  private Genome createGenome()
  {
    // 200 triangles per genome.

    ArrayList<Triangle> DNA = new ArrayList<>();
    for (int i = 0; i < 200; i++)
    {
      // Make the genome.
    	
    	//Old Version completly random.
       	
//        Triangle triangle = new Triangle();
//        triangle.setAlpha(random.nextInt(255));
//        triangle.setRed(reds[i]);
//        triangle.setBlue(blues[i]);
//        triangle.setGreen(greens[i]);
//        triangle.setP1x(temp1[i]);
//        triangle.setP2x(temp2[i]);
//        triangle.setP3x(temp3[i]);
//        triangle.setP1y(temp4[i]);
//        triangle.setP2y(temp5[i]);
//        triangle.setP3y(temp6[i]);
//        triangle.updateTriangle();
//        DNA.add(triangle);
    	
      //Color sampling with a 10% random chance per triangle
      //of a RBG value being random to not limit the solution
      //set 
      Triangle triangle = new Triangle();
      triangle.setAlpha(random.nextInt(255));
      if(random.nextInt(10)==1)
      {
    	  triangle.setRed(random.nextInt(255));
      }
      else
      {
      triangle.setRed(reds[i]);
      }
      if(random.nextInt(10)==1)
      {
    	  triangle.setGreen(random.nextInt(255));
      }
      else
      {
      triangle.setGreen(greens[i]);
      }
      if(random.nextInt(10)==1)
      {
    	  triangle.setBlue(random.nextInt(255));
      }
      else
      {
      triangle.setBlue(blues[i]);
      }

      triangle.setP1x(random.nextInt(IMAGE_WIDTH));
      triangle.setP2x(random.nextInt(IMAGE_WIDTH));
      triangle.setP3x(random.nextInt(IMAGE_WIDTH));
      triangle.setP1y(random.nextInt(IMAGE_HEIGHT));
      triangle.setP2y(random.nextInt(IMAGE_HEIGHT));
      triangle.setP3y(random.nextInt(IMAGE_HEIGHT));
      triangle.updateTriangle();
      DNA.add(triangle);

    }

    if(!completedBackGroundTest)
    {
    	backGroundColorTest(DNA);
    	completedBackGroundTest = true;
    }
    
    return new Genome(DNA);
  }

  ArrayList<Triangle> test;


  public double getInitFitness()
  {
    return initialFitness;
  }

  public Image getInitImage()
  {
    return perspectiveImage.getImage();
  }

  public int getBestFitTribe()
  {
    return initBestFitTribe;
  }

}
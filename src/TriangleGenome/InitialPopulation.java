package TriangleGenome;

//import javafx.scene.paint.Color;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import Application.NewMain;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.stage.Stage;

/**
 * 
 * @author Christian Seely
 * @author Jesus Lopez 
 * This class is used to construct the initial populaiton for all of
 * the tribes for the application. In the creation of the tribes
 * the members of the tribe are ordered from most to least fit. 
 *
 */
public class InitialPopulation extends Stage
{

  Image image;
  private ImageView perspectiveImage;
  private Renderer imageRenderer;
  private Random random = new Random();
  private double initialFitness;
  private UtilityClass util;
  private Color backGroundColor;
  FitnessFunction startFitness;
  int IMAGE_WIDTH;
  int IMAGE_HEIGHT;
  // For color sampling. 
  int boundingBoxWidth;
  int boundingBoxHeight;
  private int NUM_TRIBES;
  //For color sampling. 
  private int[] reds;
  private int[] greens;
  private int[] blues;
  
  private boolean completedBackGroundTest;
  
  
  private int INITIAL_TRIBE_POPULATIONS = 100;

  private PixelReader reader;

  // Array List to hold all the tribes.
  private ArrayList<Tribe> tribes = new ArrayList<>();
  // Each tribe has the genetic algorithm operating soley for
  // itself so each one needs an object.
  private ArrayList<GA> tribesGA = new ArrayList<>();


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
    //Create pixel reader used for color sampling. 
    this.reader = image.getPixelReader();
    //For color sampling there are 100 bounding boxes. 
    this.boundingBoxWidth = IMAGE_WIDTH / 10;
    this.boundingBoxHeight = IMAGE_HEIGHT / 10;
    //Perform color sampling. 
    colorSampling(image);
    perspectiveImage = new ImageView();

    // Convert the initial image into a buffered image.
    BufferedImage temp = SwingFXUtils.fromFXImage(image, null);
    // Initialize the fitness function object with the bufferedImage
    // version of the original image.
    startFitness = new FitnessFunction(temp);
    //Create the tribes. 
    for (int i = 0; i < NUM_TRIBES; i++)
    {
      tribes.add(createTribe());
    }
    //Find the initial best fit genome from all of the tribes. 
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
    //Set the best genomes info as the genome that is going to 
    //be initially displayed. 
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
  }
  
  /**
   * To figure out what background color to use this method is
   * called once upon the creation of the very first genome and with
   * the first genome the background color is decided. 
   * @param ArrayList<Triangle> DNA 
   */
  private void backGroundColorTest(ArrayList<Triangle>DNA)
  {
      //Using the very first genome render it with both a white
      //and a black background and what ever one has a better
      //fitness use that one. 
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
	      backGroundColor = Color.BLACK;
	      imageRenderer.setBackGroundColor(backGroundColor);
	    }
	    else
	    {
	      backGroundColor = Color.WHITE;
	      imageRenderer.setBackGroundColor(backGroundColor);
	    }
  }
  
  
  
  /**
   * Grab colors from triangular bounding boxes to be used to color
   * sample part of the initial populaiton. 
   * @param Image originalImage- the original image. 
   */
  private void colorSampling(Image originalImage)
  {
    Polygon triangle1;
    Polygon triangle2;
    int index = -1;
    //Create 100 bounding boxes and split them down the diagnal to get 
    //triangles, then with those triangles do some color sampling which
    //will be used to aid in the creation of the initial population. 
    for (int i = 0; i < 10; i++)
    {
      for (int j = 0; j < 10; j++)
      {

          int P1x = (j * boundingBoxWidth);
          int P2x = (j * boundingBoxWidth);
          int P3x = (((j + 1) * boundingBoxWidth));
          int P1y = (i * boundingBoxHeight);
          int P2y = ((i + 1) * boundingBoxHeight);
          int P3y=((i + 1) * boundingBoxHeight);
          triangle1 = new Polygon(new int[]{P1x,P2x,P3x},new int[]{P1y,P2y,P3y},3);

          int P1xt2 = (j * boundingBoxWidth);
          int P2xt2 = (((j + 1) * boundingBoxWidth));
          int P3xt2 = (((j + 1) * boundingBoxWidth));
          int P1yt2 = (i * boundingBoxHeight);
          int P2yt2 = (i * boundingBoxHeight);
          int P3yt2 = ((i + 1) * boundingBoxHeight);

          triangle2 = new Polygon(new int[]{P1xt2,P2xt2,P3xt2},new int[]{P1yt2,P2yt2,P3yt2},3);
          

          // Get the start x/y location for the triangles for calculating
          // their average RGB.
          int startX = j * boundingBoxWidth;
          int startY = i * boundingBoxHeight;
          // Calculate the average RGB of pixels contained in each of the
          // triangles
          // and set that average RGB color as the color of the triangle.
          calculateAverageRGB(triangle1, boundingBoxWidth, boundingBoxHeight , startX, startY,++index);
          calculateAverageRGB(triangle2, boundingBoxWidth, boundingBoxHeight , startX, startY,++index);

      }
    }
  }

  /**
   * Calculate the RGB values to be used when creating part of the initial
   * population. 
   * @param Polygon triangle for color sampling. 
   * @param int width of bounding box.
   * @param int height of bounding box. 
   * @param int startX of top left corner of bounding box. 
   * @param int startY of top left corner of bounding box. 
   * @param int index or colors arrays. 
   */
  private void calculateAverageRGB(Polygon triangle, int width, int height, int startX, int startY,int index)
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
        if (triangle.contains(i, j))
        {
          ++pixelCount;
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
    reds[index] = newRedValue;
    greens[index] = newGreenValue;
    blues[index] = newBlueValue;
  }
  
  /**
   * 
   * @return An array list of the tribse. 
   */
  public ArrayList<Tribe> getTribes()
  {
    return tribes;
  }
  /**
   * 
   * @return An Array List of the Tribes GA objects. 
   */
  public ArrayList<GA> getTribesGAs()
  {
    return tribesGA;
  }
  /**
   * 
   * @return Color best background color following testing. 
   */
  public Color getBackGroundColor()
  {
    return backGroundColor;
  }

  /**
   * 
   * @return Tribe- The newly created tribe. 
   */
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
   * 
   * @return Genome- Create and return the newly created genome.
   * Note 10% of the genomes genes are randomly selected and the
   * rest are from the results of the color sampling. 
   */
  private Genome createGenome()
  {
    // 200 triangles per genome.
    ArrayList<Triangle> DNA = new ArrayList<>();
    for (int i = 0; i < 200; i++)
    {    	
      //Color sampling with a 10% random chance per triangle
      //of a RBG value being random to not limit the solution
      //set. 
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
    //Complete background test with first genome. 
    if(!completedBackGroundTest)
    {
    	backGroundColorTest(DNA);
    	completedBackGroundTest = true;
    }
    
    return new Genome(DNA);
  }
  /**
   * 
   * @return double the initial fitness of the best
   * genome from all tribes. 
   */
  public double getInitFitness()
  {
    return initialFitness;
  }
  /**
   * 
   * @return The best genome as an image. 
   */
  public Image getInitImage()
  {
    return perspectiveImage.getImage();
  }
  /**
   * 
   * @return The index of the thread containing
   * the best genome. 
   */
  public int getBestFitTribe()
  {
    return initBestFitTribe;
  }

}
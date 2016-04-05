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
  private Renderer imageRenderer;
  private Random random = new Random();
  private double initialFitness;
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
    // The images should have the same height/width.
    this.IMAGE_HEIGHT = (int) image.getHeight();
    this.IMAGE_WIDTH = (int) image.getWidth();
    // Start imageRenderer with black as default.
    this.imageRenderer = new Renderer(IMAGE_WIDTH, IMAGE_HEIGHT, Color.BLACK);

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
    int g = 0;
    Genome bestGenome = tribes.get(g).getGenomesInTribe().get(0);
    double bestFit = bestGenome.getFitness();
    for (int i = 1; i < NUM_TRIBES; i++)
    {
      if (tribes.get(i).getGenomesInTribe().get(0).getFitness() > bestFit)
      {
        g = i;
        bestFit = tribes.get(i).getGenomesInTribe().get(0).getFitness();
        bestGenome = tribes.get(i).getGenomesInTribe().get(0);
      }
    }

    imageRenderer.setBackGroundColor(Color.BLACK);
    imageRenderer.render(bestGenome.getDNA());
    BufferedImage blackTest = imageRenderer.getBuff();
    startFitness.calculateFitness(blackTest);
    double blackFit = startFitness.getFitness();
    imageRenderer.setBackGroundColor(Color.WHITE);
    imageRenderer.render(bestGenome.getDNA());
    BufferedImage whiteTest = imageRenderer.getBuff();
    startFitness.calculateFitness(whiteTest);
    double whiteFit = startFitness.getFitness();

    if (blackFit > whiteFit)
    {
      System.out.println("Best Color is Black.");
      backGroundColor = Color.BLACK;
      initialFitness = blackFit;
      perspectiveImage.setImage(SwingFXUtils.toFXImage(blackTest, null));
    }
    else
    {
      // this means we have to recalculate all the fitness values since the
      // white background is better and the previous fitness values were
      // determined with a black background. We then set the perspectiveImage
      // and the initial fitness with the appropriate objects.

      System.out.println("Best Color is White.");
      backGroundColor = Color.WHITE;

      updateGenomeBackgrounds();
      imageRenderer.setBackGroundColor(backGroundColor);
      bestGenome = tribes.get(0).getGenomesInTribe().get(0);
      initBestFitTribe = 0;
      bestFit = bestGenome.getFitness();
      for (int i = 1; i < NUM_TRIBES; i++)
      {
        if (tribes.get(i).getGenomesInTribe().get(0).getFitness() > bestFit)
        {
          bestGenome = tribes.get(i).getGenomesInTribe().get(0);
          bestFit = bestGenome.getFitness();
          initBestFitTribe = i;
        }
      }

      imageRenderer.render(bestGenome.getDNA());
      whiteTest = imageRenderer.getBuff();
      startFitness.calculateFitness(whiteTest);
      initialFitness = startFitness.getFitness();
      perspectiveImage.setImage(SwingFXUtils.toFXImage(whiteTest, null));

    }

    // Initialize the GA with the initial population and give main a reference
    // to it and the initial population and fitness
    for (int i = 0; i < NUM_TRIBES; i++)
    {
      Genome gen = tribes.get(i).getGenomesInTribe().get(0);
      tribesGA.add(new GA(tribes.get(i), gen.getFitness(), image, gen.getImg().getImage(), IMAGE_WIDTH, IMAGE_HEIGHT, main, backGroundColor));
    }

    // For now just use the first tribes GA.
//    main.setGA(tribesGA.get(0), perspectiveImage.getImage(), initialFitness);
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
      BufferedImage img = imageRenderer.getBuff();
      genome.setImg(SwingFXUtils.toFXImage(img, null));
      genome.setFitness(startFitness.getFitness());
      // Insert the genome into the tribe sorted so that the genomes
      // in the tribe go from the most fit to the least fit as the
      // index increases.
      insertSorted(genome, genomes);

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
  private void updateGenomeBackgrounds()
  {
    for (int i = 0; i < NUM_TRIBES; i++)
    {
      ArrayList<Genome> newGenomes = new ArrayList<>();
      Genome genome;
      for (int j = 0; j < tribes.get(i).getTribePopulation(); j++)
      {
        genome = tribes.get(i).getGenomesInTribe().get(j);
        imageRenderer.setBackGroundColor(Color.WHITE);
        imageRenderer.render(genome.getDNA());
        startFitness.calculateFitness(imageRenderer.getBuff());
        genome.setFitness(startFitness.getFitness());

        BufferedImage img = imageRenderer.getBuff();
        genome.setImg(SwingFXUtils.toFXImage(img, null));
        genome.setFitness(startFitness.getFitness());

        insertSorted(genome, newGenomes);
      }

      tribes.get(i).updateGenome(newGenomes);
    }
  }

  private void insertSorted(Genome genome, ArrayList<Genome> genomes)
  {
    for (int i = 0; i < genomes.size(); i++)
    {
      if (genomes.get(i).getFitness() > genome.getFitness())
        continue;

      genomes.add(i, genome);
      return;
    }

    // Append to very end of list;
    genomes.add(genome);

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

  public Image getInitImage()
  {
    return perspectiveImage.getImage();
  }

  public int getBestFitTribe()
  {
    return initBestFitTribe;
  }

}
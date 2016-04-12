package TriangleGenome;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import Application.NewMain;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.text.Text;

/**
 * @author Christian Seely
 * @author Jesus Lopez 
 * 
 * This is the main class that handles the flow of the hill climbing/
 * cross over mutation. Each thread/tribe gets a GA object which
 * will perform its hill climbing/cross over. To use this method
 * you must create a GA object which initializes the classes
 * variables/objects and then repeatlly call the mutate function
 * to perform a mutaiton, the type of mutation will depend on whether
 * the cross over mode is toggled or not. 
 */
public class GA extends Stage
{
  public int stuckCount;
  public boolean isCrossOverMode;
  public boolean finishedCrossOver;
  double parentFitness;
  double childFitness;
  private int prevValue;
  final int IMAGE_HEIGHT;
  final int IMAGE_WIDTH;
  private final int MAX_STEP_SIZE = 10; // Eventually large steps are not
  private Image originalImage;
  private ArrayList<Triangle> DNA;
  private boolean HILL_CLIMBING_WITHOUT_CROSSOVER = true;
  private FitnessFunction checkFitness;
  private Random random = new Random();
  boolean mutationDirection; // Let false be decreasing, and true be
                             // increasing.
  private Tribe tribe;
  // Create Render object. In doing so all of the fields for it are
  // initialized/set up.
  private Renderer imageRenderer;
  boolean wasImprovement;
  private int geneMutationNum;
  private int triangleNum;
  private CrossOverMutation crossOverMutation;// = new CrossOverMutation();
  private TriangleMutation adaptiveMutation;
  int mutations;

  private Image perspectiveImage;
  private NewMain main;
  private int generations = 0;
  private int improvements = 0;
  private int hillclimbChildren = 0;
  private int crossoverChildren = 0;

  private ArrayList<Genome> globalPool;

  private int previousItGenerations = 0;
  private double previousItFitness = 0;
  private Image bestGenome;
  /**
   * 
   * @param tribe The Tribe what will use the GA.
   * @param initialFitness The initial fitness of the best genome in the
   * tribe. 
   * @param originalImage The original image (used for fitness tests)
   * @param IMAGE_WIDTH The images width
   * @param IMAGE_HEIGHT The images height. 
   * @param m Instance of the main class. 
   * @param backGroundColor The Background color to be used throughout the
   * GA. 
   * This is the constructor which initializes all things used in the class. 
   */
  public GA(Tribe tribe, double initialFitness, Image originalImage, int IMAGE_WIDTH,
      int IMAGE_HEIGHT, NewMain m, Color backGroundColor)
  {
    this.tribe = tribe;
    this.IMAGE_WIDTH = IMAGE_WIDTH;
    this.IMAGE_HEIGHT = IMAGE_HEIGHT;
    imageRenderer = new Renderer(IMAGE_WIDTH, IMAGE_HEIGHT, backGroundColor);
    this.originalImage = originalImage;
    BufferedImage temp = SwingFXUtils.fromFXImage(originalImage, null);
    this.checkFitness = new FitnessFunction(temp);
    this.parentFitness = initialFitness;
    this.wasImprovement = false; // We want to start with a random gene
                                 // mutation.
    this.globalPool = new ArrayList<>();
    this.finishedCrossOver = false;
    this.isCrossOverMode = false;
    this.adaptiveMutation = new TriangleMutation(IMAGE_WIDTH, IMAGE_HEIGHT);
    this.childFitness = 0;
    this.stuckCount = 0;
    // The fittest member in the tribe should be at the front of the arrayList.
    updateBestDNA();
    imageRenderer.render(DNA);
    bestGenome = SwingFXUtils.toFXImage(imageRenderer.getBuff(), null);
    this.crossOverMutation = new CrossOverMutation(checkFitness, imageRenderer, IMAGE_WIDTH,
        IMAGE_HEIGHT);
    main = m;
    main.initRenderer(IMAGE_WIDTH, IMAGE_HEIGHT, backGroundColor);
  }
  /**
   * 
   * @return CrossOverMutaiton object thats aleady initialized. 
   */
  public CrossOverMutation getCrossOverMutationObject()
  {
    return crossOverMutation;
  }
  /**
   * Update the best current DNA in the tribes GA (at front of list). 
   */
  public void updateBestDNA()
  {
    DNA = tribe.getGenomesInTribe().get(0).getDNA();
  }
  /**
   * 
   * @param globalPool Set the global pool which consists of
   * genomes from the other tribes, this will be used for cross
   * tribal cross over. 
   */
  public void setGlobalPool(ArrayList<Genome> globalPool)
  {
    this.globalPool = globalPool;
  }

  /**
    * METHOD NOT USED.
   */
  public void setMutateType(boolean type)
  {
    HILL_CLIMBING_WITHOUT_CROSSOVER = type;
  }

  /**
   * Called by the ApplicationLoop class. Performs a single mutation per call.
   */
  public void Mutate()
  {
    if (!isCrossOverMode)
    {
      // Perform random mutation.
      randomMutate();
      ++generations;
      ++hillclimbChildren;
      // Undo mutation if unsuccessful.
      if (!checkIfRandomMutationWasImprovement())
      {
        undoRandomMutate(triangleNum, geneMutationNum, prevValue);
      }
      else
      {
        // Random mutation resulted in an improvement now we
        // Adaptively climb our prospective hill.
        main.updateTribesList(DNA, parentFitness, bestGenome, this);
        ++improvements;
        adaptivelyClimbHill();
      }
    }
    else
    {
      // This for loop represents the cross tribal cross over.
      int singleOrDouble;// Currently there is a 50% chance of either occuring
      // again we can try altering the probability to see which one is better.
      // Thus far I am not sure which one is better I have to run longer
      // tests
      int size = tribe.getGenomesInTribe().size();
      for (int i = 0; i < globalPool.size(); i++)
      {
        singleOrDouble = random.nextInt(2);
        if (singleOrDouble == 0)
        { // Single point.
          crossOverMutation.invokeCrossOverMutation(tribe, globalPool.get(i), tribe
              .getGenomesInTribe().get(random.nextInt(size - 1)), true);
        }
        else // Double point.
        {
          crossOverMutation.invokeCrossOverMutation(tribe, globalPool.get(i), tribe
              .getGenomesInTribe().get(random.nextInt(size - 1)), false);
        }
        updateBestDNA();
        if (crossOverMutation.getLastChildFitness() > parentFitness)
        {
          updateDisplay();
          main.updateTribesList(DNA, parentFitness, bestGenome, this);
        }
        ++generations;
        ++crossoverChildren;
      }
      // This loop represents the innertribal cross over, change the bounds
      // if you want to test more or less intertribal cross over or change
      // the selection for testing (size/2) would limit selection to only
      // the top half most fit genomes in the tribes.
      for (int i = 0; i < 100; i++)
      {
        singleOrDouble = random.nextInt(2);
        if (singleOrDouble == 0) // Single point
        {
          crossOverMutation.invokeCrossOverMutation(tribe, tribe.getGenomesInTribe().get(random
              .nextInt((size - 1))), tribe.getGenomesInTribe().get(random.nextInt(size)), true);
        }
        else // Double point
        {
          crossOverMutation.invokeCrossOverMutation(tribe, tribe.getGenomesInTribe().get(random
              .nextInt((size - 1))), tribe.getGenomesInTribe().get(random.nextInt(size)), false);
        }
        updateBestDNA();
        if (crossOverMutation.getLastChildFitness() > parentFitness)
        {
          updateDisplay();
          main.updateTribesList(DNA, parentFitness, bestGenome, this);
        }
        ++generations;
        ++crossoverChildren;
      }
      finishedCrossOver = true;
      isCrossOverMode = false;
    }
    DNA = tribe.getGenomesInTribe().get(0).getDNA();
  }

  public FitnessFunction getFitObj()
  {
    return checkFitness;
  }

  public ArrayList<Triangle> getDNA()
  {
    return DNA;
  }

  /**
   * Update the display during cross over for small chance that there is a new
   * best genome. 
   */
  private void updateDisplay()
  {
    imageRenderer.render(tribe.getGenomesInTribe().get(0).getDNA());
    bestGenome = SwingFXUtils.toFXImage(imageRenderer.getBuff(), null);
    parentFitness = tribe.getGenomesInTribe().get(0).getFitness();

    main.updateTribesList(DNA, parentFitness, bestGenome, this);

  }
  /**
   * Once a successful random mutaiton occurs then we find the direciton
   * of that mutaiton and then adaptivly mutate in that direction
   * with incremental step sizes. 
   */
  private void adaptivelyClimbHill()
  {
    // First find direction of hill climb.
    boolean direction = true; // Let True be an increase in the genes value and
                              // False
    // be a decrease in the genes value.
    // We could calculate the direction value in while doing the random mutation
    // but
    // even though it would be less physical code it would be more computations
    // being
    // done.
    int stepSize = 1;// Start the step size at 1.

    boolean continueClimb = true; // Boolean flag to that gets triggered
    // when the climb just come to an end following a unsuccessful mutation.
    Triangle temp = DNA.get(triangleNum);
    switch (geneMutationNum)
    {
      case 1:
        if (temp.getAlpha() > prevValue)
        {
          direction = true;
        }
        else
        {
          direction = false;
        }
        break;
      case 2:
        if (temp.getRed() > prevValue)
        {
          direction = true;
        }
        else
        {
          direction = false;
        }
        break;
      case 3:
        if (temp.getGreen() > prevValue)
        {
          direction = true;
        }
        else
        {
          direction = false;
        }
        break;
      case 4:
        if (temp.getBlue() > prevValue)
        {
          direction = true;
        }
        else
        {
          direction = false;
        }
        break;
      case 5:
        if (temp.getP1x() > prevValue)
        {
          direction = true;
        }
        else
        {
          direction = false;
        }
        break;
      case 6:
        if (temp.getP1y() > prevValue)
        {
          direction = true;
        }
        else
        {
          direction = false;
        }
        break;
      case 7:
        if (temp.getP2x() > prevValue)
        {
          direction = true;
        }
        else
        {
          direction = false;
        }
        break;
      case 8:
        if (temp.getP2y() > prevValue)
        {
          direction = true;
        }
        else
        {
          direction = false;
        }
        break;
      case 9:
        if (temp.getP3x() > prevValue)
        {
          direction = true;
        }
        else
        {
          direction = false;
        }
        break;
      case 10:
        if (temp.getP3y() > prevValue)
        {
          direction = true;
        }
        else
        {
          direction = false;
        }
        break;
      default:
        break; // Should never reach here.
    }

    mutationDirection = direction;

    // Keep incrementally climbing the hill until there is a
    // non improvement.
    while (continueClimb)
    {
      ++generations;
      ++hillclimbChildren;
      if (takeStep(stepSize)) // Resulted in improvement
      {
        main.updateTribesList(DNA, parentFitness, bestGenome, this);
        ++improvements;
        // System.out.println(generations);
        if (stepSize < MAX_STEP_SIZE)
        {
          ++stepSize;
        }
      }
      else
      {
        undoLastStep(stepSize);
        stepSize = 1;
        continueClimb = false; // Termination condition.
      }

    }
  }

  /**
   * 
   * There is two ways a step can fail, either it results in a fitness less than
   * its parent or it hits the end range that it can travel in its current
   * direction, instead of fliping the direction which would just result in
   * undoing our positive mutations we go back to finding another hill to climb
   * (aka return false).
   */
  private boolean takeStep(int stepSize)
  {
    Triangle triangle = DNA.get(triangleNum);
    boolean hitBound = false;
    switch (geneMutationNum)
    {
      case 1:
        adaptiveMutation.mutateAlpha(triangle, stepSize, mutationDirection);
        triangle.updateTriangle();
        if (triangle.getAlpha() == 0 || triangle.getAlpha() == 255)
          hitBound = true;
        break;
      case 2:
        adaptiveMutation.mutateRed(triangle, stepSize, mutationDirection);
        triangle.updateTriangle();
        if (triangle.getRed() == 0 || triangle.getRed() == 255)
          hitBound = true;
        break;
      case 3:
        adaptiveMutation.mutateGreen(triangle, stepSize, mutationDirection);
        triangle.updateTriangle();
        if (triangle.getGreen() == 0 || triangle.getGreen() == 255)
          hitBound = true;
        break;
      case 4:
        adaptiveMutation.mutateBlue(triangle, stepSize, mutationDirection);
        triangle.updateTriangle();
        if (triangle.getBlue() == 0 || triangle.getBlue() == 255)
          hitBound = true;
        break;
      case 5:
        adaptiveMutation.mutateP1X(triangle, stepSize, mutationDirection);
        triangle.updateTriangle();
        if (triangle.getP1x() == 0 || triangle.getP1x() == IMAGE_WIDTH)
          hitBound = true;
        break;
      case 6:
        adaptiveMutation.mutateP1Y(triangle, stepSize, mutationDirection);
        triangle.updateTriangle();
        if (triangle.getP1y() == 0 || triangle.getP1y() == IMAGE_WIDTH)
          hitBound = true;
        break;
      case 7:
        adaptiveMutation.mutateP2X(triangle, stepSize, mutationDirection);
        triangle.updateTriangle();
        if (triangle.getP2x() == 0 || triangle.getP2x() == IMAGE_WIDTH)
          hitBound = true;
        break;
      case 8:
        adaptiveMutation.mutateP2Y(triangle, stepSize, mutationDirection);
        triangle.updateTriangle();
        if (triangle.getP2y() == 0 || triangle.getP2y() == IMAGE_WIDTH)
          hitBound = true;
        break;
      case 9:
        adaptiveMutation.mutateP3X(triangle, stepSize, mutationDirection);
        triangle.updateTriangle();
        if (triangle.getP3x() == 0 || triangle.getP3x() == IMAGE_WIDTH)
          hitBound = true;
        break;
      case 10:
        adaptiveMutation.mutateP3Y(triangle, stepSize, mutationDirection);
        triangle.updateTriangle();
        if (triangle.getP3y() == 0 || triangle.getP3y() == IMAGE_WIDTH)
          hitBound = true;
        break;
      default:
        break; // Should never reach here.
    }
    // update the triangle following the mutation.
    triangle.updateTriangle();
    // Check the new fitness.
    imageRenderer.render(DNA);
    // Check new fitness.
    checkFitness.calculateFitness(imageRenderer.getBuff());

    // If better fitness and no resultant bounds collision return true.
    if (checkFitness.getFitness() > parentFitness && !hitBound)
    {
      parentFitness = checkFitness.getFitness();
      // System.out.println("Step was a success, stepSize: " + stepSize);
      bestGenome = SwingFXUtils.toFXImage(imageRenderer.getBuff(), null);
      return true;
    }
    else
    {
      return false;
    }
  }

  public int getImgHeight()
  {
    return this.IMAGE_HEIGHT;
  }

  public int getImgWidth()
  {
    return this.IMAGE_WIDTH;
  }

  /**
   * Undo last adaptive hill climbing step.
   * 
   * @param stepSize
   */
  private void undoLastStep(int stepSize)
  {
    Triangle triangle = DNA.get(triangleNum);
    // To undo the last step we just need to do the same mutation except
    // in the opposite direction.
    if (mutationDirection)
    {
      mutationDirection = false;
    }
    else
    {
      mutationDirection = true;
    }
    switch (geneMutationNum)
    {
      case 1:
        adaptiveMutation.mutateAlpha(triangle, stepSize, mutationDirection);
        break;
      case 2:
        adaptiveMutation.mutateRed(triangle, stepSize, mutationDirection);
        break;
      case 3:
        adaptiveMutation.mutateGreen(triangle, stepSize, mutationDirection);
        break;
      case 4:
        adaptiveMutation.mutateBlue(triangle, stepSize, mutationDirection);
        break;
      case 5:
        adaptiveMutation.mutateP1X(triangle, stepSize, mutationDirection);
        break;
      case 6:
        adaptiveMutation.mutateP1Y(triangle, stepSize, mutationDirection);
        break;
      case 7:
        adaptiveMutation.mutateP2X(triangle, stepSize, mutationDirection);
        break;
      case 8:
        adaptiveMutation.mutateP2Y(triangle, stepSize, mutationDirection);
        break;
      case 9:
        adaptiveMutation.mutateP3X(triangle, stepSize, mutationDirection);
        break;
      case 10:
        adaptiveMutation.mutateP3Y(triangle, stepSize, mutationDirection);
        break;
      default:
        break; // Should never reach here.
    }
    // update the triangle following the mutation.
    triangle.updateTriangle();
  }

  /**
   * Random mutation adding a stochastic element to the hill climbing and is
   * used to trigger series of short adaptive climbs.
   */
  private void randomMutate()
  {
    // First select a random triangle
    triangleNum = random.nextInt(200);
    // It is very important never to change the order or remove triangles
    // from the list because the triangleNum references a triangle at a specific
    // index, thus that triangle should always be at that index.
    Triangle triangle = DNA.get(triangleNum);

    prevValue = 0; // use a single integer to hold all the values. Doesn't
                   // cause any problems since we can only mutate one of the
                   // genes at a time.

    // Next select a random gene from the triangle.
    geneMutationNum = random.nextInt(10) + 1;

    // If the gene value is on the border (e.g can't go higher or lower)
    // then change the direction).
    switch (geneMutationNum)
    {
      case 1:
        prevValue = triangle.getAlpha();
        triangle.setAlpha(random.nextInt(255));
        break;
      case 2:
        prevValue = triangle.getRed();
        triangle.setRed(random.nextInt(255));
        break;
      case 3:
        prevValue = triangle.getGreen();
        triangle.setGreen(random.nextInt(255));
        break;
      case 4:
        prevValue = triangle.getBlue();
        triangle.setBlue(random.nextInt(255));
        break;
      case 5:
        prevValue = triangle.getP1x();
        triangle.setP1x(random.nextInt(IMAGE_WIDTH));
        break;
      case 6:
        prevValue = triangle.getP1y();
        triangle.setP1y(random.nextInt(IMAGE_HEIGHT));
        break;
      case 7:
        prevValue = triangle.getP2x();
        triangle.setP2x(random.nextInt(IMAGE_WIDTH));
        break;
      case 8:
        prevValue = triangle.getP2y();
        triangle.setP2y(random.nextInt(IMAGE_HEIGHT));
        break;
      case 9:
        prevValue = triangle.getP3x();
        triangle.setP3x(random.nextInt(IMAGE_WIDTH));
        break;
      case 10:
        prevValue = triangle.getP3y();
        triangle.setP3y(random.nextInt(IMAGE_HEIGHT));
        break;
      default:
        break; // Should never reach here.
    }

    // Update the triangle following the mutation.
    triangle.updateTriangle();

  }

  /**
   * Undo the random mutation.
   * 
   * @param triangleNum
   * @param geneNum
   * @param prevGeneVal
   */
  private void undoRandomMutate(int triangleNum, int geneNum, int prevGeneVal)
  {
    switch (geneNum)
    {
      case 1:
        DNA.get(triangleNum).setAlpha(prevGeneVal);
        break;
      case 2:
        DNA.get(triangleNum).setRed(prevGeneVal);
        break;
      case 3:
        DNA.get(triangleNum).setGreen(prevGeneVal);
        break;
      case 4:
        DNA.get(triangleNum).setBlue(prevGeneVal);
        break;
      case 5:
        DNA.get(triangleNum).setP1x(prevGeneVal);
        break;
      case 6:
        DNA.get(triangleNum).setP1y(prevGeneVal);
        break;
      case 7:
        DNA.get(triangleNum).setP2x(prevGeneVal);
        break;
      case 8:
        DNA.get(triangleNum).setP2y(prevGeneVal);
        break;
      case 9:
        DNA.get(triangleNum).setP3x(prevGeneVal);
        break;
      case 10:
        DNA.get(triangleNum).setP3y(prevGeneVal);
        break;
      default:
        break; // Should never reach here.

    }
    DNA.get(triangleNum).updateTriangle();

  }
  /**
   * Update the best genome in the tribe. 
   */
  public void updateBestGenome()
  {
    imageRenderer.render(DNA);
    bestGenome = SwingFXUtils.toFXImage(imageRenderer.getBuff(), null);
    parentFitness = tribe.getGenomesInTribe().get(0).getFitness();
  }

  /**
   * @return the value of the current mutations fitness.
   */
  private double FitnessTest()
  {
    imageRenderer.render(DNA);
    // Check new fitness.
    checkFitness.calculateFitness(imageRenderer.getBuff());
    return checkFitness.getFitness();
  }

  /**
   * 
   * @return If the last mutation was an improvement (e.g the childs fitness is
   *         better than the parents.)
   */
  private boolean checkIfRandomMutationWasImprovement()
  {
    childFitness = FitnessTest();
    if (childFitness > parentFitness)
    {
      bestGenome = SwingFXUtils.toFXImage(imageRenderer.getBuff(), null);
      ;
      ++improvements;
      parentFitness = childFitness;
      return true;
    }
    else
    {
      return false;
    }
  }
  /**
   * 
   * @return The best genome in the tribes fitness. 
   */
  public double getFit()
  {
    return parentFitness;
  }
  /**
   * 
   * @return The best genome in the tribes image.
   */
  public Image getGenome()
  {
    return bestGenome;
  }
  /**
   * 
   * @return Number of generations thus far for this tribe. 
   */
  public int getNumGenerations()
  {
    return generations;
  }
  /**
   * 
   * @return Number of hill climbing generations for this tribe
   * thus far. 
   */
  public int getNumHillclimb()
  {
    return hillclimbChildren;
  }
  /**
   * 
   * @return Number of crossover generations for this tribe thus far. 
   */
  public int getNumCrossover()
  {
    return crossoverChildren;
  }
  /**
   * 
   * @return Return the previous number of generations for this
   * tribe. 
   */
  public int getPreviousNumGenerations()
  {
    return previousItGenerations;
  }
  /**I
   * 
   * @param numGenerations Update the prev number of generations. 
   */
  public void updatePreviousGeneration(int numGenerations)
  {
    previousItGenerations = numGenerations;
  }
  /**
   * 
   * @return THe last best fitness. 
   */
  public double getPreviousBestFit()
  {
    return previousItFitness;
  }
  /**
   * 
   * @param Update the previous fitness. 
   */
  public void updatePreviousFitness(double fit)
  {
    previousItFitness = fit;
  }

}
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
 * Class to handle the flow of the Genetic Algorithm/Hill climbing.
 *
 * Possible mutations (changing one of the 10 genes of a triangle) aka pure hill
 * climbing, but through explotation of patters we should be able to have
 * mutations of whole verticies (x,y), colors (a,r,g,b), size, order etc.
 * 
 * Each step size is altered by the improvementCombo integer. When the first
 * improvement is found, improvementCombo is increased to 2, and the mutation
 * following the first improvement increases/decreases a single value by 2 now.
 * Then 3, up to 5. After consecutive improvements, the value can not be higher
 * than 5. For example, we can never increase the alpha value by more than 5 per
 * step. The value is decreased back to 1 upon finding a mutation with no
 * improvement. After selecting a gene to mutate following NO improvement,
 * improvementCombo is ALWAYS 1. improvementCombo is only ever greater than 1
 * following improvements, thus, values are only increased or decreased by more
 * than 1 IMMEDIATELY following improvements
 * 
 * The mutate method is used by the animationloop in the main class to perform
 * the algorithm. Each time mutate is called, a single step in the algorithm is
 * performed. If the mutate creates an improved fitness, give main a copy of the
 * image and the fitness value.
 * 
 * Current problems I've noticed: Sometimes a triangle is completely useless.
 * Some triangles may be trapped behind a giant non transparent triangle and any
 * change to these trapped triangles result in no improvement. Not sure what to
 * do about them. Do we pop them to the very top? Leave them alone?
 */
public class GA extends Stage
{
  double parentFitness;
  double childFitness;
  private int prevValue;
  final int IMAGE_HEIGHT;
  final int IMAGE_WIDTH;
  private final int MAX_STEP_SIZE = 10; // Eventually large steps are not
	// helpful for example
	// a large step in color could skip a color spectrum.
  Image originalImage;
  ArrayList<Triangle> DNA;
  private boolean HILL_CLIMBING_WITHOUT_CROSSOVER = true;
  private FitnessFunction checkFitness;
  Random random = new Random();
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

  // Only ever values 1 through 5. Used by the GA to increase the mutation step
  // value.
  private int improvementCombo;
  private Image perspectiveImage;
  private NewMain main;
  private int generations = 0;
  private int improvements = 0;

  private double bestFit;
  private Image bestGenome;

  public GA(Tribe tribe, double initialFitness, Image originalImage, Image g, int IMAGE_WIDTH,
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
    this.adaptiveMutation = new TriangleMutation(IMAGE_WIDTH,IMAGE_HEIGHT);
    this.childFitness = 0;
    //The fittest member in the tribe should be at the front of the arrayList. 
    this.DNA = tribe.getGenomesInTribe().get(0).getDNA();
	this.crossOverMutation = new CrossOverMutation(checkFitness,imageRenderer,IMAGE_WIDTH,IMAGE_HEIGHT);
    main = m;
  }

  /**
   * Used to toggle the mutation type from the GUI
   * 
   * @param type
   *          If hard mutate is on or off
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
	    if (HILL_CLIMBING_WITHOUT_CROSSOVER) 
	    {
	    		//Perform random mutation. 
				randomMutate(); 
				++generations;
//				System.out.println(generations);
				//Undo mutation if unsuccessful. 
				if (!checkIfRandomMutationWasImprovement())
			    {
				      undoRandomMutate(triangleNum, geneMutationNum, prevValue);
				}
				else
				{
					//Random mutation resulted in an improvement now we 
					//Adaptively climb our prospective hill. 
					adaptivelyClimbHill();
				}
	    }
  }

  private void adaptivelyClimbHill()
  {
	  //First find direction of hill climb. 
	  boolean direction; //Let True be an increase in the genes value and False be a decrease in the genes value. 
	  //We could calculate the direction value in while doing the random mutation but
	  //even though it would be less physical code it would be more computations being
	  //done. 
	  int stepSize = 1;//Start the step size at 1. 
	  
	  boolean continueClimb = true; //Boolean flag to that gets triggered
	  //when the climb just come to an end following a unsuccessful mutation. 
	  Triangle temp = DNA.get(triangleNum);
	  switch(geneMutationNum)
	  {
      case 1:
    	  if(temp.getAlpha() > prevValue)
    	  {
    		  direction = true;
    	  }
    	  else
    	  {
    		  direction = false;
    	  }
          break;
        case 2:
      	  if(temp.getRed() > prevValue)
      	  {
      		  direction = true;
      	  }
      	  else
      	  {
      		  direction = false;
      	  }
          break;
        case 3:
        	  if(temp.getGreen() > prevValue)
          	  {
          		  direction = true;
          	  }
          	  else
          	  {
          		  direction = false;
          	  }
          break;
        case 4:
        	  if(temp.getBlue() > prevValue)
          	  {
          		  direction = true;
          	  }
          	  else
          	  {
          		  direction = false;
          	  }
          break;
        case 5:
        	  if(temp.getP1().x > prevValue)
          	  {
          		  direction = true;
          	  }
          	  else
          	  {
          		  direction = false;
          	  }
          break;
        case 6:
        	  if(temp.getP1().y > prevValue)
          	  {
          		  direction = true;
          	  }
          	  else
          	  {
          		  direction = false;
          	  }
          break;
        case 7:
        	  if(temp.getP2().x > prevValue)
          	  {
          		  direction = true;
          	  }
          	  else
          	  {
          		  direction = false;
          	  }
          break;
        case 8:
        	  if(temp.getP2().y > prevValue)
          	  {
          		  direction = true;
          	  }
          	  else
          	  {
          		  direction = false;
          	  }
          break;
        case 9:
        	  if(temp.getP3().x > prevValue)
          	  {
          		  direction = true;
          	  }
          	  else
          	  {
          		  direction = false;
          	  }
          break;
        case 10:
        	  if(temp.getP3().y > prevValue)
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
	  
	  //Keep incrementally climbing the hill until there is a
	  //non improvement. 
	  while(continueClimb)
	  {
		  if(takeStep(stepSize)) //Resulted in improvement
		  {
			  ++generations;
		      ++improvements;
//			  System.out.println(generations);
			  if(stepSize  < MAX_STEP_SIZE)
			  {
			  ++stepSize;
			  }
		  }
		  else
		  {
			  undoLastStep(stepSize);
			  stepSize = 1;
			  continueClimb = false; //Termination condition. 
		  }
		  
	  }
  }
  
  /**
   * 
   * There is two ways a step can fail, either it results in a fitness less
   * than its parent or it hits the end range that it can travel in its
   * current direction, instead of fliping the direction which would just
   * result in undoing our positive mutations we go back to finding another
   * hill to climb (aka return false). 
   */
  private boolean takeStep(int stepSize)
  {
	  Triangle triangle = DNA.get(triangleNum);
	  boolean hitBound = false;
	  switch (geneMutationNum)
	    {
	      case 1:
	        adaptiveMutation.mutateAlpha(triangle,stepSize,mutationDirection);
	        triangle.updateTriangle();
	        if(triangle.getAlpha()==0||triangle.getAlpha()==255)hitBound=true;
	        break;
	      case 2:
	        adaptiveMutation.mutateRed(triangle,stepSize,mutationDirection);
	        triangle.updateTriangle();
	        if(triangle.getRed()==0||triangle.getRed()==255)hitBound=true;
	        break;
	      case 3:
	        adaptiveMutation.mutateGreen(triangle,stepSize,mutationDirection);
	        triangle.updateTriangle();
	        if(triangle.getGreen()==0||triangle.getGreen()==255)hitBound=true;
	        break;
	      case 4:
	        adaptiveMutation.mutateBlue(triangle,stepSize,mutationDirection);
	        triangle.updateTriangle();
	        if(triangle.getBlue()==0||triangle.getBlue()==255)hitBound=true;
	        break;
	      case 5:
	        adaptiveMutation.mutateP1X(triangle,stepSize,mutationDirection);
	        triangle.updateTriangle();
	        if(triangle.getP1().x==0||triangle.getP1().x==IMAGE_WIDTH)hitBound=true;
	        break;
	      case 6:
	        adaptiveMutation.mutateP1Y(triangle,stepSize,mutationDirection);
	        triangle.updateTriangle();
	        if(triangle.getP1().y==0||triangle.getP1().y==IMAGE_WIDTH)hitBound=true;
	        break;
	      case 7:
	        adaptiveMutation.mutateP2X(triangle,stepSize,mutationDirection);
	        triangle.updateTriangle();
	        if(triangle.getP2().x==0||triangle.getP2().x==IMAGE_WIDTH)hitBound=true;
	        break;
	      case 8:
	        adaptiveMutation.mutateP2Y(triangle,stepSize,mutationDirection);
	        triangle.updateTriangle();
	        if(triangle.getP2().y==0||triangle.getP2().y==IMAGE_WIDTH)hitBound=true;
	        break;
	      case 9:
	        adaptiveMutation.mutateP3X(triangle,stepSize,mutationDirection);
	        triangle.updateTriangle();
	        if(triangle.getP3().x==0||triangle.getP3().x==IMAGE_WIDTH)hitBound=true;
	        break;
	      case 10:
	        adaptiveMutation.mutateP3Y(triangle,stepSize,mutationDirection);
	        triangle.updateTriangle();
	        if(triangle.getP3().y==0||triangle.getP3().y==IMAGE_WIDTH)hitBound=true;
	        break;
	      default:
	        break; // Should never reach here.
	    }
	    // update the triangle following the mutation.
	    triangle.updateTriangle();
	    //Check the new fitness. 
	    imageRenderer.render(DNA);
	    // Check new fitness.
	    checkFitness.calculateFitness(imageRenderer.getBuff());

	    //If better fitness and no resultant bounds collision return true. 
	    if(checkFitness.getFitness() > parentFitness&&!hitBound)
	    {
	    	parentFitness = checkFitness.getFitness();
//	    	System.out.println("Step was a success, stepSize: " + stepSize);
	    	bestFit = checkFitness.getFitness();
	    	bestGenome = SwingFXUtils.toFXImage(imageRenderer.getBuff(), null);
	    	return true;  
	    }
	    else
	    {
	    	return false;
	    }
  }
  
 /**
  * Undo last adaptive hill climbing step. 
  * @param stepSize
  */
  private void undoLastStep(int stepSize)
  {
	  Triangle triangle = DNA.get(triangleNum);
	  //To undo the last step we just need to do the same mutation except
	  //in the opposite direction. 
	  if(mutationDirection)
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
	        adaptiveMutation.mutateAlpha(triangle,stepSize,mutationDirection);
	        break;
	      case 2:
	        adaptiveMutation.mutateRed(triangle,stepSize,mutationDirection);
	        break;
	      case 3:
	        adaptiveMutation.mutateGreen(triangle,stepSize,mutationDirection);
	        break;
	      case 4:
	        adaptiveMutation.mutateBlue(triangle,stepSize,mutationDirection);
	        break;
	      case 5:
	        adaptiveMutation.mutateP1X(triangle,stepSize,mutationDirection);
	        break;
	      case 6:
	        adaptiveMutation.mutateP1Y(triangle,stepSize,mutationDirection);
	        break;
	      case 7:
	        adaptiveMutation.mutateP2X(triangle,stepSize,mutationDirection);
	        break;
	      case 8:
	        adaptiveMutation.mutateP2Y(triangle,stepSize,mutationDirection);
	        break;
	      case 9:
	        adaptiveMutation.mutateP3X(triangle,stepSize,mutationDirection);
	        break;
	      case 10:
	        adaptiveMutation.mutateP3Y(triangle,stepSize,mutationDirection);
	        break;
	      default:
	        break; // Should never reach here.
	    }
	    // update the triangle following the mutation.
	    triangle.updateTriangle();
  }
  
  /**
   * Random mutation adding a stochastic element to the hill climbing and 
   * is used to trigger series of short adaptive climbs. 
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
        prevValue = triangle.getP1().x;
        triangle.setP1(new Point(random.nextInt(IMAGE_WIDTH), triangle.getP1().y));
        break;
      case 6:
        prevValue = triangle.getP1().y;
        triangle.setP1(new Point(triangle.getP1().x, random.nextInt(IMAGE_HEIGHT)));
        break;
      case 7:
        prevValue = triangle.getP2().x;
        triangle.setP2(new Point(random.nextInt(IMAGE_WIDTH), triangle.getP2().y));
        break;
      case 8:
        prevValue = triangle.getP2().y;
        triangle.setP2(new Point(triangle.getP2().x, random.nextInt(IMAGE_HEIGHT)));
        break;
      case 9:
        prevValue = triangle.getP3().x;
        triangle.setP3(new Point(random.nextInt(IMAGE_WIDTH), triangle.getP3().y));
        break;
      case 10:
        prevValue = triangle.getP3().y;
        triangle.setP3(new Point(triangle.getP3().x, random.nextInt(IMAGE_HEIGHT)));
        break;
      default:
        break; // Should never reach here.
    }

    // Update the triangle following the mutation.
    triangle.updateTriangle();

  }

  /**
   * Undo the random mutation. 
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
        DNA.get(triangleNum).setP1(new Point(prevGeneVal, DNA.get(triangleNum).getP1().y));
        break;
      case 6:
        DNA.get(triangleNum).setP1(new Point(DNA.get(triangleNum).getP1().x, prevGeneVal));
        break;
      case 7:
        DNA.get(triangleNum).setP2(new Point(prevGeneVal, DNA.get(triangleNum).getP2().y));
        break;
      case 8:
        DNA.get(triangleNum).setP2(new Point(DNA.get(triangleNum).getP2().x, prevGeneVal));
        break;
      case 9:
        DNA.get(triangleNum).setP3(new Point(prevGeneVal, DNA.get(triangleNum).getP3().y));
        break;
      case 10:
        DNA.get(triangleNum).setP3(new Point(DNA.get(triangleNum).getP3().x, prevGeneVal));
        break;
      default:
        break; // Should never reach here.

    }
    DNA.get(triangleNum).updateTriangle(); 

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
      bestGenome = SwingFXUtils.toFXImage(imageRenderer.getBuff(), null);;
      ++improvements;
      parentFitness = childFitness;
      return true;
    }
    else
    {
      return false;
    }
  }

  public double getFit()
  {
    return parentFitness;
  }

  public Image getGenome()
  {
    return bestGenome;
  }


}
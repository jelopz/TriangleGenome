package TriangleGenome;

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
  final int IMAGE_HEIGHT;
  final int IMAGE_WIDTH;
  Image originalImage;
  ArrayList<Triangle> DNA;
  private boolean IS_HARD_MUTATE_MODE = true;
  private FitnessFunction checkFitness;
  Random random = new Random();
  boolean mutationDirection; // Let false be decreasing, and true be
                             // increasing.

  // Create Render object. In doing so all of the fields for it are
  // initialized/set up.
  private Renderer imageRenderer;
  boolean wasImprovement;
  private int geneMutationNum;
  private int triangleNum;
  int mutations;

  // Only ever values 1 through 5. Used by the GA to increase the mutation step
  // value.
  private int improvementCombo;

  private Image perspectiveImage;
  private NewMain main;

  public GA(ArrayList<Triangle> DNA, double initialFitness, Image originalImage, int IMAGE_WIDTH, int IMAGE_HEIGHT, NewMain m)
  {
    this.IMAGE_WIDTH = IMAGE_WIDTH;
    this.IMAGE_HEIGHT = IMAGE_HEIGHT;
    imageRenderer = new Renderer(IMAGE_WIDTH, IMAGE_HEIGHT);
    this.originalImage = originalImage;
    BufferedImage temp = SwingFXUtils.fromFXImage(originalImage, null);
    this.checkFitness = new FitnessFunction(temp);
    this.parentFitness = initialFitness;
    this.wasImprovement = false; // We want to start with a random gene
                                 // mutation.
    this.childFitness = 0;
    this.DNA = DNA;

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
    IS_HARD_MUTATE_MODE = type;
  }

  /**
   * Called by the ApplicationLoop class. Performs a single mutation per call.
   */
  public void Mutate()
  {
    if (IS_HARD_MUTATE_MODE)
    {
      System.out.print("Hard Mutate:  ");
      hardMutate();
    }
    else
    {
      System.out.print("Soft Mutate:  ");
      // If the last mutation was an improvement (better fitness)
      // Then do it again, otherwise do a new random gene mutation.
      if (wasImprovement)
      {
        increaseCombo();
        selectGeneFollowingImprovement();
      }
      else
      {
        improvementCombo = 1;
        selectGeneFollowingNoImprovement();
      }
      // Check if last mutation was an improvement.
      wasImprovement = checkIfMutationWasImprovement();
    }
  }

  /**
   * Used to limit the value to a max of 5.
   */
  private void increaseCombo()
  {
    if (improvementCombo < 5)
    {
      improvementCombo++;
    }
  }

  // For selecting a random gene to mutate, first pick a random number 1-10
  // for the 10 genes per triangle and then mutate the gene associated
  // with that number as such:
  // 1 Alpha
  // 2 Red
  // 3 Green
  // 4 Blue
  // 5 Vertex One X
  // 6 Vertex One Y
  // 7 Vertex Two X
  // 8 Vertex Two Y
  // 9 Vertex Three X
  // 10 Vertex Four Y
  // Total is 1/2000 probability of a single gene being selected to mutate from
  // the genome.
  private void selectGeneFollowingNoImprovement()
  {
    // First select a random triangle
    triangleNum = random.nextInt(200);
    // It is very important never to change the order or remove triangles
    // from the list because the triangleNum references a triangle at a specific
    // index, thus that triangle should always be at that index.
    Triangle triangle = DNA.get(triangleNum);

    // ImprovementCombo is ALWAYS 1 here! Only increment/decrement by 1 after
    // following no improvement
    TriangleMutation mutateTriangle = new TriangleMutation(triangle, improvementCombo);

    // Next select a random gene from the triangle.
    geneMutationNum = random.nextInt(10) + 1;
    // Pick random direction.
    if (random.nextInt(2) == 0)
    {
      mutationDirection = true;
    }
    else
    {
      mutationDirection = false;
    }
    // If the gene value is on the border (e.g can't go higher or lower)
    // then change the direction).
    switch (geneMutationNum)
    {
      case 1:
        if (triangle.getAlpha() >= 255)
        {
          mutationDirection = false;
        }
        if (triangle.getAlpha() <= 0)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateAlpha(mutationDirection);
        break;
      case 2:
        if (triangle.getRed() >= 255)
        {
          mutationDirection = false;
        }
        if (triangle.getRed() <= 0)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateRed(mutationDirection);
        break;
      case 3:
        if (triangle.getGreen() >= 255)
        {
          mutationDirection = false;
        }
        if (triangle.getGreen() <= 0)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateGreen(mutationDirection);
        break;
      case 4:

        if (triangle.getBlue() >= 230)
        {
          mutationDirection = false;
        }
        if (triangle.getBlue() <= 25)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateBlue(mutationDirection);
        break;
      case 5:
        if (triangle.getP1().x >= IMAGE_WIDTH - 1)
        {
          mutationDirection = false;
        }
        if (triangle.getP1().x <= 0)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateP1X(mutationDirection);
        break;
      case 6:
        if (triangle.getP1().y >= IMAGE_HEIGHT - 1)
        {
          mutationDirection = false;
        }
        if (triangle.getP1().y <= 0)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateP1Y(mutationDirection);
        break;
      case 7:
        if (triangle.getP2().x >= IMAGE_WIDTH - 1)
        {
          mutationDirection = false;
        }
        if (triangle.getP2().x <= 0)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateP2X(mutationDirection);
        break;
      case 8:
        if (triangle.getP2().y >= IMAGE_HEIGHT - 1)
        {
          mutationDirection = false;
        }
        if (triangle.getP2().y <= 0)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateP2Y(mutationDirection);
        break;
      case 9:
        if (triangle.getP3().x >= IMAGE_WIDTH - 1)
        {
          mutationDirection = false;
        }
        if (triangle.getP3().x <= 0)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateP3X(mutationDirection);
        break;
      case 10:
        if (triangle.getP3().y >= IMAGE_HEIGHT - 1)
        {
          mutationDirection = false;
        }
        if (triangle.getP3().y <= 0)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateP3Y(mutationDirection);
        break;
      default:
        break; // Should never reach here.
    }
    // Update the triangle following the mutation.
    triangle.updateTriangle();
  }

  private void hardMutate()
  {
    // First select a random triangle
    triangleNum = random.nextInt(200);
    // It is very important never to change the order or remove triangles
    // from the list because the triangleNum references a triangle at a specific
    // index, thus that triangle should always be at that index.
    Triangle triangle = DNA.get(triangleNum);

    int prevValue = 0; // use a single integer to hold all the values. Doesn't
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

    if (!checkIfMutationWasImprovement())
    {
      undoHardMutate(triangleNum, geneMutationNum, prevValue);
    }
  }

  private void undoHardMutate(int triangleNum, int geneNum, int prevGeneVal)
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
  }

  /**
   * If the previous mutation was successful then do it again.
   */
  private void selectGeneFollowingImprovement()
  {
    // Same triangle and same direction.
    Triangle triangle = DNA.get(triangleNum);
    TriangleMutation mutateTriangle = new TriangleMutation(triangle, improvementCombo);

    // Same mutation type.
    switch (geneMutationNum)
    {
      case 1:
        if (triangle.getAlpha() >= 255)
        {
          mutationDirection = false;
        }
        if (triangle.getAlpha() <= 0)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateAlpha(mutationDirection);
        break;
      case 2:
        if (triangle.getRed() >= 255)
        {
          mutationDirection = false;
        }
        if (triangle.getRed() <= 0)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateRed(mutationDirection);
        break;
      case 3:
        if (triangle.getGreen() >= 255)
        {
          mutationDirection = false;
        }
        if (triangle.getGreen() <= 0)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateGreen(mutationDirection);
        break;
      case 4:

        if (triangle.getBlue() >= 255)
        {
          mutationDirection = false;
        }
        if (triangle.getBlue() <= 0)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateBlue(mutationDirection);
        break;
      case 5:
        if (triangle.getP1().x >= IMAGE_WIDTH)
        {
          mutationDirection = false;
        }
        if (triangle.getP1().x <= 0)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateP1X(mutationDirection);
        break;
      case 6:
        if (triangle.getP1().y >= IMAGE_HEIGHT)
        {
          mutationDirection = false;
        }
        if (triangle.getP1().y <= 0)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateP1Y(mutationDirection);
        break;
      case 7:
        if (triangle.getP2().x >= IMAGE_WIDTH)
        {
          mutationDirection = false;
        }
        if (triangle.getP2().x <= 0)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateP2X(mutationDirection);
        break;
      case 8:
        if (triangle.getP2().y >= IMAGE_HEIGHT)
        {
          mutationDirection = false;
        }
        if (triangle.getP2().y <= 0)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateP2Y(mutationDirection);
        break;
      case 9:
        if (triangle.getP3().x >= IMAGE_WIDTH)
        {
          mutationDirection = false;
        }
        if (triangle.getP3().x <= 0)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateP3X(mutationDirection);
        break;
      case 10:
        if (triangle.getP3().y >= IMAGE_HEIGHT)
        {
          mutationDirection = false;
        }
        if (triangle.getP3().y <= 0)
        {
          mutationDirection = true;
        }
        mutateTriangle.mutateP3Y(mutationDirection);
        break;
      default:
        break; // Should never reach here.
    }
    // update the triangle following the mutation.
    triangle.updateTriangle();
  }

  /**
   * @return the value of the current mutations fitness.
   */
  private double FitnessTest()
  {
    // Pass the genome/DNA to the renderer class to render the triangles
    // off-screen
    // to an eventual buffered image using OpenGL/JOGL.
    imageRenderer.render(DNA);
    // Convert buffered Image to an FX image.
    perspectiveImage = SwingFXUtils.toFXImage(imageRenderer.getBuff(), null);
    // Check new fitness.
    checkFitness.calculateFitness(imageRenderer.getBuff());
    return checkFitness.getFitness();
  }

  /**
   * 
   * @return If the last mutation was an improvement (e.g the childs fitness is
   *         better than the parents.)
   */
  private boolean checkIfMutationWasImprovement()
  {
    childFitness = FitnessTest();
    if (childFitness > parentFitness)
    {
      main.updateInfo(perspectiveImage, childFitness); // update main with new
                                                       // best image and fitness

      System.out.println("Improvement from " + parentFitness + ", new best fitness: " + childFitness);
      parentFitness = childFitness;
      return true;
    }
    else
    {
      System.out.println("Not Improvement from " + parentFitness);
      undo();
      return false;
    }
  }

  /**
   * Undo the last mutation following no improvement in the fitness.
   */
  private void undo()
  {
    Triangle triangle = DNA.get(triangleNum);
    TriangleMutation mutateTriangle = new TriangleMutation(triangle, improvementCombo);
    // Toggle direction other way.
    if (mutationDirection)
    {
      mutationDirection = false;
    }
    if (!mutationDirection)
    {
      mutationDirection = true;
    }
    switch (geneMutationNum)
    {
      case 1:
        mutateTriangle.mutateAlpha(mutationDirection);
        break;
      case 2:
        mutateTriangle.mutateRed(mutationDirection);
        break;
      case 3:
        mutateTriangle.mutateGreen(mutationDirection);
        break;
      case 4:
        mutateTriangle.mutateBlue(mutationDirection);
        break;
      case 5:
        mutateTriangle.mutateP1X(mutationDirection);
        break;
      case 6:
        mutateTriangle.mutateP1Y(mutationDirection);
        break;
      case 7:
        mutateTriangle.mutateP2X(mutationDirection);
        break;
      case 8:
        mutateTriangle.mutateP2Y(mutationDirection);
        break;
      case 9:
        mutateTriangle.mutateP3X(mutationDirection);
        break;
      case 10:
        mutateTriangle.mutateP3Y(mutationDirection);
        break;
      default:
        break; // Should never reach here.
    }
    // Update the triangle following the mutation.
    triangle.updateTriangle();
  }

  /**
   * Create a random initial population for testing.
   */
  private void randomPop()
  {
    for (Triangle triangle : DNA)
    {
      triangle.setAlpha(random.nextInt(255));
      triangle.setRed(random.nextInt(255));
      triangle.setBlue(random.nextInt(255));
      triangle.setGreen(random.nextInt(255));
      triangle.setP1(new Point(random.nextInt(IMAGE_WIDTH), random.nextInt(IMAGE_HEIGHT)));
      triangle.setP2(new Point(random.nextInt(IMAGE_WIDTH), random.nextInt(IMAGE_HEIGHT)));
      triangle.setP3(new Point(random.nextInt(IMAGE_WIDTH), random.nextInt(IMAGE_HEIGHT)));
      triangle.updateTriangle();
    }

  }
}
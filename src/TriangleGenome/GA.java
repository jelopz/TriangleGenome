package TriangleGenome;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.text.Text;

import java.awt.image.VolatileImage;

/**
 * 
 * Class to handle the flow of the Genetic Algorithm/Hill climbing.
 *
 * Possible mutations (changing one of the 10 genes of a triangle) aka pure hill
 * climbing, but through explotation of patters we should be able to have
 * mutations of whole verticies (x,y), colors (a,r,g,b), size, order etc.
 * 
 * 3/24/2016: Now, when the GA runs in its current state, after 100 mutations, the image
 * that is shown at the end will PROBABLY have a higher fitness than the
 * initial. Since it's the last mutation done, it's impossible to be sure if it
 * was actually an improvement from our most fit iteration, but there likely
 * will have been at least a few improvements up to that point. Still not a
 * solid GA, but we're at least hill climbing (slowly).
 * 
 * Another problem is the time taken per generation which make its difficult to
 * test as it takes a while to run a decent number of generations. There are two
 * main things that take time during each generation. The first is rendering the
 * triangles to an image (I am currently working on a way to increase the speed
 * via openGL) and the second is the fitness function (which from what I'v heard
 * we will probably have to use something called openCL to increase its speed).
 */
public class GA extends Stage
{

  private Group root;
  private BorderPane bp;
  double parentFitness;
  double childFitness;
  final int IMAGE_HEIGHT;
  final int IMAGE_WIDTH;
  Image originalImage;
  ArrayList<Triangle> DNA;
  private FitnessFunction checkFitness;
  Random random = new Random();
  boolean mutationDirection; // Let false but decreasing, and true be
                             // increasing.
  boolean wasImprovement;
  private BufferedImage writableImage;
  private int geneMutationNum;
  private int triangleNum;
  int mutations;

  GA(ArrayList<Triangle> DNA, double initialFitness, Image originalImage, int IMAGE_WIDTH, int IMAGE_HEIGHT)
  {
    this.IMAGE_WIDTH = IMAGE_WIDTH;
    this.IMAGE_HEIGHT = IMAGE_HEIGHT;
    this.originalImage = originalImage;
    BufferedImage temp = SwingFXUtils.fromFXImage(originalImage, null);
    this.checkFitness = new FitnessFunction(temp);
    this.parentFitness = initialFitness;
    this.wasImprovement = false; // We want to start with a random gene
                                 // mutation.

    // Make sure the BufferedImage type includes alpha since we have to take
    // account for it,
    // ARGB is alpha, red, green, blue.
    this.writableImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    this.childFitness = 0;
    this.DNA = DNA;
    // Start the GA.
    startGA();
  }

  private void startGA()
  {
    bp = new BorderPane();
    root = new Group();
    root.getChildren().add(bp);
    Scene scene = new Scene(root);
    this.setScene(scene);

    // If you want to see how the program works with a random initial
    // population you can click this. Note it is much slower than the
    // other initial population because of overlap and more lower alpha
    // values (which requires much more computation for some reason).
    // The rendering speed is something that is going to have to be
    // improved, I think I have found a way (with openGL) but I still
    // need to make sure it works before pushing it.
    // randomPop();

    // This for loop is for the number of mutations. Since currently
    // it only shows the image after all mutations have finished we
    // can't put it in an infinite loop. Note the larger the number the longer
    // it takes especially if you start with a random population. Also you will
    // notice not much changes during the mutation because the step size is
    // very small for the mutations (size of 1), and currently mutations
    // that result in a lower fitness are ignored.
    for (int i = 0; i < 100; i++)
    {
      ++mutations;
      Mutate();
    }
  }

  private void Mutate()
  {
    // If the last mutation was an improvement (better fitness)
    // Then do it again, otherwise do a new random gene mutation.
    if (wasImprovement)
    {
      selectGeneFollowingImprovement();
    }
    else
    {
      selectGeneFollowingNoImprovement();
    }
    // Check if last mutation was an improvement.
    wasImprovement = checkIfMutationWasImprovement();
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
    TriangleMutation mutateTriangle = new TriangleMutation(triangle);
    // Next select a random gene from the triangle.
    int geneMutationNum = random.nextInt(10);
    // Pick random direction.
    if (random.nextInt(1) == 0)
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

  /**
   * If the previous mutation was successful then do it again.
   */
  private void selectGeneFollowingImprovement()
  {
    // Same triangle and same direction.
    Triangle triangle = DNA.get(triangleNum);
    TriangleMutation mutateTriangle = new TriangleMutation(triangle);

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

  private double FitnessTest()
  {
    Graphics2D genome = writableImage.createGraphics();

    // This might not effect anything or it might
    // have a big effect, what ever we set the background color
    // to e.g black or white it will play as the base of the image.
    genome.setColor(java.awt.Color.BLACK);
    genome.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
    // Draw each triangle.
    for (Triangle triangle : DNA)
    {
      genome.setColor(triangle.getColor());
      genome.fillPolygon(triangle.getTriangle());
    }

    // Convert buffered Image to an FX image.
    Image perspectiveImage = SwingFXUtils.toFXImage(writableImage, null);

    // Check new fitness.
    checkFitness.calculateFitness(writableImage);
    // Set the image, and fitness to a screen.
    bp.setTop(new ImageView(perspectiveImage));
    bp.setBottom(new Text("Fitness: " + checkFitness.getFitness()));
    return checkFitness.getFitness();
  }

  /**
   * There is a major bug that is illustrated in this method that is (probably)
   * the main reason the GA doesn't work correctly. Although a fitness is drawn
   * to screen, the two fitness results for the parent and child being compared
   * here are both equal to 0 in every instance. Thus, there is never an
   * improvement as 0 is not greater than 0, and so every single mutation is
   * undone.
   * 
   * Since the fitness numbers ARE drawn to the windows though, the issue should
   * probably be a simple fix.
   * 
   * @return If the last mutation was an improvement (e.g the childs fitness is
   *         better than the parents.)
   */
  private boolean checkIfMutationWasImprovement()
  {
    childFitness = FitnessTest();

    if (childFitness > parentFitness)
    {
      System.out.println("Improvement");
      parentFitness = childFitness;
      return true;
    }
    else
    {
      System.out.println("Not Improvement");
      undo();
      parentFitness = childFitness;
      return false;
    }
  }

  /**
   * Undo the last mutation following no improvement in the fitness.
   */
  private void undo()
  {
    Triangle triangle = DNA.get(triangleNum);
    TriangleMutation mutateTriangle = new TriangleMutation(triangle);
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

package TriangleGenome;

import java.awt.Color;

//import com.jogamp.opengl.DefaultGLCapabilitiesChooser;
//import com.jogamp.opengl.GL;
//import com.jogamp.opengl.GL2;
//import com.jogamp.opengl.GLAutoDrawable;
//import com.jogamp.opengl.GLCapabilities;
//import com.jogamp.opengl.GLDrawableFactory;
//import com.jogamp.opengl.GLProfile;
//import com.jogamp.opengl.util.GLReadBufferUtil;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
/**
 * 
 * @author Christian Seely
 * @author Jesus Lopez
 *
 * This class takes the a potential solution (the genome/DNA) and
 * renders it to a volatile image, and then returns a snap shot of it
 * (which converts it to a buffered images). Note the performance
 * of the program is VERY dependent on the amount of VRAM you have
 * as this is where volatile images are stored which can be directly
 * accessed by the GPU (making it much faster). To use this class
 * you need to create a single renderer object (to initialize field
 * and to create a SINGLE volatile image.) Next all you have to do 
 * is call the renderer method with the DNA to render it to a buffered
 * image. Note each thread/tribe has its own renderer object and in turn
 * a single image it is writing to. 
 */

public class Renderer
{
  public int IMAGE_WIDTH;
  public int IMAGE_HEIGHT;
  private int[] xPoints;
  private int[] yPoints;
  private int numPoints;
  private Color backGroundColor;
  GraphicsEnvironment ge;
  GraphicsConfiguration gc;
  private BufferedImage image;
  private VolatileImage vImage;

  /**
   * 
   * @param ArrayList<Triangle> DNA/Genome a combination of 200 triangle. 
   * This class renders the DNA to a volatile image and then
   * a buffered image.
   */
  public void render(ArrayList<Triangle> DNA)
  {
   
    // Create volatile image based off the systems
    // environment/configuration.

    do
    {
      Graphics2D genome = vImage.createGraphics();
      genome.setColor(backGroundColor);
      genome.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
      // Draw each triangle.
      for (Triangle triangle : DNA)
      {
        //Fill the polygon using the coordinates of the triangles
        //verticies. 
        genome.setColor(triangle.getColor());
        xPoints[0] = triangle.getP1x();
        xPoints[1] = triangle.getP2x();
        xPoints[2] = triangle.getP3x();
        yPoints[0] = triangle.getP1y();
        yPoints[1] = triangle.getP2y();
        yPoints[2] = triangle.getP3y();
        genome.fillPolygon(xPoints, yPoints, numPoints);
        //If the image is lost and the volatile image is incompatible then
        //create new one. 
        if (vImage.contentsLost())
        {
          if (vImage.validate(gc) == VolatileImage.IMAGE_INCOMPATIBLE)
          {
            vImage = gc.createCompatibleVolatileImage(IMAGE_WIDTH, IMAGE_HEIGHT,
                Transparency.TRANSLUCENT);
          }
        }
      }
      image = vImage.getSnapshot();
    //Keep looping until the contents are renderer and not lost.
    } while (vImage.contentsLost());

  }

  /**
   * 
   * @param int IMAGE_WIDTH - Width of image. 
   * @param int IMAGE_HEIGHT- Heigh of image. 
   * @param Color backGroundColor - Background color of the image. 
   * This class initializes all values once in the creation of the object
   * so all you have to do to render something is call the renderer method. 
   */
  public Renderer(int IMAGE_WIDTH, int IMAGE_HEIGHT, Color backGroundColor)
  {
    this.IMAGE_WIDTH = IMAGE_WIDTH;
    this.IMAGE_HEIGHT = IMAGE_HEIGHT;
    this.backGroundColor = backGroundColor;
    this.numPoints = 3;
    this.xPoints = new int[3];
    this.yPoints = new int[3];
    ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
    //Create the buffered and volatile images to be rendered on. 
    image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT,
        BufferedImage.TYPE_INT_RGB);
    vImage = gc.createCompatibleVolatileImage(IMAGE_WIDTH, IMAGE_HEIGHT,
        Transparency.TRANSLUCENT);
  }
  /**
   * 
   * @param Color backGroundColor - Set Background color.
   * 
   */
  public void setBackGroundColor(Color backGroundColor)
  {
    this.backGroundColor = backGroundColor;
  }

  /**
   * 
   * @return The genome/DNA rendered to a buffered Image.
   */
  public BufferedImage getBuff()
  {
    return image;
  }
}

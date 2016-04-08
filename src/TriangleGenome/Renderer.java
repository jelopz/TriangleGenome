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
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import TriangleGenome.Triangle;
import java.awt.image.VolatileImage;
/**
 * 
 * Class using OpenGL/JOGL to take a genome and render the genomes
 * triangles off-screen to a buffer which gets converted into
 * a buffered image. 
 *
 *
 *	The reason everything is commented out related to OpenGL/JOGL is
 * Unfortunately rendering graphics in this manor ended up to be a
 * really large problem as soon as I started testing things on multiple
 * threads. The thing is only one thread can access/possess a GL context
 * at a time. This makes it impossible to have two threads rendering at the
 * same exact time. At first I tried having the threads wait for each other
 * and have the application thread (holds GL context) render everything
 * but that got too ugly. So I decided to scrap it (it is still here in case
 * we want to use it for initial population fitness which is generated
 * prior to the threads). With the OpenGL/JOGL on my 4 core computer I was
 * getting 55 generations a second. After removing it, it dropped to 
 * 11 generations a second using java's awt graphics (which is always slow for
 * things like this) to draw the polygons to the buffered image. Luckily I found
 * something that will hopefully work for the whole program (it currently
 * works now with a couple threads I am not sure how many will do). Instead
 * of using java's awt graphics to draw to a buffered image I draw to a 
 * volatile image and then get a snapshot of it (as buffered image) and return it
 * This increased the generations to around 35 a second. Using a volatile image
 * is actually still utilizing the GPU because it saves it in VRAM instead of
 * RAM so the GPU has quicker access. 
 * NOTE: A volatile image can disappear at any time so we need to be make
 * sure its handled correctly. (I think the way I set it up should deal with
 * that problem though). 
 * Also note my computer has ~1 GB VRAM dedicated and ~3.8 GB VRAM possible. 
 */
public class Renderer {
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
	private ArrayList<Triangle> DNA;

	public void render(ArrayList<Triangle> DNA) {
		this.DNA = DNA;
		// Create volatile image based off the systems
		// environment/configuration.
	
		do {
			Graphics2D genome = vImage.createGraphics();
			genome.setColor(backGroundColor);
			genome.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
			// Draw each triangle.
			for (Triangle triangle : DNA) {
				genome.setColor(triangle.getColor());
				xPoints[0] = triangle.getP1x();
				xPoints[1] = triangle.getP2x();
				xPoints[2] = triangle.getP3x();
				yPoints[0] = triangle.getP1y();
				yPoints[1] = triangle.getP2y();
				yPoints[2] = triangle.getP3y();
				genome.fillPolygon(xPoints,yPoints,numPoints);
				if (vImage.contentsLost()) {
					System.out.println("LOST CONTENTS");
					 if (vImage.validate(gc) ==
				              VolatileImage.IMAGE_INCOMPATIBLE)
				          {
				             System.out.println("Incompatible, must recreate new Vimage.");
						   vImage = gc.createCompatibleVolatileImage(IMAGE_WIDTH, IMAGE_HEIGHT, Transparency.TRANSLUCENT);
				          }
				}
			}
			image = vImage.getSnapshot();

		} while (vImage.contentsLost());

    }
    //This method is only called once so all the triangles are constatly
    //being drawn to one signel bufferedImage and then cleared? 
    public Renderer(int IMAGE_WIDTH, int IMAGE_HEIGHT, Color backGroundColor) {
    	this.IMAGE_WIDTH = IMAGE_WIDTH;
    	this.IMAGE_HEIGHT = IMAGE_HEIGHT;
    	this.backGroundColor = backGroundColor;
    	this.numPoints = 3;
    	this.xPoints = new int[3];
    	this.yPoints = new int[3];
         ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
         gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
         image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
     	 vImage = gc.createCompatibleVolatileImage(IMAGE_WIDTH, IMAGE_HEIGHT, Transparency.TRANSLUCENT);
    }



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

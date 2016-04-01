package TriangleGenome;

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
    GraphicsEnvironment ge;
	GraphicsConfiguration gc;
	// private final GLProfile glp;
	//// private final GLDrawableFactory factory;
	// private final GLAutoDrawable drawable;
	/// private final GL2 gl;
	// private final GLReadBufferUtil bufferUtil;
	// private final BufferedImage image;
	// private final BufferedImage prev;
	private BufferedImage image;
	// private final int[] imageArr;
	// private final IntBuffer imageBuf;
	private VolatileImage vImage;
	private ArrayList<Triangle> DNA;

	public void render(ArrayList<Triangle> DNA) {
		this.DNA = DNA;
		// Create volatile image based off the systems
		// environment/configuration.
		vImage = gc.createCompatibleVolatileImage(IMAGE_WIDTH, IMAGE_HEIGHT, Transparency.TRANSLUCENT);
		do {
			Graphics2D genome = vImage.createGraphics();
			genome.setColor(java.awt.Color.BLACK);
			genome.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
			// Draw each triangle.
			for (Triangle triangle : DNA) {
				genome.setColor(triangle.getColor());
				genome.fillPolygon(triangle.getTriangle());
				if (vImage.contentsLost()) {
					System.out.println("LOST CONTENTS");
				}
			}
			image = vImage.getSnapshot();

		} while (vImage.contentsLost());
    	
    	
//		//Set up bounded region to render triangles to. 
//    	//Select the GL_PROJECTION mode so we can do our
////    	//orthogonal projection 
//		gl.glMatrixMode(GL2.GL_PROJECTION);
//		//Load the identity matrix.
//		gl.glLoadIdentity();
//		//Use glOrtho method to define the coordinate system of the
//		//bounded region through a orthographic projection (parallel projection)
//		//by multiplying the current matrix with the matrix defined by the 
//		//parameters of the glOrtho function. 
//		gl.glOrtho(0, IMAGE_WIDTH, 0, IMAGE_HEIGHT, -1, 1);
//		//Select the GL_MODELVIEW mode so we can set up the view port. 
//		gl.glMatrixMode(GL2.GL_MODELVIEW);
//		//Load the identity matrix.
//		gl.glLoadIdentity();
//		//Set up the view port (region bounds triangles will be rendered to)
//		gl.glViewport(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
//		
//		//Set up blending to support transparency. 
//		gl.glEnable(GL.GL_BLEND);
//		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
//		//Disable writing to a depth buffer (as this is a 2D image so
//		//we do not want to extend in the z direction). 
//		gl.glDepthMask(false);
//		//Enable edges to be anti-aliased.
//		gl.glEnable(GL.GL_LINE_SMOOTH);
//		//Set the background color for when glClear is called. 
//		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//		//Call method to render the triangles. 
//		renderTriangles();
//		//Get the pixels into a bufferUtil from the triangles
//		//that have been renderd off screen.
//		bufferUtil.readPixels(gl, false);
//		//Create intBuffer for the pixels. 
//		final IntBuffer pixelBuf = ((ByteBuffer) bufferUtil.getPixelBuffer().buffer).asIntBuffer();
//		//Add the new pixels into the image buffer (note this also changes
//		//the values in the imageArr as the buffer is wrapped with it. 
//		imageBuf.put(pixelBuf);
//		//Rewind the buffer back to position zero. 
//		imageBuf.rewind();
    }
    //This method is only called once so all the triangles are constatly
    //being drawn to one signel bufferedImage and then cleared? 
    public Renderer(int IMAGE_WIDTH, int IMAGE_HEIGHT) {
    	this.IMAGE_WIDTH = IMAGE_WIDTH;
    	this.IMAGE_HEIGHT = IMAGE_HEIGHT;
         ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
         gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
    	//Define the capabilities a rendering context should support.
//        glp = GLProfile.getDefault();
//        caps = new GLCapabilities(glp);
//        caps.setHardwareAccelerated(true);
//        caps.setDoubleBuffered(false);
//        caps.setAlphaBits(0);
//        caps.setRedBits(8);
//        caps.setBlueBits(8);
//        caps.setGreenBits(8);
//        caps.setOnscreen(false);
//        //Given the defined capabilities create a rendering context to render
//        //things off-screen in a bounding box defined by the IMAGE_WIDTH
//        //and IMAGE_HEIGHT.
//        factory = GLDrawableFactory.getFactory(glp);
//        drawable = factory.createOffscreenAutoDrawable(factory.getDefaultDevice(), caps, new DefaultGLCapabilitiesChooser(), IMAGE_WIDTH, IMAGE_HEIGHT);
//        //Trigger OpenGL rendering. 
//        drawable.display();
//        //This might be an issue when multi-threading this prevents multiple
//        //threads from using this method call at once I think. 
//        drawable.getContext().makeCurrent();
//        gl = drawable.getGL().getGL2();
//        //Create buffer utility that supports alpha channel. 
//        bufferUtil = new GLReadBufferUtil(true, false);
//        //Create buffered image. 
//        image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
       
        //Get an integer array with each entry representing a pixel. 
       // imageArr = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        //Wrap the array in an IntBuffer so that when ever a change is made to
        //the the buffer the change will be made to the int array (this is done
        //in the render method every time it's called). 
        //imageBuf = IntBuffer.wrap(imageArr);
    }
    /**
     * Render the triangles from the Genome/DNA
     */
//	public void renderTriangles() {
//		//Set background color in the area defined by the view port. 
//		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
//		//Define what shape we will be rendering. 
//		gl.glBegin(GL.GL_TRIANGLES);
//		//Loop through the 200 triangles and render them. 
//		for (int i = 0; i < 200; i++) {
//			Triangle triangle = DNA.get(i);
//			//For some reason the blue and red channels are flipped I am not 
//			//sure why. 
//			gl.glColor4f(triangle.getBlue()/255f, triangle.getGreen()/255f, triangle.getRed()/255f, triangle.getAlpha()/255f);
//			gl.glVertex2i(triangle.getP1().x, triangle.getP1().y);
//			gl.glVertex2i(triangle.getP2().x, triangle.getP2().y);
//			gl.glVertex2i(triangle.getP3().x, triangle.getP3().y);
//		}
//		gl.glEnd();
//	}

	/**
	 * 
	 * @return The genome/DNA rendered to a buffered Image. 
	 */
    public BufferedImage getBuff()
    {
    	return image;
    }
}

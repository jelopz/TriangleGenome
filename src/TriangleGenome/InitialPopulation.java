package TriangleGenome;

import java.awt.Point;
import java.awt.TextField;
import java.util.ArrayList;

import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.Group;
import javafx.scene.image.WritableImage;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 * @author Christian Seely
 * This is a class for creating the initial population.
 * Please note this is just a possibility for the initial
 * population. 
 * Methodology for this potential initial population:
 * The picture is divided into a 10x10 grid consisting of 100 rectangular
 * regions. Next each rectangular region is split along the main diagonal 
 * to create 200 triangles. Each triangle color in the new image is the
 * average RGB color of all the pixels contained in the a triangle at the same
 * position on the original image. 
 */
public class InitialPopulation extends Stage{

	Image image;
    private Group root;
    private BorderPane bp;
    private ImageView originalImage;
    private ImageView perspectiveImage;
    private WritableImage writableImage;
    private double initialFitness;
	int IMAGE_WIDTH;
	int IMAGE_HEIGHT;
	//For this initial population all triangles have the
	//same height/width
	int triangleWidth;
	int triangleHeight;
	//Due to the fact that it is very unlikely to have a picture consisting
	//of a pixel height/width both divisible by 10 its most likely that
	//there will be extra pixels, to fix this some of the triangles will
	//just be a pixel larger in width/height.
	double leftOverPixelsWidth;
	double leftOverPixelsHeight;
	private PixelWriter pixelWriter;
    private PixelReader reader;
    //Just something to hold to triangles so they can be kept track of. 
    private ArrayList<Triangle> listOfTriangles = new ArrayList<>();

    /**
     * Initialize fields and trigger the calculation and display of 
     * the initial population. 
     * @param Start Image.
     */
	InitialPopulation(Image image)
	{
		this.image = image;
		//The images should have the same height/width.
		this.IMAGE_HEIGHT =(int)image.getHeight();
		this.IMAGE_WIDTH = (int)image.getWidth();
		//Create a writeable image of the same size as the original image.
		this.writableImage = new WritableImage(IMAGE_WIDTH, IMAGE_HEIGHT);
		this.pixelWriter = writableImage.getPixelWriter();
		this.reader = image.getPixelReader();  
		//Using grid method we know there will be 10 rows. 
		triangleWidth = IMAGE_WIDTH/10;
		//Calculate left over pixels in the width. 
		leftOverPixelsWidth = IMAGE_WIDTH- triangleWidth*10;
		//Using grid method we know there will be 10 columns. 
		triangleHeight = IMAGE_HEIGHT/10;
		//Calculate left over pixels in the height. 
		leftOverPixelsHeight = IMAGE_HEIGHT-triangleHeight*10;
		
		originalImage = new ImageView(image);
		//Calculate the initial population 
		CalculateInitialPopulation(image);
		
		//Grab the new image consisting of the initial population.
		perspectiveImage = createNewPerspectiveImage();

		bp = new BorderPane();
		//Right side of border pane is the original image. 
		bp.setRight(originalImage);
		//Left side of border pane is the initial population image. 
		bp.setLeft(perspectiveImage);
		root = new Group();
		root.getChildren().add(bp);
		Scene scene  = new Scene(root);
		this.setScene(scene);
		
	}

	/**
	 * Method to calculate the initial population. 
	 * @param Start Image
	 */
	private void CalculateInitialPopulation(Image originalImage)
	{
		Triangle triangle1;
		Triangle triangle2;
		//It seems all the pictures only have extra height pixels
		//if they end up having any extra pixels. 
		int offset=0;
		//Iterate through grid creating two triangle objects at a time
		//(The two triangles you would see when splitting a rectangle down
		//the diagonal). 
		for(int i = 0; i < 10; i++)
		{
			for(int j = 0; j < 10; j++)
			{
				//Add extra pixels to the last row, if we want we could
				//more evenly distribute them over the rows but I'm not sure
				//that will make much of a difference. 
				if(i==9&&leftOverPixelsHeight>0)
				{
					offset=(int)leftOverPixelsHeight;
				}
					
				//Calculate the vertices of each new triangle and set them to their
				//respective triangle objects, the triangles are created in pairs of two
				//because we are iterating over the squares of the board and each square
				//consists of two triangles. 
				triangle1 = new Triangle();
				triangle1.setP1(new Point(j*triangleWidth,i*triangleHeight));
				triangle1.setP2(new Point(j*triangleWidth,(i+1)*triangleHeight+offset));
				triangle1.setP3(new Point((j+1)*triangleWidth,(i+1)*triangleHeight+offset));
				triangle1.setAlpha(255);//Set to opaque 
				triangle2 = new Triangle();
				triangle2.setP1(new Point(j*triangleWidth,i*triangleHeight));
				triangle2.setP2(new Point((j+1)*triangleWidth,i*triangleHeight));
				triangle2.setP3(new Point((j+1)*triangleWidth,(i+1)*triangleHeight+offset));
				triangle2.setAlpha(255);//Set to opaque 
				//Update the triangles with the new data that was set. 
				triangle1.updateTriangle();
				triangle2.updateTriangle();
				//Get the start x/y location for the triangles for calculating
				//their average RGB. 
				int startX = j*triangleWidth;
				int startY = i*triangleHeight;
				//Calculate the average RGB of pixels contained in each of the triangles
				//and set that average RGB color as the color of the triangle. 				
				calculateAverageRGB(triangle1,triangleWidth,triangleHeight+offset,startX,startY);
				calculateAverageRGB(triangle2,triangleWidth,triangleHeight+offset,startX,startY);

				//Add the two triangle objects to the list of triangles. 
				listOfTriangles.add(triangle1);
				listOfTriangles.add(triangle2);

			}
		}
	}
    
	/**
	 * @param Triangle object, with height and width of the triangle (which is also
	 * the height and width of the bounding box), and the start x/y. 
	 * 
	 * Method for iteration of triangles is using bounding rectangular boxes and checking if	 
	 * each pixel in the bounding box, if its inside the triangle then the RGB values
	 * of the pixel are used for calculation. Another method that could be done which 
	 * we might want to look into later is triangle rasterization which allows for accessing
	 * all pixels in a bounding area defined by 3 vertices (this method is usually used for
	 * drawling triangles pixel by pixel or line by line).*/
	private void calculateAverageRGB(Triangle triangle, int width,int height, int startX,int startY)
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
		//Iterate through the bounding box, if the pixel is in the triangle
		//then increment the pixel count and the counts for red, blue and green.
		for(int i = startX; i < boundingBoxWidth+startX; i++)
		{
			for(int j = startY; j < boundingBoxHeight+startY; j++)
			{
				if(triangle.getTriangle().contains(i,j)) 
				{
					++pixelCount;
					//Note the getRed/blue/green methods return a value
					//from 0.0 to 1.0 not 0 to 255. 
					redCount += reader.getColor(i, j).getRed();
					greenCount += reader.getColor(i, j).getGreen();
					blueCount += reader.getColor(i, j).getBlue();
				}
			}
		}
		
		//Average the average RGB value for the triangular region and set
		//that as the RGB value of the new triangle. 
		newRedValue = (int)((redCount/pixelCount)*(255));
		newGreenValue = (int)((greenCount/pixelCount)*(255));
		newBlueValue = (int)((blueCount/pixelCount)*(255));
		triangle.setRed(newRedValue);
		triangle.setGreen(newGreenValue);
		triangle.setBlue(newBlueValue);
		//Update the triangle with its new color values. 
		triangle.updateTriangle();
		//Actually draw the triangle to the initialPopulation image.
		Color color = triangle.getColor();
		for(int i = startX; i < boundingBoxWidth+startX; i++)
		{
			for(int j = startY; j < boundingBoxHeight+startY; j++)
			{
				if(triangle.getTriangle().contains(i,j)) 
				{
				      
		                pixelWriter.setColor(i, j, color);
				}
			}
		}
		
	}
	

	/**
	 * 
	 * @return ImageView of the initialPopulation. 
	 */
	private ImageView createNewPerspectiveImage()
	{
		return new ImageView(writableImage);
	}

	
}
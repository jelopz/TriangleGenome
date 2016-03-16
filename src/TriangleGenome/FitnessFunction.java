package TriangleGenome;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

/**
 * 
 * @author Christian Seely
 * 
 * Brute force fitness function of comparing two
 * the difference between two images pixel by pixel.
 * Performance decreases as pixel size increases,
 * maybe we could compress all photos to say 150 by 150 pixels
 * for calculation without hindering the accuracy of the calculation.
 * 
 * Also there is other means of optimization that we can and probably
 * should look into to. 
 * 
 * Also another note based some people I have talked to we might want to
 * (it might even be required) to do the image rendering/display through 
 * the GPU, supposedly it's pretty hard to do right) If/when we go down
 * that path we should probably look at: JogAmp JOCL and OpenCL
 *
 */
public class FitnessFunction {

	private Image originalImage;
	private Image perspectiveImage;
	private final double IMAGE_HEIGHT;
	private final double IMAGE_WIDTH;
	private PixelReader readerOriginal;
	private PixelReader readerPerspective;
	double fitness; //(Rate between 0 and 100% where 100 is the most fit
	//e.g perfect match.) We will probably want to change the scale because
	//its not really possible to get 100% fitness under these conditions.
	//as the photos will never be exactly alike we could normalize it to make
	//it more realistic and have a 75% percent fitness be considered our 100%. 
	
	/**
	 * 
	 * @param Original Image, Perspective Image. 
	 */
	FitnessFunction(Image originalImage,Image perspectiveImage)
	{
		this.originalImage = originalImage;
		this.readerOriginal = originalImage.getPixelReader();
		this.perspectiveImage = perspectiveImage;
		this.readerPerspective = perspectiveImage.getPixelReader();
		//The images should have the same dimensions. 
		this.IMAGE_HEIGHT = originalImage.getHeight();
		this.IMAGE_WIDTH = originalImage.getWidth();
		calculateFitness();
	}
	
	/**
	 * Function to calculate the fitness between the two images. 
	 */
	private void calculateFitness()
	{

		double blueCount = 0;
		double redCount = 0;
		double greenCount = 0;
		double error = 0;
		double percentError = 0;
		//Iterate through the images (they have the same dimensions), and 
		//increment the error based off the difference between each pixels
		//RGB values. 
		for(int i = 0; i < IMAGE_WIDTH; i++)
		{
			for(int j = 0; j < IMAGE_HEIGHT; j++)
			{		
				error += getPixelColorDiff(readerOriginal.getColor(i, j),readerPerspective.getColor(i, j));			
			}
		}
		//Multiple by 100 since we want a percentage, and divide by the number
		//of pixels times 3 (since each pixel has three parameters RGB)
		percentError = (error*100.0)/(IMAGE_WIDTH*IMAGE_HEIGHT*3.0);
		//Flip the percentage e.g before 0% was most fit we want that to be 100%.
		percentError = 100.0-percentError;
		this.fitness = percentError;

	}
	/**
	 * 
	 * @param Color object of pixel from original image. 
	 * @param Color object of pixel from perspective image. 
	 * @return The sum off the differential in the red, blue and 
	 * green value of the two pixels. 
	 */
	private double getPixelColorDiff(Color original, Color perspective)
	{
		
		double red = Math.abs(original.getRed() - perspective.getRed());
		double green = Math.abs(original.getGreen() - perspective.getGreen());
		double blue = Math.abs(original.getBlue() - perspective.getBlue());
		return ((red+green+blue));
	}
	/**
	 * @return The fitness. 
	 */
	public double getFitness()
	{
		return this.fitness;
	}
	
	
}

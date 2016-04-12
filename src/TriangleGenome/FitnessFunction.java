package TriangleGenome;

import java.awt.image.BufferedImage;

/**
 * 
 * @author Christian Seely
 * @author Jesus Lopez 
 * Brute force fitness function of comparing two
 * the difference between two images pixel by pixel.
 *
 */
public class FitnessFunction {

	private BufferedImage originalImage;
	private int IMAGE_HEIGHT;
	private int IMAGE_WIDTH;
	double fitness; 
	BufferedImage test;
	

	/**
	 * 
	 * @param Original Image, Perspective Image. 
	 */
	FitnessFunction(BufferedImage originalImage)
	{
		this.originalImage = originalImage;
	}
	/**
	 * Function to calculate the fitness between the two images. 
	 */
	public void calculateFitness(BufferedImage perspectiveImage)
	{

		//The images dimensions should be the same for both photos. 
		this.IMAGE_HEIGHT = perspectiveImage.getHeight();
		this.IMAGE_WIDTH = perspectiveImage.getWidth();
		int error = 0;
		double percentError = 0;
		//Iterate through the images (they have the same dimensions), and
		//get integer representing the color channels at each pixel location.
		//The buffered Image also has getRed() etc to get the color values 
		//if you get the color from the getRGB method. To optimize it slightly
		//those method calls are eliminated by using some bit manipulation
		//to get the rgb values (this is actually the exact same method that 
		//the getRed() and other methods use to get the value). 
		for(int y = 0; y < IMAGE_HEIGHT; y++)
		{
			for(int x = 0; x < IMAGE_WIDTH; x++)
			{	
				
				int rgb1 = originalImage.getRGB(x, y);
				int red1 = (rgb1 >> 16) & 0x000000FF;
				int green1 = (rgb1 >> 8 ) & 0x000000FF;
				int blue1 = (rgb1) & 0x000000FF;
				
				int rgb2 = perspectiveImage.getRGB(x, y);
				int red2 = (rgb2 >> 16) & 0x000000FF;
				int green2 = (rgb2 >> 8 ) & 0x000000FF;
				int blue2 = (rgb2) & 0x000000FF;

				//For each pair of pixels find the differential between
				//each of the color channels and add it to the total error. 
			    error += ((Math.abs(red1-red2)+Math.abs(green1-green2)+Math.abs(blue1-blue2)));
			}
		}
	
		//Multiple by 100 since we want a percentage, and divide by the
		//dimension of the image and a value of 765 (all possibilities of the
		//three color channels 255*3).
		percentError = (error*100.0)/(IMAGE_WIDTH*IMAGE_HEIGHT*765.0);
		//Flip the percentage e.g before 0% was most fit we want that to be 100%.
		percentError = 100.0-percentError;
		this.fitness = percentError;

	}

	/**
	 * @return The fitness. 
	 */
	public double getFitness()
	{
		return this.fitness;
	}

	
	
}

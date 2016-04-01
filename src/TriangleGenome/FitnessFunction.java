package TriangleGenome;

import java.awt.image.BufferedImage;

/**
 * 
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

	private BufferedImage originalImage;
	private int IMAGE_HEIGHT;
	private int IMAGE_WIDTH;
	double fitness; //(Rate between 0 and 100% where 100 is the most fit
	//e.g perfect match.) We will probably want to change the scale because
	//its not really possible to get 100% fitness under these conditions.
	//as the photos will never be exactly alike we could normalize it to make
	//it more realistic and have a 75% percent fitness be considered our 100%. 
	BufferedImage test;
	
	public void setTest(BufferedImage test)
	{
		this.test = test;
	}
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
		int error2 = 0;
		double perror2 = 0;
		double percentError = 0;
		//Iterate through the images (they have the same dimensions), and
		//get integer representing the color channels at each pixel location.
		//The buffered Image also has getRed() etc to get the color values 
		//if you get the color from the getRGB method. To optimize it slightly
		//those method calls are eliminated by using some bit manipulation
		//to get the rgb values (this is actually the exact same method that 
		//the getRed() and other methods use to get the value). Note we might
		//have to factor in the alpha channel but I am not sure. 
		//int red2;
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
	double fit2;
	/**
	 * @return The fitness. 
	 */
	public double getFitness()
	{
		return this.fitness;
	}
	public double get2()
	{
		return fit2;
	}
	
	
}

package TriangleGenome;

import java.awt.Point;
import java.util.Random;

/**
 * 
 * This class handles the actual mutation of a gene in the
 * triangle.
 * The direction tells which direction the mutation is going
 * e.g increasing or decreasing in one of the gene values. Also
 * currently the step size is always 1. In the end it should not be
 * 1, we should have some system to increase the step size when
 * we determine it should. (Adaptive Hill Climbing).
 * 
 *
 */
public class TriangleMutation {
	Random random = new Random();
	Triangle triangle;
	/**
	 * 
	 * @param The triangle that will have genes mutated. 
	 */
	TriangleMutation(Triangle triangle)
	{
		this.triangle = triangle;
	}
	
	public void mutateP1X(boolean direction)
	{
		if(direction)
		{
			triangle.setP1(new Point((triangle.getP1().x+1), triangle.getP1().y));
		}
		else
		{
			triangle.setP1(new Point((triangle.getP1().x-1), triangle.getP1().y));
		}
	}
	public void mutateP1Y(boolean direction)
	{
		if(direction)
		{
			triangle.setP1(new Point((triangle.getP1().x), triangle.getP1().y+1));
		}
		else
		{
			triangle.setP1(new Point((triangle.getP1().x), triangle.getP1().y-1));
		}
	}
	public void mutateP2X(boolean direction)
	{
		if(direction)
		{
			triangle.setP2(new Point((triangle.getP2().x+1), triangle.getP2().y));
		}
		else
		{
			triangle.setP2(new Point((triangle.getP2().x-1), triangle.getP2().y));
		}
	}
	public void mutateP2Y(boolean direction)
	{
		if(direction)
		{
			triangle.setP2(new Point((triangle.getP2().x), triangle.getP2().y+1));
		}
		else
		{
			triangle.setP2(new Point((triangle.getP2().x), triangle.getP2().y-1));
		}
	}
	public void mutateP3X(boolean direction)
	{
		if(direction)
		{
			triangle.setP3(new Point((triangle.getP3().x+1), triangle.getP3().y));
		}
		else
		{
			triangle.setP3(new Point((triangle.getP3().x-1), triangle.getP3().y));
		}
	}
	public void mutateP3Y(boolean direction)
	{
		if(direction)
		{
			triangle.setP3(new Point((triangle.getP3().x), triangle.getP3().y+1));
		}
		else
		{
			triangle.setP3(new Point((triangle.getP3().x), triangle.getP3().y-1));
		}
	}
	public void mutateAlpha(boolean direction)
	{
		if(direction)
		{
			triangle.setAlpha(triangle.getAlpha()+1);
		}
		else
		{
			triangle.setAlpha(triangle.getAlpha()-1);
		}
	}
	public void mutateRed(boolean direction)
	{
		if(direction)
		{
			triangle.setRed(triangle.getRed()+1);
		}
		else
		{
			triangle.setRed(triangle.getRed()-1);
		}
	}
	public void mutateBlue(boolean direction)
	{
		if(direction)
		{
			triangle.setBlue(triangle.getBlue()+1);
		}
		else
		{
			triangle.setBlue(triangle.getBlue()-1);
		}
	}
	public void mutateGreen(boolean direction)
	{
		if(direction)
		{
			triangle.setGreen(triangle.getGreen()+1);
		}
		else
		{
			triangle.setGreen(triangle.getGreen()-1);
		}
	}
	
}

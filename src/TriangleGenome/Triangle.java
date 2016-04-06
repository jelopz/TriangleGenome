package TriangleGenome;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;

/**
 * 
 * Class to represent a triangle each consisting of 10 genes
 * all 200 triangles together make up the DNA or genome for a total
 * of 2000 genes per genome. The 10 genes of each triangle are
 * the Red value, Blue value, Green Value, Alpha (transparency) and 
 * three vertices (each consisting of a x/y value meaning 6 total
 * genes for the three verticies).
 *
 */
public class Triangle
{
  private Point p1;
  private Point p2;
  private Point p3;
  private int p1x;
  private int p1y;
  private int p2x;
  private int p2y;
  private int p3x;
  private int p3y;
  
  private int red;
  private int green;
  private int blue;
  private int alpha; //Transparency value
  private Color color;
  private Polygon triangle; //For ease of display each triangle is 
  //represented as a Polygon. 

  public int getP1x()
  {
	  return p1x;
  }
  public int getP1y()
  {
	  return p1y;
  }
  public int getP2x()
  {
	  return p2x;
  }
  public int getP2y()
  {
	  return p2y;
  }
  public int getP3x()
  {
	  return p3x;
  }
  public int getP3y()
  {
	  return p3y;
  }
  public void setP1x(int p1x)
  {
	  this.p1x = p1x;
  }
  public void setP1y(int p1y)
  {
	  this.p1y = p1y;
  }
  public void setP2x(int p2x)
  {
	  this.p2x = p2x;
  }
  public void setP2y(int p2y)
  {
	  this.p2y = p2y;
  }
  public void setP3x(int p3x)
  {
	  this.p1x = p1x;
  }
  public void setP3y(int p3y)
  {
	  this.p3y = p3y;
  }

  /**
   * @return Red value of triangle (0-255).
   */
  public int getRed()
  {
    return red;
  }

  /**
   * @param red  - New red value of triangle (0-255).
   */
  public void setRed(int red)
  {
    this.red = red;
  }

  /**
   * @return Green value of triangle (0-255).
   */
  public int getGreen()
  {
    return green;
  }

  /**
   * @param green - New green value of triangle (0-255).
   */
  public void setGreen(int green)
  {
    this.green = green;
   // updateTriangle();
  }

  /**
   * @return Blue value of triangle (0-255).
   */
  public int getBlue()
  {
    return blue;
  }

  /**
   * @param blue - New blue value of triangle (0-255).
   */
  public void setBlue(int blue)
  {
    this.blue = blue;
  }

  /**
   * Gets the alpha (transparency) component of the triangles 
   * such that a value of 0 is completely transparent and a value of 255 is opaque
   * 
   * @return The alpha value.
   */
  public int getAlpha()
  {
    return alpha;
  }
  /**
   * @return The color of the triangle (as color object). 
   */
  public Color getColor()
  {
	  return color;
  }
  /**
   * sets the alpha (transparency) component of this triangles color space
   * such that a value of 0 is completely transparent and a value of 255 is opaque.
   * 
   * @param alpha - The new alpha value of the triangle.
   */
  public void setAlpha(int alpha)
  {
    this.alpha = alpha;
  }

  /**
   * @return Java.awt polygon representation of the triangle.
   */
  public Polygon getTriangle()
  {
    return triangle;
  }
  
  public void updateTriangle(){	 
	//Update the color and the vertices of the triangle. 
    color = new Color(red, green, blue, alpha);
  }
}

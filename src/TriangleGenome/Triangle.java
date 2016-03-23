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
  private int red;
  private int green;
  private int blue;
  private int alpha; //Transparency value
  private Color color;
  private Polygon triangle; //For ease of display each triangle is 
  //represented as a Polygon. 
  
  /**
   * Constructor initializes point objects and polygon object. 
   */
  public Triangle(){
    p1 = new Point();
    p2 = new Point();
    p3 = new Point();
    triangle = new Polygon(new int[]{p1.x, p2.x, p3.x} , new int[]{p1.y, p2.y, p3.y}, 3);
  }

  /**
   * 
   * @return The first vertex.
   */
  public Point getP1()
  {
    return p1;
  }

  /**
   * @param p1 - Point to represent new first vertex value.
   */
  public void setP1(Point p1)
  {
    this.p1 = p1;
  }

  /**
   * @return The second vertex.
   */
  public Point getP2()
  {
    return p2;
  }

  /**
   * @param p2 - Point to represent new second vertex value.
   */
  public void setP2(Point p2)
  {
    this.p2 = p2;
  }

  /**
   * @return The third vertex.
   */
  public Point getP3()
  {
    return p3;
  }

  /**
   * @param p3 - Point to represent new third vertex value.
   */
  public void setP3(Point p3)
  {
    this.p3 = p3;
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
	triangle = new Polygon(new int[]{p1.x, p2.x, p3.x} , new int[]{p1.y, p2.y, p3.y}, 3);
    color = new Color(red, green, blue, alpha);
  }
}

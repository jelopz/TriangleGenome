package TriangleGenome;

import java.awt.Color;

/**
 * @author Christian Seely
 * @author Jesus Lopez
 * This class represents a triangle each consisting of 10 genes.
 * All 200 triangles together make up the DNA or genome for a total of
 * 2000 genes per genome. The 10 genes of each triangle are the
 * Red, Green, Blue and Alpah value, and the three verticies 
 * (each consisting of a x/y value meaning six totla genes for the
 * three verticies). 
 *
 */
public class Triangle
{
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
  
  /**
   * 
   * @return Vertex One X value. 
   */
  public int getP1x()
  {
	  return p1x;
  }
  /**
   * 
   * @return Vertex One Y value. 
   */
  public int getP1y()
  {
	  return p1y;
  }
  /**
   * 
   * @return Vertex Two X value. 
   */
  public int getP2x()
  {
	  return p2x;
  }
  /**
   * 
   * @return Vertex Two Y value. 
   */
  public int getP2y()
  {
	  return p2y;
  }
  /**
   * 
   * @return Vertex Three X value.
   */
  public int getP3x()
  {
	  return p3x;
  }
  /**
   * 
   * @return Vertex Three Y value. 
   */
  public int getP3y()
  {
	  return p3y;
  }
  /**
   * 
   * @param Set Vertex One X value. 
   */
  public void setP1x(int p1x)
  {
	  this.p1x = p1x;
  }
  /**
   * 
   * @param Set Vertex One Y value. 
   */
  public void setP1y(int p1y)
  {
	  this.p1y = p1y;
  }
  /**
   * 
   * @param Set Vertex Two X value. 
   */
  public void setP2x(int p2x)
  {
	  this.p2x = p2x;
  }
  /**
   * 
   * @param Set Vertex Two Y value. 
   */
  public void setP2y(int p2y)
  {
	  this.p2y = p2y;
  }
  /**
   * 
   * @param Set Vertex Three X value. 
   */
  public void setP3x(int p3x)
  {
	  this.p3x = p3x;
  }
  /**
   * 
   * @param Set Vertex Three Y value. 
   */
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
   * such that a value of 0 is completely transparent and a 
   * value of 255 is opaque
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
   * Sets the alpha (transparency) component of this triangles color space
   * such that a value of 0 is completely transparent and a value
   * of 255 is opaque.
   * 
   * @param alpha - The new alpha value of the triangle.
   */
  public void setAlpha(int alpha)
  {
    this.alpha = alpha;
  }
  /**
   * Update the color object for the triangle following 
   * changes of the color values. 
   */
  public void updateTriangle(){	 
	//Update the color and the vertices of the triangle. 
    color = new Color(red, green, blue, alpha);
  }
  
  /**
   * Used when saving a genome to a file.
   * 
   * @return the 10 variables separated by spaces in a String.
   */
  public String valuesToString()
  {
    return (p1x + " " + p1y + " " + p2x + " " + p2y + " " + p3x + " " + p3y + " " + red + " "
        + green + " " + blue + " " + alpha);
  }
}

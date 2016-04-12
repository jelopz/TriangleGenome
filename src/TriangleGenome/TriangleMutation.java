package TriangleGenome;

import java.awt.Point;
import java.util.Random;
/**
 * 
 * @author Christian Seely
 * @author Jesus Lopez
 * 
 * This class contains the methodology (via functions) to perform
 * mutations on one of the ten genes that make up the respective triangle.
 * This class is used during the adaptive hill climbing process and 
 * utializes via method parameters mutation involving a direction and
 * a step size. To use this class you must make an instansiation of
 * the TriangleMutation class and then you can perform as many mutations
 * as you want via method calls. 
 *
 */
public class TriangleMutation
{
  Random random = new Random();
  private int IMAGE_WIDTH;
  private int IMAGE_HEIGHT;
/**
 * 
 * @param int IMAGE_WIDTH - Width of image. 
 * @param int IMAGE_HEIGHT - Height of image. 
 */
  TriangleMutation(int IMAGE_WIDTH, int IMAGE_HEIGHT)
  {
    this.IMAGE_HEIGHT = IMAGE_HEIGHT;
    this.IMAGE_WIDTH = IMAGE_WIDTH;
  }
  /**
   * 
   * @param Triangle triangle- The triangle to have mutations performed on. 
   * @param int stepSize- Scale of the mutations change. 
   * @param boolean direction - Is the is the mutation increasing or
   * decreating the previous genes value. 
   * This method is used for mutating the x coordinate at vertex one of
   * the triangle. The mutation is performed while staying within range
   * of the genes possible values. 
   */
  public void mutateP1X(Triangle triangle, int stepSize, boolean direction)
  {
    int value = triangle.getP1x();

    if (direction)
    {
      value += stepSize;
      if (!isMinOrMaxXPointComponent(value))
      {
        triangle.setP1x(triangle.getP1x() + stepSize);
      } else
      {
        triangle.setP1x(IMAGE_WIDTH); 
      }
    } else
    {
      value -= stepSize;
      if (!isMinOrMaxXPointComponent(value))
      {
        triangle.setP1x(triangle.getP1x() - stepSize);
      } else
      {
        triangle.setP1x(0);
      }
    }
  }
  /**
   * 
   * @param Triangle triangle- The triangle to have mutations performed on. 
   * @param int stepSize- Scale of the mutations change. 
   * @param boolean direction - Is the is the mutation increasing or
   * decreating the previous genes value. 
   * This method is used for mutating the y coordinate at vertex one of
   * the triangle. The mutation is performed while staying within range
   * of the genes possible values. 
   */
  public void mutateP1Y(Triangle triangle, int stepSize, boolean direction)
  {
    int value = triangle.getP1y();

    if (direction)
    {
      value += stepSize;
      if (!isMinOrMaxYPointComponent(value))
      {
        triangle.setP1y(triangle.getP1y() + stepSize);
      } else
      {
        triangle.setP1y(IMAGE_HEIGHT);
      }
    } else
    {
      value -= stepSize;
      if (!isMinOrMaxYPointComponent(value))
      {
        triangle.setP1y(triangle.getP1y() - stepSize);
      } else
      {
        triangle.setP1y(0);
      }
    }
  }
  /**
   * 
   * @param Triangle triangle- The triangle to have mutations performed on. 
   * @param int stepSize- Scale of the mutations change. 
   * @param boolean direction - Is the is the mutation increasing or
   * decreating the previous genes value. 
   * This method is used for mutating the x coordinate at vertex two of
   * the triangle. The mutation is performed while staying within range
   * of the genes possible values. 
   */
  public void mutateP2X(Triangle triangle, int stepSize, boolean direction)
  {
    int value = triangle.getP2x();

    if (direction)
    {
      value += stepSize;
      if (!isMinOrMaxXPointComponent(value))
      {
        triangle.setP2x(triangle.getP2x() + stepSize);
      } else
      {
        triangle.setP2x(IMAGE_WIDTH);
      }
    } else
    {
      value -= stepSize;
      if (!isMinOrMaxXPointComponent(value))
      {
        triangle.setP2x(triangle.getP2x() - stepSize);
      } else
      {
        triangle.setP2x(0);
      }
    }
  }
  /**
   * 
   * @param Triangle triangle- The triangle to have mutations performed on. 
   * @param int stepSize- Scale of the mutations change. 
   * @param boolean direction - Is the is the mutation increasing or
   * decreating the previous genes value. 
   * This method is used for mutating the y coordinate at vertex two of
   * the triangle. The mutation is performed while staying within range
   * of the genes possible values. 
   */
  public void mutateP2Y(Triangle triangle, int stepSize, boolean direction)
  {
    int value = triangle.getP2y();

    if (direction)
    {
      value += stepSize;
      if (!isMinOrMaxYPointComponent(value))
      {
        triangle.setP2y(triangle.getP2y() + stepSize);
      } else
      {
        triangle.setP2y(IMAGE_HEIGHT);
      }
    } else
    {
      value -= stepSize;
      if (!isMinOrMaxYPointComponent(value))
      {
        triangle.setP2y(triangle.getP2y() - stepSize);
      } else
      {
        triangle.setP2y(0);
      }
    }
  }
  /**
   * 
   * @param Triangle triangle- The triangle to have mutations performed on. 
   * @param int stepSize- Scale of the mutations change. 
   * @param boolean direction - Is the is the mutation increasing or
   * decreating the previous genes value. 
   * This method is used for mutating the x coordinate at vertex three of
   * the triangle. The mutation is performed while staying within range
   * of the genes possible values. 
   */
  public void mutateP3X(Triangle triangle, int stepSize, boolean direction)
  {
    int value = triangle.getP3x();

    if (direction)
    {
      value += stepSize;
      if (!isMinOrMaxXPointComponent(value))
      {
        triangle.setP3x(triangle.getP3x() + stepSize);
      } else
      {
        triangle.setP3x(IMAGE_WIDTH);
      }
    } else
    {
      value -= stepSize;
      if (!isMinOrMaxXPointComponent(value))
      {
        triangle.setP3x(triangle.getP3x() - stepSize);
      } else
      {
        triangle.setP3x(0);
      }
    }
  }
  /**
   * 
   * @param Triangle triangle- The triangle to have mutations performed on. 
   * @param int stepSize- Scale of the mutations change. 
   * @param boolean direction - Is the is the mutation increasing or
   * decreating the previous genes value. 
   * This method is used for mutating the x coordinate at vertex one of
   * the triangle. The mutation is performed while staying within range
   * of the genes possible values. 
   */
  public void mutateP3Y(Triangle triangle, int stepSize, boolean direction)
  {
    int value = triangle.getP3y();

    if (direction)
    {
      value += stepSize;
      if (!isMinOrMaxYPointComponent(value))
      {
        triangle.setP3y(triangle.getP3y() + stepSize);
      } else
      {
        triangle.setP3y(IMAGE_HEIGHT);
      }
    } else
    {
      value -= stepSize;
      if (!isMinOrMaxYPointComponent(value))
      {
        triangle.setP3y(triangle.getP3y() - stepSize);
      } else
      {
        triangle.setP3y(0);
      }
    }
  }
  /**
   * 
   * @param Triangle triangle- The triangle to have mutations performed on. 
   * @param int stepSize- Scale of the mutations change. 
   * @param boolean direction - Is the is the mutation increasing or
   * decreating the previous genes value. 
   * This method is used for mutating the alpha value in the triangle. The
   * mutation is performed while staying within range of the genes
   * possible values. 
   */
  public void mutateAlpha(Triangle triangle, int stepSize, boolean direction)
  {
    int value = triangle.getAlpha();
    if (direction)
    {
      value += stepSize;
      if (!isMinOrMaxRGBA(value))
      {
        triangle.setAlpha(value);
      } else
      {
        triangle.setAlpha(255);
      }
    } else
    {
      value -= stepSize;
      if (!isMinOrMaxRGBA(value))
      {
        triangle.setAlpha(value);
      } else
      {
        triangle.setAlpha(0);
      }
    }
  }
  /**
   * 
   * @param Triangle triangle- The triangle to have mutations performed on. 
   * @param int stepSize- Scale of the mutations change. 
   * @param boolean direction - Is the is the mutation increasing or
   * decreating the previous genes value. 
   * This method is used for mutating the red value of a triangle. The
   * mutation is performed while staying within range of the genes
   * possible values. 
   */
  public void mutateRed(Triangle triangle, int stepSize, boolean direction)
  {
    int value = triangle.getRed();
    if (direction)
    {
      value += stepSize;
      if (!isMinOrMaxRGBA(value))
      {
        triangle.setRed(value);
      } else
      {
        triangle.setRed(255);
      }
    } else
    {
      value -= stepSize;
      if (!isMinOrMaxRGBA(value))
      {
        triangle.setRed(value);
      } else
      {
        triangle.setRed(0);
      }
    }
  }
  /**
   * 
   * @param Triangle triangle- The triangle to have mutations performed on. 
   * @param int stepSize- Scale of the mutations change. 
   * @param boolean direction - Is the is the mutation increasing or
   * decreating the previous genes value. 
   * This method is used for mutating the blue value of a triangle. The
   * mutation is performed while staying within range of the genes
   * possible values. 
   */
  public void mutateBlue(Triangle triangle, int stepSize, boolean direction)
  {
    int value = triangle.getBlue();

    if (direction)
    {
      value += stepSize;
      if (!isMinOrMaxRGBA(value))
      {
        triangle.setBlue(value);
      } else
      {
        triangle.setBlue(255);
      }
    } else
    {
      value -= stepSize;
      if (!isMinOrMaxRGBA(value))
      {
        triangle.setBlue(value);
      } else
      {
        triangle.setBlue(0);
      }
    }
  }
  /**
   * 
   * @param Triangle triangle- The triangle to have mutations performed on. 
   * @param int stepSize- Scale of the mutations change. 
   * @param boolean direction - Is the is the mutation increasing or
   * decreating the previous genes value. 
   * This method is used for mutating the green value of a triangle. The
   * mutation is performed while staying within range of the genes
   * possible values. 
   */
  public void mutateGreen(Triangle triangle, int stepSize, boolean direction)
  {
    int value = triangle.getGreen();

    if (direction)
    {
      value += stepSize;
      if (!isMinOrMaxRGBA(value))
      {
        triangle.setGreen(value);
      } else
      {
        triangle.setGreen(255);
      }
    } else
    {
      value -= stepSize;
      if (!isMinOrMaxRGBA(value))
      {
        triangle.setGreen(value);
      } else
      {
        triangle.setGreen(0);
      }
    }
  }
   /**
    * 
    * @param X coord value. 
    * @return True if the x coordinate is in range. 
    */
  private boolean isMinOrMaxXPointComponent(int value)
  {
    if (value < 0 || value > IMAGE_WIDTH)
    {
      return true;
    } else
    {
      return false;
    }
  }
  /**
   * 
   * @param Y coord value. 
   * @return True if the y coordinate is in range. 
   */
  private boolean isMinOrMaxYPointComponent(int value)
  {
    if (value < 0 || value > IMAGE_HEIGHT)
    {
      return true;
    } else
    {
      return false;
    }
  }

  /**
   * @param value
   *          the specific r,g,b, or a value (range 0-255) of the triangle
   * @return true if the value is <= 0 or >= 255
   */
  private boolean isMinOrMaxRGBA(int value)
  {
    if (value <= 0 || value >= 255)
    {
      return true;
    }
    return false;
  }

}

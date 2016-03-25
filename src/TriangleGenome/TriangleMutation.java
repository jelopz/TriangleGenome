package TriangleGenome;

import java.awt.Point;
import java.util.Random;

/**
 * 
 * This class handles the actual mutation of a gene in the triangle. The
 * direction tells which direction the mutation is going e.g increasing or
 * decreasing in one of the gene values.
 * 
 * When creating an object of type TriangleMutation we initialize the stepValue,
 * which tells it how much a gene will either increase or decrease by. Right now
 * this logic only holds for single mutations. In the future, adding in multiple
 * mutations per step could cause problems.
 * 
 * The GA will handle how much the stepValue increases by after improvements.
 * 
 * When dealing with the rgba values, we can only have numbers [0,255]. If the
 * GA attempts to alter the number to a value less than 0 or greater than 255,
 * the value is instead 0 or 255, respectively. If it tries to again go passed
 * the bounds, the triangle will not mutate at all, essentially wasting a step
 * and causes no improvement. For example, If the r value is 7 and the step
 * value is 10 and we're decreasing, the r value will be 0, not -3. If that's an
 * improvement and the GA wants to again decrease r, the value remains 0 and
 * causes no improvement - So, the GA moves on.
 */
public class TriangleMutation
{
  Random random = new Random();
  Triangle triangle;
  int stepValue;

  /**
   * 
   * @param triangle
   *          the triangle that will have genes mutated
   * @param step
   *          the value to increase or decrease a gene by
   */
  TriangleMutation(Triangle triangle, int step)
  {
    this.triangle = triangle;
    stepValue = step;
  }

  public void mutateP1X(boolean direction)
  {
    if (direction)
    {
      triangle.setP1(new Point((triangle.getP1().x + stepValue), triangle.getP1().y));
    }
    else
    {
      triangle.setP1(new Point((triangle.getP1().x - stepValue), triangle.getP1().y));
    }
  }

  public void mutateP1Y(boolean direction)
  {
    if (direction)
    {
      triangle.setP1(new Point((triangle.getP1().x), triangle.getP1().y + stepValue));
    }
    else
    {
      triangle.setP1(new Point((triangle.getP1().x), triangle.getP1().y - stepValue));
    }
  }

  public void mutateP2X(boolean direction)
  {
    if (direction)
    {
      triangle.setP2(new Point((triangle.getP2().x + stepValue), triangle.getP2().y));
    }
    else
    {
      triangle.setP2(new Point((triangle.getP2().x - stepValue), triangle.getP2().y));
    }
  }

  public void mutateP2Y(boolean direction)
  {
    if (direction)
    {
      triangle.setP2(new Point((triangle.getP2().x), triangle.getP2().y + stepValue));
    }
    else
    {
      triangle.setP2(new Point((triangle.getP2().x), triangle.getP2().y - stepValue));
    }
  }

  public void mutateP3X(boolean direction)
  {
    if (direction)
    {
      triangle.setP3(new Point((triangle.getP3().x + stepValue), triangle.getP3().y));
    }
    else
    {
      triangle.setP3(new Point((triangle.getP3().x - stepValue), triangle.getP3().y));
    }
  }

  public void mutateP3Y(boolean direction)
  {
    if (direction)
    {
      triangle.setP3(new Point((triangle.getP3().x), triangle.getP3().y + stepValue));
    }
    else
    {
      triangle.setP3(new Point((triangle.getP3().x), triangle.getP3().y - stepValue));
    }
  }

  public void mutateAlpha(boolean direction)
  {
    int value = triangle.getAlpha();

    if (isValidRGBAMutation(direction, value))
    {
      if (direction)
      {
        value += stepValue;
        if (!isMinOrMaxRGBA(value))
        {
          triangle.setAlpha(value);
        }
        else
        {
          triangle.setAlpha(255);
        }
      }
      else
      {
        value -= stepValue;
        if (!isMinOrMaxRGBA(value))
        {
          triangle.setAlpha(value);
        }
        else
        {
          triangle.setAlpha(0);
        }
      }
    }
  }

  public void mutateRed(boolean direction)
  {
    int value = triangle.getRed();

    if (isValidRGBAMutation(direction, value))
    {
      if (direction)
      {
        value += stepValue;
        if (!isMinOrMaxRGBA(value))
        {
          triangle.setRed(value);
        }
        else
        {
          triangle.setRed(255);
        }
      }
      else
      {
        value -= stepValue;
        if (!isMinOrMaxRGBA(value))
        {
          triangle.setRed(value);
        }
        else
        {
          triangle.setRed(0);
        }
      }
    }
  }

  public void mutateBlue(boolean direction)
  {
    int value = triangle.getBlue();

    if (isValidRGBAMutation(direction, value))
    {
      if (direction)
      {
        value += stepValue;
        if (!isMinOrMaxRGBA(value))
        {
          triangle.setBlue(value);
        }
        else
        {
          triangle.setBlue(255);
        }
      }
      else
      {
        value -= stepValue;
        if (!isMinOrMaxRGBA(value))
        {
          triangle.setBlue(value);
        }
        else
        {
          triangle.setBlue(0);
        }
      }
    }
  }

  public void mutateGreen(boolean direction)
  {
    int value = triangle.getGreen();

    if (isValidRGBAMutation(direction, value))
    {
      if (direction)
      {
        value += stepValue;
        if (!isMinOrMaxRGBA(value))
        {
          triangle.setGreen(value);
        }
        else
        {
          triangle.setGreen(255);
        }
      }
      else
      {
        value -= stepValue;
        if (!isMinOrMaxRGBA(value))
        {
          triangle.setGreen(value);
        }
        else
        {
          triangle.setGreen(0);
        }
      }
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

  /**
   * Determines if a mutation we are trying to make to an RGBA value is valid or
   * not. There are two invalid moves, decreasing 0 or increasing 255. If the
   * value is any number between (but not including) 0 and 255, there is no
   * invalid mutation.
   * 
   * @param direction
   *          true if increasing, false if decreasing
   * @param value
   *          the specific r,g,b, or a value of the triangle
   * @return true if a valid mutation
   */
  private boolean isValidRGBAMutation(boolean direction, int value)
  {
    if (!isMinOrMaxRGBA(value))
    {
      // if the initial value is not a min or max we can alter the value 100%
      return true;
    }
    else
    {
      if (value == 0 && !direction)
      {
        // if the initial value is 0 and we're trying to decrease, detect an
        // illegal mutation
        return false;
      }
      else if (value == 255 && direction)
      {
        // if the initial value is 255 and we're trying to increase, detect an
        // illegal mutation
        return false;
      }
      else
      {
        // we are attempting to increase a min or decrease a max, which is legal
        return true;
      }
    }
  }

}

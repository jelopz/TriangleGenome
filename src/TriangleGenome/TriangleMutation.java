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
public class TriangleMutation {
	Random random = new Random();
	private int IMAGE_WIDTH;
	private int IMAGE_HEIGHT;

	TriangleMutation(int IMAGE_WIDTH, int IMAGE_HEIGHT) {
		this.IMAGE_HEIGHT = IMAGE_HEIGHT;
		this.IMAGE_WIDTH = IMAGE_WIDTH;
	}

	public void mutateP1X(Triangle triangle, int stepSize, boolean direction) {
		int value = triangle.getP1x();

		if (direction) {
			value += stepSize;
			if (!isMinOrMaxXPointComponent(value)) {
				triangle.setP1x(triangle.getP1x()+stepSize);
			} else {
				triangle.setP1x(IMAGE_WIDTH);
			}
		} else {
			value -= stepSize;
			if (!isMinOrMaxXPointComponent(value)) {
				triangle.setP1x(triangle.getP1x()-stepSize);
			} else {
				triangle.setP1x(0);
			}
		}
	}

	public void mutateP1Y(Triangle triangle, int stepSize, boolean direction) {
		int value = triangle.getP1y();

		if (direction) {
			value += stepSize;
			if (!isMinOrMaxYPointComponent(value)) {
				triangle.setP1y(triangle.getP1y() + stepSize);
			} else {
				triangle.setP1y(IMAGE_HEIGHT);
			}
		} else {
			value -= stepSize;
			if (!isMinOrMaxYPointComponent(value)) {
				triangle.setP1y(triangle.getP1y() - stepSize);
			} else {
				triangle.setP1y(0);
			}
		}
	}

	public void mutateP2X(Triangle triangle, int stepSize, boolean direction) {
		int value = triangle.getP2x();

		if (direction) {
			value += stepSize;
			if (!isMinOrMaxXPointComponent(value)) {
				triangle.setP2x(triangle.getP2x()+stepSize);
			} else {
				triangle.setP2x(IMAGE_WIDTH);
			}
		} else {
			value -= stepSize;
			if (!isMinOrMaxXPointComponent(value)) {
				triangle.setP2x(triangle.getP2x()-stepSize);
			} else {
				triangle.setP2x(0);
			}
		}
	}

	public void mutateP2Y(Triangle triangle, int stepSize, boolean direction) {
		int value = triangle.getP2y();

		if (direction) {
			value += stepSize;
			if (!isMinOrMaxYPointComponent(value)) {
				triangle.setP2y(triangle.getP2y() + stepSize);
			} else {
				triangle.setP2y(IMAGE_HEIGHT);
			}
		} else {
			value -= stepSize;
			if (!isMinOrMaxYPointComponent(value)) {
				triangle.setP2y(triangle.getP2y() - stepSize);
			} else {
				triangle.setP2y(0);
			}
		}
	}

	public void mutateP3X(Triangle triangle, int stepSize, boolean direction) {
		int value = triangle.getP3x();

		if (direction) {
			value += stepSize;
			if (!isMinOrMaxXPointComponent(value)) {
				triangle.setP3x(triangle.getP3x()+stepSize);
			} else {
				triangle.setP3x(IMAGE_WIDTH);
			}
		} else {
			value -= stepSize;
			if (!isMinOrMaxXPointComponent(value)) {
				triangle.setP3x(triangle.getP3x()-stepSize);
			} else {
				triangle.setP3x(0);
			}
		}
	}

	public void mutateP3Y(Triangle triangle, int stepSize, boolean direction) {
		int value = triangle.getP3y();

		if (direction) {
			value += stepSize;
			if (!isMinOrMaxYPointComponent(value)) {
				triangle.setP3y(triangle.getP3y() + stepSize);
			} else {
				triangle.setP3y(IMAGE_HEIGHT);
			}
		} else {
			value -= stepSize;
			if (!isMinOrMaxYPointComponent(value)) {
				triangle.setP3y(triangle.getP3y() - stepSize);
			} else {
				triangle.setP3y(0);
			}
		}
	}

	public void mutateAlpha(Triangle triangle, int stepSize, boolean direction) {
		int value = triangle.getAlpha();
		if (direction) {
			value += stepSize;
			if (!isMinOrMaxRGBA(value)) {
				triangle.setAlpha(value);
			} else {
				triangle.setAlpha(255);
			}
		} else {
			value -= stepSize;
			if (!isMinOrMaxRGBA(value)) {
				triangle.setAlpha(value);
			} else {
				triangle.setAlpha(0);
			}
		}
	}

	public void mutateRed(Triangle triangle, int stepSize, boolean direction) {
		int value = triangle.getRed();
		if (direction) {
			value += stepSize;
			if (!isMinOrMaxRGBA(value)) {
				triangle.setRed(value);
			} else {
				triangle.setRed(255);
			}
		} else {
			value -= stepSize;
			if (!isMinOrMaxRGBA(value)) {
				triangle.setRed(value);
			} else {
				triangle.setRed(0);
			}
		}
	}

	public void mutateBlue(Triangle triangle, int stepSize, boolean direction) {
		int value = triangle.getBlue();

		if (direction) {
			value += stepSize;
			if (!isMinOrMaxRGBA(value)) {
				triangle.setBlue(value);
			} else {
				triangle.setBlue(255);
			}
		} else {
			value -= stepSize;
			if (!isMinOrMaxRGBA(value)) {
				triangle.setBlue(value);
			} else {
				triangle.setBlue(0);
			}
		}
	}

	public void mutateGreen(Triangle triangle, int stepSize, boolean direction) {
		int value = triangle.getGreen();

		if (direction) {
			value += stepSize;
			if (!isMinOrMaxRGBA(value)) {
				triangle.setGreen(value);
			} else {
				triangle.setGreen(255);
			}
		} else {
			value -= stepSize;
			if (!isMinOrMaxRGBA(value)) {
				triangle.setGreen(value);
			} else {
				triangle.setGreen(0);
			}
		}
	}

	private boolean isMinOrMaxXPointComponent(int value) {
		if (value < 0 || value > IMAGE_WIDTH) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isMinOrMaxYPointComponent(int value) {
		if (value < 0 || value > IMAGE_HEIGHT) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param value
	 *            the specific r,g,b, or a value (range 0-255) of the triangle
	 * @return true if the value is <= 0 or >= 255
	 */
	private boolean isMinOrMaxRGBA(int value) {
		if (value <= 0 || value >= 255) {
			return true;
		}
		return false;
	}

}

package ml;

import java.io.Serializable;

/**
 * Created by corona10 on 2016. 6. 25..
 */
public class Rgb implements Serializable {

  private int red = 0;
  private int blue = 0;
  private int green = 0;

  /**
   * Constructor of the class from integers
   *
   * @param red : integer value for red (0...255)
   * @param green : integer value for green (0...255)
   * @param blue : integer value for blue (0...255)
   */
  public Rgb(int red, int green, int blue) {
    this.red = red;
    this.blue = blue;
    this.green = green;
  }

  /**
   * @return red value
   */
  public int getRed() {
    return (this.red);
  }

  /**
   * @return blue value
   */
  public int getBlue() {
    return (this.blue);
  }

  /**
   * @return green value
   */
  public int getGreen() {
    return (this.green);
  }

  public float[] getXY()
  {
    float x = (0.490f*(this.red) + 0.310f*(this.green) + 0.200f*(this.blue));
    float y = (0.177f*(this.red) + 0.813f*(this.green) + 0.011f*(this.blue));
    float z = (0.000f*(this.red) + 0.010f*(this.green) + 0.990f*(this.blue));

    float fx = x/(x+y+z);
    float fy = y/(x+y+z);

    float[] xy = new float[2];
    xy[0] = fx;
    xy[1] = fy;
    return xy;

  }
}

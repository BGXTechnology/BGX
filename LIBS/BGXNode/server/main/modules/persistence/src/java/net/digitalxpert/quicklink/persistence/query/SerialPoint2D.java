package net.bgx.bgxnetwork.persistence.query;

import java.io.Serializable;
import java.awt.geom.Point2D;

/**
 * Class SerialPoint2D
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class SerialPoint2D implements Serializable {
  private static final long serialVersionUID = 1L;
  private double x=0;
  private double y=0;

  public SerialPoint2D() {
  }

  public SerialPoint2D(double v, double v1) {
    x=v;
    y=v1;
  }

  public SerialPoint2D(Point2D coordinates) {
    this(coordinates.getX(), coordinates.getY());
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }
}

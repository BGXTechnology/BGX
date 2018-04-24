package net.bgx.bgxnetwork.bgxop.engine;

import java.awt.*;

/**
 * Class DefaultGraphSizeFunction
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class DefaultGraphSizeFunction implements IGraphSizeFunction {
  public DefaultGraphSizeFunction() {
  }

  public Dimension getGraphSize(int vertexCount, int linkCount) {
    int w;
    if (vertexCount<=10) w = 600;
    else if (vertexCount<=100) w = 600 + (vertexCount-10)*10;
    else if (vertexCount<=500) w = 1500 + (vertexCount-100)*3;
    else w = 3000;

    return new Dimension(w,w);
  }
}

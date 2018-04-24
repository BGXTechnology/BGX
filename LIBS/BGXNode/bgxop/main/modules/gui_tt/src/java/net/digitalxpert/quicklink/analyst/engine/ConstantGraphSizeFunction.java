package net.bgx.bgxnetwork.bgxop.engine;

import java.awt.*;

/**
 * Class ConstantGraphSizeFunction
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class ConstantGraphSizeFunction implements IGraphSizeFunction {
  private Dimension size;

  public ConstantGraphSizeFunction(Dimension size) {
    this.size = size;
  }

  public Dimension getGraphSize(int vertexCount, int linkCount) {
    return size;
  }

}

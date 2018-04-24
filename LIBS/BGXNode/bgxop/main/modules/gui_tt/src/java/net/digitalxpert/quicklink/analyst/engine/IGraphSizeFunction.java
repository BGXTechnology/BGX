package net.bgx.bgxnetwork.bgxop.engine;

import java.awt.*;

/**
 * Interface IGraphSizeFunction
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public interface IGraphSizeFunction {

  public Dimension getGraphSize(int vertexCount, int linkCount);

}

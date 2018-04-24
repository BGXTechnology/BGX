package net.bgx.bgxnetwork.bgxop.engine;

import java.awt.*;

/**
 * Class GraphCustoms
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class GraphCustoms {
  private Dimension graphSize = null;
  private ILayoutIterationsFunction iterationsFunction = null;

  public GraphCustoms() {
  }

  public GraphCustoms(Dimension graphSize, ILayoutIterationsFunction iterationsFunction) {
    this.graphSize = graphSize;
    this.iterationsFunction = iterationsFunction;
  }

  public Dimension getGraphSize() {
    return graphSize;
  }

  public void setGraphSize(Dimension graphSize) {
    this.graphSize = graphSize;
  }

  public ILayoutIterationsFunction getIterationsFunction() {
    return iterationsFunction;
  }

  public void setIterationsFunction(ILayoutIterationsFunction iterationsFunction) {
    this.iterationsFunction = iterationsFunction;
  }

  public boolean isEmpty() {
    return (graphSize==null && iterationsFunction==null);
  }
}

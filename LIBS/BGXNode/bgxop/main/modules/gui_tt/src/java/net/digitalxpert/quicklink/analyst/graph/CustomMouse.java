package net.bgx.bgxnetwork.bgxop.graph;

import ru.zsoft.jung.viewer.*;

import java.awt.event.InputEvent;

/**
 * Class CustomMouse
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class CustomMouse extends BufferedMouse {
  public CustomMouse(BufferedViewer v) {
    super(v);
  }

  public CustomMouse(BufferedViewer v, float in, float out) {
    super(v, in, out);
  }

  protected void loadPlugins(BufferedViewer v) {
    pickingPlugin = new OutlinePickingPlugin(v, InputEvent.BUTTON1_MASK, InputEvent.BUTTON1_MASK | InputEvent.CTRL_MASK);
    translatingPlugin = new OutlineTranslationPlugin(v, InputEvent.BUTTON3_MASK);
    scalingPlugin = new BufferedZoomingPlugin(v, in, out);
    rotatingPlugin = new OutlineRotatingPlugin(v, InputEvent.BUTTON3_MASK | InputEvent.SHIFT_MASK);
    shearingPlugin = new OutlineShearingPlugin(v, InputEvent.BUTTON3_MASK | InputEvent.CTRL_MASK);

    add(scalingPlugin);
    add(pickingPlugin);
    add(translatingPlugin);
    add(rotatingPlugin);
    add(shearingPlugin);
    setMode(Mode.TRANSFORMING);
  }

  public Mode getMode() {
    return mode;
  }

  /**
   * setter for the Mode.
   */
  public void setMode(Mode mode) {
    this.mode = mode;
  }

  protected void setPickingMode() {
  }

  protected void setTransformingMode() {
  }

}

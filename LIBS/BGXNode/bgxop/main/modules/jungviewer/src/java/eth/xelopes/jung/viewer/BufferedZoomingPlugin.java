package ru.zsoft.jung.viewer;

import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseEvent;

import ru.zsoft.jung.viewer.BufferedViewer;

/**
 * Class BufferedZoomingPlugin
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class BufferedZoomingPlugin extends ScalingGraphMousePlugin {
  private BufferedViewer v;

  public BufferedZoomingPlugin(BufferedViewer v) {
    super(new CrossoverScalingControl(), MouseEvent.NOBUTTON);
    this.v = v;
  }

  public BufferedZoomingPlugin(BufferedViewer view, float v, float v1) {
      super(new CrossoverScalingControl(), MouseEvent.NOBUTTON,v, v1);
      this.v = view;
  }

  public void mouseWheelMoved(MouseWheelEvent event) {
    v.unlock();
    super.mouseWheelMoved(event);
  }

  public void zoomIn() {
    v.unlock();
    scaler.scale(v, in, v.getCenter());
    v.repaint();
  }

  public void zoomOut() {
    v.unlock();
    scaler.scale(v, out, v.getCenter());
    v.repaint();
  }

}

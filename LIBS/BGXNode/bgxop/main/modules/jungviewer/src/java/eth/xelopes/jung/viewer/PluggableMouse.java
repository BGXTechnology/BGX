package ru.zsoft.jung.viewer;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.GraphMousePlugin;

import java.awt.event.*;
import java.awt.*;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * Class PluggableMouse
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class PluggableMouse implements VisualizationViewer.GraphMouse {

  MouseListener[] mouseListeners;
  MouseMotionListener[] mouseMotionListeners;
  MouseWheelListener[] mouseWheelListeners;
  Set mousePluginList = new LinkedHashSet();
  Set mouseMotionPluginList = new LinkedHashSet();
  Set mouseWheelPluginList = new LinkedHashSet();

  public void add(GraphMousePlugin plugin) {
    if(plugin instanceof MouseListener) {
      mousePluginList.add(plugin);
      mouseListeners = null;
    }
    if(plugin instanceof MouseMotionListener) {
      mouseMotionPluginList.add(plugin);
      mouseMotionListeners = null;
    }
    if(plugin instanceof MouseWheelListener) {
      mouseWheelPluginList.add(plugin);
      mouseWheelListeners = null;
    }
  }

  public void remove(GraphMousePlugin plugin) {
    if(plugin instanceof MouseListener) {
      boolean wasThere = mousePluginList.remove(plugin);
      if(wasThere) mouseListeners = null;
    }
    if(plugin instanceof MouseMotionListener) {
      boolean wasThere = mouseMotionPluginList.remove(plugin);
      if(wasThere) mouseMotionListeners = null;
    }
    if(plugin instanceof MouseWheelListener) {
      boolean wasThere = mouseWheelPluginList.remove(plugin);
      if(wasThere) mouseWheelListeners = null;
    }
  }

  private void checkMouseListeners() {
    if(mouseListeners == null) {
      mouseListeners = (MouseListener[])
          mousePluginList.toArray(new MouseListener[mousePluginList.size()]);
    }
  }

  private void checkMouseMotionListeners() {
    if(mouseMotionListeners == null){
      mouseMotionListeners = (MouseMotionListener[])
          mouseMotionPluginList.toArray(new MouseMotionListener[mouseMotionPluginList.size()]);
    }
  }

  private void checkMouseWheelListeners() {
    if(mouseWheelListeners == null) {
      mouseWheelListeners = (MouseWheelListener[])
          mouseWheelPluginList.toArray(new MouseWheelListener[mouseWheelPluginList.size()]);
    }
  }

  public void mouseClicked(MouseEvent e) {
    checkMouseListeners();
    for(int i=0; i<mouseListeners.length; i++) {
      mouseListeners[i].mouseClicked(e);
      if(e.isConsumed()) break;
    }
    if (e.isConsumed()) ((Component) e.getSource()).repaint();
  }

  public void mousePressed(MouseEvent e) {
    checkMouseListeners();
    for(int i=0; i<mouseListeners.length; i++) {
      mouseListeners[i].mousePressed(e);
      if(e.isConsumed()) break;
    }
    if (e.isConsumed()) ((Component) e.getSource()).repaint();
  }

  public void mouseReleased(MouseEvent e) {
    checkMouseListeners();
    for(int i=0; i<mouseListeners.length; i++) {
      mouseListeners[i].mouseReleased(e);
      if(e.isConsumed()) break;
    }
    if (e.isConsumed()) ((Component) e.getSource()).repaint();
  }

  public void mouseEntered(MouseEvent e) {
    checkMouseListeners();
    for(int i=0; i<mouseListeners.length; i++) {
      mouseListeners[i].mouseEntered(e);
      if(e.isConsumed()) break;
    }
    if (e.isConsumed()) ((Component) e.getSource()).repaint();
  }

  public void mouseExited(MouseEvent e) {
    checkMouseListeners();
    for(int i=0; i<mouseListeners.length; i++) {
      mouseListeners[i].mouseExited(e);
      if(e.isConsumed()) break;
    }
    if (e.isConsumed()) ((Component) e.getSource()).repaint();
  }

  public void mouseDragged(MouseEvent e) {
    checkMouseMotionListeners();
    for(int i=0; i<mouseMotionListeners.length; i++) {
      mouseMotionListeners[i].mouseDragged(e);
      if(e.isConsumed()) break;
    }
    if (e.isConsumed()) ((Component) e.getSource()).repaint();
  }

  public void mouseMoved(MouseEvent e) {
    checkMouseMotionListeners();
    for(int i=0; i<mouseMotionListeners.length; i++) {
      mouseMotionListeners[i].mouseMoved(e);
      if(e.isConsumed()) break;
    }
    if (e.isConsumed()) ((Component) e.getSource()).repaint();
  }

  public void mouseWheelMoved(MouseWheelEvent e) {
    checkMouseWheelListeners();
    for(int i=0; i<mouseWheelListeners.length; i++) {
      mouseWheelListeners[i].mouseWheelMoved(e);
      if(e.isConsumed()) break;
    }
    if (e.isConsumed()) ((Component) e.getSource()).repaint();
  }

}

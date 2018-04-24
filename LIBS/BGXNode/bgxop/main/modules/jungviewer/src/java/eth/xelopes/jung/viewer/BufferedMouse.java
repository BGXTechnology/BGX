package ru.zsoft.jung.viewer;

import edu.uci.ics.jung.visualization.control.*;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;

/**
 * Class BufferedMouse
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class BufferedMouse extends PluggableMouse implements ModalGraphMouse, ItemSelectable {

  /**
   * used by the scaling plugins for zoom in
   */
  protected float in;
  /**
   * used by the scaling plugins for zoom out
   */
  protected float out;
  /**
   * a listener for mode changes
   */
  protected ItemListener modeListener;
  /**
   * a JComboBox control available to set the mode
   */
  protected JComboBox modeBox;
  /**
   * a menu available to set the mode
   */
  protected JMenu modeMenu;
  /**
   * the current mode
   */
  protected Mode mode;
  /**
   * listeners for mode changes
   */
  protected EventListenerList listenerList = new EventListenerList();

  protected GraphMousePlugin pickingPlugin;
  protected GraphMousePlugin translatingPlugin;
  protected GraphMousePlugin scalingPlugin;
  protected GraphMousePlugin rotatingPlugin;
  protected GraphMousePlugin shearingPlugin;

  /**
   * create an instance with default values
   *
   */
  public BufferedMouse(BufferedViewer v) {
    this(v, 1.1f, 0.9f);
  }

  /**
   * create an instance with passed values
   * @param in override value for scale in
   * @param out override value for scale out
   */
  public BufferedMouse(BufferedViewer v, float in, float out) {
    this.in = in;
    this.out = out;
    loadPlugins(v);
  }

  /**
   * create the plugins, and load the plugins for TRANSFORMING mode
   *
   */
  protected void loadPlugins(BufferedViewer v) {
    pickingPlugin = new OutlinePickingPlugin(v);
    translatingPlugin = new OutlineTranslationPlugin(v, InputEvent.BUTTON1_MASK);
    scalingPlugin = new BufferedZoomingPlugin(v, in, out);
    rotatingPlugin = new OutlineRotatingPlugin(v);
    shearingPlugin = new OutlineShearingPlugin(v);

    add(scalingPlugin);
    setMode(Mode.TRANSFORMING);
  }

  public void zoomIn() {
    if (scalingPlugin instanceof BufferedZoomingPlugin)
      ((BufferedZoomingPlugin)scalingPlugin).zoomIn();
  }

  public void zoomOut() {
    if (scalingPlugin instanceof BufferedZoomingPlugin)
      ((BufferedZoomingPlugin)scalingPlugin).zoomOut();
  }

  public Mode getMode() {
    return mode;
  }

  /**
   * setter for the Mode.
   */
  public void setMode(Mode mode) {
    if(this.mode != mode) {
      fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED,
          this.mode, ItemEvent.DESELECTED));
      this.mode = mode;
      if(mode == Mode.TRANSFORMING) {
        setTransformingMode();
      } else if(mode == Mode.PICKING) {
        setPickingMode();
      }
      if(modeBox != null) {
        modeBox.setSelectedItem(mode);
      }
      fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, mode, ItemEvent.SELECTED));
    }
  }
  /* (non-Javadoc)
  * @see edu.uci.ics.jung.visualization.control.ModalGraphMouse#setPickingMode()
  */
  protected void setPickingMode() {
    remove(translatingPlugin);
    remove(rotatingPlugin);
    remove(shearingPlugin);
    add(pickingPlugin);
  }

  /* (non-Javadoc)
  * @see edu.uci.ics.jung.visualization.control.ModalGraphMouse#setTransformingMode()
  */
  protected void setTransformingMode() {
    remove(pickingPlugin);
    add(translatingPlugin);
    add(rotatingPlugin);
    add(shearingPlugin);
  }

  /**
   * @param zoomAtMouse The zoomAtMouse to set.
   */
  public void setZoomAtMouse(boolean zoomAtMouse) {
    ((ScalingGraphMousePlugin) scalingPlugin).setZoomAtMouse(zoomAtMouse);
  }

  /**
   * listener to set the mode from an external event source
   */
  class ModeListener implements ItemListener {
    public void itemStateChanged(ItemEvent e) {
      setMode((Mode) e.getItem());
    }
  }

  /* (non-Javadoc)
  * @see edu.uci.ics.jung.visualization.control.ModalGraphMouse#getModeListener()
  */
  public ItemListener getModeListener() {
    if (modeListener == null) {
      modeListener = new ModeListener();
    }
    return modeListener;
  }

  /**
   * add a listener for mode changes
   */
  public void addItemListener(ItemListener aListener) {
    listenerList.add(ItemListener.class,aListener);
  }

  /**
   * remove a listener for mode changes
   */
  public void removeItemListener(ItemListener aListener) {
    listenerList.remove(ItemListener.class,aListener);
  }

  /**
   * Returns an array of all the <code>ItemListener</code>s added
   * to this JComboBox with addItemListener().
   *
   * @return all of the <code>ItemListener</code>s added or an empty
   *         array if no listeners have been added
   * @since 1.4
   */
  public ItemListener[] getItemListeners() {
    return (ItemListener[])listenerList.getListeners(ItemListener.class);
  }

  public Object[] getSelectedObjects() {
    if ( mode == null )
      return new Object[0];
    else {
      Object result[] = new Object[1];
      result[0] = mode;
      return result;
    }
  }

  /**
   * Notifies all listeners that have registered interest for
   * notification on this event type.
   * @param e  the event of interest
   *
   * @see EventListenerList
   */
  protected void fireItemStateChanged(ItemEvent e) {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for ( int i = listeners.length-2; i>=0; i-=2 ) {
      if ( listeners[i]==ItemListener.class ) {
        ((ItemListener)listeners[i+1]).itemStateChanged(e);
      }
    }
  }
}

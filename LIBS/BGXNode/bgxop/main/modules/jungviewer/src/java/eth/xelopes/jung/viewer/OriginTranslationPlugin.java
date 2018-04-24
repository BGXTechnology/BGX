package ru.zsoft.jung.viewer;

import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Class OriginTranslationPlugin
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class OriginTranslationPlugin extends AbstractGraphMousePlugin
    implements MouseListener, MouseMotionListener {

  private BufferedViewer v = null;

  /**
   */
  public OriginTranslationPlugin(BufferedViewer v) {
    this(v, MouseEvent.BUTTON1_MASK);
  }

  /**
   * create an instance with passed modifer value
   * @param modifiers the mouse event modifier to activate this function
   */
  public OriginTranslationPlugin(BufferedViewer v, int modifiers) {
    super(modifiers);
    this.v = v;
    this.cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
  }

  /**
   * Check the event modifiers. Set the 'down' point for later
   * use. If this event satisfies the modifiers, change the cursor
   * to the system 'move cursor'
   * @param e the event
   */
  public void mousePressed(MouseEvent e) {
    boolean accepted = checkModifiers(e);
    down = e.getPoint();
    if(accepted) {
      v.setCursor(cursor);
    }
  }

  /**
   * unset the 'down' point and change the cursoe back to the system
   * default cursor
   */
  public void mouseReleased(MouseEvent e) {
    down = null;
    v.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }

  /**
   * chack the modifiers. If accepted, translate the graph according
   * to the dragging of the mouse pointer
   * @param e the event
   */
  public void mouseDragged(MouseEvent e) {
    boolean accepted = checkModifiers(e);
    if(accepted) {
      try {
        Point2D q = v.inverseTransform(down);
        Point2D p = v.inverseTransform(e.getPoint());
        int dx = (int) (p.getX()-q.getX());
        int dy = (int) (p.getY()-q.getY());
        v.shiftBuffer(dx, dy);
        down.x = e.getX();
        down.y = e.getY();
      } catch(RuntimeException ex) {
        System.err.println("down = "+down+", e = "+e);
        throw ex;
      }

      e.consume();
    }
  }

  public void mouseClicked(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mouseMoved(MouseEvent e) {
  }
}

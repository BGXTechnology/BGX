package ru.zsoft.jung.viewer;

import edu.uci.ics.jung.visualization.control.RotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;

import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Class OutlineRotatingPlugin
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class OutlineRotatingPlugin extends RotatingGraphMousePlugin {

  private BufferedViewer v;
  private OutlinePaintable outlinePaintable;
  private boolean dragged = false;
  private Cursor cursor = null;

  public OutlineRotatingPlugin(BufferedViewer v) {
    super();
    this.v = v;
    this.outlinePaintable = new OutlinePaintable(v);
  }

  public OutlineRotatingPlugin(BufferedViewer v, int modifiers) {
    super(modifiers);
    this.v = v;
    this.outlinePaintable = new OutlinePaintable(v);
  }

  public void mousePressed(MouseEvent e) {
    boolean accepted = checkModifiers(e);
    if(accepted) {
      down = e.getPoint();
      cursor = v.getCursor();
      v.setCursor(cursor);
      dragged = false;
      outlinePaintable.setDown(down);
      v.addDirectPaintable(outlinePaintable);
    }
  }

  public void mouseReleased(MouseEvent e) {
    if(down == null) return;
    v.setCursor(cursor);
    v.removeDirectPaintable(outlinePaintable);
    if (dragged) v.unlock();
    e.consume();
    down = null;
  }

  public void mouseDragged(MouseEvent e) {
    if(down == null) return;
    rotateTransform(e.getPoint());
    down.x = e.getX();
    down.y = e.getY();
    v.clearDirectVertexCache();
    dragged = true;
    e.consume();
  }

  private void rotateTransform(Point p) {
    MutableTransformer modelTransformer = v.getLayoutTransformer();
    Point2D center = v.getCenter();
    Point2D q = down;
    Point2D v1 = new Point2D.Double(center.getX()-p.getX(), center.getY()-p.getY());
    Point2D v2 = new Point2D.Double(center.getX()-q.getX(), center.getY()-q.getY());
    double theta = angleBetween(v1, v2);
    modelTransformer.rotate(theta, v.inverseViewTransform(center));
  }

}

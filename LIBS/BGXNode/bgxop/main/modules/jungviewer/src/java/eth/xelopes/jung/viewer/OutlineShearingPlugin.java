package ru.zsoft.jung.viewer;

import edu.uci.ics.jung.visualization.control.ShearingGraphMousePlugin;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Class OutlineShearingPlugin
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class OutlineShearingPlugin extends ShearingGraphMousePlugin {

  private BufferedViewer v;
  private OutlinePaintable outlinePaintable;
  private boolean dragged = false;
  private Cursor cursor = null;

  public OutlineShearingPlugin(BufferedViewer v) {
    super();
    this.v = v;
    this.outlinePaintable = new OutlinePaintable(v);
  }

  public OutlineShearingPlugin(BufferedViewer v, int modifiers) {
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
      v.lock();
      dragged = false;
      outlinePaintable.setDown(down);
      v.addDirectPaintable(outlinePaintable);
    }
  }

  public void mouseReleased(MouseEvent e) {
    if (down==null) return;
    v.setCursor(cursor);
    v.removeDirectPaintable(outlinePaintable);
    if (dragged) v.unlock();
    e.consume();
    down = null;
  }

  public void mouseDragged(MouseEvent e) {
    if(down == null) return;
    shearTransform(e.getPoint());
    down.x = e.getX();
    down.y = e.getY();
    v.clearDirectVertexCache();
    dragged = true;
    e.consume();
  }

  protected void shearTransform(Point p) {
    MutableTransformer modelTransformer = v.getLayoutTransformer();
    float dx = (float) (p.getX()-down.getX());
    float dy = (float) (p.getY()-down.getY());

    Dimension d = v.getSize();
    float shx = 2.f*dx/d.height;
    float shy = 2.f*dy/d.width;
    Point2D center = v.getCenter();
    if(p.getX() < center.getX()) {
      shy = -shy;
    }
    if(p.getY() < center.getY()) {
      shx = -shx;
    }
    modelTransformer.shear(shx, shy, center);
  }

}

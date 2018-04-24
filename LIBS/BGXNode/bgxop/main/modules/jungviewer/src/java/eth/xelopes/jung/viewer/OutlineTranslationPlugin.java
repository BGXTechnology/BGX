package ru.zsoft.jung.viewer;

import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;

import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Class OutlineTranslationPlugin
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class OutlineTranslationPlugin extends TranslatingGraphMousePlugin {

  private BufferedViewer v = null;
  private OutlinePaintable outlinePaintable;
  private boolean dragged = false;
  private Cursor cursor = null;

  public OutlineTranslationPlugin(BufferedViewer v) {
    super();
    this.v = v;
    this.outlinePaintable = new OutlinePaintable(v);
  }

  public OutlineTranslationPlugin(BufferedViewer v, int modifiers) {
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
/*
    if (down==null) return;
    v.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    v.unlock();
    v.removeDirectPaintable(outlinePaintable);
    MutableTransformer modelTransformer = v.getLayoutTransformer();
    v.setCursor(cursor);
    try {
      Point2D q = v.inverseTransform(down);
      Point2D p = v.inverseTransform(e.getPoint());
      float dx = (float) (p.getX()-q.getX());
      float dy = (float) (p.getY()-q.getY());
      modelTransformer.translate(dx, dy);
      down.x = e.getX();
      down.y = e.getY();
    } catch(RuntimeException ex) {
      System.err.println("down = "+down+", e = "+e);
      throw ex;
    }
    e.consume();
    down = null;
*/
    if (down==null) return;
    v.setCursor(cursor);
    v.removeDirectPaintable(outlinePaintable);
    if (dragged) v.unlock();
    e.consume();
    down = null;

  }

  public void mouseDragged(MouseEvent e) {
    if (down==null) return;
    translateTransform(e.getPoint());
    v.clearDirectVertexCache();
    dragged = true;
    e.consume();
  }

  private void translateTransform(Point p) {
    MutableTransformer modelTransformer = v.getLayoutTransformer();
    v.setCursor(cursor);
    try {
      Point2D q = v.inverseTransform(down);
      Point2D po = v.inverseTransform(p);
      float dx = (float) (po.getX()-q.getX());
      float dy = (float) (po.getY()-q.getY());

      modelTransformer.translate(dx, dy);
      down.x = p.x;
      down.y = p.y;
    } catch(RuntimeException ex) {
      System.err.println("down = "+down+", current = "+p);
      throw ex;
    }

  }

}

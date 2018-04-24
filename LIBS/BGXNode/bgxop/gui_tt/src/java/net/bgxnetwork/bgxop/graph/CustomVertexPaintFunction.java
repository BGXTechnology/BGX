package net.bgx.bgxnetwork.bgxop.graph;

import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.PluggableRenderer;

import java.awt.*;
import java.awt.geom.Point2D;

import net.bgx.bgxnetwork.bgxop.tools.GraphDataUtil;
import ru.zsoft.jung.viewer.BufferedViewer;
import ru.zsoft.jung.viewer.CustomShapeFunction;

/**
 * Class MyVertexPaintFunction
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class CustomVertexPaintFunction implements VertexPaintFunction {
  private static final String key_cache = "paintCache";
    //цвет невыбранного узла
  protected static final Color normal = new Color(200, 230, 250);
    //цвет выбранного узла
  protected static final Color selected = new Color(255, 211, 132);
    // цвет соседнего с выбранным узла
  protected static final Color highlight = new Color(125, 255, 98);
    // цвет узла - исходного для запроса
  protected static final Color marker = new Color(254, 98, 247);

  private BufferedViewer view;

  public CustomVertexPaintFunction(BufferedViewer view) {
    this.view = view;
  }

  public Paint getDrawPaint(Vertex vertex) {
    return Color.black;
  }

  public Paint getFillPaint(Vertex v) {
    Color c;
    if (view.getPickedState().isPicked(v)) c = selected;
    else if (GraphDataUtil.getMarked(v)) c = marker;
    else if (GraphDataUtil.getHighlighted(v)) c = highlight;
    else {
      c = GraphDataUtil.getColor(v);
      if (c==null) c = normal;
    }
    //  для прямоугольника
   // Rectangle r = ((CustomShapeFunction)((PluggableRenderer)view.getRenderer()).getVertexShapeFunction()).getEdgeShape(v).getBounds();
      Rectangle r  = ((PluggableRenderer)view.getRenderer()).getVertexShapeFunction().getShape(v).getBounds();
    Point2D loc = view.getBufferedVertexPosition(v);
    int x = (int) loc.getX();
    int y = (int) loc.getY();
    Color bg = view.getBackground();

    CacheEntry e = (CacheEntry) v.getUserDatum(key_cache);
    if (e!=null && e.paint.getColor1().equals(bg) && e.paint.getColor2().equals(c)
        && e.x==x && e.y==y && e.h==r.height && e.w==r.width) return e.paint;

    //cache not hit
    if (e==null) e = new CacheEntry();
    GradientPaint paint = new GradientPaint(x+r.x, y+r.y, bg, x+r.x, y+r.y+r.height, c);
    e.set(paint, x, y, r.width, r.height);
    v.setUserDatum(key_cache, e, GraphDataUtil.clone);

    return paint;
  }

  public static class CacheEntry implements Cloneable{
    protected GradientPaint paint;
    protected int x;
    protected int y;
    protected int w;
    protected int h;

    public CacheEntry() {
    }

    public CacheEntry(GradientPaint paint, int x, int y, int w, int h) {
      set(paint, x, y, w, h);
    }

    public void set(GradientPaint paint, int x, int y, int w, int h) {
      this.paint = paint;
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
    }

      public Object clone() throws CloneNotSupportedException {
          CacheEntry cacheEntry = (CacheEntry)super.clone();
          cacheEntry.paint=this.paint;
          cacheEntry.x=this.x;
          cacheEntry.y=this.y;
          cacheEntry.w=this.w;
          cacheEntry.h=this.h;
          return cacheEntry;
      }

  }
}

package ru.zsoft.jung.viewer;

import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.PickedState;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.graph.decorators.VertexShapeFunction;
import edu.uci.ics.jung.graph.Vertex;

import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Iterator;

/**
 * Class OutlinePickingPlugin
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class OutlinePickingPlugin extends PickingGraphMousePlugin implements VisualizationViewer.Paintable {
  private MouseEvent draggedEvent = null;
  private BufferedViewer v = null;

  public OutlinePickingPlugin(BufferedViewer v) {
    this.v = v;
  }

  public OutlinePickingPlugin(BufferedViewer v, int modifiers, int addModifiers) {
    super(modifiers, addModifiers);
    this.v = v;
  }

  public void mousePressed(MouseEvent event) {
    if (event.getModifiers()==modifiers || event.getModifiers()==addToSelectionModifiers) {
      super.mousePressed(event);
      draggedEvent = null;
      v.addDirectPaintable(lensPaintable);
      v.addDirectPaintable(this);
    }
  }

  public void mouseDragged(MouseEvent event) {
    if (down==null) return;
    draggedEvent = event;
    event.consume();
  }

  public void mouseReleased(MouseEvent event) {
    if (down==null) return;
    v.removeDirectPaintable(lensPaintable);
    v.removeDirectPaintable(this);
    boolean positionChanged = (draggedEvent!=null);
    if (positionChanged) {
      v.unlock();
      super.mouseDragged(draggedEvent);
      draggedEvent = null;
    }
    super.mouseReleased(event);
    event.consume();
  }

  public void paint(Graphics g) {
    if (draggedEvent==null) return;
    Graphics2D g2d = (Graphics2D) g;
    if(!locked) {
      if(vertex != null) {
        Point p = draggedEvent.getPoint();
        Point2D graphPoint = v.inverseTransform(p);
        Point2D graphDown = v.inverseTransform(down);
        PluggableRenderer r = (PluggableRenderer) v.getRenderer();
        VertexShapeFunction sf = r.getVertexShapeFunction();
        double dx = graphPoint.getX() - graphDown.getX();
        double dy = graphPoint.getY() - graphDown.getY();
        PickedState ps = v.getPickedState();

        Paint paint = g2d.getPaint();
        g2d.setPaint(Color.RED);
        Vertex vx;
        Point2D lp, vp, dp;
        for(Iterator iterator=ps.getPickedVertices().iterator(); iterator.hasNext(); ) {
          vx = (Vertex)iterator.next();
          lp = v.getGraphLayout().getLocation(vx);
          vp = new Point2D.Double(lp.getX()+dx, lp.getY()+dy);
          dp = v.getLayoutTransformer().transform(vp);
          g2d.translate(dp.getX(), dp.getY());
          g2d.draw(sf.getShape(vx));
          g2d.translate(-dp.getX(), -dp.getY());
        }
        g2d.setPaint(paint);
      } else {
        Point2D out = draggedEvent.getPoint();
        if(draggedEvent.getModifiers() == this.addToSelectionModifiers ||
            draggedEvent.getModifiers() == modifiers) {
          rect.setFrameFromDiagonal(down,out);
        }
      }
    }
  }

  public boolean useTransform() {
    return true;
  }

}

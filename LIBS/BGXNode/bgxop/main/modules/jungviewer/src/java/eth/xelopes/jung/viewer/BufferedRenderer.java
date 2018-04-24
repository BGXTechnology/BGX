package ru.zsoft.jung.viewer;

import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.UndirectedEdge;
import edu.uci.ics.jung.graph.decorators.ConstantDirectionalEdgeValue;
import edu.uci.ics.jung.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;

/**
 * Class BufferedRenderer
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class BufferedRenderer extends PluggableRenderer {
  private Rectangle deviceRectangle = null;

  public BufferedRenderer() {
  }

  public void setDeviceRectangle(Rectangle deviceRectangle) {
    this.deviceRectangle = deviceRectangle;
  }

/*
  public void setDeviceSize(Dimension d) {
    deviceRectangle = new Rectangle(0, 0, d.width, d.height);
  }

  protected Rectangle getDeviceRectangle(Graphics2D g) {
    Dimension d = null;
    if (deviceRectangle != null) d = new Dimension(deviceRectangle.width, deviceRectangle.height);

    if (d==null && screenDevice != null) {
      d = screenDevice.getSize();
      if (d.width <= 0 || d.height <= 0)
        d = screenDevice.getPreferredSize();
    }
    if (d==null) return null;

    AffineTransform af = g.getTransform();
    Rectangle rectangle2 =
        new Rectangle((int) af.getTranslateX(), (int) af.getTranslateY(), d.width, d.height);
    Rectangle rectangle1 = new Rectangle(
        0, 0,
        d.width, d.height);
    return rectangle1.union(rectangle2);
  }
*/

  protected Rectangle getDeviceRectangle(Graphics2D g) {
    if (deviceRectangle != null) return deviceRectangle;

    Dimension d = null;
    if (screenDevice != null) {
      d = screenDevice.getSize();
      if (d.width <= 0 || d.height <= 0)
        d = screenDevice.getPreferredSize();
    }
    if (d==null) return null;

    AffineTransform af = g.getTransform();
    Rectangle rectangle2 =
        new Rectangle((int) af.getTranslateX(), (int) af.getTranslateY(), d.width, d.height);
    Rectangle rectangle1 = new Rectangle(
        0, 0,
        d.width, d.height);
    return rectangle1.union(rectangle2);
  }

  protected void drawSimpleEdge(Graphics2D g, Edge e, int x1, int y1, int x2, int y2) {
    Pair endpoints = e.getEndpoints();
    Vertex v1 = (Vertex) endpoints.getFirst();
    Vertex v2 = (Vertex) endpoints.getSecond();
    boolean isLoop = v1.equals(v2);
    Shape s2 = vertexShapeFunction.getShape(v2);
    Shape edgeShape = edgeShapeFunction.getShape(e);

    boolean edgeHit = true;
    boolean arrowHit = true;

    //*** CHANGED
    Rectangle deviceRectangle = getDeviceRectangle(g);
    //*** END CHANGED

    AffineTransform xform = AffineTransform.getTranslateInstance(x1, y1);

    if (isLoop) {
      // this is a self-loop. scale it is larger than the vertex
      // it decorates and translate it so that its nadir is
      // at the center of the vertex.
      Rectangle2D s2Bounds = s2.getBounds2D();
      xform.scale(s2Bounds.getWidth(), s2Bounds.getHeight());
      xform.translate(0, -edgeShape.getBounds2D().getWidth() / 2);
    } else {
      // this is a normal edge. Rotate it to the angle between
      // vertex endpoints, then scale it to the distance between
      // the vertices
      float dx = x2 - x1;
      float dy = y2 - y1;
      float thetaRadians = (float) Math.atan2(dy, dx);
      xform.rotate(thetaRadians);
      float dist = (float) Math.sqrt(dx * dx + dy * dy);
      xform.scale(dist / edgeShape.getBounds().getWidth(), 1.0);
    }

    edgeShape = xform.createTransformedShape(edgeShape);

    edgeHit = g.hit(deviceRectangle, edgeShape, true);
    if (edgeHit == true) {

      Paint oldPaint = g.getPaint();

      // get Paints for filling and drawing
      // (filling is done first so that drawing and label use same Paint)
      Paint fill_paint = edgePaintFunction.getFillPaint(e);
      if (fill_paint != null) {
        g.setPaint(fill_paint);
        g.fill(edgeShape);
      }
      Paint draw_paint = edgePaintFunction.getDrawPaint(e);
      if (draw_paint != null) {
        g.setPaint(draw_paint);
        g.draw(edgeShape);
      }

      float scalex = (float) g.getTransform().getScaleX();
      float scaley = (float) g.getTransform().getScaleY();
      // see if arrows are too small to bother drawing
      if (scalex < .3 || scaley < .3) return;

      if (edgeArrowPredicate.evaluate(e)) {

        Shape destVertexShape =
            vertexShapeFunction.getShape((Vertex) e.getEndpoints().getSecond());
        AffineTransform xf = AffineTransform.getTranslateInstance(x2, y2);
        destVertexShape = xf.createTransformedShape(destVertexShape);

        arrowHit = g.hit(deviceRectangle, destVertexShape, true);
        if (arrowHit) {

          AffineTransform at =
              getArrowTransform(new GeneralPath(edgeShape), destVertexShape);

          if (at == null) return;
          Shape arrow = edgeArrowFunction.getArrow(e);
          arrow = at.createTransformedShape(arrow);
          // note that arrows implicitly use the edge's draw paint
          g.fill(arrow);
          if (e instanceof UndirectedEdge) {
            Shape vertexShape =
                vertexShapeFunction.getShape((Vertex) e.getEndpoints().getFirst());
            xf = AffineTransform.getTranslateInstance(x1, y1);
            vertexShape = xf.createTransformedShape(vertexShape);

            arrowHit = g.hit(deviceRectangle, vertexShape, true);

            if (arrowHit) {
              at = getReverseArrowTransform(new GeneralPath (edgeShape), vertexShape, !isLoop);

              if (at == null) return;
              arrow = edgeArrowFunction.getArrow(e);
              arrow = at.createTransformedShape(arrow);
              g.fill(arrow);
            }
          }
        }
      }
      // use existing paint for text if no draw paint specified
      if (draw_paint == null)
        g.setPaint(oldPaint);
      String label = edgeStringer.getLabel(e);
      if (label != null) {
        labelEdge(g, e, label, x1, x2, y1, y2);
      }

      // restore old paint
      g.setPaint(oldPaint);
    }
  }

  public void paintVertex(Graphics g, Vertex v, int x, int y) {
    if (!vertexIncludePredicate.evaluate(v))
      return;

    boolean vertexHit = true;
    Graphics2D g2d = (Graphics2D) g;
    //*** CHANGED
    Rectangle deviceRectangle = getDeviceRectangle(g2d);
    //*** END CHANGED


    Stroke old_stroke = g2d.getStroke();
    Stroke new_stroke = vertexStrokeFunction.getStroke(v);
    if (new_stroke != null) {
      g2d.setStroke(new_stroke);
    }
    // todo restangle get the shape to be rendered
    //Shape s = ((CustomShapeFunction) vertexShapeFunction).getEdgeShape(v);
      Shape s = vertexShapeFunction.getShape(v);

    // create a transform that translates to the location of
    // the vertex to be rendered
    AffineTransform xform = AffineTransform.getTranslateInstance(x, y);
    // transform the vertex shape with xtransform
    s = xform.createTransformedShape(s);

    vertexHit = g2d.hit(deviceRectangle, s, true);

    if (vertexHit) {
      if (vertexIconFunction != null) {
        paintIconForVertex(g2d, v, x, y);
      } else {
        paintShapeForVertex(g2d, v, s);
      }

      if (new_stroke != null) {
        g2d.setStroke(old_stroke);
      }
      String label = vertexStringer.getLabel(v);
      if (label != null) {
        labelVertex(g, v, label, x, y);
      }
    }
  }
    protected void labelEdge(Graphics2D g2d, Edge e, String label, int x1, int x2, int y1, int y2){
        int distX = x2 - x1;
        int distY = y2 - y1;
        double totalLength = Math.sqrt(distX * distX + distY * distY);
        double closeness = new ConstantDirectionalEdgeValue(0.4, 0.4).getNumber(e).doubleValue();
        int posX = (int) (x1 + (closeness) * distX);
        int posY = (int) (y1 + (closeness) * distY);
        int xDisplacement = (int) (LABEL_OFFSET * (distY / totalLength));
        int yDisplacement = (int) (LABEL_OFFSET * (-distX / totalLength));
        Component component = prepareRenderer(graphLabelRenderer, label, getPickedKey().isPicked(e), e);

        Dimension d = component.getPreferredSize();
        Shape edgeShape = edgeShapeFunction.getShape(e);
        double parallelOffset = 1;
        parallelOffset += parallelEdgeIndexFunction.getIndex(e);
        if(edgeShape instanceof Ellipse2D){
            parallelOffset += edgeShape.getBounds().getHeight();
            parallelOffset = -parallelOffset;
        }
        parallelOffset *= d.height;
        AffineTransform old = g2d.getTransform();
        AffineTransform xform = new AffineTransform(old);
        xform.translate(posX + xDisplacement, posY + yDisplacement);
        double dx = x2 - x1;
        double dy = y2 - y1;
        if(graphLabelRenderer.isRotateEdgeLabels()){
            double theta = Math.atan2(dy, dx);
            if(dx < 0){
                theta += Math.PI;
            }
            xform.rotate(theta);
        }
        if(dx < 0){
            parallelOffset = -parallelOffset;
        }
        xform.translate(-d.width / 2, -(d.height / 2 - parallelOffset));
        g2d.setTransform(xform);
        rendererPane.paintComponent(g2d, component, screenDevice, 0, 0, d.width, d.height, true);
        g2d.setTransform(old);
    }
}

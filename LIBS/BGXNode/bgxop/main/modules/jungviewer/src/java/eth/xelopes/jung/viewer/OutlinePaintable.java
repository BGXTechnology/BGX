package ru.zsoft.jung.viewer;

import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.graph.decorators.VertexShapeFunction;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Iterator;

import ru.zsoft.jung.viewer.BufferedViewer;

/**
 * Class OutlinePaintable
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class OutlinePaintable implements VisualizationViewer.Paintable {
    private BufferedViewer v;
    private Point down = null;

    public OutlinePaintable(BufferedViewer v) {
        this(v, null);
    }

    public OutlinePaintable(BufferedViewer v, Point down) {
        this.v = v;
        this.down = down;
    }

    public void setDown(Point down) {
        this.down = down;
    }

    public void paint(Graphics g) {
        if (down == null) return;
        Graphics2D g2d = (Graphics2D) g;
        PluggableRenderer r = (PluggableRenderer) v.getRenderer();
        VertexShapeFunction sf = r.getVertexShapeFunction();
        Graph graph = v.getGraphLayout().getGraph();

        Paint paint = g2d.getPaint();
        g2d.setColor(Color.RED);
        Point2D vp;
        Vertex vx;
        for (Iterator iterator = graph.getVertices().iterator(); iterator.hasNext();) {
            vx = (Vertex) iterator.next();
            vp = v.getDirectVertexPosition(vx);
            if (vp != null) {
                g2d.translate(vp.getX(), vp.getY());
                g2d.draw(sf.getShape(vx));
                g2d.translate(-vp.getX(), -vp.getY());
            }
        }
        g2d.setPaint(paint);
    }

    public boolean useTransform() {
        return true;
    }

}

package ru.zsoft.jung.viewer;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.algorithms.blockmodel.GraphCollapser;
import net.bgx.bgxnetwork.bgxop.tools.GraphDataUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Class MyViewer
 *
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class BufferedViewer extends VisualizationViewer {
    private boolean lock = false;
    private ArrayList<Paintable> directPaintables = new ArrayList<Paintable>();
    private Point shiftPoint = new Point();
    private AffineTransform preXfrom = null, Xfrom = null;
    private PickedState emptyState = new MultiPickedState();
    private Point2D.Double upLeft = new Point2D.Double();
    private Point2D.Double bottomRight = new Point2D.Double();
    private int screenshotFrameSize = 20;
    private int defaultNumberEdgeAttributes = 10;

    public BufferedViewer(Layout layout, BufferedRenderer renderer) {
        super(layout, renderer);
        setDoubleBuffered(true);
        setPickSupport(new ShapePickSupport());
        setGraphMouse(new BufferedMouse(this));
        addDirectPaintable(new SelectionPaintable());
    }

    public void zoomIn() {
        if (graphMouse instanceof BufferedMouse)
            ((BufferedMouse) graphMouse).zoomIn();
    }

    public void zoomOut() {
        if (graphMouse instanceof BufferedMouse)
            ((BufferedMouse) graphMouse).zoomOut();
    }

    public BufferedImage getScreenshot() {
        Vertex maxVertex = findBounds();
        if (maxVertex == null) {
            return null;
        }

        int edgeHeight = getMaxLinkHeight();

        BufferedRenderer renderer = (BufferedRenderer) getRenderer();
        Rectangle maxRectangle = renderer.getVertexShapeFunction().getShape(maxVertex).getBounds();
        // todo для rectangle Label
        //Rectangle labelRectangle = ((CustomShapeFunction)renderer.getVertexShapeFunction()).getEdgeShape(null).getBounds();
        Rectangle labelRectangle = new Rectangle2D.Float(-60, -25, 125, 32).getBounds();
        int dx = (int) Math.ceil(labelRectangle.width / 2);
        int dy = (int) Math.ceil(labelRectangle.height / 2);
        upLeft.x -= dx + screenshotFrameSize + edgeHeight;
        upLeft.y -= dy + screenshotFrameSize + edgeHeight;
        bottomRight.x += dx + screenshotFrameSize + edgeHeight;
        bottomRight.y += maxRectangle.height - dy + screenshotFrameSize + edgeHeight;
        int mx = (int) Math.floor(upLeft.x);
        int my = (int) Math.floor(upLeft.y);
        int width = (int) Math.ceil(bottomRight.x) - mx;
        int height = (int) Math.ceil(bottomRight.y) - my;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.translate(-mx, -my);
        width += Math.abs(mx);
        height += Math.abs(my);
        if (mx > 0) {
            mx = 0;
        }
        if (my > 0) {
            my = 0;
        }

        renderer.setDeviceRectangle(new Rectangle(mx, my, width, height));
        paintBackground(g2d, true);
        renderGraph(g2d, false);
        renderer.setDeviceRectangle(null);
        return image;
    }

    /*
     * public BufferedImage getScreenshot() { Dimension d = getSize();
     * BufferedImage image = new BufferedImage(d.width, d.height,
     * BufferedImage.TYPE_INT_ARGB); Graphics2D g2d = (Graphics2D)
     * image.getGraphics(); Color c = getBackground();
     * setBackground(Color.WHITE); renderGraph(g2d, true); setBackground(c); //
     * layoutTransformer = trans; // new TestScreenshotFrame(image); return
     * image; }
     */
    protected Vertex findBounds() {
        Layout layout = getGraphLayout();
        upLeft.x = Double.MAX_VALUE;
        upLeft.y = Double.MAX_VALUE;
        bottomRight.x = -Double.MAX_VALUE;
        bottomRight.y = -Double.MAX_VALUE;
        Point2D pos;
        Vertex v = null;
        Vertex maxHeightVertex = null;
        for (Iterator it = layout.getGraph().getVertices().iterator(); it.hasNext();) {
            v = (Vertex) it.next();
            if (v == null) continue;
            if (!GraphDataUtil.getVisible(v)) continue;

            maxHeightVertex = v;
            pos = getDirectVertexPosition(v);
            if (pos != null) {
                if (pos.getX() < upLeft.getX())
                    upLeft.x = pos.getX();
                if (pos.getX() > bottomRight.getX())
                    bottomRight.x = pos.getX();
                if (pos.getY() < upLeft.getY())
                    upLeft.y = pos.getY();
                if (pos.getY() > bottomRight.getY())
                    bottomRight.y = pos.getY();
            }
        }
        return maxHeightVertex;
    }

    protected int getMaxLinkHeight() {
        Font eFont = UIManager.getFont("graph.edge.font");
        return eFont.getSize() * defaultNumberEdgeAttributes;
    }

    public void lock() {
        lock = true;
    }

    public void unlock() {
        lock = false;
    }

    public void addDirectPaintable(Paintable paintable) {
        directPaintables.add(paintable);
    }

    public void removeDirectPaintable(Paintable paintable) {
        directPaintables.remove(paintable);
    }

    // public void addOverBufferPaintable(Paintable paintable) {
    // overBufferPaintables.add(paintable);
    // }
    //
    // public void removeOverBufferPaintable(Paintable paintable) {
    // overBufferPaintables.remove(paintable);
    // }
    public Point getBufferShiftPoint() {
        return shiftPoint;
    }

    public void setBufferShiftPoint(Point shiftPoint) {
        this.shiftPoint = shiftPoint;
    }

    public void shiftBuffer(int dx, int dy) {
        shiftPoint.x += dx;
        shiftPoint.y += dy;
    }

    public Point2D getDirectVertexPosition(Vertex v) {
        // Point2D p = directLocationMap.get(v);
        // if(p == null) {
        // todo если нет координат вершины значит это узел групповой и его нужно создать.
        Point2D p = getGraphLayout().getLocation(v);
        if (p == null) {
            Vertex other = getLocationGroupedVertex(v);
            if (other != null) {
                p = getGraphLayout().getLocation(other);
                v.importUserData(other);
            }
        }
        if (p == null)
            return p;

        p = layoutTransformer.transform(p);
        if (p == null)
            return p;
        // directLocationMap.put(v, p);
        // }
        return p;
    }

    private Vertex getLocationGroupedVertex(Vertex vertex) {
        if (vertex instanceof GraphCollapser.CollapsedVertex) {
            Set<Vertex> vertices = getVertexesFrom(vertex);
            Point2D p = null;
            for (Vertex v : vertices) {
                p = getGraphLayout().getLocation(v);
                if (p != null)
                    return v;
            }
            return null;
        } else
            return null;
    }

    public Set<Vertex> getVertexesFrom(Vertex collapsedVertex) {
        HashSet<Vertex> res = new HashSet<Vertex>();
        if (collapsedVertex instanceof GraphCollapser.CollapsedVertex) {
            for (Vertex v : (Set<Vertex>) ((GraphCollapser.CollapsedVertex) collapsedVertex).getRootSet()) {
                if (v instanceof GraphCollapser.CollapsedVertex)
                    res.addAll(getVertexesFrom(v));
                else
                    res.add(v);
            }
        }
        return res;
    }

    public Point2D getBufferedVertexPosition(Vertex v) {
        Point2D p = (Point2D) locationMap.get(v);
        if (p == null) {
            p = getGraphLayout().getLocation(v);
            if (p == null) {
                Vertex other = getLocationGroupedVertex(v);
                if (other != null) {
                    p = getGraphLayout().getLocation(other);
                    v.importUserData(other);
                }
            }
            if (p == null)
                return p;
            p = layoutTransformer.transform(p);
            locationMap.put(v, p);
        }
        return p;
    }


    public void clearDirectVertexCache
            () {
        // directLocationMap.clear();
    }

    public void clearBufferedVertexCache
            () {
        locationMap.clear();
    }


    protected void checkOffscreenImage
            (Dimension
                    dimension) {
        BufferedImage buffer = offscreen;
// do not refresh buffer image if picture is locked
        super.checkOffscreenImage(dimension);
        if (buffer != offscreen)
            lock = false;
    }

    protected void paintBackground
            (Graphics2D
                    g2d, boolean forPrint) {
        if (forPrint) {
            BufferedRenderer renderer = (BufferedRenderer) getRenderer();
            Rectangle r = renderer.getDeviceRectangle(g2d);
            g2d.setColor(Color.white);
            g2d.fillRect(r.x, r.y, r.width, r.height);
        } else {
            getUI().update(g2d, this);
        }
    }

    protected synchronized void paintComponent
            (Graphics
                    g) {
        // the size of the VisualizationViewer
        Graphics2D g2d = (Graphics2D) g;
// clear the offscreen image
        paintBackground((Graphics2D) g, false);
// paint buffered image
        preXfrom = g2d.getTransform();
        Xfrom = new AffineTransform(preXfrom);
        Xfrom.concatenate(viewTransformer.getTransform());
        if (!lock) {
            checkOffscreenImage(getSize());
            paintBackground(offscreenG2d, false);
            renderGraph(offscreenG2d, true);
        }
        g2d.drawImage(offscreen, null, 0, 0);
// //additional over-buffer paint if needed
// for (Paintable p : overBufferPaintables) {
// if (p.useTransform()) g2d.setTransform(bufferXfrom);
// p.paint(g);
// if (p.useTransform()) g2d.setTransform(preXfrom);
// }
// additional direct paint if needed
        for (Paintable p : directPaintables) {
            if (p.useTransform())
                g2d.setTransform(Xfrom);
            p.paint(g);
            if (p.useTransform())
                g2d.setTransform(preXfrom);
        }
    }

    protected void renderGraph
            (Graphics2D
                    g2d, boolean doTransform) {
        Layout layout = model.getGraphLayout();
        g2d.setRenderingHints(renderingHints);
        PickedState realState = getPickedState();
        setPickedState(emptyState);
        BufferedRenderer renderer = (BufferedRenderer) getRenderer();
        AffineTransform preBufferXfrom = g2d.getTransform();
        AffineTransform bufferXfrom = new AffineTransform(preBufferXfrom);
        if (doTransform)
            bufferXfrom.concatenate(viewTransformer.getTransform());
        g2d.setTransform(bufferXfrom);
// if there are preRenderers set, paint them
        for (Object o : preRenderers) {
            Paintable paintable = (Paintable) o;
            if (paintable.useTransform()) {
                paintable.paint(g2d);
            } else {
                g2d.setTransform(preBufferXfrom);
                paintable.paint(g2d);
                g2d.setTransform(bufferXfrom);
            }
        }
        clearDirectVertexCache();
        clearBufferedVertexCache();
// paint all the edges
        try {
            for (Object o : layout.getGraph().getEdges()) {
                Edge e = (Edge) o;
                Vertex v1 = (Vertex) e.getEndpoints().getFirst();
                Vertex v2 = (Vertex) e.getEndpoints().getSecond();
                Point2D p = getBufferedVertexPosition(v1);
                Point2D q = getBufferedVertexPosition(v2);
                if (p != null && q != null) {
                    renderer.paintEdge(g2d, e, (int) p.getX(), (int) p.getY(), (int) q.getX(), (int) q.getY());
                }
            }
        }
        catch (ConcurrentModificationException cme) {
            repaint();
        }
        // paint all the vertices
        try {
            for (Object o : layout.getGraph().getVertices()) {
                Vertex v = (Vertex) o;
                Point2D p = getBufferedVertexPosition(v);
                if (p != null) {
                    renderer.paintVertex(g2d, v, (int) p.getX(), (int) p.getY());
                }
            }
        }
        catch (ConcurrentModificationException cme) {
            repaint();
        }
        // if there are postRenderers set, do it
        for (Object o : postRenderers) {
            Paintable paintable = (Paintable) o;
            if (paintable.useTransform()) {
                paintable.paint(g2d);
            } else {
                g2d.setTransform(preBufferXfrom);
                paintable.paint(g2d);
                g2d.setTransform(bufferXfrom);
            }
        }
        g2d.setTransform(preBufferXfrom);
        setPickedState(realState);
        lock();
    }

    protected class SelectionPaintable implements Paintable {
        public void paint(Graphics g) {
            // paint selected edges
            for (Object o : getPickedState().getPickedEdges()) {
                Edge e = (Edge) o;
                Vertex v1 = (Vertex) e.getEndpoints().getFirst();
                Vertex v2 = (Vertex) e.getEndpoints().getSecond();
                Point2D p = getBufferedVertexPosition(v1);
                Point2D q = getBufferedVertexPosition(v2);
                if (p != null && q != null) {
                    renderer.paintEdge(g, e, (int) p.getX(), (int) p.getY(), (int) q.getX(), (int) q.getY());
                    renderer.paintVertex(g, v1, (int) p.getX(), (int) p.getY());
                    renderer.paintVertex(g, v2, (int) q.getX(), (int) q.getY());
                }
            }

            // paint selected vertices

            for (Object o : getPickedState().getPickedVertices()) {
                Vertex v = (Vertex) o;
                Point2D p = getBufferedVertexPosition(v);
                if (p != null)
                    renderer.paintVertex(g, v, (int) p.getX(), (int) p.getY());
            }


        }


        public boolean useTransform() {
            return true;
        }
    }


}
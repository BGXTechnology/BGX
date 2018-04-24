package ru.zsoft.jung.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Iterator;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.Layout;

public class GraphScrollPane extends JPanel {
    protected BufferedViewer vv;
    protected JScrollBar hsb;
    protected JScrollBar vsb;
    protected JComponent corner;
    protected boolean scrollBarsMayControlAdjusting = true;
    protected JPanel south;
    protected float normal = 0;
    private Double width;
    private Double height;
    private Point2D.Double upLeft = new Point2D.Double();
    private static final int SPACE_FROM_DIM = 10;

    public GraphScrollPane(BufferedViewer v) {
        super(new BorderLayout());
        this.vv = v;
        findBounds();
        addComponentListener(new ResizeListener());
        Dimension d = vv.getGraphLayout().getCurrentSize();
        vsb = new JScrollBar(JScrollBar.VERTICAL, 0, d.height, 0, d.height);
        hsb = new JScrollBar(JScrollBar.HORIZONTAL, 0, d.width, 0, d.width);
        vsb.addAdjustmentListener(new VerticalAdjustmentListenerImpl());
        hsb.addAdjustmentListener(new HorizontalAdjustmentListenerImpl());
        vv.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                BufferedViewer vv = (BufferedViewer) evt.getSource();
                findBounds();
                setScrollBars(vv);
            }
        });
        add(vv);
        vv.addMouseListener(new MouseEventListener());
        add(vsb, BorderLayout.EAST);
        south = new JPanel(new BorderLayout());
        south.add(hsb);
        setCorner(new JPanel());
        add(south, BorderLayout.SOUTH);
    }

    private void setScrollBars(BufferedViewer vv) {
        Dimension d = vv.getSize();
        Point2D vvc = vv.viewTransform(upLeft);
        double x = vv.getCenter().getX() - vvc.getX();
        double y = vv.getCenter().getY() - vvc.getY();
        Point2D lc = new Point2D.Float(width.intValue() / 2, height.intValue() / 2);
        float layoutScale = (float) vv.getLayoutTransformer().getScale();
        float viewScale = (float) vv.getViewTransformer().getScale();
        float scale = layoutScale * viewScale;
        if (normal == 0)
            normal = 1 / scale;
        scale *= normal;
        float xmax = width.intValue();
        float ymax = height.intValue();
        float xmin = 0;
        float ymin = 0;
        vvc = new Point2D.Double(x / scale, y / scale);
        float xext = d.width / scale;
        float yext = d.height / scale;
        float xval = (float) ((xmax - xext) / 2.f + vvc.getX() - lc.getX());
        float yval = (float) ((ymax - yext) / 2.f + vvc.getY() - lc.getY());
        scrollBarsMayControlAdjusting = false;
        hsb.setValues((int) xval, (int) xext, (int) xmin, (int) xmax);
        vsb.setValues((int) yval, (int) yext, (int) ymin, (int) ymax);
        scrollBarsMayControlAdjusting = true;
    }

    public void findBounds() {
        Point2D.Double bottomRight = new Point2D.Double();
        Layout layout = vv.getModel().getGraphLayout();
        upLeft.x = upLeft.y = Double.MAX_VALUE;
        bottomRight.x = bottomRight.y = -Double.MAX_VALUE;
        Point2D pos;
        Vertex v = null;
        for (Iterator it = layout.getGraph().getVertices().iterator(); it.hasNext();) {
            v = (Vertex) it.next();
            pos = vv.getDirectVertexPosition(v);
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
        BufferedRenderer renderer = (BufferedRenderer) vv.getRenderer();
        Rectangle r = renderer.getVertexShapeFunction().getShape(v).getBounds();
        int dx = (int) Math.ceil(r.width / 2);
        int dy = (int) Math.ceil(r.height / 2);
        upLeft.x -= dx + SPACE_FROM_DIM;
        upLeft.y -= dy + SPACE_FROM_DIM;
        bottomRight.x += dx;
        bottomRight.y += dx;
        width = Math.abs(bottomRight.x - upLeft.x);
        height = Math.abs(bottomRight.y - upLeft.y);
    }

    protected class ResizeListener extends ComponentAdapter {
        public void componentHidden(ComponentEvent e) {
        }

        public void componentResized(ComponentEvent e) {
            setScrollBars(vv);
        }

        public void componentShown(ComponentEvent e) {
        }
    }

    public JComponent getCorner() {
        return corner;
    }

    public void setCorner(JComponent corner) {
        this.corner = corner;
        corner.setPreferredSize(new Dimension(vsb.getPreferredSize().width, hsb.getPreferredSize().height));
        south.add(this.corner, BorderLayout.EAST);
    }

    class HorizontalAdjustmentListenerImpl implements AdjustmentListener {
        int previous = 0;

        public void adjustmentValueChanged(AdjustmentEvent e) {
            int hval = e.getValue();
            float dh = previous - hval;
            previous = hval;
            if (dh != 0 && scrollBarsMayControlAdjusting) {
                AffineTransform at = AffineTransform.getTranslateInstance(dh, 0);
                vv.getLayoutTransformer().concatenate(at);
                vv.unlock();
            }
        }
    }

    class VerticalAdjustmentListenerImpl implements AdjustmentListener {
        int previous = 0;

        public void adjustmentValueChanged(AdjustmentEvent e) {
            JScrollBar sb = (JScrollBar) e.getSource();
            BoundedRangeModel m = sb.getModel();
            int vval = m.getValue();
            float dv = previous - vval;
            previous = vval;
            if (dv != 0 && scrollBarsMayControlAdjusting) {
                AffineTransform at = AffineTransform.getTranslateInstance(0, dv);
                vv.getLayoutTransformer().concatenate(at);
                vv.unlock();
            }
        }
    }

    class MouseEventListener implements MouseListener {
        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
            findBounds();
            setScrollBars(vv);
        }
    }
}

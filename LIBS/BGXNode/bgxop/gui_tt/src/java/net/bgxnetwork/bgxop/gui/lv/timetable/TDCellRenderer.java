/*
$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/timedia/TDCellRenderer.java#2 $
$DateTime: 2007/07/04 16:44:07 $
$Change: 19378 $
$Author: O.Lukashevich $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.timetable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import net.bgx.bgxnetwork.bgxop.gui.lv.tools.LVGraphNetworkUtil;
import net.bgx.bgxnetwork.transfer.tt.TDLink;
import net.bgx.bgxnetwork.transfer.tt.TDObject;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.EdgeShape;
import edu.uci.ics.jung.graph.decorators.EdgeShapeFunction;
import edu.uci.ics.jung.graph.decorators.ToolTipFunction;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;
import edu.uci.ics.jung.graph.decorators.VertexShapeFunction;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.transform.MutableAffineTransformer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;

class TDCellRenderer implements TableCellRenderer, ChangeListener {
    private VertexShapeFunction vertexShapeFunction = null;
    private VertexPaintFunction vertexPaintFunction = null;
    private TDPaintable preRendererPaintable = null;
    private ToolTipFunction tooltipFunction = null;
    private EdgeShapeFunction edgeShapeFunction = null;
    private TableCellRenderer defaultCellRenderer = null;
    private Color selectedColor = null;
    private Map renderingHints = null;
    private Map<Object, Component> renderersCache = null;
    private MutableTransformer transformer = null;
    private double scrollPosition = 0;

    public TDCellRenderer() {
        vertexShapeFunction = new TDVertexShapeFunction();
        vertexPaintFunction = new TDVertexPaintFunction();
        preRendererPaintable = new TDPaintable();
        tooltipFunction = new TDTooltipFunction();
        edgeShapeFunction = new EdgeShape.Line();
        defaultCellRenderer = new DefaultTableCellRenderer();
        transformer = new MutableAffineTransformer();
        selectedColor = new Color(220, 220, 255);
        renderersCache = new HashMap<Object, Component>();
        renderingHints = new HashMap();
        renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        PluggableRenderer renderer = null;
        VisualizationViewer viewer = null;
        Layout layout = null;
        VisualizationModel model = null;
        Component cellRenderer = null;
        Dimension layoutSize = null;
        if (value instanceof VisualizationModel) {
            cellRenderer = renderersCache.get(value);
            if (cellRenderer == null) {
                model = (VisualizationModel) value;
                layout = model.getGraphLayout();
                layoutSize = layout.getCurrentSize();

                renderer = new PluggableRenderer();
                renderer.setVertexShapeFunction(vertexShapeFunction);
                renderer.setVertexPaintFunction(vertexPaintFunction);
                renderer.setEdgeShapeFunction(edgeShapeFunction);

                viewer = new VisualizationViewer(model, renderer, layoutSize);
                viewer.addPreRenderPaintable(preRendererPaintable);
                viewer.setToolTipFunction(tooltipFunction);
                viewer.setRenderingHints(renderingHints);
                viewer.setDoubleBuffered(false);
                viewer.setViewTransformer(transformer);
                viewer.setPickSupport(new ShapePickSupport());

                cellRenderer = viewer;
                renderersCache.put(value, cellRenderer);
            }
        } else if (value instanceof TDObject) {
            cellRenderer = defaultCellRenderer.getTableCellRendererComponent(table,
                    LVGraphNetworkUtil.TAG_HTML_START +
                            LVGraphNetworkUtil.formatLabel(((TDObject) value).getName(), 15) +
                            LVGraphNetworkUtil.TAG_HTML_END
                    , isSelected, hasFocus, row, column);
            ((JLabel) cellRenderer).setHorizontalAlignment(SwingConstants.CENTER);
        }
        cellRenderer.setBackground(isSelected ? selectedColor : Color.white);
        return cellRenderer;
    }

    public void scroll(double offsetx, double offsety) {
        scrollPosition -= offsetx;
        transformer.translate(offsetx, offsety);
    }

    public double getScrollPosition() {
        return scrollPosition;
    }

    public void stateChanged(ChangeEvent e) {
        renderersCache.clear();
    }
}

class TDVertexShapeFunction implements VertexShapeFunction {
    private Shape shapeEvent = new Ellipse2D.Float(-5, -5, 10, 10);
    private Shape shapeKnot = new Rectangle2D.Float(-1, -1, 2, 2);

    public Shape getShape(Vertex vertex) {
        return vertex.getUserDatum(TDConstants.KEY_VERTEX_LINKOBJECT) == null ? shapeKnot : shapeEvent;
    }
}

class TDPaintable implements VisualizationViewer.Paintable {
    private Stroke stroke = new BasicStroke(1);

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(Color.BLACK);
        g2d.setStroke(stroke);
        g2d.drawLine(0, TDConstants.LAYOUT_Y_CENTER, 10000, TDConstants.LAYOUT_Y_CENTER);
    }
    public boolean useTransform() {
        return false;
    }
}

class TDVertexPaintFunction implements VertexPaintFunction {
    private Paint paintLinkSingle = TDConstants.COLOR_VERTEX_SINGLE;
    private Paint paintLinkIntegrated = TDConstants.COLOR_VERTEX_INTEGRATED;
    private Paint paintDefault = Color.BLACK;

    public Paint getDrawPaint(Vertex vertex) {
        return paintDefault;
    }
    public Paint getFillPaint(Vertex vertex) {
        Paint paint = paintDefault;
        Object linkObject = vertex.getUserDatum(TDConstants.KEY_VERTEX_LINKOBJECT);
        if (linkObject != null) {
            Object linkObjectIntegrated = vertex.getUserDatum(TDConstants.KEY_VERTEX_LINKOBJECT_INTEGRATED);
            if (linkObjectIntegrated == null) {
                paint = paintLinkSingle;
            } else {
                paint = paintLinkIntegrated;
            }
        }
        return paint;  
    }
}

class TDTooltipFunction implements ToolTipFunction {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public String getToolTipText(Vertex vertex) {
        TDLink link = (TDLink) vertex.getUserDatum(TDConstants.KEY_VERTEX_LINKOBJECT);
        String tooltipText = null;
        if (link != null) {
            tooltipText = dateFormat.format(new Date(link.getTimestamp()));
        }
        return tooltipText;
    }
    public String getToolTipText(Edge edge) {
        return null;
    }
    public String getToolTipText(MouseEvent event) {
        return null;
    }
}
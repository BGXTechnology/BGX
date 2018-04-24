package net.bgx.bgxnetwork.bgxop.graph;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.EdgeShape;
import edu.uci.ics.jung.graph.decorators.PickableEdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.UserDatumNumberVertexValue;
import edu.uci.ics.jung.visualization.DefaultGraphLabelRenderer;
import net.bgx.bgxnetwork.bgxop.gui.QueryController;
import net.bgx.bgxnetwork.bgxop.tools.GraphDataUtil;
import ru.zsoft.jung.viewer.BufferedRenderer;
import ru.zsoft.jung.viewer.BufferedViewer;
import ru.zsoft.jung.viewer.CustomShapeFunction;

import javax.swing.*;
import java.awt.*;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Class gtest.CustomRenderer
 *
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class CustomRenderer extends BufferedRenderer {
    protected ResourceBundle rb = PropertyResourceBundle.getBundle("engine");
    protected LabelBuilder labelBuilder;
    private BufferedViewer view;
    private Font vertexFont;
    private Font edgeFont;

    public CustomRenderer() {
//        vertexShapeFunction = new CustomShapeFunction();
        CustomVertexSizeFunction function = new CustomVertexSizeFunction(new UserDatumNumberVertexValue(new Integer(0)));
        vertexShapeFunction = function;
        vertexFont = UIManager.getFont("graph.vertex.font");
        edgeFont = UIManager.getFont("graph.edge.font");
        if (vertexFont == null)
            vertexFont = new Font("Tahoma", Font.PLAIN, 11);
        if (edgeFont == null)
            edgeFont = vertexFont;
        edgeStringer = new NameEdgeStringer();
        vertexStringer = new NameVertexStringer();
        graphLabelRenderer =
                new DefaultGraphLabelRenderer(Color.blue, Color.MAGENTA);
        // edgeStrokeFunction = new ConstantEdgeStrokeFunction(
        // new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL,
        // 10,
        // new float[] {10}, 0));
    }

    public void setViewer(BufferedViewer v) {
        this.view = v;
        //edgeShapeFunction= new EdgeShape.BentLine();
        vertexPaintFunction = new CustomVertexPaintFunction(v);
        edgePaintFunction = new GradientCustomEdgePaintFunction(new PickableEdgePaintFunction(v.getPickedState(),
                Color.BLACK, Color.GREEN), v, v, v.getPickedState(), GradientType.GRADIENT_NONE);
        edgeStrokeFunction = new CustomEdgeWeightStrokeFunction();
        parallelEdgeIndexFunction = CustomParallelEdgeIndexFunction.getInstance();
        if (edgeShapeFunction instanceof EdgeShape.ParallelRendering) {
            ((EdgeShape.ParallelRendering) edgeShapeFunction).setParallelEdgeIndexFunction(parallelEdgeIndexFunction);
            edgeArrowFunction = new StrokeDirectionalEdgeArrowFunction(10, 8, 4, edgeStrokeFunction);
        }

        // labelBuilder = new CustomLabelBuilder(v, rb, vertexFont);
    }

    public void setVerticesHeight() {
        Rectangle r = null;
        FontMetrics metrics = view.getFontMetrics(vertexFont);
        Set<Vertex> vertices = view.getGraphLayout().getGraph().getVertices();
        if (vertices != null) {
            for (Vertex v : vertices) {
                if (r == null) {
                    r = ((CustomShapeFunction) vertexShapeFunction).getEdgeShape(v).getBounds();
                }
                ((CustomShapeFunction) vertexShapeFunction).setShape(v, metrics.getHeight() * labelBuilder.getNumLines(v, r.width));
            }
        }
    }

    public void setColorForRootVertex(QueryController controller) {
        Set<Vertex> roots = controller.getCenterVertices();
        Color color = new Color(249, 137, 126);
        if (roots != null) {
            for (Vertex v : roots) {
                GraphDataUtil.setColor(v, color);
            }
        }
    }

    public void paintShapeForVertex(Graphics2D g, Vertex v, Shape shape) {
        if (v != null && GraphDataUtil.getVisible(v)) {
            super.paintShapeForVertex(g, v, shape);
            Rectangle r = shape.getBounds();
            //labelBuilder.getLabel(v);
            //labelBuilder.drawLabel(g, v, r);
        }
    }

    public void paintVertex(Graphics g, Vertex v, int x, int y) {
        if (v != null && GraphDataUtil.getVisible(v))
            super.paintVertex(g, v, x, y);
    }

    public void paintEdge(Graphics graphics, Edge edge, int i, int i1, int i2, int i3) {
        if (edge != null && GraphDataUtil.getVisible(edge))
            super.paintEdge(graphics, edge, i, i1, i2, i3);
    }
}

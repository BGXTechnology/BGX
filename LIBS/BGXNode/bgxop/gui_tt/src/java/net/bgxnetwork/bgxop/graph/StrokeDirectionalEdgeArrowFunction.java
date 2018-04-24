package net.bgx.bgxnetwork.bgxop.graph;

import edu.uci.ics.jung.graph.decorators.DirectionalEdgeArrowFunction;
import edu.uci.ics.jung.graph.decorators.EdgeStrokeFunction;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.DirectedEdge;
import edu.uci.ics.jung.graph.UndirectedEdge;
import edu.uci.ics.jung.visualization.ArrowFactory;

import java.awt.*;

/**
 * User: O.Gerasimenko
 * Date: 12.07.2007
 * Time: 16:31:53
 */
public class StrokeDirectionalEdgeArrowFunction extends DirectionalEdgeArrowFunction {
    private EdgeStrokeFunction edgeStrokeFunction;
    private int length;
    private int width;
    private int notch_depth;
    public StrokeDirectionalEdgeArrowFunction(int length, int width, int notch_depth, EdgeStrokeFunction edgeStrokeFunction) {
        super(length, width, notch_depth);
        this.edgeStrokeFunction = edgeStrokeFunction;
        directed_arrow = ArrowFactory.getNotchedArrow(width, length, notch_depth);
        undirected_arrow = ArrowFactory.getWedgeArrow(width, length);
        this.length=length;
        this.width=width;
        this.notch_depth=notch_depth;
    }

    public Shape getArrow(Edge e) {
        if (e instanceof DirectedEdge) {
            Stroke stroke = edgeStrokeFunction.getStroke(e);
            if (stroke instanceof BasicStroke &&((BasicStroke)stroke).getLineWidth()!=1f) {
                return  ArrowFactory.getNotchedArrow(width+((BasicStroke)stroke).getLineWidth(), length+((BasicStroke)stroke).getLineWidth(),notch_depth);
            }
            return directed_arrow;
        } else if (e instanceof UndirectedEdge)
            return undirected_arrow;
        else
            throw new IllegalArgumentException("Unrecognized edge type");
    }
}

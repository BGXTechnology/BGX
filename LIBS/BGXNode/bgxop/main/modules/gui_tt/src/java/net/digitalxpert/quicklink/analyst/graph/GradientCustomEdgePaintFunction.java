package net.bgx.bgxnetwork.bgxop.graph;

import edu.uci.ics.jung.graph.decorators.GradientEdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.EdgePaintFunction;
import edu.uci.ics.jung.graph.predicates.SelfLoopEdgePredicate;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.visualization.PickedInfo;
import edu.uci.ics.jung.visualization.HasGraphLayout;
import edu.uci.ics.jung.visualization.transform.LayoutTransformer;
import edu.uci.ics.jung.utils.ParallelEdgeIndexSingleton;
import org.apache.commons.collections.Predicate;

import java.awt.*;

import net.bgx.bgxnetwork.bgxop.tools.GraphDataUtil;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.transfer.query.LinkType;

/**
 * User: O.Gerasimenko
 * Date: 25.06.2007
 * Time: 14:20:35
 */
public class GradientCustomEdgePaintFunction extends GradientEdgePaintFunction implements GradientType {

    private PickedInfo pi;
    private EdgePaintFunction defaultFunc;
    private final Predicate self_loop = SelfLoopEdgePredicate.getInstance();
    protected boolean fill_edge = false;
    protected int gradientLevel = GRADIENT_NONE;
    private int maxCountParallel = 50;

    public GradientCustomEdgePaintFunction(EdgePaintFunction defaultEdgePaintFunction, HasGraphLayout vv,
                                           LayoutTransformer transformer, PickedInfo pi, int gradientLevel) {
        super(Color.WHITE, Color.BLACK, vv, transformer);
        this.defaultFunc = defaultEdgePaintFunction;
        this.pi = pi;
        this.gradientLevel = gradientLevel;
        try {
            String maxParallel = System.getProperty("net.bgx.bgxnetwork.bgxop.graph.style.edge.MAX_COUNT_PARALLEL_EDGE");
            if (maxParallel != null) {
                maxCountParallel = Integer.parseInt(maxParallel);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void useFill(boolean b) {
        fill_edge = b;
    }

    public Paint getDrawPaint(Edge e) {
        if (gradientLevel == GRADIENT_NONE) {
            if (e != null && GraphDataUtil.getVisible(e)) {
                int count = GraphNetworkUtil.getCountVisibleIntegrationLink(e);
                count = count == 0 ? 1 : count;
                float weight = count > maxCountParallel ? 1 : Float.valueOf(count).floatValue() / maxCountParallel;
                if (count > 4)
                    return new Color(Math.round(weight * 255), 0,
                            Math.round((1 - weight) * 255));
                else
                    return GraphNetworkUtil.getType(e) != null && GraphNetworkUtil.getType(e).equals(LinkType.IntegrationLink) ? Color.BLUE : Color.BLACK;
            }
            else
                return Color.BLACK;

        } else {
            return super.getDrawPaint(e);
        }
    }

    protected Color getColor2(Edge e) {
        return pi.isPicked(e) ? Color.RED : c2;
    }

    protected Color getColor1(Edge e) {
        return pi.isPicked(e) ? Color.GREEN : c1;
    }

    public Paint getFillPaint(Edge e) {

        if (self_loop.evaluate(e) || !fill_edge) {
            return null;
        } else
            return getDrawPaint(e);
    }

}
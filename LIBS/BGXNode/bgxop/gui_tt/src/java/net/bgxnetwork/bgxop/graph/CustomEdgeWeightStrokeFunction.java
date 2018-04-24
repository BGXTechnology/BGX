package net.bgx.bgxnetwork.bgxop.graph;

import edu.uci.ics.jung.graph.decorators.EdgeStrokeFunction;
import edu.uci.ics.jung.graph.decorators.NumberEdgeValue;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.utils.ParallelEdgeIndexSingleton;

import java.awt.*;

import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.tools.GraphDataUtil;

/**
 * User: O.Gerasimenko
 * Date: 25.06.2007
 * Time: 16:41:53
 */
public class CustomEdgeWeightStrokeFunction implements EdgeStrokeFunction {
    protected static final Stroke basic = new BasicStroke(1);
    private int maxThickStroke = 10;
    private int maxCountParallel = 50;


    protected boolean weighted = true;
    protected NumberEdgeValue edge_weight;

    public CustomEdgeWeightStrokeFunction() {
        this.edge_weight = edge_weight;
        try {
            String maxThick = System.getProperty("net.bgx.bgxnetwork.bgxop.graph.style.edge.MAX_THICK_STROKE_EDGE");
            String maxParallel = System.getProperty("net.bgx.bgxnetwork.bgxop.graph.style.edge.MAX_COUNT_PARALLEL_EDGE");
            if (maxThick != null) {
                maxThickStroke = Integer.parseInt(maxThick);
            }
            if (maxParallel != null) {
                maxCountParallel = Integer.parseInt(maxParallel);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWeighted(boolean weighted) {
        this.weighted = weighted;
    }

    public Stroke getStroke(Edge e) {
        int weight = 1;
        if (weighted) {
            weight = drawHeavy(e);
            if (weight > maxThickStroke)
                return new BasicStroke(maxThickStroke);
            else
                return new BasicStroke(weight == 0 ? 1 : weight);
        } else
            return basic;
    }

    protected int drawHeavy(Edge e) {
        if (e!= null && GraphDataUtil.getVisible(e)) {
            double value = GraphNetworkUtil.getCountVisibleIntegrationLink(e);

            return (int) Math.round((value * maxThickStroke) / maxCountParallel);
        }

        return 1;
    }
}

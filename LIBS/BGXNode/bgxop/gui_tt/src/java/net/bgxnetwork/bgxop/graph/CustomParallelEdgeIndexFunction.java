package net.bgx.bgxnetwork.bgxop.graph;

import edu.uci.ics.jung.utils.DefaultParallelEdgeIndexFunction;
import edu.uci.ics.jung.utils.Pair;
import edu.uci.ics.jung.utils.ParallelEdgeIndexFunction;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

import net.bgx.bgxnetwork.bgxop.tools.GraphDataUtil;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.transfer.query.LinkType;

/**
 * User: O.Gerasimenko
 * Date: 12.07.2007
 * Time: 12:23:34
 */
public class CustomParallelEdgeIndexFunction implements ParallelEdgeIndexFunction {
    private CustomParallelEdgeIndexFunction() {

    }

    public static CustomParallelEdgeIndexFunction getInstance() {
        return new CustomParallelEdgeIndexFunction();
    }

    protected Map edge_index = new HashMap();

    /**
     * Returns the index for the specified edge.
     * Calculates the indices for <code>e</code> and for all edges parallel
     * to <code>e</code>.
     */
    public int getIndex(Edge e) {
        Integer index = (Integer) edge_index.get(e);
        if (index == null)
            index = getIndex_internal(e);
        return index.intValue();
    }

    protected Integer getIndex_internal(Edge e) {
        int count = 0;
        boolean visibledEdge = (e!= null && GraphDataUtil.getVisible(e));
        if (visibledEdge && !GraphNetworkUtil.getType(e).equals(LinkType.IntegrationLink)) {
            Pair endpoints = e.getEndpoints();
            Vertex u = (Vertex) (endpoints.getFirst());
            Vertex v = (Vertex) (endpoints.getSecond());
            Set commonEdgeSet = u.findEdgeSet(v);
            for (Iterator iterator = commonEdgeSet.iterator(); iterator.hasNext();) {
                Edge other = (Edge) iterator.next();
                if (e.equals(other) == false) {
                    edge_index.put(other, new Integer(count));
                    count++;
                }
            }
        }
        else
            count =2;
        Integer index = new Integer(count);
        edge_index.put(e, index);


        return index;
    }

    /**
     * Resets the indices for this edge and its parallel edges.
     * Should be invoked when an edge parallel to <code>e</code>
     * has been added or removed.
     *
     * @param e
     */
    public void reset(Edge e) {
        getIndex_internal(e);
    }

    /**
     * Clears all edge indices for all edges in all graphs.
     * Does not recalculate the indices.
     */
    public void reset() {
        edge_index.clear();
    }
}

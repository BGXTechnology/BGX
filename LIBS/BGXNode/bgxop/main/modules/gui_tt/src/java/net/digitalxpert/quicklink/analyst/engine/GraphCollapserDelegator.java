package net.bgx.bgxnetwork.bgxop.engine;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.Layout;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * User: O.Gerasimenko
 * Date: 28.06.2007
 * Time: 12:09:55
 */

public class GraphCollapserDelegator implements Collapser {
    private Collapser instanceCollapser;

    public GraphCollapserDelegator(Collapser collapser) {
        this.instanceCollapser = collapser;

    }

    public GraphCollapserDelegator(Layout layout) {
        this.instanceCollapser = new CustomGraphCollapser(layout);
    }


    public Graph collapse(Graph inGraph, Graph clusterGraph, String nameGroup) {
        return instanceCollapser.collapse(inGraph, clusterGraph, nameGroup);
    }


    public Graph expand(Graph inGraph, Graph clusterGraph) {
        return instanceCollapser.expand(inGraph, clusterGraph);
    }

    public Graph getClusterGraph(Graph inGraph, Collection picked) {
        return instanceCollapser.getClusterGraph(inGraph, picked);
    }

    public Set<Vertex> getVertexesFrom(Vertex collapsedVertex) {
        return instanceCollapser.getVertexesFrom(collapsedVertex);
    }

    public Map<Long, Set<Vertex>> getVertexGroups(Graph graph) {
        return instanceCollapser.getVertexGroups(graph);
    }

    public Layout getOriginalLayout() {
        return instanceCollapser.getOriginalLayout();
    }

    public Map<Long, String> getGroupNames(Graph graph) {
        return instanceCollapser.getGroupNames(graph);
    }
}

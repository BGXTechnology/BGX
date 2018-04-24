package net.bgx.bgxnetwork.bgxop.engine;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.Layout;

import java.util.Collection;
import java.util.Set;
import java.util.Map;

/**
 * User: O.Gerasimenko
 * Date: 28.06.2007
 * Time: 12:00:28
 */
public interface Collapser {
    public Graph collapse(Graph inGraph, Graph clusterGraph, String nameGroup);


    public Graph expand(Graph inGraph, Graph clusterGraph);


    public Graph getClusterGraph(Graph inGraph, Collection picked);

    public Set<Vertex> getVertexesFrom(Vertex collapsedVertex);

    public Map<Long, Set<Vertex>> getVertexGroups(Graph graph);


    Layout getOriginalLayout();

    Map<Long, String> getGroupNames(Graph graph);
}

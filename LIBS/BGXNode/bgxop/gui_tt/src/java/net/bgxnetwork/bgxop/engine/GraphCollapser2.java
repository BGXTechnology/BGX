package net.bgx.bgxnetwork.bgxop.engine;

import edu.uci.ics.jung.algorithms.blockmodel.GraphCollapser;
import edu.uci.ics.jung.graph.DirectedEdge;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.utils.Pair;
import edu.uci.ics.jung.visualization.Layout;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.tools.lv.ObjectWorker;
import oracle.spatial.network.NetworkFactory;
import oracle.spatial.network.Node;

import java.util.*;
import java.util.logging.Logger;

/**
 * User: O.Gerasimenko
 * Date: 28.06.2007
 * Time: 12:40:44
 */
public abstract class GraphCollapser2 implements Collapser {
    private static final Logger logger = Logger.getLogger(GraphCollapser2.class.getClass().getName());
    private Layout originalLayout;


    public GraphCollapser2(Layout layout) {
        this.originalLayout = layout;
    }

    private Graph createGraph() throws InstantiationException, IllegalAccessException {
        // return (Graph) originalGraph.getClass().newInstance();
        return new SparseGraph();
    }

    public Graph collapse(Graph inGraph, Graph clusterGraph, String nameGroup) {

        if (clusterGraph.getVertices().size() < 2) return inGraph;
        if (nameGroup == null || nameGroup.length() == 0) return inGraph;

        Graph graph;
        try {
            graph = createGraph();
            Collection cluster = clusterGraph.getVertices();

            // add all vertices in the delegate, unless the vertex is in the
            // cluster.
            for (Object v : inGraph.getVertices()) {
                if (!cluster.contains(v)) {
                    createVertexFrom(graph, (Vertex) v);
                }
            }
            // add the clusterGraph as a vertex]
            GraphCollapser.CollapsedVertex superVertex = getCollapsedVertex(clusterGraph);
            Vertex firstVetex = (Vertex) cluster.iterator().next();
            Node node = NetworkFactory.createNode(GraphNetworkUtil.getID(firstVetex), nameGroup);
            if (cluster != null && cluster.size() > 0)
                superVertex.importUserData(firstVetex);
            GraphNetworkUtil.setNode(superVertex, node);
            graph.addVertex(superVertex);
            setPropertyValForGroupVertex(cluster, GraphNetworkUtil.getID(firstVetex));

            //add all edges from the inGraph, unless both endpoints of
            // the edge are in the cluster
            for (Edge e : (Set<Edge>) inGraph.getEdges()) {
                Pair endpoints = e.getEndpoints();
                // don't add edges whose endpoints are both in the cluster
                if (cluster.contains(endpoints.getFirst()) && cluster.contains(endpoints.getSecond())) {
                    continue;
                } else {
                    if (cluster.contains(endpoints.getFirst())) {
                        graphAddEdge(graph, e, new Pair(superVertex, endpoints.getSecond()), getEdgeType(inGraph, e));

                    } else if (cluster.contains(endpoints.getSecond())) {
                        graphAddEdge(graph, e, new Pair(endpoints.getFirst(), superVertex), getEdgeType(inGraph, e));

                    } else {
                        graphAddEdge(graph, e, new Pair(endpoints.getFirst(), endpoints.getSecond()), getEdgeType(inGraph, e));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return originalLayout.getGraph();
        }

        return graph;
    }

    private void setPropertyValForGroupVertex(Collection cluster, long idGroup) {
        for (Object v : cluster) {
            if (!(v instanceof GraphCollapser.CollapsedVertex) &&
                    v instanceof Vertex) {
                ObjectWorker worker = new ObjectWorker(GraphNetworkUtil.getControlObject((Vertex) v));
                worker.setGroupId(idGroup);
            } else {
                if (v instanceof GraphCollapser.CollapsedVertex) {
                    setPropertyValForGroupVertex(((GraphCollapser.CollapsedVertex) v).getRootSet(), idGroup);
                }
            }
        }
    }

    private Set<Vertex> getVertexesByGroupId(Collection cluster, long idGroup) {
        Set<Vertex> result = new HashSet<Vertex>();
        for (Object v : cluster) {
            if (!(v instanceof GraphCollapser.CollapsedVertex) &&
                    v instanceof Vertex) {
                ObjectWorker worker = new ObjectWorker(GraphNetworkUtil.getControlObject((Vertex) v));
                if (worker.getGroupId() != null && worker.getGroupId().longValue() == idGroup) {
                    result.add((Vertex) v);
                }
            } else {
                if (v instanceof GraphCollapser.CollapsedVertex) {
                    result.addAll(getVertexesByGroupId(((GraphCollapser.CollapsedVertex) v).getRootSet(), idGroup));
                }
            }
        }
        return result;
    }

    public Graph expand(Graph inGraph, Graph clusterGraph) {
        if (getCollapsedVertexes(inGraph).size() == 1) {
            return originalLayout.getGraph();
        }
        try {
            Graph graph = createGraph();

            Collection cluster = clusterGraph.getVertices();
            logger.fine("cluster to expand is " + cluster);

            // add all the vertices from the current graph except for
            // the cluster we are expanding
            for (Vertex v : (Collection<Vertex>) inGraph.getVertices()) {
                if (!clusterGraph.getVertices().contains(v) || (clusterGraph.getVertices().contains(v) && (!(v instanceof GraphCollapser.CollapsedVertex)))) {
                    createVertexFrom(graph, v);
                }
            }

            // put all clusterGraph vertices and edges into the new Graph
            Set<Pair> madePair = new HashSet<Pair>();
            Set<Pair> madeOutGroupPair = new HashSet<Pair>();
            for (Object v : cluster) {
                if (v instanceof GraphCollapser.CollapsedVertex) {
                    setPropertyValForGroupVertex(((GraphCollapser.CollapsedVertex) v).getRootSet(), -1);

                    for (Object innerVertex : ((GraphCollapser.CollapsedVertex) v).getRootSet()) {
                        Set<Vertex> vertexes = getVertexesFrom((Vertex) innerVertex);
                        if (vertexes.size() > 0) {
                            for (Vertex clusterVert : vertexes) {
                                createVertexFrom(graph, clusterVert);
                                madeOutGroupPair.clear();
                                for (Object edge : clusterVert.getIncidentEdges()) {
                                    Pair endpoints = ((Edge) edge).getEndpoints();
                                    if (!madePair.contains(endpoints)) {
                                        graphAddEdge(graph, (Edge) edge,
                                                new Pair(endpoints.getFirst(),
                                                        endpoints.getSecond()),
                                                getEdgeType(graph, (Edge) edge));
                                        madeOutGroupPair.add(endpoints);
                                    }
                                }
                                madePair.addAll(madeOutGroupPair);
                            }
                        } else {
                            createVertexFrom(graph, (Vertex) innerVertex);
                            madeOutGroupPair.clear();
                            for (Object edge : ((Vertex) innerVertex).getIncidentEdges()) {
                                Pair endpoints = ((Edge) edge).getEndpoints();
                                if (!madePair.contains(endpoints) &&
                                        (!(endpoints.getFirst() instanceof GraphCollapser.CollapsedVertex ||
                                                endpoints.getSecond() instanceof GraphCollapser.CollapsedVertex))) {
                                    graphAddEdge(graph, (Edge) edge,
                                            new Pair(endpoints.getFirst(),
                                                    endpoints.getSecond()),
                                            getEdgeType(graph, (Edge) edge));
                                    madeOutGroupPair.add(endpoints);
                                }
                            }
                            madePair.addAll(madeOutGroupPair);
                        }
                    }

                }
            }

            // now that all vertices have been added, add the edges,
            // ensuring that no edge contains a vertex that has not
            // already been added
            Pair pair;
            madeOutGroupPair.clear();
            for (Vertex v : (Set<Vertex>) inGraph.getVertices()) {
                madeOutGroupPair.clear();
                if (!clusterGraph.getVertices().contains(v)
                        || (clusterGraph.getVertices().contains(v) && (!(v instanceof GraphCollapser.CollapsedVertex)))) {

                    for (Edge edge : (Set<Edge>) v.getIncidentEdges()) {
                        Pair endpoints = edge.getEndpoints();
                        Object v1 = endpoints.getFirst();
                        Object v2 = endpoints.getSecond();
                        if (cluster.contains(v1) || cluster.contains(v2)) {
                            if (cluster.contains(v1) &&
                                    v1 instanceof GraphCollapser.CollapsedVertex &&
                                    !madePair.contains(endpoints)) {
                                Set<Edge> edgeFromGroup = getOutEdgeGroupStart(originalLayout.getGraph(), (Vertex) v2, ((GraphCollapser.CollapsedVertex) v1).getRootSet());
                                for (Edge e : edgeFromGroup) {
                                    pair = e.getEndpoints();
                                    if (pair != null) {
                                        Object originalV1 = pair.getFirst();
                                        Object newV1 = findVertex(graph, originalV1);
                                        assert newV1 != null : "newV1 for " + originalV1 + " was not found!";
                                        graphAddEdge(graph, e, new Pair(newV1, v2), getEdgeType(inGraph, e));
                                    }
                                }
                                madePair.add(endpoints);
                            } else if (cluster.contains(v2) &&
                                    v2 instanceof GraphCollapser.CollapsedVertex &&
                                    !madePair.contains(endpoints)) {
                                Set<Edge> edgeToGroup = getOutEdgeGroupEnd(originalLayout.getGraph(), (Vertex) v1, ((GraphCollapser.CollapsedVertex) v2).getRootSet());
                                for (Edge e : edgeToGroup) {
                                    pair = e.getEndpoints();
                                    if (pair != null) {
                                        Object originalV2 = pair.getSecond();
                                        Object newV2 = findVertex(graph, originalV2);
                                        assert newV2 != null : "newV2 for " + originalV2 + " was not found!";
                                        graphAddEdge(graph, e, new Pair(v1, newV2), getEdgeType(inGraph, e));
                                    }
                                }
                                madePair.add(endpoints);
                            } else {
                                if (!madePair.contains(endpoints)) {
                                    graphAddEdge(graph, edge, new Pair(v1, v2), getEdgeType(inGraph, edge));
                                    madeOutGroupPair.add(endpoints);
                                }
                            }
                        } else {
                            if (!madePair.contains(endpoints)) {
                                graphAddEdge(graph, edge, new Pair(v1, v2), getEdgeType(inGraph, edge));
                                madeOutGroupPair.add(endpoints);
                            }
                        }

                    }
                    madePair.addAll(madeOutGroupPair);
                } else {
                    if (v instanceof GraphCollapser.CollapsedVertex) {
                        for (Edge edge : (Set<Edge>) v.getIncidentEdges()) {
                            Pair endpoints = edge.getEndpoints();
                            Object v1 = endpoints.getFirst();
                            Object v2 = endpoints.getSecond();
                            madeOutGroupPair.clear();
                            if (cluster.contains(v1) && cluster.contains(v2) && (!madePair.contains(endpoints))) {
                                Set<Edge> edgeFromGroup = getOutEdgeGroupStart(originalLayout.getGraph(), (Vertex) v2, ((GraphCollapser.CollapsedVertex) v1).getRootSet());
                                for (Edge innerEdge : edgeFromGroup) {
                                    pair = innerEdge.getEndpoints();
                                    if (pair != null && !madePair.contains(pair)) {
                                        Object firstV = findVertex(graph, pair.getFirst());
                                        Object secondV = findVertex(graph, pair.getSecond());

                                        assert firstV != null : "v1 for " + secondV + " was not found!";
                                        graphAddEdge(graph, innerEdge, new Pair(firstV, secondV), getEdgeType(inGraph, innerEdge));
                                        madeOutGroupPair.add(pair);
                                    }
                                }
                                madePair.addAll(madeOutGroupPair);
                                madePair.add(endpoints);
                            }
                        }
                    }
                }


            }
            return graph;
        } catch (Exception
                ex) {
            ex.printStackTrace();
            return originalLayout.getGraph();
        }
    }


    private Set<Edge> getOutEdgeGroupStart
            (Graph
                    originalGraph, Vertex
                    vertex, Collection
                    cluster) {
        Set<Edge> result = new HashSet();
        Vertex v = (Vertex) findVertex(originalGraph, vertex);

        if (v != null) {
            for (Edge e : (Set<Edge>) v.getInEdges()) {
                if (cluster.contains(e.getEndpoints().getFirst())) {
                    result.add(e);
                }
            }
        } else {
            if (vertex instanceof GraphCollapser.CollapsedVertex) {
                for (Vertex inner : (Set<Vertex>) ((GraphCollapser.CollapsedVertex) vertex).getRootSet()) {
                    result.addAll(getOutEdgeGroupStart(originalGraph, inner, cluster));
                }
            }
        }
        return result;
    }

    private Set<Edge> getOutEdgeGroupEnd
            (Graph
                    originalGraph, Vertex
                    vertex, Collection
                    cluster) {
        Set<Edge> result = new HashSet();
        Vertex v = (Vertex) findVertex(originalGraph, vertex);
        if (v != null) {
            for (Edge e : (Set<Edge>) v.getOutEdges()) {
                if (cluster.contains(e.getEndpoints().getSecond())) {
                    result.add(e);
                }
            }
        } else {
            if (vertex instanceof GraphCollapser.CollapsedVertex) {
                for (Vertex inner : (Set<Vertex>) ((GraphCollapser.CollapsedVertex) vertex).getRootSet()) {
                    result.addAll(getOutEdgeGroupEnd(originalGraph, inner, cluster));
                }
            }
        }
        return result;
    }

    private Set getCollapsedVertexes
            (Graph
                    inGraph) {
        Set result = new HashSet();
        for (Object v : inGraph.getVertices()) {
            if (v instanceof GraphCollapser.CollapsedVertex) {
                result.add(v);
            }
        }
        return result;
    }

    private GraphCollapser.CollapsedVertex getCollapsedVertex
            (Graph
                    clusterGraph) {
        return new GraphCollapser.CollapsedSparseVertex(clusterGraph.getVertices());
    }

    private EdgeType getEdgeType
            (Graph
                    graph, Edge
                    edge) {

        if (edge instanceof DirectedEdge)
            return EdgeType.DIRECTED;
        else
            return EdgeType.UNDIRECTED;
    }

    private Pair getEndpoints
            (Graph
                    graph, Edge
                    e) {
        for (Edge edge : (Collection<Edge>) graph.getEdges()) {
            if (edge.equals(e)) {
                return edge.getEndpoints();
            }
        }
        return null;
    }


    Object findVertex
            (Graph
                    inGraph, Object
                    vertex) {
        Collection vertices = inGraph.getVertices();
        for (Vertex v : (Collection<Vertex>) vertices) {
            if (v.equals(vertex))
                return v;
        }
        for (Object v : vertices) {
            if (v instanceof Graph) {
                Graph g = (Graph) v;
                if (contains(g, vertex)) {
                    return v;
                }
            }
        }
        return null;
    }

    private boolean contains
            (Graph
                    inGraph, Object
                    vertex) {
        boolean contained = false;
        if (inGraph.getVertices().contains(vertex)) return true;
        for (Object v : inGraph.getVertices()) {
            if (v instanceof Graph) {
                contained |= contains((Graph) v, vertex);
            }
        }
        return contained;
    }

    private boolean containsEdge
            (Graph
                    inGraph, Edge
                    edge) {
        boolean contained = false;
        if (inGraph.getEdges().contains(edge)) return true;
        return contained;
    }

    public Graph getClusterGraph
            (Graph
                    inGraph, Collection
                    picked) {
        Graph clusterGraph;
        try {
            clusterGraph = createGraph();

        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        Set<Pair> madePair = new HashSet<Pair>();
        Set<Pair> madeOutGroupPair = new HashSet<Pair>();
        Pair pair;
        for (Object v : picked) {
            createVertexFrom(clusterGraph, (Vertex) v);
            Collection<Edge> edges = ((Vertex) v).getIncidentEdges();
            madeOutGroupPair.clear();
            for (Edge edge : edges) {
                Pair endpoints = edge.getEndpoints();
                if (endpoints != null) {
                    Object v1 = endpoints.getFirst();
                    Object v2 = endpoints.getSecond();
                    // todo per one tree node
/*
                    if (picked.contains(endpoints.getFirst()) && !(picked.contains(endpoints.getSecond()))) {
                        graphAddEdge(clusterGraph, edge, new Pair(v1, v2), getEdgeType(inGraph, edge));

                    }
*/                 // todo per selection node
                    if (picked.contains(endpoints.getFirst()) && picked.contains(endpoints.getSecond())) {
                        pair = new Pair(v1, v2);
                        if (!madePair.contains(pair)) {
                            graphAddEdge(clusterGraph, edge, pair, getEdgeType(inGraph, edge));
                            madeOutGroupPair.add(pair);
                        }
                    }
                }
            }
            madePair.addAll(madeOutGroupPair);
        }
        return clusterGraph;
    }

    private Vertex createVertexFrom
            (Graph
                    graph, Vertex
                    vertex) {
        Vertex newInstance = (Vertex) findVertex(graph, vertex);
        if (newInstance != null)
            return newInstance;
        newInstance = (Vertex) vertex.copy(graph);
        GraphNetworkUtil.setNode(newInstance, GraphNetworkUtil.getNode(vertex));
        return newInstance;

    }


    public boolean graphAddEdge
            (Graph
                    graph, Edge
                    edge, Pair
                    endpoints, EdgeType
                    edgeType) {
        Pair new_endpoints = getValidatedEndpoints(graph, edge, endpoints);
        if (new_endpoints == null)
            return false;
        Edge newEdge;
        Vertex v1 = (Vertex) new_endpoints.getFirst();
        Vertex v2 = (Vertex) new_endpoints.getSecond();

        // undirected edges and directed edges are not considered to be parallel to each other,
        // so as long as anything that's returned by findEdge is not of the same type as
        // edge, we're fine
        Edge connection = (Edge) findEdge(graph, new Pair(v1, v2));
/*        if (connection != null && getEdgeType(graph, connection) == getEdgeType(graph, edge))
            return true;

            throw new IllegalArgumentException("This graph does not accept parallel edges; " + new_endpoints +
                    " are already connected by " + connection);
*/
        v1 = createVertexFrom(graph, v1);
        v2 = createVertexFrom(graph, v2);

        if (getEdgeType(graph, edge).equals(EdgeType.DIRECTED)) {
            newEdge = new DirectedSparseEdge(v1, v2);
        } else {
            newEdge = new UndirectedSparseEdge(v1, v2);
        }

        GraphNetworkUtil.setLink(newEdge, GraphNetworkUtil.getLink(edge));
        graph.addEdge(newEdge);

        return true;
    }

    private Edge findEdge
            (Graph
                    graph, Pair
                    pair) {
        for (Edge edge : (Collection<Edge>) graph.getEdges()) {
            if (edge.getEndpoints().equals(pair)) {
                return edge;
            }
        }
        return null;
    }

    protected Pair getValidatedEndpoints
            (Graph
                    graph, Edge
                    edge, Pair
                    endpoints) {
        if (edge == null)
            throw new IllegalArgumentException("input edge may not be null");

        if (endpoints == null)
            throw new IllegalArgumentException("endpoints may not be null");

        Pair new_endpoints = new Pair(endpoints.getFirst(), endpoints.getSecond());
        if (containsEdge(graph, edge)) {
            Pair existing_endpoints = getEndpoints(graph, edge);
            if (!existing_endpoints.equals(new_endpoints)) {
                throw new IllegalArgumentException("EdgeType " + edge +
                        " exists in this graph with endpoints " + existing_endpoints);
            } else {
                return null;
            }
        }
        return new_endpoints;
    }

    protected void annotateEdge
            (GraphCollapser.CollapsedEdge
                    newEdge, Collection
                    edgesFromWhichWeMightDeriveData) {
    }


    public Layout getOriginalLayout
            () {
        return originalLayout;
    }

    public Map<Long, String> getGroupNames
            (Graph
                    graph) {
        Map<Long, String> result = new HashMap();
        Node node;
        for (Vertex vertex : (Set<Vertex>) graph.getVertices()) {
            if (vertex instanceof GraphCollapser.CollapsedVertex) {
                node = GraphNetworkUtil.getNode(vertex);
                result.put(new Long(node.getID()), node.getName());
            }
        }
        return result;
    }

    public Set<Vertex> getVertexesFrom
            (Vertex
                    collapsedVertex) {
        HashSet<Vertex> res = new HashSet<Vertex>();
        if (collapsedVertex instanceof GraphCollapser.CollapsedVertex) {
            for (Vertex v : (Set<Vertex>) ((GraphCollapser.CollapsedVertex) collapsedVertex).getRootSet()) {
                if (v instanceof GraphCollapser.CollapsedVertex)
                    res.addAll(getVertexesFrom(v));
                else
                    res.add((Vertex) findVertex(originalLayout.getGraph(), v));
            }
        }
        return res;
    }

    public Map<Long, Set<Vertex>> getVertexGroups
            (Graph
                    graph) {
        ObjectWorker worker;
        Set<Vertex> groups;
        Map<Long, Set<Vertex>> result = new HashMap();
        for (Vertex vertex : (Set<Vertex>) graph.getVertices()) {
            worker = new ObjectWorker(GraphNetworkUtil.getControlObject(vertex));
            if (worker.getGroupId() != null && worker.getGroupId() != -1) {
                groups = getVertexesByGroupId(graph.getVertices(), worker.getGroupId());
                if (!result.containsKey(worker.getGroupId())) {
                    result.put(worker.getGroupId(), groups);
                }
            }

        }
        return result;
    }
}

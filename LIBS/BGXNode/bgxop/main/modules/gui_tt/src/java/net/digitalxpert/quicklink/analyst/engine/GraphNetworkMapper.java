package net.bgx.bgxnetwork.bgxop.engine;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.utils.ParallelEdgeIndexSingleton;
import edu.uci.ics.jung.algorithms.blockmodel.GraphCollapser;
import net.bgx.bgxnetwork.bgxop.tools.GraphDataUtil;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.exception.query.ErrorList;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;
import net.bgx.bgxnetwork.exception.query.QueryDataException;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;
import oracle.spatial.network.Link;
import oracle.spatial.network.Network;
import oracle.spatial.network.NetworkDataException;
import oracle.spatial.network.Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Class GraphNetworkMapper
 *
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class GraphNetworkMapper implements Serializable {
    private Network net = null;
    private Graph graph = null;
    private int minCountIntegration = 4;

    public GraphNetworkMapper(Network net) {
        this.net = net;
        try {
            String val = System.getProperty("net.bgx.bgxnetwork.bgxop.graph.MIN_COUNT_INTEGRATION");
            if (val != null && val.length() > 0) {
                minCountIntegration = Integer.parseInt(val);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Network getNetwork() {
        return net;
    }

    public Graph getGraph() {
        return graph;
    }

    public String getName() {
        return net.getName();
    }

    public boolean graphExists() {
        return graph != null;
    }

    protected Vertex createVertex() {
        return new SparseVertex();
    }

    protected Edge createEdge(Vertex v1, Vertex v2) {
        return new DirectedSparseEdge(v1, v2);
    }

    protected Graph createGraph() {
        return new SparseGraph();
    }

    public void restoreGraph() throws QueryBusinesException {
        if (graphExists())
            return;
        graph = createGraph();
        GraphDataUtil.setName(graph, net.getName());
        GraphNetworkUtil.setNetwork(graph, net);
        GraphNetworkUtil.setGraph(net, graph);
        Vertex vx, vx2;
        Node n, n2;
        Edge e;
        Link l;
        for (Iterator it = net.getNodes(); it.hasNext();) {
            n = (Node) it.next();
            vx = graph.addVertex(createVertex());
            GraphNetworkUtil.setNode(vx, n);
            GraphNetworkUtil.setVertex(n, vx);
        }
        for (Iterator it = net.getLinks(); it.hasNext();) {
            l = (Link) it.next();
            n = l.getStartNode();
            n2 = l.getEndNode();
            vx = GraphNetworkUtil.getVertex(n);
            vx2 = GraphNetworkUtil.getVertex(n2);
            if (vx == null) {
                String message = "Edge " + l.getID() + " points to unknown node " + n.getID();
                throw new QueryBusinesException(ErrorList.BUSINES_GRAPH_BUILD_EXCEPTION, new Object[]{message});
            }
            if (vx2 == null) {
                String message = "Edge " + l.getID() + " points to unknown node " + n2.getID();
                throw new QueryBusinesException(ErrorList.BUSINES_GRAPH_BUILD_EXCEPTION, new Object[]{message});
            }
            e = graph.addEdge(createEdge(vx, vx2));
            GraphNetworkUtil.setLink(e, l);
            GraphNetworkUtil.setEdge(l, e);

        }
        setFilterIntegrationLink(false);
    }


    public void setFilterIntegrationLink(boolean isFilterMode) {
        int indexParallel = 0;
        for (Edge edge : (Set<Edge>) graph.getEdges()) {
            if (edge != null && GraphDataUtil.getVisible(edge)) {
                indexParallel = ParallelEdgeIndexSingleton.getInstance().getIndex(edge);
                if (indexParallel > minCountIntegration) {
                    GraphNetworkUtil.setVisibleParallerEdge((Vertex) edge.getEndpoints().getFirst(),
                            (Vertex) edge.getEndpoints().getSecond(),
                            edge, false, isFilterMode);
                } else
                    GraphNetworkUtil.setSimpleLink(edge);
            }
        }
    }


    public Node getNode(int id) throws QueryDataException, QueryBusinesException {
        if (net == null) {
            String message = "No network data.";
            throw new QueryBusinesException(ErrorList.BUSINES_NETWORK_DATA_EXCEPTION, new Object[]{message});
        }
        try {
            return net.getNode(id);
        } catch (NetworkDataException e) {
            String message = "No node in graph for id " + id;
            throw new QueryDataException(ErrorList.DATA_NETWORK_ORACLE_EXCEPTION, new Object[]{message});
        }
    }

    public Vertex getVertex(int id) throws QueryDataException, QueryBusinesException {
        return GraphNetworkUtil.getVertex(getNode(id));
    }

    public List<LinkObject> getParallelEdge(Edge edge) {
        Vertex firstVertex = (Vertex) edge.getEndpoints().getFirst();
        Vertex secondVertex = (Vertex) edge.getEndpoints().getSecond();
        List<LinkObject> result = new ArrayList();
        LinkObject linkObject = null;
        linkObject = GraphNetworkUtil.getLinkObject(edge);
        if (linkObject != null)
            result.add(linkObject);
        Set commonEdgeSet = firstVertex.findEdgeSet(secondVertex);
        for (Iterator iterator = commonEdgeSet.iterator(); iterator.hasNext();) {
            Edge other = (Edge) iterator.next();
            if (!edge.equals(other)) {
                linkObject = GraphNetworkUtil.getLinkObject(other);
                if (linkObject != null)
                    result.add(linkObject);
            }
        }
        return result;
    }


    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public int getCountGroup() {
        int count = 0;
        for (Vertex vertex : (Set<Vertex>) graph.getVertices()) {
            if (vertex instanceof GraphCollapser.CollapsedVertex) {
                count++;
            }
        }
        return count;
    }
}

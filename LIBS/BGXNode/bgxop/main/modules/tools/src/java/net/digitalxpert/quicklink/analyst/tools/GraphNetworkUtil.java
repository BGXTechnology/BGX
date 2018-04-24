package net.bgx.bgxnetwork.bgxop.tools;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.UserDataContainer;
import net.bgx.bgxnetwork.bgxop.tools.lv.LinkWorker;
import net.bgx.bgxnetwork.bgxop.tools.lv.ObjectWorker;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;
import net.bgx.bgxnetwork.transfer.query.ComplexId;
import net.bgx.bgxnetwork.transfer.query.LinkType;
import net.bgx.bgxnetwork.transfer.query.ObjectType;
import oracle.spatial.network.Link;
import oracle.spatial.network.Network;
import oracle.spatial.network.Node;

import javax.swing.*;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Class GraphNetworkUtil
 *
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public abstract class GraphNetworkUtil {
    protected static final String key_network = "network";
    protected static final String key_node = "node";
    protected static final String key_link = "link";
    protected static final String key_type = "type";
    protected static final String key_desc = "desc";
    protected static final String key_dataLimited = "data_limited";
    protected static final String key_nodelist = "nodelist";
    protected static final String key_idlist = "idlist";
    protected static final String key_card = "card";
    protected static final String KEY_CARD_DATA = "cardData";

    private static final String KEY_CONTROL_OBJECT = "controlObject";
    private static final String KEY_LINK_OBJECT = "linkObject";

    public static Network getNetwork(UserDataContainer obj) {
        Object o = obj.getUserDatum(key_network);
        if (o != null && (o instanceof Network))
            return (Network) o;
        return null;
    }

    public static void setNetwork(UserDataContainer obj, Network network) {
        obj.setUserDatum(key_network, network, GraphDataUtil.shared);
    }

    public static Node getNode(UserDataContainer obj) {
        Object o = obj.getUserDatum(key_node);
        if (o != null && (o instanceof Node))
            return (Node) o;
        return null;
    }

    public static void setNode(UserDataContainer obj, Node node) {
        if (node != null)
            obj.setUserDatum(key_node, node, GraphDataUtil.shared);
    }

    public static Link getLink(UserDataContainer obj) {
        Object o = obj.getUserDatum(key_link);
        if (o != null && (o instanceof Link))
            return (Link) o;
        return null;
    }

    public static void setLink(UserDataContainer obj, Link link) {
        if (link != null)
            obj.setUserDatum(key_link, link, GraphDataUtil.shared);
    }

    public static Graph getGraph(Network obj) {
        Object o = obj.getUserData();
        if (o != null && (o instanceof Graph))
            return (Graph) o;
        return null;
    }

    public static void setGraph(Network obj, Graph graph) {
        obj.setUserData(graph);
    }

    public static Vertex getVertex(Node obj) {
        Object o = obj.getUserData();
        if (o != null && (o instanceof Vertex))
            return (Vertex) o;
        return null;
    }

    public static void setVertex(Node obj, Vertex v) {
        obj.setUserData(v);
    }

    public static Edge getEdge(Link obj) {
        Object o = obj.getUserData();
        if (o != null && (o instanceof Edge))
            return (Edge) o;
        return null;
    }

    public static void setEdge(Link obj, Edge e) {
        obj.setUserData(e);
    }

    public static String getName(Vertex v) {
        ObjectWorker worker = new ObjectWorker(getControlObject(v));
        return worker.getName();
    }

    public static String getName(Node n) {
        return n.getName();
    }

    public static String getName(Edge e) {
        Link n = getLink(e);
        if (n != null)
            return n.getName();
        else
            return GraphDataUtil.getName(e);
    }

    public static String getName(Link l) {
        return l.getName();
    }

    public static int getID(Vertex v) {
        Node n = getNode(v);
        if (n != null)
            return n.getID();
        else
            return GraphDataUtil.getId(v);
    }

    public static int getID(Node n) {
        return n.getID();
    }

    public static int getID(Edge e) {
        Link l = getLink(e);
        if (l != null)
            return l.getID();
        else
            return GraphDataUtil.getId(e);
    }

    public static int getID(Link l) {
        return l.getID();
    }

    public static ObjectType getType(Node n) {
        return (ObjectType) n.getUserData(key_type);
    }

    public static ObjectType getType(Vertex v) {
        Node n = getNode(v);
        if (n != null)
            return getType(n);
        Object o = v.getUserDatum(key_type);
        if (o instanceof ObjectType)
            return (ObjectType) o;
        return null;
    }

    public static void setType(Node n, ObjectType t) {
        n.setUserData(key_type, t);
    }

    public static LinkType getType(Link l) {
        return (LinkType) l.getUserData(key_type);
    }

    public static LinkType getType(Edge e) {
        Link l = getLink(e);
        if (l != null)
            return getType(l);
        Object o = e.getUserDatum(key_type);
        if (o instanceof LinkType)
            return (LinkType) o;
        return null;
    }

    public static void setType(Link l, LinkType t) {
        l.setUserData(key_type, t);
    }

    public static void setObjectsLimit(Network net, int val) {
        net.setUserData(key_dataLimited, val);
    }

    public static Integer getObjectsLimit(Network net) {
        return (Integer) net.getUserData(key_dataLimited);
    }

    public static Integer getObjectsLimit(Graph graph) {
        Network net = getNetwork(graph);
        if (net != null)
            return getObjectsLimit(net);
        return GraphDataUtil.getInt(graph, key_dataLimited);
    }

    public static void setIdList(Node node, List<ComplexId> list) {
        node.setUserData(key_idlist, list);
    }

    public static List<ComplexId> getIdList(Node node) {
        return (List<ComplexId>) node.getUserData(key_idlist);
    }

    public static List<ComplexId> getIdList(Vertex vx) {
        Node node = getNode(vx);
        if (node != null)
            return getIdList(node);
        return (List<ComplexId>) vx.getUserDatum(key_idlist);
    }

    public static void setCard(Node node, JDialog val) {
        node.setUserData(key_card, val);
    }

    public static void setCard(Vertex vx, JDialog val) {
        Node node = getNode(vx);
        if (node != null)
            setCard(node, val);
        else
            vx.setUserDatum(key_card, val, GraphDataUtil.shared);
    }

    public static JDialog getCard(Node node) {
        return (JDialog) node.getUserData(key_card);
    }

    public static JDialog getCard(Vertex vx) {
        Node node = getNode(vx);
        if (node != null)
            return getCard(node);
        return (JDialog) vx.getUserDatum(key_card);
    }

    public static void setCard(Link link, JDialog val) {
        link.setUserData(key_card, val);
    }

    public static void setCard(Edge edge, JDialog val) {
        Link link = getLink(edge);
        if (link != null)
            setCard(link, val);
        else
            edge.setUserDatum(key_card, val, GraphDataUtil.shared);
    }

    public static JDialog getCard(Link link) {
        return (JDialog) link.getUserData(key_card);
    }

    public static JDialog getCard(Edge edge) {
        Link link = getLink(edge);
        if (link != null)
            return getCard(link);
        return (JDialog) edge.getUserDatum(key_card);
    }

    public static void setCardData(Node node, Object val) {
        node.setUserData(KEY_CARD_DATA, val);
    }

    public static void setCardData(Vertex vx, Object val) {
        Node node = getNode(vx);
        if (node != null)
            setCardData(node, val);
        else
            vx.setUserDatum(KEY_CARD_DATA, val, GraphDataUtil.shared);
    }

    public static Object getCardData(Node node) {
        return node.getUserData(KEY_CARD_DATA);
    }

    public static Object getCardData(Vertex vx) {
        Node node = getNode(vx);
        if (node != null)
            return getCardData(node);
        return vx.getUserDatum(KEY_CARD_DATA);
    }

    public static void setControlObject(Node node, ControlObject co) {
        node.setUserData(KEY_CONTROL_OBJECT, co);
    }

    public static void setControlObject(Vertex vx, ControlObject co) {
        Node node = getNode(vx);
        if (node != null) {
            setControlObject(node, co);
        }
    }

    public static ControlObject getControlObject(Node node) {
        Object obj = node.getUserData(KEY_CONTROL_OBJECT);
        if (obj != null)
            return (ControlObject) obj;
        else return null;
    }

    public static ControlObject getControlObject(Vertex vx) {
        Node node = getNode(vx);
        if (node != null)
            return getControlObject(node);
        return null;
    }

    public static void setLinkObject(Link l, LinkObject linkObject) {
        l.setUserData(KEY_LINK_OBJECT, linkObject);
    }

    public static void setLinkObject(Edge edge, LinkObject linkObject) {
        Link l = getLink(edge);
        if (l != null)
            setLinkObject(l, linkObject);
    }

    public static LinkObject getLinkObject(Link l) {
        return (LinkObject) l.getUserData(KEY_LINK_OBJECT);
    }

    public static LinkObject getLinkObject(Edge e) {
        Link l = getLink(e);
        if (l != null)
            return getLinkObject(l);
        return null;
    }

    public static Integer getIdVertex(Vertex vertex) {
        if (vertex == null) return null;
        String v = vertex.toString();
        v = v.indexOf(":") > -1 ? v.substring(1, v.indexOf(":")) : v.substring(1);
        return v != null && v.length() > 0 ? new Integer(v) : new Integer(0);
    }

    public static int getCountVisibleIntegrationLink(Edge edge) {
        int count = 0;
        Edge edgeTemp;
        Set commonEdgeSet = ((Vertex) edge.getEndpoints().getFirst()).
                findEdgeSet(((Vertex) edge.getEndpoints().getSecond()));
        LinkType valueType;
        for (Iterator iterator = commonEdgeSet.iterator(); iterator.hasNext();) {
            edgeTemp = (Edge) iterator.next();
            LinkWorker linkWorker = new LinkWorker(getLinkObject(edgeTemp));
            valueType = getType(edgeTemp);
            if (valueType != null) {
                if (valueType.equals(LinkType.IntegrationLink) && linkWorker.isVisibleInIntegratedLink()) {
                    count++;
                }
            }
        }
        return count;
    }

    public static void setVisibleParallerEdge(Vertex firstVertex,
                                              Vertex secondVertex,
                                              Edge edge,
                                              boolean flagVisible,
                                              boolean isFilterMode) {
        Set commonEdgeSet = firstVertex.findEdgeSet(secondVertex);
        for (Iterator iterator = commonEdgeSet.iterator(); iterator.hasNext();) {
            Edge other = (Edge) iterator.next();
            if (!edge.equals(other)) {
                GraphDataUtil.setVisible(other, new Boolean(flagVisible));
                setIntegrationLink(other, isFilterMode);
            } else {
                setIntegrationLink(edge, isFilterMode);
            }

        }
    }

    private static void setIntegrationLink(Edge edge, boolean isFilterMode) {
        LinkWorker linkWorker = new LinkWorker(GraphNetworkUtil.getLinkObject(edge));
        if (!linkWorker.existPropertyVisibleInIntegratedLink() ||
                !isFilterMode)
            linkWorker.setVisibleInIntegratedLink(true);
        setType(getLink(edge), LinkType.IntegrationLink);
    }

    public static void setSimpleLink(Edge edge) {
        LinkWorker linkWorker = new LinkWorker(GraphNetworkUtil.getLinkObject(edge));
        linkWorker.setVisibleInIntegratedLink(false);
        setType(getLink(edge), LinkType.Esteblish);
    }


}

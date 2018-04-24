package net.bgx.bgxnetwork.bgxop.gui;

import edu.uci.ics.jung.algorithms.blockmodel.GraphCollapser;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.ParallelEdgeIndexSingleton;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import net.bgx.bgxnetwork.bgxop.engine.Collapser;
import net.bgx.bgxnetwork.bgxop.engine.GraphCollapserDelegator;
import net.bgx.bgxnetwork.bgxop.engine.GraphNetworkMapper;
import net.bgx.bgxnetwork.bgxop.tools.GraphDataUtil;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.persistence.query.LayoutCoordinates;
import ru.zsoft.jung.viewer.BufferedViewer;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * Class GraphController
 *
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class GraphController implements ItemListener, VisualizationViewer.Paintable, MouseListener, ISelectable, IVertexContainer, IEdgeContainer {
    private BufferedViewer source;
    private GraphDataModel model;
    private HashSet<Vertex> neighbours = new HashSet<Vertex>();
    private boolean selectNeighbours = false;
    private boolean lockRecursion = false;
    private Set<Vertex> marked = new HashSet<Vertex>();
    private ArrayList<IObjectSelectionListener> singleObjectListeners = new ArrayList<IObjectSelectionListener>();
    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui");
    private MainFrame mainFrame;
    private Collapser collapser = null;

    public GraphController(MainFrame owner, BufferedViewer source, GraphDataModel model) {
        this.source = source;
        this.model = model;
        this.mainFrame = owner;
        model.addSelectable(this);
        source.getPickedState().addItemListener(this);
        source.addMouseListener(new VertexPopupListener(owner, this));
        source.addMouseListener(new EdgePopupListener(owner, this));
        collapser = new GraphCollapserDelegator(source.getModel().getGraphLayout());

    }

    public boolean isSelectNeighbours() {
        return selectNeighbours;
    }

    public void setSelectNeighbours(boolean selectNeighbours) {
        this.selectNeighbours = selectNeighbours;
        clearNeighbourSelection();
        if (selectNeighbours)
            createNeighbourSelection();
    }

    public void clearMarked() {
        marked.clear();
    }

    public void addMarked(Vertex v) {
        marked.add(v);
    }

    public void removeMarked(Vertex v) {
        marked.remove(v);
    }

    public void removeEdge(Edge e) {
        Graph g = source.getGraphLayout().getGraph();
        source.getPickedState().pick(e, false);
        g.removeEdge(e);
    }

    public void removeVertex(Vertex v) {
        Graph g = source.getGraphLayout().getGraph();
        for (Object o : v.getIncidentEdges())
            removeEdge((Edge) o);
        removeMarked(v);
        source.getPickedState().pick(v, false);
        g.removeVertex(v);
    }

    public void addSingleObjectListener(IObjectSelectionListener l) {
        singleObjectListeners.add(l);
    }

    public void removeSingleObjectListener(IObjectSelectionListener l) {
        singleObjectListeners.remove(l);
    }

    public Vertex getVertexByPoint(Point p) {
        Point2D ip = source.inverseViewTransform(p);
        Vertex vertex = source.getPickSupport().getVertex(ip.getX(), ip.getY());
        Object[] vertices = getSelectedVertices();
        boolean isSelected = false;
        for (int i = 0; i < vertices.length; i++) {
            if (vertex != null &&
                    GraphNetworkUtil.getID(vertex) == GraphNetworkUtil.getID((Vertex) vertices[i])) {
                isSelected = true;
            }
        }
        if (!isSelected) {
            HashSet<Vertex> set = new HashSet<Vertex>();
            if (vertex != null) {
                set.add(vertex);
                selectVertices(set);
            }
        }
        return vertex;
    }

    public void selectVertexByPoint(Point p) {
        HashSet<Vertex> set = new HashSet<Vertex>();
        Vertex v = getVertexByPoint(p);
        if (v != null) {
            set.add(v);
            selectVertices(set);
        }
    }

    public void itemStateChanged(ItemEvent e) {
        if (selectNeighbours) {
            clearNeighbourSelection();
            createNeighbourSelection();
        }
        if (lockRecursion)
            return;

        if (source.getPickedState().getPickedEdges().size() > 0) {
            lockRecursion = true;
            HashSet<Edge> visibleEdges = new HashSet<Edge>();
            Set edges = source.getPickedState().getPickedEdges();
            for (Object o : edges){
                if (o instanceof Edge){
                    if (GraphDataUtil.getVisible((Edge)o)){
                        visibleEdges.add((Edge)o);
                    }
                }
            }
            model.selectEdges(visibleEdges);
            lockRecursion = false;
            mainFrame.setEdgeEnableControl((visibleEdges).size() > 0);
            if (visibleEdges.size()>0)
                mainFrame.setCurrentSelectedEdge((Edge) (visibleEdges).toArray()[0]);

        } else {
            mainFrame.setEdgeEnableControl(false);
        }

        if (source.getPickedState().getPickedVertices().size() > 0) {
            lockRecursion = true;
            HashSet<Vertex> visibleVertexes = new HashSet<Vertex>();
            Set vertexes = source.getPickedState().getPickedVertices();
            for (Object o : vertexes){
                if (o instanceof Vertex){
                    if (GraphDataUtil.getVisible((Vertex)o)){
                        visibleVertexes.add((Vertex)o);
                    }
                }
            }
            model.selectVertices(visibleVertexes);
            lockRecursion = false;
            if (visibleVertexes.size() > 1) mainFrame.setDisabledOpenCard();
            if (visibleVertexes.size()>0)
                mainFrame.setCurrentSelectedVertex((Vertex) (visibleVertexes).toArray()[0]);
            mainFrame.setVertexEnableControl(visibleVertexes.size() > 0);

        } else {
            mainFrame.setVertexEnableControl(false);
        }

        source.repaint();
    }

    public void selectVertices(Set<Vertex> vertices) {
        if (lockRecursion)
            return;
        lockRecursion = true;
        source.getPickedState().clearPickedVertices();
        if (vertices == null) return;
        for (Vertex o : vertices) {
            if (GraphDataUtil.getVisible(o))
                source.getPickedState().pick(o, true);
        }
        lockRecursion = false;
    }

    public void selectEdges(Set<Edge> edges) {
        if (lockRecursion)
            return;
        lockRecursion = true;
        source.getPickedState().clearPickedEdges();
        for (Edge o : edges) {
            if (GraphDataUtil.getVisible(o))
                source.getPickedState().pick(o, true);
        }
        lockRecursion = false;

    }

    protected void clearNeighbourSelection() {
        neighbours.clear();
    }

    protected void createNeighbourSelection() {
        Set s = source.getPickedState().getPickedVertices();
        Set n;
        Vertex v;
        for (Object o : s) {
            v = (Vertex) o;
            n = v.getNeighbors();
            for (Object o1 : n)
                if (!s.contains(o1)) {
                    neighbours.add((Vertex) o1);
                }
        }
    }

    public void paint(Graphics g) {
        if (selectNeighbours) {
            // paint neighbour vertices
            for (Vertex v : neighbours) {
                Point2D p = source.getBufferedVertexPosition(v);
                if (p != null) {
                    GraphDataUtil.setHighlighted(v, true);
                    source.getRenderer().paintVertex(g, v, (int) p.getX(), (int) p.getY());
                    GraphDataUtil.setHighlighted(v, false);
                }
            }
        }
        if (marked.size() > 0) {
            // paint neighbour vertices
            for (Vertex v : marked) {
                if (selectNeighbours && neighbours.contains(v))
                    continue;
                Point2D p = source.getBufferedVertexPosition(v);
                if (p != null) {
                    GraphDataUtil.setMarked(v, true);
                    source.getRenderer().paintVertex(g, v, (int) p.getX(), (int) p.getY());
                    GraphDataUtil.setMarked(v, false);
                }
            }
        }
    }

    public boolean useTransform() {
        return true;
    }

    protected void fireObjectSelectionEvent(Vertex v) {
        for (IObjectSelectionListener l : singleObjectListeners)
            l.objectSelected(v);
    }

    protected void fireEdgeSelectionEvent(Edge e) {
        for (IObjectSelectionListener l : singleObjectListeners)
            l.linkSelected(e);
    }

    public void mouseClicked(MouseEvent e) {
        Set selectedVertexs = source.getPickedState().getPickedVertices();
        Set selectedEdges = source.getPickedState().getPickedEdges();
        Vertex v = null;
        Edge edge = null;
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (selectedVertexs.size() >= 1) {
                v = (Vertex) selectedVertexs.iterator().next();
                fireObjectSelectionEvent(v);
            }
            if (selectedEdges.size() >= 1) {
                edge = (Edge) selectedEdges.iterator().next();
                fireEdgeSelectionEvent(edge);
            }
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public Object[] getSelectedVertices() {
        Set<Vertex> vertices = source.getPickedState().getPickedVertices();
        return vertices.toArray();
    }

    public Edge getEdgeByPoint(Point p) {
        Point2D ip = source.inverseViewTransform(p);
        Edge edge = source.getPickSupport().getEdge(ip.getX(), ip.getY());
        Object[] edges = getSelectedEdges();
        boolean isSelected = false;
        for (int i = 0; i < edges.length; i++) {
            if (edge != null && GraphNetworkUtil.getID(edge) == GraphNetworkUtil.getID((Edge) edges[i])) {
                isSelected = true;
            }
        }
        if (!isSelected) {
            HashSet<Edge> set = new HashSet<Edge>();
            if (edge != null) {
                set.add(edge);
                selectEdges(set);
            }
        }
        return edge;
    }

    public Object[] getSelectedEdges() {
        Set<Edge> edges = source.getPickedState().getPickedEdges();
        return edges.toArray();
    }

    public void selectEdgeByPoint(Point p) {
        HashSet<Edge> set = new HashSet<Edge>();
        Edge e = getEdgeByPoint(p);
        if (e != null) {
            set.add(e);
            selectEdges(set);
        }
    }

    public int getParallelIndex(Edge e) {
        return ParallelEdgeIndexSingleton.getInstance().getIndex(e);
    }


    public Set<Vertex> collapseVertex(GraphNetworkMapper mapper, Set picked, String nameGroup) {
        Graph inGraph = source.getModel().getGraphLayout().getGraph();
        Graph graph = collapser.collapse(inGraph, collapser.getClusterGraph(inGraph, picked), nameGroup);
        if (graph != null) {
            mapper.setGraph(graph);
        }
        return selectedGroupVertex(graph, nameGroup);
    }

    private Set<Vertex> selectedGroupVertex(Graph graph, String nameGroup) {
        HashSet<Vertex> res = new HashSet<Vertex>();
        if (graph != null && nameGroup != null) {
            for (Vertex v : (Set<Vertex>) graph.getVertices()) {
                if (nameGroup.equalsIgnoreCase(GraphNetworkUtil.getNode(v).getName())) {
                    res.add(v);
                }
            }
        }
        return res;
    }

    public Set<Vertex> expandVertex(GraphNetworkMapper mapper, Set picked) {
        Graph inGraph = source.getModel().getGraphLayout().getGraph();
        HashSet<Vertex> res = new HashSet<Vertex>();
        try {
            Graph clusterGraph = null;
            for (Object v : picked) {
                if (v instanceof GraphCollapser.CollapsedVertex) {
                    clusterGraph = collapser.getClusterGraph(inGraph, picked);
                    Graph graph = collapser.expand(inGraph, clusterGraph);
                    if (graph != null) {
                        mapper.setGraph(graph);
                    }
                    break;
                }
            }
            if (clusterGraph != null) {
                for (Vertex vertex : (Set<Vertex>) clusterGraph.getVertices()) {
                    if (vertex instanceof GraphCollapser.CollapsedVertex) {
                        res.addAll(collapser.getVertexesFrom(vertex));
                    }
                }
            }
            return res;

        }
        catch (Exception ex) {
            ex.printStackTrace();
            return res;
        }
    }


    public Map<Long, Set<Vertex>> getVertexGroups(Graph graph) {
        return collapser.getVertexGroups(graph);
    }

    public Map<Long, String> getGroupNames(Graph graph) {
        return collapser.getGroupNames(graph);
    }

    public Layout getOriginalLayout() {
        return collapser.getOriginalLayout();
    }

    public Set<Vertex> madeGroupGraph(GraphNetworkMapper mapper, LayoutCoordinates layoutCoordinates) {
        Map<Long, Set<Vertex>> vertexGroups = getVertexGroups(source.getModel().getGraphLayout().getGraph());
        Set<Vertex> result = new HashSet<Vertex>();
        Set<Vertex> tempResult;

        if (vertexGroups.size() > 0) {
            if (layoutCoordinates != null && layoutCoordinates.getNames() != null && layoutCoordinates.getNames().size() > 0) {
                Map<Long, String> groupNames = layoutCoordinates.getNames();
                Graph graphIn = source.getModel().getGraphLayout().getGraph();
                Graph graph = source.getModel().getGraphLayout().getGraph();
                String nameGroup;
                for (Long key : vertexGroups.keySet()) {
                    selectVertices(vertexGroups.get(key));
                    nameGroup = groupNames.get(key);
                    graph = collapser.collapse(graph, collapser.getClusterGraph(graphIn, source.getPickedState().getPickedVertices()), nameGroup);
                    if (graph != null) {
                        mapper.setGraph(graph);
                        tempResult = selectedGroupVertex(graph, nameGroup);
                        if (tempResult != null)
                            result.addAll(tempResult);
                    }

                }
                return result;
            }
        }
        return result;
    }
}

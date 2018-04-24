package net.bgx.bgxnetwork.bgxop.engine;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.contrib.CircleLayout;
import edu.uci.ics.jung.visualization.contrib.KKLayout;
import edu.uci.ics.jung.visualization.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.persistence.query.LayoutCoordinates;
import net.bgx.bgxnetwork.persistence.query.SerialPoint2D;

/**
 * Class AlgorithmsHolder
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class AlgorithmsHolder {

    private static AlgorithmsHolder instance = null;
    private static final GraphCustoms defaultCustoms = new GraphCustoms(null, new DefaultLayoutIterationsFunction());

    public static AlgorithmsHolder getInstance() {
        if (instance == null)
            instance = new AlgorithmsHolder();
        return instance;
    }

    public static void initialize(AlgorithmsHolder cfg) {
        instance = cfg;
    }

    public static LayoutCoordinates storeCoordinates(Layout l) {
        LayoutCoordinates lc = new LayoutCoordinates();
//    lc.setKey(l.getBaseKey());
        lc.setLayoutSize(l.getCurrentSize());
        HashMap<Integer, SerialPoint2D> coordinates = new HashMap<Integer, SerialPoint2D>();
        Vertex v;
        for (Iterator it = l.getVertexIterator(); it.hasNext();) {
            v = (Vertex) it.next();
            coordinates.put(GraphNetworkUtil.getID(v), new SerialPoint2D(l.getX(v), l.getY(v)));
        }
        lc.setCoordinates(coordinates);
        return lc;
    }

    public static LayoutCoordinates storeCoordinates(Layout l, Map<Long, String> groupNames) {
        LayoutCoordinates lc = new LayoutCoordinates();
//    lc.setKey(l.getBaseKey());
        lc.setLayoutSize(l.getCurrentSize());
        HashMap<Integer, SerialPoint2D> coordinates = new HashMap<Integer, SerialPoint2D>();
        Vertex v;
        int id;
        for (Iterator it = l.getVertexIterator(); it.hasNext();) {
            v = (Vertex) it.next();
            id = GraphNetworkUtil.getID(v);
            coordinates.put(id, new SerialPoint2D(l.getX(v), l.getY(v)));
        }
        lc.setCoordinates(coordinates);
        lc.setNames(groupNames);
        return lc;
    }

    public static Layout loadCoordinates(LayoutCoordinates lc, Graph g) {
        return new CustomLayout(lc, g);
    }

    //*************************** nonstatic *******************************

    private IGraphAlgorithms algorithms = new LocalGraphAlgorithms();
    private IGraphSizeFunction graphSizeFunction = new DefaultGraphSizeFunction();
    private ILayoutIterationsFunction iterationsFunction = new DefaultLayoutIterationsFunction();
    private HashMap<Long, GraphCustoms> graphCustoms = new HashMap<Long, GraphCustoms>();

    protected AlgorithmsHolder() {
    }

    protected AlgorithmsHolder(IGraphAlgorithms algorithms, IGraphSizeFunction graphSizeFunction,
                               ILayoutIterationsFunction iterationsFunction) {
        if (algorithms != null)
            this.algorithms = algorithms;
        if (graphSizeFunction != null)
            this.graphSizeFunction = graphSizeFunction;
        if (iterationsFunction != null)
            this.iterationsFunction = iterationsFunction;
    }

    public IGraphAlgorithms getAlgorithms() {
        return algorithms;
    }

    public IGraphSizeFunction getGraphSizeFunction() {
        return graphSizeFunction;
    }

    public ILayoutIterationsFunction getIterationsFunction() {
        return iterationsFunction;
    }

    public void setAlgorithms(IGraphAlgorithms algorithms) {
        this.algorithms = algorithms;
    }

    public void setGraphSizeFunction(IGraphSizeFunction graphSizeFunction) {
        this.graphSizeFunction = graphSizeFunction;
    }

    public void setIterationsFunction(ILayoutIterationsFunction iterationsFunction) {
        this.iterationsFunction = iterationsFunction;
    }

    public boolean graphCustomsExists(long queryId) {
        return graphCustoms.containsKey(queryId);
    }

    public GraphCustoms getGraphCustoms(long queryId) {
        return graphCustoms.get(queryId);
    }

    public GraphCustoms createGraphCustoms(long queryId) {
        GraphCustoms c = graphCustoms.get(queryId);
        if (c == null) c = graphCustoms.put(queryId, new GraphCustoms());
        return c;
    }

    public GraphCustoms removeGraphCustoms(long queryId) {
        return graphCustoms.remove(queryId);
    }

    public void setGraphCustoms(long queryId, GraphCustoms customs) {
        graphCustoms.put(queryId, customs);
    }

    public Layout doCustomLayout(LayoutType layoutType, Graph graph, Dimension bounds, int iterations) {
        AbstractLayout layout = null;
        switch (layoutType) {
            case Circle:
                layout = new CircleLayout(graph);
                break;
            case FR:
                layout = new FRLayout(graph);
                break;
            case ISOM:
                layout = new ISOMLayout(graph);
                break;
            case KK:
                layout = new KKLayout(graph);
                break;
            case Spring:
                layout = new edu.uci.ics.jung.visualization.SpringLayout(graph);
                break;
            case Static:
                layout = new StaticLayout(graph);
                break;
            default:
                return null;
        }
        OffscreenLayout offscreen = new OffscreenLayout(layout, layoutType);
        algorithms.doLayout(offscreen, bounds, iterations);
        return offscreen;
    }

    public Layout doDefaultLayout(LayoutType layoutType, Graph graph) {
        Dimension dim = graphSizeFunction.getGraphSize(graph.numVertices(), graph.numEdges());
        int iterations = iterationsFunction.getIterations(layoutType);
        return doCustomLayout(layoutType, graph, dim, iterations);
    }

    public Layout doLayout(long queryId, LayoutType layoutType, Graph graph) {
        GraphCustoms customs = graphCustoms.get(queryId);
        if (customs == null) return doDefaultLayout(layoutType, graph);
        Dimension size = customs.getGraphSize();
        if (size == null) size = graphSizeFunction.getGraphSize(graph.numVertices(), graph.numEdges());
        ILayoutIterationsFunction iterFunction = customs.getIterationsFunction();
        if (iterFunction == null) iterFunction = iterationsFunction;
        return doCustomLayout(layoutType, graph, size, iterFunction.getIterations(layoutType));
    }

    public GraphCustoms createCommonCustoms() {
        GraphCustoms res = new GraphCustoms();
        if (graphSizeFunction instanceof ConstantGraphSizeFunction) {
            ConstantGraphSizeFunction f = (ConstantGraphSizeFunction) graphSizeFunction;
            res.setGraphSize(f.getGraphSize(0, 0));
        }
        res.setIterationsFunction(iterationsFunction);
        return res;
    }

    public void setCommonCustoms(GraphCustoms customs) {
        if (customs.getGraphSize() != null) {
            graphSizeFunction = new ConstantGraphSizeFunction(customs.getGraphSize());
        } else if (!(graphSizeFunction instanceof DefaultGraphSizeFunction)) {
            graphSizeFunction = new DefaultGraphSizeFunction();
        }
        if (customs.getIterationsFunction() != null) {
            iterationsFunction = customs.getIterationsFunction();
        }
    }

    public GraphCustoms getDefaultCustoms() {
        return defaultCustoms;
    }
}

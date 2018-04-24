package net.bgx.bgxnetwork.bgxop.engine;
import java.awt.Dimension;
import java.util.Iterator;
import oracle.spatial.network.NetworkDataException;
import oracle.spatial.network.NetworkManager;
import oracle.spatial.network.Path;
import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import net.bgx.bgxnetwork.exception.query.ErrorList;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;
import net.bgx.bgxnetwork.exception.query.QueryDataException;

/**
 * Class LocalGraphAlgorithms
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class LocalGraphAlgorithms implements IGraphAlgorithms{
    public void doLayout(OffscreenLayout layout, Dimension bounds, int iterations){
        layout.initialize(bounds);
        layout.doLayout(iterations);
    }
    public Iterator getClusters(GraphNetworkMapper data, int degree) throws QueryBusinesException {
        check4graph(data);
        if(degree > data.getGraph().numEdges())
            degree = data.getGraph().numEdges();
        EdgeBetweennessClusterer clusterer = new EdgeBetweennessClusterer(degree);
        return clusterer.extract(data.getGraph()).iterator();
    }
    public Path shortestPath(GraphNetworkMapper data, int from, int to) throws QueryBusinesException, QueryDataException{
        check4network(data);
        try{
            return NetworkManager.shortestPathAStar(data.getNetwork(), from, to);
        }catch (NetworkDataException e){
            throw new QueryDataException(ErrorList.DATA_NETWORK_ORACLE_EXCEPTION);
        }
    }
    public Path[] allPaths(GraphNetworkMapper data, int from, int to, int depthLimit, int pathCount) throws QueryBusinesException, QueryDataException{
        check4network(data);
        try{
            return NetworkManager.allPaths(data.getNetwork(), from, to, depthLimit, depthLimit, pathCount);
        }catch (NetworkDataException e){
            throw new QueryDataException(ErrorList.DATA_NETWORK_ORACLE_EXCEPTION);
        }
    }
    public Path pathThrough(GraphNetworkMapper data, int[] nodes) throws QueryBusinesException, QueryDataException{
        check4network(data);
        try{
            return NetworkManager.tspPath(data.getNetwork(), nodes, null);
        }catch (NetworkDataException e){
            throw new QueryDataException(ErrorList.DATA_NETWORK_ORACLE_EXCEPTION);
        }
    }
    protected void check4graph(GraphNetworkMapper data) throws QueryBusinesException{
        if(!data.graphExists()){
            data.restoreGraph();
        }
        if(!data.graphExists()){
            throw new QueryBusinesException(ErrorList.BUSINES_GRAPH_BUILD_EXCEPTION, new Object[] { "Cannot restore graph data." });
        }
    }
    // what's this???
    protected void check4network(GraphNetworkMapper data) throws QueryBusinesException{
    }
}

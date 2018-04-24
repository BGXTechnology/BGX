package net.bgx.bgxnetwork.bgxop.engine;
import java.awt.Dimension;
import java.util.Iterator;
import oracle.spatial.network.Path;
import net.bgx.bgxnetwork.exception.query.QueryDataException;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;

/**
 * Class IRemoteGraphAlgorithms
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public interface IGraphAlgorithms{
    public Iterator getClusters(GraphNetworkMapper data, int degree) throws QueryBusinesException;
    public Path shortestPath(GraphNetworkMapper data, int from, int to) throws QueryBusinesException, QueryDataException;
    public Path[] allPaths(GraphNetworkMapper data, int from, int to, int depthLimit, int pathCount) throws QueryBusinesException,
            QueryDataException;
    public Path pathThrough(GraphNetworkMapper data, int[] nodes) throws QueryBusinesException, QueryDataException;
    public void doLayout(OffscreenLayout layout, Dimension bounds, int iterations);
}

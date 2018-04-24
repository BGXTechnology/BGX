package net.bgx.bgxnetwork.bgxop.engine;
import java.awt.Dimension;
import net.bgx.bgxnetwork.persistence.query.LayoutCoordinates;
import net.bgx.bgxnetwork.exception.query.QueryDataException;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;

/**
 * Class IRemoteGraphAlgorithms
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public interface IRemoteGraphAlgorithms{
    public int[][] getClusters(String graphName, int degree) throws QueryBusinesException;
    public int[] shortestPathLinks(String graphName, int from, int to) throws QueryBusinesException, QueryDataException;
    public int[][] allPathsLinks(String graphName, int from, int to, int depthLimit, int pathCount) throws QueryBusinesException, QueryDataException;
    public int[] pathThroughLinks(String graphName, int[] nodes) throws QueryBusinesException, QueryDataException;
    public LayoutCoordinates doLayout(String graphName, LayoutType layoutType, Dimension bounds, int iterations);
}

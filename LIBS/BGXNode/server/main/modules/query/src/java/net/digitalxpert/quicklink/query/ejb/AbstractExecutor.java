package net.bgx.bgxnetwork.query.ejb;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import net.bgx.bgxnetwork.persistence.query.QueryEntity;
import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.transfer.query.QueryData;
import net.bgx.bgxnetwork.transfer.query.QueryParameterType;
import net.bgx.bgxnetwork.dao.DAOFactory;
import net.bgx.bgxnetwork.dao.AbstractDAO;
import net.bgx.bgxnetwork.query.dao.interfaces.AbstractExecutorDAO;
import net.bgx.bgxnetwork.query.dao.ExecutorDAO;
import net.bgx.bgxnetwork.exception.query.QueryDataException;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;
import net.bgx.bgxnetwork.exception.query.ErrorList;
import oracle.spatial.network.NetworkFactory;
import org.apache.log4j.Logger;

/**
 * Class AbstractExecutor
 *
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public abstract class AbstractExecutor implements IExecutor {
    private static int timeoutIter = 5;
    private static long timeout = 500;
    private static Logger log = Logger.getLogger(AbstractExecutor.class.getName());
    private DataSource dataSource;
    private EntityManager entityManager;
    private Locale clientLocale = Locale.getDefault();

    public AbstractDAO _dao = null;
    private DAOFactory _daoFactory = DAOFactory.getInstance(AbstractExecutorDAO.class.getPackage().getName(),
            getClass().getClassLoader());

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Locale getClientLocale() {
        return clientLocale;
    }

    public void setClientLocale(Locale clientLocale) {
        this.clientLocale = clientLocale;
    }

    private AbstractDAO getDAO() {
        if (_dao == null)
            _dao = (AbstractDAO) _daoFactory.getDAO(AbstractExecutorDAO.class);
        return _dao;
    }

    protected void setDAO(AbstractDAO dao) {
        if (_dao != null) doClose();
        _dao = dao;
    }

    public QueryData execute(Query q) throws QueryDataException, QueryBusinesException, SQLException {
        log.info("Query " + q.getId() + " '" + q.getName() + "' execution started.");
        ExecutorDAO dao = (ExecutorDAO) getDAO();
        //dao.executeQuery(q.getId());
        log.info("Query " + q.getId() + " '" + q.getName() + "' execution finished normally.");
        return null;
    }

    public void commitQueryData(QueryEntity q, QueryData data) throws QueryDataException {
    }

    public void commitQueryData(Long qId, String netName, QueryData queryData) throws QueryDataException {
    }

    public QueryData readQueryData(QueryEntity q) throws QueryDataException {
        ExecutorDAO dao = (ExecutorDAO) getDAO();
        QueryData data = dao.readQueryData(q.getId(), q.getLimited());
        return data;
    }

    public void removeQueryData(QueryEntity q) throws QueryDataException {
        AbstractExecutorDAO dao = (AbstractExecutorDAO) getDAO();
        dao.removeQueryData(q.getId());
    }

    public void removeQueryData(Long qId) throws QueryDataException {
        ExecutorDAO dao = (ExecutorDAO) getDAO();
        dao.removeQueryData(qId);
    }

    protected static String[] list2strings(List<Integer> list) {
        int blockSize = 500;
        int arrSize = list.size() / blockSize;
        if (list.size() % blockSize != 0)
            arrSize++;
        String[] res = new String[arrSize];
        for (int i = 0; i < list.size(); i++) {
            if (i % blockSize == 0)
                res[i / blockSize] = list.get(i).toString();
            else
                res[i / blockSize] += "," + list.get(i).toString();
        }
        return res;
    }

    public void removeFromGraph(Long qId, String netName, List<Integer> vertices, List<Integer> edges) throws QueryDataException {
        if (netName == null || netName.length() == 0)
            return;
        String[] vxLists = list2strings(vertices);
        String[] lkLists = list2strings(edges);
        int res;

        ExecutorDAO dao = (ExecutorDAO) getDAO();
        if (edges != null) {
            res = dao.removeLinksFromGraph(qId, netName, lkLists);
            log.info(res + " links removed.");

        }
        if (vertices != null) {
            res = dao.removeNodesFromGraph(qId, netName, lkLists);
            log.info(res + " vertices removed.");
        }
    }

    protected void closeConnection(Connection con, Statement st) throws QueryDataException {
        try {
            if (st != null) {
                st.close();
            }
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    public void doClose() {
        ((AbstractDAO) getDAO()).doClose(null, null);
    }

    public AbstractDAO getExecDAO() {
        return (AbstractDAO) getDAO();
    }

    protected int parseIntParameter(Query q, QueryParameterType type) throws QueryBusinesException {
        Object s = q.getParameter(type);
        if (s == null)
            return 0;
        try {
            return Integer.parseInt((String) s);
        } catch (NumberFormatException e) {
            String message = "Cannot parse " + type + " as integer.";
            throw new QueryBusinesException(ErrorList.BUSINES_CONVERSION_TYPE_EXCEPTION, new Object[]{message});
        }
    }
}

package net.bgx.bgxnetwork.query.ejb;

import java.util.List;
import java.util.Locale;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import net.bgx.bgxnetwork.exception.query.QueryDataException;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;
import net.bgx.bgxnetwork.persistence.query.QueryEntity;
import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.transfer.query.QueryData;
import net.bgx.bgxnetwork.dao.AbstractDAO;

/**
 * Interface IExecutor
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public interface IExecutor {
    public DataSource getDataSource();
    public void setDataSource(DataSource dataSource);
    public EntityManager getEntityManager();
    public void setEntityManager(EntityManager entityManager);
    public Locale getClientLocale();
    public void setClientLocale(Locale clientLocale);
    public QueryData execute(Query q) throws QueryDataException, QueryBusinesException, SQLException;
    public void commitQueryData(Long qId, String netName, QueryData data) throws QueryDataException;
    public void removeQueryData(QueryEntity q) throws QueryDataException;
    public void removeQueryData(Long qId) throws QueryDataException;
    public QueryData readQueryData(QueryEntity q) throws QueryDataException;
    public QueryData getNodeList(QueryEntity q, int vxId) throws QueryDataException;
    public void removeFromGraph(Long qId, String netName, List<Integer> vertices, List<Integer> edges) throws QueryDataException;
    public abstract void doClose();
    public AbstractDAO getExecDAO();
    public QueryData copyDataFromQuery(Long oldQID, Long newQID) throws QueryBusinesException, QueryDataException, SQLException;
}

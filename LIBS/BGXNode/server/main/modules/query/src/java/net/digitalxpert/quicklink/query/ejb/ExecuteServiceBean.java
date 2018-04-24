package net.bgx.bgxnetwork.query.ejb;

import net.bgx.bgxnetwork.persistence.query.QueryEntity;
import net.bgx.bgxnetwork.query.interfaces.ExecuteServiceLocal;
import net.bgx.bgxnetwork.query.interfaces.ExecuteServiceRemote;
import net.bgx.bgxnetwork.query.interfaces.QueryServiceManagerLocal;
import net.bgx.bgxnetwork.system.SystemSetting;
import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.transfer.query.QueryData;
import net.bgx.bgxnetwork.exception.query.QueryDataException;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;
import net.bgx.bgxnetwork.exception.query.ErrorList;
import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;
import java.util.Locale;
import java.sql.SQLException;

/**
 * Class ExecuteServiceBean
 *
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
@Stateless
@SecurityDomain(SystemSetting.SECURITY_DOMAIN)
@RemoteBinding(clientBindUrl = SystemSetting.QL_CLIENT_BIND_URL)
public class ExecuteServiceBean implements ExecuteServiceRemote, ExecuteServiceLocal {
    private static Logger log = Logger.getLogger(ExecuteServiceBean.class.getName());
    @Resource(mappedName = "java:/bgxnetworkDS_CORE")
    private javax.sql.DataSource ds;

    @EJB
    QueryServiceManagerLocal queryManagerService;

    @RolesAllowed( { "LVSystem" })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public QueryData executeQuery(Query q, String executorClassName, Locale clientLocale) throws QueryDataException, QueryBusinesException {
        IExecutor exec = getExecutor(executorClassName);
        queryManagerService.setOracleSessionId(q.getId(), exec.getExecDAO().getSessionId());
        QueryData result = null;
        try {
            exec.setClientLocale(clientLocale);
            result = exec.execute(q);
            queryManagerService.setOracleSessionId(q.getId(), null);
        } catch (SQLException ignore) {
            // ignore exception from killed oracle session (query entity removed by user)
        }
        finally {
            exec.doClose();
        }
        return result;
    }

    @RolesAllowed( { "LVSystem" })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void removeQueryData(Long qId, String executorClassName) throws QueryDataException, QueryBusinesException {
        IExecutor exec = getExecutor(executorClassName);
        try{
          queryManagerService.setOracleSessionId(qId, exec.getExecDAO().getSessionId());
          exec.removeQueryData(qId);
          queryManagerService.setOracleSessionId(qId, null);
        }
        catch(Exception e){
            log.error("Couldn't remove query data"+e);
        }
        finally{
          exec.doClose();
        }
    }

    @RolesAllowed( { "LVSystem" })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void removeQueryData(QueryEntity qe, String executorClassName) throws QueryDataException, QueryBusinesException{
        IExecutor exec = getExecutor(executorClassName);
        try{
          queryManagerService.setOracleSessionId(qe.getId(), exec.getExecDAO().getSessionId());
          exec.removeQueryData(qe);
          queryManagerService.setOracleSessionId(qe.getId(), null);
        }
        finally{
          exec.doClose();
        }
    }

    @RolesAllowed( { "LVSystem" })
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public QueryData getNodeList(QueryEntity qe, int vxId, Locale clientLocale) throws QueryDataException, QueryBusinesException{
        IExecutor exec = getExecutor(qe.getQueryType().getExecutorClassName());
        QueryData result = null;
        try{
          queryManagerService.setOracleSessionId(qe.getId(), exec.getExecDAO().getSessionId());
          exec.setClientLocale(clientLocale);
          result = exec.getNodeList(qe, vxId);
          queryManagerService.setOracleSessionId(qe.getId(), null);
        }
        finally{
          exec.doClose();
        }
        return result;
    }

    @RolesAllowed( { "LVSystem" })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void commitQueryData(Long qId, String netName, QueryData queryData, String executorClassName) throws QueryDataException, QueryBusinesException {
        IExecutor exec = getExecutor(executorClassName);
        try{
          queryManagerService.setOracleSessionId(qId, exec.getExecDAO().getSessionId());
          exec.commitQueryData(qId, netName, queryData);
          queryManagerService.setOracleSessionId(qId, null);
        }
        finally{
          exec.doClose();
        }
    }

    public void removeFromGraph(Long qId, List<Integer> verticles, List<Integer> edges, String executorClassName, String netName) throws QueryDataException, QueryBusinesException {
        IExecutor exec = getExecutor(executorClassName);
        try {
            queryManagerService.setOracleSessionId(qId, exec.getExecDAO().getSessionId());
            exec.removeFromGraph(qId, netName, verticles, edges);
            queryManagerService.setOracleSessionId(qId, null);
        }
        finally {
            exec.doClose();
        }
    }

    private IExecutor getExecutor(String executorClassName) throws QueryBusinesException {
        try{
            return (IExecutor) Class.forName(executorClassName).newInstance();
        }
        catch(Exception e){
            throw new QueryBusinesException(ErrorList.BUSINES_CANNOT_INSTANTIATE_EXECUTOR);
        }
    }

    @RolesAllowed( { "LVSystem" })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public QueryData copyQuery(Long qId, Long newId, String executorClassName, Locale clientLocale) throws QueryDataException, QueryBusinesException {
        IExecutor exec = getExecutor(executorClassName);
        queryManagerService.setOracleSessionId(newId, exec.getExecDAO().getSessionId());
        QueryData result = null;
        try {
            exec.setClientLocale(clientLocale);
            result = exec.copyDataFromQuery(qId, newId);
            queryManagerService.setOracleSessionId(newId, null);
        } catch (SQLException ignore) {
            // ignore exception from killed oracle session (query entity removed by user)
        }
        finally {
            exec.doClose();
        }
        return result;
    }

}

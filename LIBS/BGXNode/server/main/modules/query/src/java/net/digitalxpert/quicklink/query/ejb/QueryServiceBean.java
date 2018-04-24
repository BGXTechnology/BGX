package net.bgx.bgxnetwork.query.ejb;

import java.util.*;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.interceptor.Interceptors;
import javax.interceptor.ExcludeClassInterceptors;

import net.bgx.bgxnetwork.exception.query.ErrorList;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;
import net.bgx.bgxnetwork.exception.query.QueryDataException;
import net.bgx.bgxnetwork.persistence.query.*;
import net.bgx.bgxnetwork.persistence.metadata.PropertyType;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;
import net.bgx.bgxnetwork.query.interfaces.QueryServiceImplLocal;
import net.bgx.bgxnetwork.query.interfaces.QueryServiceLocal;
import net.bgx.bgxnetwork.query.interfaces.QueryServiceManagerLocal;
import net.bgx.bgxnetwork.query.mdb.MessageObject;
import net.bgx.bgxnetwork.query.mdb.MessageCode;
import net.bgx.bgxnetwork.query.cache.QueryCache;
import net.bgx.bgxnetwork.system.SystemSetting;
import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.transfer.query.QueryData;
import net.bgx.bgxnetwork.transfer.query.QueryStatus;
import net.bgx.bgxnetwork.transfer.query.QueryType;
import net.bgx.bgxnetwork.transfer.tt.TDPair;
import net.bgx.bgxnetwork.transfer.tt.TimedDiagrammDataSnapshot;
import net.bgx.bgxnetwork.audit.annotation.Audit;
import net.bgx.bgxnetwork.audit.strategy.AuditCode;
import net.bgx.bgxnetwork.audit.strategy.AuditLogs;
import net.bgx.bgxnetwork.audit.aspect.AuditInterceptor;
import net.bgx.bgxnetwork.audit.aspect.AuditPreProceedInterceptor;
import net.bgx.bgxnetwork.security.auth.server.LanUsers;
import org.jboss.annotation.security.SecurityDomain;
import org.jboss.util.Base64;
import org.apache.log4j.Logger;

/**
 * Class QueryServiceBean
 *
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
@Stateless
@SecurityDomain(SystemSetting.SECURITY_DOMAIN)
@RunAs("LVSystem")
@Interceptors({AuditInterceptor.class})
public class QueryServiceBean implements QueryServiceLocal {
    private static Logger log = Logger.getLogger(QueryServiceBean.class.getName());
    @PersistenceContext(unitName = "business_manager")
    private EntityManager manager;
    @EJB
    QueryServiceImplLocal impl;
    @EJB
    QueryServiceManagerLocal queryService;
    @Resource
    private javax.ejb.SessionContext ctx;

    @RolesAllowed({"LV_ROLE"})
    public List<QueryType> getQueryTypeList() throws QueryBusinesException, QueryDataException {
        if (QueryCache.getInstance().getQueryTypeList() == null)
            QueryCache.getInstance().setQueryTypeList(impl.getQueryTypeList());
        return QueryCache.getInstance().getQueryTypeList();
    }

    @RolesAllowed({"LV_ROLE"})
    public List<Query> getQueryList() throws QueryBusinesException, QueryDataException {
        return impl.getQueryList(LanUsers.getUserId(ctx.getCallerPrincipal().getName()));
    }

    public List<Query> getQueryListByUser(Long userId) throws QueryBusinesException, QueryDataException {
        return impl.getQueryListByUser(userId);
    }

    @RolesAllowed({"LV_ROLE"})
    @Audit(code = AuditCode.CREATE_VERSION, strategy = AuditLogs.QUERY_LOG)
    public Long create(Query q) throws QueryBusinesException, QueryDataException {
        QueryTypeEntity qte = findQueryEntityTypeByID(q.getQueryType().getId());
        if (qte == null) {
            throwQueryTypeException("Unknown query type " + q.getQueryType().getId() + " for query '" + q.getName() + "'");
        }
        if (!ctx.isCallerInRole(qte.getRoleName())) {
            throwRoleException("Role '" + qte.getRoleName() + "' required to create query '" + q.getName() + "'");
        }
        return queryService.create(q);
    }

    @RolesAllowed({"LV_ROLE"})
    @Audit(code = AuditCode.SAVE_QUERY, strategy = AuditLogs.QUERY_LOG)
    public void update(Query q) throws QueryBusinesException, QueryDataException {
        QueryTypeEntity qte = findQueryEntityTypeByID(q.getQueryType().getId());
        if (qte == null) {
            throwQueryTypeException("Unknown query type " + q.getQueryType().getId() + " for query '" + q.getName() + "'");
        }
        if (!ctx.isCallerInRole(qte.getRoleName())) {
            throwRoleException("Role '" + qte.getRoleName() + "' required to create query '" + q.getName() + "'");
        }
        manager.clear();

        queryService.update(q);
    }

    @RolesAllowed({"LV_ROLE"})
        @Audit(code = AuditCode.SAVE_QUERY, strategy = AuditLogs.QUERY_LOG)
    public void updateName(Query q) throws QueryBusinesException, QueryDataException {
        QueryTypeEntity qte = findQueryEntityTypeByID(q.getQueryType().getId());
        if (qte == null) {
            throwQueryTypeException("Unknown query type " + q.getQueryType().getId() + " for query '" + q.getName() + "'");
        }
        if (!ctx.isCallerInRole(qte.getRoleName())) {
            throwRoleException("Role '" + qte.getRoleName() + "' required to create query '" + q.getName() + "'");
        }
        queryService.updateName(q);
    }

    @RolesAllowed({"LV_ROLE"})
    public void updateTTParameters(Long qId, String data) throws QueryBusinesException, QueryDataException {
        queryService.updateTTParameters(qId, data);
    }

    @RolesAllowed({"LV_ROLE"})
    public void updateControlObjects(ArrayList<ControlObject> objects) throws QueryBusinesException, QueryDataException {
        MessageObject messageObject = new MessageObject();
        messageObject.setCode(MessageCode.UPDATE_DATA_OBJECTS);
        messageObject.setUserObject(objects);
        MessageSender.getInstance().sendMessage(messageObject);
    }

    public void updateLinkObjects(ArrayList<LinkObject> objects) throws QueryBusinesException, QueryDataException {
        MessageObject messageObject = new MessageObject();
        messageObject.setCode(MessageCode.UPDATE_DATA_LINKS);
        messageObject.setUserObject(objects);
        MessageSender.getInstance().sendMessage(messageObject);
    }

    @RolesAllowed({"LV_ROLE"})
    @Audit(code = AuditCode.VISIBLE_OBJECT, strategy = AuditLogs.QUERY_LOG)
    public HashMap<Long, List<PropertyType>> setVisibleAttributes(Long qId, List<Long> codes) throws QueryBusinesException, QueryDataException {
        return queryService.setVisibleAttributes(qId, codes);
    }

    @RolesAllowed({"LV_ROLE"})
    @ExcludeClassInterceptors
    @Interceptors({AuditPreProceedInterceptor.class})
    @Audit(code = AuditCode.DELETE_QUERY, strategy = AuditLogs.QUERY_LOG)
    public void remove(long queryId) throws QueryDataException, QueryBusinesException {
        queryService.remove(queryId);
    }


    @RolesAllowed({"LV_ROLE"})
    @Audit(code = AuditCode.EXECUTE_QUERY, strategy = AuditLogs.QUERY_LOG)
    public QueryStatus execute(long queryId, Locale clientLocale) throws QueryBusinesException, QueryDataException {
        QueryEntity qe = findQueryEntityByID(queryId);
        if (qe == null) {
            String message = "Cannot find query '" + queryId + "'. Query doesn't exist.";
            log.error(message);
            throw new QueryDataException(ErrorList.DATA_CANNOT_FIND_DATA, new Object[]{message});
        }
        if (!ctx.isCallerInRole(qe.getQueryType().getRoleName())) {
            throwRoleException("Role '" + qe.getQueryType().getRoleName() + "' required to create query '" + qe.getName() + "'");
        }
        manager.clear();
        QueryStatus status = queryService.execute(queryId, clientLocale);
        MessageObject messageObject = new MessageObject();
        messageObject.setCode(MessageCode.EXECUTE_QUERY);
        messageObject.setQueryID(queryId);
        messageObject.setLocale(clientLocale);
        log.info("Create new message, by type: " + MessageCode.EXECUTE_QUERY);
        MessageSender.getInstance().sendMessage(messageObject);
        return status;
    }

    @RolesAllowed({"LV_ROLE"})
    public Long createAndExecute(Query q, Locale clientLocale) throws QueryBusinesException, QueryDataException {
        Long id = create(q);

        if (id == null) {
            String message = "Could not create query '" + q.getName() + "'";
            log.error(message);
            throw new QueryBusinesException(ErrorList.BUSINES_CANNOT_CREATE_QUERY, new Object[]{message});
        }

        execute(id, clientLocale);
        return id;
    }

    @RolesAllowed({"LV_ROLE"})
    public QueryStatus updateAndExecute(Query q, Locale clientLocale) throws QueryBusinesException, QueryDataException {
        update(q);
        return execute(q.getId(), clientLocale);
    }

    @RolesAllowed({"LV_ROLE"})
    @Audit(code = AuditCode.OPEN_VERSION, strategy = AuditLogs.QUERY_LOG)
    public QueryData getQueryData(long queryId, Locale clientLocale) throws QueryBusinesException, QueryDataException {
        QueryEntity qe = findQueryEntityByID(queryId);
        if (qe == null) {
            String message = "Cannot find query '" + queryId + "'. Query doesn't exist.";
            log.error(message);
            throw new QueryDataException(ErrorList.DATA_CANNOT_FIND_DATA, new Object[]{message});
        }
        if (!ctx.isCallerInRole(qe.getQueryType().getRoleName())) {
            throwRoleException("Role '" + qe.getQueryType().getRoleName() + "' required to create query '" + qe.getName() + "'");
        }
        return impl.getQueryData(queryId, clientLocale);
    }

    @RolesAllowed({"LV_ROLE"})
    public void removeFromGraph(long queryId, List<Integer> vertices, List<Integer> edges) throws QueryDataException, QueryBusinesException {
        queryService.removeFromGraph(queryId, vertices, edges);
    }

    @RolesAllowed({"LV_ROLE"})
    @Audit(code = AuditCode.SAVE_LAYOUT, strategy = AuditLogs.QUERY_LOG)
    public boolean saveLayout(long queryId, LayoutCoordinates layout) throws QueryDataException {
        return impl.saveLayout(queryId, layout);
    }

    @RolesAllowed({"LV_ROLE"})
    public LayoutCoordinates getLayout(long queryId) throws QueryDataException {
        return impl.getLayout(queryId);
    }

    @RolesAllowed({"LV_ROLE"})
    public boolean saveAnnotation(long queryId, GraphAnnotation annotation) {
        log.info("Not implemented.");
        return false;
    }

    @RolesAllowed({"LV_ROLE"})
    public GraphAnnotation getAnnotation(long queryId) {
        log.info("Not implemented.");
        return null;
    }

    private void throwRoleException(String message) throws QueryBusinesException {
        log.error(message);
        throw new QueryBusinesException(ErrorList.BUSINES_ROLE_PERMISSION_EXCEPTION, new Object[]{message});
    }

    private void throwQueryTypeException(String message) throws QueryBusinesException {
        log.error(message);
        throw new QueryBusinesException(ErrorList.BUSINES_UNKNOW_QUERY_TYPE, new Object[]{message});
    }

    private QueryEntity findQueryEntityByID(Long id) throws QueryDataException {
        QueryEntity qe = null;
        try {
            qe = manager.find(QueryEntity.class, id);
        } catch (Exception e) {
            throwExecuteException(e);
        }
        return qe;
    }

    private QueryTypeEntity findQueryEntityTypeByID(Integer id) throws QueryDataException {
        QueryTypeEntity qet = null;
        try {
            qet = manager.find(QueryTypeEntity.class, id);
        } catch (Exception e) {
            throwExecuteException(e);
        }
        return qet;
    }

    private void throwExecuteException(Exception e) throws QueryDataException {
        if (e instanceof IllegalArgumentException) {
            throw new QueryDataException(ErrorList.DATA_ILLEGAL_ARGUMENT_EXCEPTION);
        } else {
            throw new QueryDataException(ErrorList.DATA_CANNOT_FIND_DATA);
        }
    }

    @RolesAllowed({"LV_ROLE"})
    public List<String> getInnDictionary() throws QueryBusinesException, QueryDataException {
        return impl.getInnDictionary();
    }

    @RolesAllowed({"LV_ROLE"})
    @Audit(code = AuditCode.COPY_VERSION, strategy = AuditLogs.QUERY_LOG)
    public Long copyQueryFromExist(Query query, Locale clientLocale) throws QueryBusinesException, QueryDataException {
        query.setQueryStatus(QueryStatus.NotSaved);
        Long oldId = query.getId();
        Long newId = create(query);                                                                                                               

        MessageObject messageObject = new MessageObject();
        messageObject.setCode(MessageCode.COPY_DATA_FROM_QUERY);
        messageObject.setQueryID(oldId);
        messageObject.setNewQueryID(newId);
        messageObject.setLocale(clientLocale);
        MessageSender.getInstance().sendMessage(messageObject);
        return newId;
    }

    @RolesAllowed({"LV_ROLE"})
    public boolean saveTTPairs(long queryId, LinkedList<TDPair> pairs) throws QueryDataException {
        return impl.saveTTPairs(queryId, pairs);
    }

    @RolesAllowed({"LV_ROLE"})
    public LinkedList<TDPair> getTTPairs(long queryId) throws QueryDataException {
        return impl.getTTPairs(queryId);
    }

//    @RolesAllowed({"LV_ROLE"})
//    public boolean saveTTParameters(long queryId, Object data) throws QueryDataException {
//        return impl.saveTTParameters(queryId, data);
//    }
    @RolesAllowed({"LV_ROLE"})
    public boolean saveTTParameters(long queryId, TimedDiagrammDataSnapshot data) throws QueryDataException {
        boolean res = impl.saveTTParameters(queryId, data);
        return res;
    }

    @RolesAllowed({"LV_ROLE"})
    public TimedDiagrammDataSnapshot getTTParameters(long queryId) throws QueryDataException {
        QueryEntity qe = manager.find(QueryEntity.class, queryId);
        try{
            return impl.getTTParameters(queryId);
//            String serializeObj = qe.getTtParameters();
//            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(serializeObj));
//            ObjectInputStream ois = new ObjectInputStream(bais);
//            Object obj = ois.readObject();
//            TimedDiagrammDataSnapshot res = (TimedDiagrammDataSnapshot)obj;
//            return res;
        }
        catch(Exception e){
            return null;
        }
    }
}

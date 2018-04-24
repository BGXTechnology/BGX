package net.bgx.bgxnetwork.query.ejb;
import net.bgx.bgxnetwork.persistence.auditmanager.EventLog;
import net.bgx.bgxnetwork.persistence.query.ParameterValueEntity;
import net.bgx.bgxnetwork.persistence.query.QueryEntity;
import net.bgx.bgxnetwork.persistence.query.QueryLobEntity;
import net.bgx.bgxnetwork.persistence.query.QueryTypeEntity;
import net.bgx.bgxnetwork.persistence.metadata.*;
import net.bgx.bgxnetwork.query.interfaces.QueryServiceManagerLocal;
import net.bgx.bgxnetwork.query.interfaces.QueryServiceLocal;
import net.bgx.bgxnetwork.query.interfaces.MetaDataServiceLocal;
import net.bgx.bgxnetwork.query.mdb.MessageCode;
import net.bgx.bgxnetwork.query.mdb.MessageObject;
import net.bgx.bgxnetwork.query.mdb.MessagePropertyName;
import net.bgx.bgxnetwork.system.SystemSetting;
import net.bgx.bgxnetwork.transfer.query.*;
import net.bgx.bgxnetwork.transfer.tt.TimedDiagrammDataSnapshot;
import net.bgx.bgxnetwork.exception.query.QueryDataException;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;
import net.bgx.bgxnetwork.exception.query.ErrorList;
import net.bgx.bgxnetwork.security.auth.server.LanUsers;
import net.bgx.bgxnetwork.toolkit.ejb.ServiceLocator;
import org.jboss.annotation.security.SecurityDomain;
import org.jboss.util.Base64;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * User: A.Borisenko Date: 23.10.2006 Time: 16:02:55 To change this template use
 * File | Settings | File Templates.
 */
@Stateless
@SecurityDomain(SystemSetting.SECURITY_DOMAIN)
@RolesAllowed( { "LVSystem" })
public class QueryServiceManagerBean implements QueryServiceManagerLocal{
    public static final String netName = "pln";
    @Resource
    private EJBContext _ctx;
    @PersistenceContext(unitName = "business_manager")
    private EntityManager manager;
    @Resource(mappedName = "java:/bgxnetworkDS_CORE")
    private javax.sql.DataSource ds;
    @EJB
    private MetaDataServiceLocal metaDataService;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long create(Query q) throws QueryDataException, QueryBusinesException{
        if(q.getQueryStatus() != QueryStatus.NotSaved){
            String message = "Cannot create query '" + q.getName() + "'.";
            throw new QueryDataException(ErrorList.DATA_INVALID_QUERY_STATUS, new Object[] { message });
        }
        Long existQueryId = getFromExistsQuery(q);
        if (existQueryId != null) return existQueryId;

        q.setQueryStatus(QueryStatus.Saved);
        QueryEntity entity = null;
        entity = createEntity(q, new QueryEntity());
        entity.setId(null);
        entity.setLob(new QueryLobEntity());
        entity.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        manager.persist(entity);
        q.setId(entity.getId());
        return q.getId();
    }

    private Long getFromExistsQuery(Query q){
        try {
            QueryServiceLocal queryServiceLocal =
                (QueryServiceLocal) ServiceLocator.findEjb3LocalByDefault("QueryServiceBean");
            List<Query> queries = queryServiceLocal.getQueryListByUser(LanUsers.getUserId(_ctx.getCallerPrincipal().getName()));

            QueryType qt = q.getQueryType();
            if (qt.getId() == QueryType.REQUEST)
                return getFromExistRequests(q, queries);
            else if (qt.getId() == QueryType.QUEST)
                return getFromExistQuests(q, queries);
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    private Long getFromExistRequests(Query q, List<Query> existQueries){
        String requestId = (String)q.getParameter(QueryParameterType.REQUEST_ID);
        for(Query existQuery : existQueries){
            if (existQuery.getQueryType().getId() == QueryType.REQUEST){
                String existRequestId = (String) existQuery.getParameter(QueryParameterType.REQUEST_ID);
                if (existRequestId.equals(requestId))
                    return existQuery.getId();
            }
        }
        return null;
    }

    private Long getFromExistQuests(Query q, List<Query> existQueries){
        ArrayList<String> questIds = (ArrayList<String>)q.getParameter(QueryParameterType.QUEST_ID);
        for(Query existQuery : existQueries){
            if (existQuery.getQueryType().getId() == QueryType.QUEST)
                if (existQuery.getParent().equals(q.getParent())) {
                    ArrayList<String> existQuestIds = (ArrayList<String>) existQuery.getParameter(QueryParameterType.QUEST_ID);
                    if (existQuestIds.size() == questIds.size()) {
                        if (existQuestIds.containsAll(questIds))
                            if (q.getName().equals(existQuery.getName()))
                                return existQuery.getId();
                    }
                }
        }
        return null;
    }


    public void update(Query q) throws QueryDataException, QueryBusinesException{
        long qId = updateQuery(q);
        QueryEntity qe = manager.find(QueryEntity.class, qId);
        qe.getLob().setLayout(null);
        qe.getLob().setAnnotation(null);
        updateStatusBy(qe, QueryStatus.Saved, qId);
        manager.merge(qe);
    }

    public void updateName(Query q) throws QueryDataException, QueryBusinesException {
        QueryEntity qe = manager.find(QueryEntity.class, q.getId());
        if(qe == null){
            String message = "Cannot find query '" + q.getName() + "'. ";
            throw new QueryBusinesException(ErrorList.BUSINES_REQUEST_IS_NULL, new Object[] { message });
        }
        qe.setName(q.getName());
        manager.merge(qe);
    }

    public void updateTTParameters(Long qId, String data) throws QueryDataException, QueryBusinesException {
        QueryEntity qe = manager.find(QueryEntity.class, qId);

        qe.setTtParameters(data);
        manager.merge(qe);
    }

    public HashMap<Long, List<PropertyType>> setVisibleAttributes(Long qId, List<Long> codes) throws QueryDataException, QueryBusinesException {
        QueryEntity qe = manager.find(QueryEntity.class, qId);
        if(qe == null){
            String message = "Cannot find query '" + qId + "'. ";
            throw new QueryBusinesException(ErrorList.BUSINES_REQUEST_IS_NULL, new Object[] { message });
        }

        List<PropertyTypeView> ptViews = metaDataService.getListPropertyTypeView();
        HashSet<PropertyTypeView> ptvs = new HashSet<PropertyTypeView>();

        for(PropertyTypeView propertyTypeView : ptViews){
            if (propertyTypeView == null) continue;
                for (Long codeId : codes){
                    if (propertyTypeView.getPropertyType().getCodePropertyType() != null){
                        if (codeId.equals(Long.parseLong(propertyTypeView.getPropertyType().getCodePropertyType()))){
                            ptvs.add(propertyTypeView);
                        }
                    }
                }
        }

        qe.setPropertyViews(ptvs);
        manager.merge(qe);

        //set Visible PropertyType
        HashMap<Long, List<PropertyType>> visibleAttributes = new HashMap<Long, List<PropertyType>>();

        for(PropertyTypeView ptv : ptvs){
            if (ptv != null){
                net.bgx.bgxnetwork.persistence.metadata.ObjectType objectType = ptv.getObjectType();
                PropertyType propertyType = ptv.getPropertyType();
                if (objectType != null && propertyType != null){
                    List<PropertyType> ptByObjectType = visibleAttributes.get((long)objectType.getIdObjectType());
                    if (ptByObjectType == null)
                        ptByObjectType = new ArrayList<PropertyType>();

                    if (!ptByObjectType.contains(propertyType))
                        ptByObjectType.add(propertyType);

                    visibleAttributes.put((long)objectType.getIdObjectType(), ptByObjectType);
                }
            }
        }
        return visibleAttributes;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private long updateQuery(Query q) throws QueryDataException, QueryBusinesException{
        QueryEntity qe = manager.find(QueryEntity.class, q.getId());
        if(qe == null){
            String message = "Cannot create query '" + q.getName() + "'. ";
            throw new QueryBusinesException(ErrorList.BUSINES_REQUEST_IS_NULL, new Object[] { message });
        }
        q.setQueryStatus(QueryStatus.Saved);
        // remove old values
        removeQueryParameters(qe);
        // filled qe by new params
        qe = createEntity(q, qe);
        qe.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        manager.merge(qe);
        return qe.getId();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void remove(long queryId) throws QueryDataException, QueryBusinesException{
        QueryEntity qe = manager.find(QueryEntity.class, queryId);
        MessageObject messageObject = new MessageObject();
        messageObject.setCode(MessageCode.REMOVE_QUERY);
        messageObject.setQueryID(queryId);
        MessageSender.getInstance().sendMessage(messageObject);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeQuery(long queryId) throws QueryDataException, QueryBusinesException{
        QueryEntity qe = manager.find(QueryEntity.class, queryId);
        int cnt = 0;
        while (qe.getChilds().size() != 0 && cnt < 100){
            manager.refresh(qe);
        }
        if (cnt == 100) throw  new QueryDataException("Couldn't remove query, exist childs for Query: "+queryId);
        manager.remove(qe);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public QueryStatus execute(long queryId, Locale clientLocale) throws QueryDataException, QueryBusinesException{
        long qId = executeQuery(queryId, clientLocale);
        return QueryStatus.Executing;
    }

    private long executeQuery(long queryId, Locale clientLocale) throws QueryBusinesException{
        QueryEntity qe = manager.find(QueryEntity.class, queryId);
        // try to instantiate executor
        qe.setQueryStatus(QueryStatus.Executing.getValue());
        qe.getLob().setLayout(null);
        qe.getLob().setAnnotation(null);
        qe.setStartedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        setQueryStatus(qe, QueryStatus.Executing);
        manager.merge(qe);
        manager.flush();
        return qe.getId();
    }

    private void removeQueryParameters(QueryEntity qe){
        Collection<ParameterValueEntity> params = qe.getParameterValues();
        qe.setParameterValues(new ArrayList<ParameterValueEntity>());
        manager.merge(qe);
        for(ParameterValueEntity pv : params)
            manager.remove(pv);
    }

    private void setQueryStatus(QueryEntity qe, QueryStatus status){
      updateStatusBy(qe, status, qe.getId());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setQueryStatus(Long qId, QueryStatus status){
        QueryEntity qe = manager.find(QueryEntity.class, qId);
        updateStatusBy(qe, status, qId);
    }

  private void updateStatusBy(QueryEntity qe, QueryStatus status, Long qId) {
    qe.setQueryStatus(status.getValue());
    if(status == QueryStatus.Saved || status == QueryStatus.NotSaved || status == QueryStatus.Executing)
        qe.setPercent(0F);
    if(status == QueryStatus.Ready || status == QueryStatus.Error || status == QueryStatus.Limited){
        Long finishTime = Calendar.getInstance().getTimeInMillis();
        qe.setCompletedDate(new Timestamp(finishTime));
        if(status == QueryStatus.Error)
            qe.setPercent(0F);
        else
            qe.setPercent(100F);
        updateEvent(qId, finishTime);
    }else
        qe.setCompletedDate(null);
  }

  private void updateEvent(long qid, Long finishTime){
      StringBuffer strQuery = new StringBuffer("select l from EventLog l");
      strQuery.append(" where l.params=:id and l.code=22 order by l.eventTime desc");
      javax.persistence.Query query = manager.createQuery(strQuery.toString());
      query.setParameter("id", "" + qid);
      List<EventLog> events = query.getResultList();
      if(events.size() != 0){
          EventLog event = (EventLog) events.get(0);
          Long startTime = event.getEventTime();
          event.setEventDurationTime(finishTime - startTime);
          manager.merge(event);
      }
  }
    protected QueryEntity createEntity(Query q, QueryEntity res) throws QueryDataException{
        res.setId(q.getId());
        res.setName(q.getName());
        res.setDescription(q.getDescription());
        res.setCreatedDate(null);
        res.setCompletedDate(null);
        res.setNetworkName(netName);
        Long userID = LanUsers.getUserId(_ctx.getCallerPrincipal().getName());
        res.setOwnerId(userID);
        res.setQueryStatus(q.getQueryStatus().getValue());
        res.setQueryType(manager.find(QueryTypeEntity.class, q.getQueryType().getId()));
        res.setLimited(0);
        if (q.getParent() != null) {
            QueryEntity parentQuery = manager.find(QueryEntity.class, q.getParent());
            res.setParent(parentQuery);
        }
        ArrayList<ParameterValueEntity> vals = new ArrayList<ParameterValueEntity>();
        ParameterValueEntity pv;
        for(QueryParameterType param : q.getParameters().keySet()){
            QueryParameterDataType qpdt = q.getQueryType().getParameterDataType(param);
            if(qpdt != null)
                switch (qpdt) {
                    case String:
                        pv = new ParameterValueEntity();
                        pv.setParameterTypeId(param.getValue());
                        pv.setParameterValue((String) q.getParameter(param));
                        vals.add(pv);
                        break;
                    case Array:
                        List<String> list = (List<String>) q.getParameter(param);
                        if(list != null)
                            for(String s : list){
                                pv = new ParameterValueEntity();
                                pv.setParameterTypeId(param.getValue());
                                pv.setParameterValue(s);
                                vals.add(pv);
                            }
                }
        }
        res.setParameterValues(vals);
// disable set all parameters visible for link
        List<PropertyTypeView> ptViews = metaDataService.getListPropertyTypeView();
        HashMap<Long, List<PropertyType>> visibleAttributes = q.getViewedAttributes();
        if (visibleAttributes != null){
            for(Long objectTypeId : visibleAttributes.keySet()){
                //todo hardcode
                //if (objectTypeId != 1) continue;
                List<PropertyType> propertyTypes = visibleAttributes.get(objectTypeId);
                for (PropertyTypeView propertyTypeView : ptViews) {
                    for (PropertyType propertyType : propertyTypes) {
                        if (propertyType != null)
                            if ((long) propertyTypeView.getObjectType().getIdObjectType() == objectTypeId
                                    &&
                                    propertyTypeView.getPropertyType().getPropertyTypeId() == propertyType.getPropertyTypeId()) {
                                res.getPropertyViews().add(propertyTypeView);
                            }
                    }
                }
            }
        }
/**/
        return res;
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setLimited(Long qId, int limited) throws QueryDataException{
        QueryEntity qe = manager.find(QueryEntity.class, qId);
        qe.setLimited(limited);
        manager.merge(qe);
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeQueryResultLob(Long qId) throws QueryDataException{
        QueryEntity qe = manager.find(QueryEntity.class, qId);
        qe.getLob().setLayout(null);
        qe.getLob().setAnnotation(null);
        manager.merge(qe);
    }
    public void setOracleSessionId(Long queryId, Long sessionId){
        QueryEntity qe = manager.find(QueryEntity.class, queryId);
        qe.setSessionId(sessionId);
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeFromGraph(long queryId, List<Integer> vertices, List<Integer> edges) throws QueryDataException{
        MessageObject messageObject = new MessageObject();
        messageObject.setCode(MessageCode.REMOVE_FROM_GRAPH);
        messageObject.setQueryID(queryId);
        messageObject.addParameter(MessagePropertyName.VERTICLES, vertices);
        messageObject.addParameter(MessagePropertyName.EDGES, edges);
        MessageSender.getInstance().sendMessage(messageObject);
    }
}

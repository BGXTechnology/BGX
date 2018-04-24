package net.bgx.bgxnetwork.query.ejb;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.bgx.bgxnetwork.persistence.query.GraphAnnotation;
import net.bgx.bgxnetwork.persistence.query.LayoutCoordinates;
import net.bgx.bgxnetwork.persistence.query.ParameterTypeEntity;
import net.bgx.bgxnetwork.persistence.query.ParameterValueEntity;
import net.bgx.bgxnetwork.persistence.query.QueryEntity;
import net.bgx.bgxnetwork.persistence.query.QueryTypeEntity;
import net.bgx.bgxnetwork.persistence.metadata.PropertyTypeView;
import net.bgx.bgxnetwork.persistence.metadata.ObjectType;
import net.bgx.bgxnetwork.persistence.metadata.PropertyType;
import net.bgx.bgxnetwork.query.interfaces.ExecuteServiceRemote;
import net.bgx.bgxnetwork.query.interfaces.QueryServiceImplLocal;
import net.bgx.bgxnetwork.system.SystemSetting;
import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.transfer.query.QueryData;
import net.bgx.bgxnetwork.transfer.query.QueryParameterDataType;
import net.bgx.bgxnetwork.transfer.query.QueryParameterType;
import net.bgx.bgxnetwork.transfer.query.QueryStatus;
import net.bgx.bgxnetwork.transfer.query.QueryType;
import net.bgx.bgxnetwork.transfer.tt.TDPair;
import net.bgx.bgxnetwork.transfer.tt.TimedDiagrammDataSnapshot;
import net.bgx.bgxnetwork.transfer.tt.TransferControlObjectPair;
import net.bgx.bgxnetwork.exception.query.QueryDataException;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;
import net.bgx.bgxnetwork.exception.query.ErrorList;
import net.bgx.bgxnetwork.exception.query.QueryEJBException;
import org.jboss.annotation.security.SecurityDomain;
import org.jboss.resource.adapter.jdbc.WrappedCallableStatement;
import org.jboss.util.Base64;
import oracle.jdbc.driver.OracleTypes;
import oracle.jdbc.driver.OracleCallableStatement;

/**
 * Class QueryServiceBean
 *
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
@Stateless
@SecurityDomain(SystemSetting.SECURITY_DOMAIN)
public class QueryServiceImplBean implements QueryServiceImplLocal {
    private static Logger log = Logger.getLogger(QueryServiceImplBean.class.getName());
    public static final String netName = "pln";
    @PersistenceContext(unitName = "business_manager")
    private EntityManager manager;
    @EJB
    ExecuteServiceRemote executeService;
    @Resource(mappedName = "java:/bgxnetworkDS_CORE")
    private javax.sql.DataSource ds;

    @RolesAllowed( { "LVSystem" })
    public List<QueryType> getQueryTypeList() throws QueryDataException, QueryBusinesException {
        List<QueryTypeEntity> res = null;
        try{
            javax.persistence.Query query = manager.createQuery("from QueryTypeEntity qte");
            res = query.getResultList();
        }catch (Exception e){
            throwExecuteException(e);
        }
        List<QueryType> out = new ArrayList<QueryType>();
        QueryType qt;
        Boolean collection, optional;
        QueryParameterDataType dt;
        QueryParameterType pt;
        for(QueryTypeEntity entity : res){
            qt = new QueryType();
            qt.setId(entity.getId());
            qt.setName(entity.getName());
            qt.setDescription(entity.getDescription());
            qt.setDialogClassName(entity.getDialogClassName());
            qt.setRoleName(entity.getRoleName());
            for(ParameterTypeEntity param : entity.getParameters()){
                if(param.getParameterId() == null || (pt = QueryParameterType.getByValue(param.getParameterId())) == null){
                    String message = "Parameter type id is invalid or missed.";
                    throw new QueryBusinesException(ErrorList.BUSINES_PARAMETERS_NOT_SET, new Object[] { message });
                }
                collection = param.getCollection();
                if(collection != null && collection)
                    dt = QueryParameterDataType.Array;
                else
                    dt = QueryParameterDataType.String;
                optional = param.getOptional();
                qt.addParameter(pt, dt, (optional != null && optional));
            }
            out.add(qt);
        }
        return out;
    }

    @RolesAllowed( { "LVSystem" })
    public List<Query> getQueryList(Long accountId) throws QueryBusinesException, QueryDataException{
        List<QueryEntity> res = null;
        try{
            javax.persistence.Query query =
                    manager.createQuery("from QueryEntity qe where ownerId=" + accountId+ " and qe.parent is null order by name");
            res = query.getResultList();
        }catch (Exception e){
            throwExecuteException(e);
        }
        List<QueryType> types = getQueryTypeList();
        List<Query> out = new ArrayList<Query>();
        Query query;

        for(QueryEntity qe : res){
            if (qe.getParent() == null){
                query = createTransfer(qe, types);
                addChilds(query, qe);
                out.add(query);
            }
        }

        return out;
    }

    public List<Query> getQueryListByUser(Long accountId) throws QueryBusinesException, QueryDataException{
        List<QueryEntity> res = null;
        try{
            javax.persistence.Query query =
                    manager.createQuery("from QueryEntity qe where ownerId=" + accountId+ " order by name");
            res = query.getResultList();
        }catch (Exception e){
            throwExecuteException(e);
        }
        List<QueryType> types = getQueryTypeList();
        List<Query> out = new ArrayList<Query>();
        Query query;

        for(QueryEntity qe : res){
            query = createTransfer(qe, types);
            out.add(query);
        }
        return out;
    }

    private void addChilds(Query parentQuery, QueryEntity parentQueryEntity) throws QueryBusinesException, QueryDataException {
        List<QueryType> types = getQueryTypeList();
        for (QueryEntity childEntity : parentQueryEntity.getChilds()){
            Query childQuery = createTransfer(childEntity, types);
            parentQuery.addChild(childQuery);
            addChilds(childQuery, childEntity);
        }
    }

    public static Query createTransfer(QueryEntity qe, List<QueryType> types) throws QueryBusinesException{
        Query q;
        QueryTypeEntity qte;
        QueryParameterType pt = null;
        Integer pti;
        q = new Query();
        q.setId(qe.getId());
        q.setName(qe.getName());
        qte = qe.getQueryType();
        for(QueryType qt : types)
            if(qt.getId() == qte.getId())
                q.setQueryType(qt);
        if(q.getQueryType() == null){
            log.warning("Query " + qe.getName() + " is of unknown type.");
            return null;
        }
        q.setDescription(qe.getDescription());
        q.setCreatedDate(qe.getCreatedDate());
        q.setCompletedDate(qe.getCompletedDate());
        q.setStartedDate(qe.getStartedDate());
        q.setNetworkName(qe.getNetworkName());

        if (qe.getParent() == null)
            q.setParent(null);
        else q.setParent(qe.getParent().getId());

        if(qe.getPercent() == null)
            q.setPercent(0);
        else
            q.setPercent(qe.getPercent());
        if(qe.getLimited() != null)
            q.setObjectsLimit(qe.getLimited());
        try{
            QueryStatus qs = QueryStatus.getByValue(qe.getQueryStatus());
            if(qs != null)
                q.setQueryStatus(qs);
        }catch (Exception e){
        }
        for(ParameterValueEntity param : qe.getParameterValues()){
            pti = param.getParameterTypeId();
            if(pti == null || (pt = QueryParameterType.getByValue(pti)) == null){
                String message = "Unknown parameter type " + pti;
                log.severe(message);
                throw new QueryBusinesException(ErrorList.BUSINES_PARAMETERS_NOT_SET, new Object[] { message });
            }
            switch (q.getQueryType().getParameterDataType(pt)) {
                case String:
                    q.addParameter(pt, param.getParameterValue());
                    break;
                case Array:
                    List<String> list = (List<String>) q.getParameter(pt);
                    if(list == null){
                        list = new ArrayList<String>();
                        q.addParameter(pt, list);
                    }
                    list.add(param.getParameterValue());
            }
        }
        //set Visible PropertyType
        for(PropertyTypeView ptv : qe.getPropertyViews()){
            if (ptv != null){
                ObjectType objectType = ptv.getObjectType();
                PropertyType propertyType = ptv.getPropertyType();
                if (objectType != null && propertyType != null){
                    q.addViewAttribute((long)objectType.getIdObjectType(), propertyType);
                }
            }
        }

        return q;
    }

    @RolesAllowed( { "LVSystem" })
    public QueryData getQueryData(long queryId, Locale clientLocale) throws QueryDataException, QueryBusinesException{
        QueryEntity qe = findQueryEntityByID(queryId);
        // try to instantiate executor
        IExecutor executor = null;
        try{
            executor = (IExecutor) Class.forName(qe.getQueryType().getExecutorClassName()).newInstance();
            executor.setDataSource(ds);
            executor.setEntityManager(manager);
            executor.setClientLocale(clientLocale);
        }catch (Exception e){
            throwInstantientExcecutorException("Cannot instantiate executor '" + qe.getQueryType().getExecutorClassName() + "' for query "
                    + queryId);
        }
        QueryData data = executor.readQueryData(qe);
        executor.doClose();
        return data;
    }

    @RolesAllowed( { "LVSystem" })
    public QueryData getNodeList(long queryId, int vxId, Locale clientLocale) throws QueryDataException, QueryBusinesException{
        QueryEntity qe = findQueryEntityByID(queryId);
        return executeService.getNodeList(qe, vxId, clientLocale);
    }

    @RolesAllowed( { "LVSystem" })
    public boolean saveLayout(long queryId, LayoutCoordinates layout) throws QueryDataException{
        QueryEntity qe = findQueryEntityByID(queryId);
        try{
            if(qe == null)
                return false;
            qe.getLob().setLayout(layout);
            manager.merge(qe);
        }catch (EJBException e){
            throw new QueryEJBException(ErrorList.EJB_CANNOT_UPDATE_DATA);
        }
        return true;
    }
    @RolesAllowed( { "LVSystem" })
    public LayoutCoordinates getLayout(long queryId) throws QueryDataException{
        QueryEntity qe = findQueryEntityByID(queryId);
        if(qe == null)
            return null;
        return qe.getLob().getLayout();
    }
    @RolesAllowed( { "LVSystem" })
    public boolean saveAnnotation(long queryId, GraphAnnotation annotation){
        log.warning("Not implemented.");
        return false;
    }
    @RolesAllowed( { "LVSystem" })
    public GraphAnnotation getAnnotation(long queryId){
        log.warning("Not implemented.");
        return null;
    }
    @RolesAllowed( { "LVSystem" })
    public Query getQueryById(long queryId){
        //for audit module (InfoAspect.java)
        QueryEntity qe = null;
        try{
            qe = findQueryEntityByID(queryId);
        }catch (QueryDataException ex){
            ex.printStackTrace();
        }
        if(qe == null)
            return null;
        try{
            List<QueryType> types = getQueryTypeList();
            return createTransfer(qe, types);
        }catch (Exception e){
            return null;
        }
    }

    @RolesAllowed( { "LVSystem" })
    public String getExecutorNameByQuery(long queryId){
        QueryEntity qe = null;
        try{
            qe = findQueryEntityByID(queryId);
            return qe.getQueryType().getExecutorClassName();
        }
        catch (QueryDataException ex){
            ex.printStackTrace();
            return null;
        }
    }

    private QueryEntity findQueryEntityByID(Long id) throws QueryDataException{
        QueryEntity qe = null;
        try{
            qe = manager.find(QueryEntity.class, id);
        }catch (Exception e){
            throwExecuteException(e);
        }
        return qe;
    }

    private void throwInstantientExcecutorException(String message) throws QueryBusinesException{
        log.severe(message);
        throw new QueryBusinesException(ErrorList.BUSINES_CANNOT_INSTANTIATE_EXECUTOR, new Object[] { message });
    }
    private void throwExecuteException(Exception e) throws QueryDataException{
        if(e instanceof IllegalArgumentException){
            throw new QueryDataException(ErrorList.DATA_ILLEGAL_ARGUMENT_EXCEPTION);
        }else{
            throw new QueryDataException(ErrorList.DATA_CANNOT_FIND_DATA);
        }
    }

    @RolesAllowed( { "LVSystem" })
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<String> getInnDictionary() throws QueryBusinesException, QueryDataException{
        Connection con = null;
        CallableStatement cstmt = null;
        ArrayList<String> innMaskList = new ArrayList<String>();
        try{
            con = ds.getConnection();
            String pctName = System.getProperty("QLDS.PACKET.SP_MAIN");
            cstmt = con.prepareCall("{call QLDS." + pctName + ".GET_INN_MASK_LIST (?)}");
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);

            cstmt.execute();
            OracleCallableStatement ostmt = (OracleCallableStatement) ((WrappedCallableStatement) cstmt).getUnderlyingStatement();
            ResultSet rs = ostmt.getCursor(1);
            while (rs.next()){
                innMaskList.add(rs.getString(1));
            }
            rs.close();
        }
        catch (SQLException e){
            throw new QueryDataException(ErrorList.DATA_CONNECTION_ERROR);
        }
        finally{
            try{
                if(cstmt != null){
                    cstmt.close();
                }
            }catch (SQLException ex){
                System.out.println(ex.toString());
            }
            try {
                if(con != null && !con.isClosed()){
                  con.close();
                }
            }
            catch (SQLException e) {
                System.out.println(""+e);
            }
        }
        return innMaskList;
    }

    @RolesAllowed( { "LVSystem" })
    public boolean saveTTPairs(long queryId, LinkedList<TDPair> pairs) throws QueryDataException{
        QueryEntity qe = findQueryEntityByID(queryId);
        try{
            if(qe == null)
                return false;
            LinkedList<Object> obj = new LinkedList<Object>();
            for (TDPair pair : pairs){
                obj.add(pair);
            }
            qe.getLob().setTimetablePairs(obj);
            manager.merge(qe);
        }catch (EJBException e){
            throw new QueryEJBException(ErrorList.EJB_CANNOT_UPDATE_DATA);
        }
        return true;
    }
    @RolesAllowed( { "LVSystem" })
    public LinkedList<TDPair> getTTPairs(long queryId) throws QueryDataException{
        QueryEntity qe = findQueryEntityByID(queryId);
        if(qe == null)
            return null;
        LinkedList<Object> objs = qe.getLob().getTimetablePairs();
        LinkedList<TDPair> ret = new LinkedList<TDPair>();
        if (objs != null)
            for (Object obj : objs)
                ret.add((TDPair)obj);
        return ret;
    }

//    public boolean saveTTParameters(long queryId, Object data) throws QueryDataException {
//        QueryEntity qe = findQueryEntityByID(queryId);
//        try{
//            if(qe == null)
//                return false;
//
//            qe.getLob().setTimetableParameters((TimedDiagrammDataSnapshot)data);
//            manager.merge(qe);
//        }catch (EJBException e){
//            throw new QueryEJBException(ErrorList.EJB_CANNOT_UPDATE_DATA);
//        }
//        return true;
//    }

    @RolesAllowed( { "LVSystem" })
    public boolean saveTTParameters(long queryId, TimedDiagrammDataSnapshot data) throws QueryDataException {
        QueryEntity qe = findQueryEntityByID(queryId);
        try{
            if(qe == null)
                return false;
            ArrayList<Object> objs = new ArrayList<Object>();
            objs.add(data);
            qe.getLob().setTimetableParameters(objs);
            manager.merge(qe);
        }catch (EJBException e){
            throw new QueryEJBException(ErrorList.EJB_CANNOT_UPDATE_DATA);
        }
        return true;
    }

    @RolesAllowed( { "LVSystem" })
    public TimedDiagrammDataSnapshot getTTParameters(long queryId) throws QueryDataException {
        QueryEntity qe = findQueryEntityByID(queryId);
        if(qe == null)
            return null;
        ArrayList<Object> objs = qe.getLob().getTimetableParameters();
        if (objs.size() >0){
            TimedDiagrammDataSnapshot ret = (TimedDiagrammDataSnapshot)objs.get(0);
            return ret;
        }    
        return null;
    }
}

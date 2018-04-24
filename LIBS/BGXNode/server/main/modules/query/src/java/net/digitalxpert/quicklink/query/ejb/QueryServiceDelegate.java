package net.bgx.bgxnetwork.query.ejb;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.bgx.bgxnetwork.persistence.query.GraphAnnotation;
import net.bgx.bgxnetwork.persistence.query.LayoutCoordinates;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;
import net.bgx.bgxnetwork.query.interfaces.QueryServiceLocal;
import net.bgx.bgxnetwork.query.interfaces.QueryServiceRemote;
import net.bgx.bgxnetwork.system.SystemSetting;
import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.transfer.tt.TDPair;
import net.bgx.bgxnetwork.transfer.tt.TimedDiagrammDataSnapshot;
import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;

@Stateless
@SecurityDomain(SystemSetting.SECURITY_DOMAIN)
@PermitAll
@RemoteBinding(clientBindUrl = SystemSetting.QL_CLIENT_BIND_URL)
public class QueryServiceDelegate implements QueryServiceRemote{
    @EJB
    private QueryServiceLocal queryService;
    public Object create(Query q){
        try{
            return queryService.create(q);
        }catch (Exception ex){
            return ex;
        }
    }
    public Object createAndExecute(Query q, Locale clientLocale){
        try{
            return queryService.createAndExecute(q, clientLocale);
        }catch (Exception ex){
            return ex;
        }
    }
    public Object execute(long queryId, Locale clientLocale){
        try{
            return queryService.execute(queryId, clientLocale);
        }catch (Exception ex){
            return ex;
        }
    }
    public Object getAnnotation(long queryId){
        try{
            return queryService.getAnnotation(queryId);
        }catch (Exception ex){
            return ex;
        }
    }
    public Object getLayout(long queryId){
        try{
            return queryService.getLayout(queryId);
        }catch (Exception ex){
            return ex;
        }
    }
    public Object getQueryData(long queryId, Locale clientLocale){
        try{
            return queryService.getQueryData(queryId, clientLocale);
        }catch (Exception ex){
            return ex;
        }
    }
    public Object getQueryList(){
        try{
            return queryService.getQueryList();
        }catch (Exception ex){
            return ex;
        }
    }
    public Object getQueryTypeList(){
        try{
            return queryService.getQueryTypeList();
        }catch (Exception ex){
            return ex;
        }
    }
    public Object remove(long queryId){
        try{
            queryService.remove(queryId);
        }catch (Exception ex){
            return ex;
        }
        return null;
    }
    public Object removeFromGraph(long queryId, List<Integer> vertices, List<Integer> edges){
        try{
            queryService.removeFromGraph(queryId, vertices, edges);
        }catch (Exception ex){
            return ex;
        }
        return null;
    }

    public Object saveAnnotation(long queryId, GraphAnnotation annotation){
        try{
            return queryService.saveAnnotation(queryId, annotation);
        }catch (Exception ex){
            return ex;
        }
    }
    public Object saveLayout(long queryId, LayoutCoordinates layout){
        try{
            return queryService.saveLayout(queryId, layout);
        }catch (Exception ex){
            return ex;
        }
    }
    public Object update(Query q){
        try{
            queryService.update(q);
        }catch (Exception ex){
            return ex;
        }
        return null;
    }

    public Object updateName(Query q) {
        try{
            queryService.updateName(q);
        }catch (Exception ex){
            return ex;
        }
        return null;
    }

    public Object updateTTParams(Long qId, String data) {
        try{
            queryService.updateTTParameters(qId, data);
        }catch (Exception ex){
            return ex;
        }
        return null;
    }

    public Object setVisibleAttributes(Long qId, List<Long> codes) {
        try{
            return queryService.setVisibleAttributes(qId, codes);
        }catch (Exception ex){
            return ex;
        }
    }

    public Object updateAndExecute(Query q, Locale clientLocale){
        try{
            return queryService.updateAndExecute(q, clientLocale);
        }catch (Exception ex){
            return ex;
        }
    }

    public Object getInnDictionary(){
      try{
        return queryService.getInnDictionary();
      }
      catch(Exception e){
        return e;
      }
    }

    public Object copyQueryFromExist(Query query, Locale clientLocale ) {
        try{
            return queryService.copyQueryFromExist(query, clientLocale);
        }
        catch (Exception ex){
            return ex;
        }
    }

    public Object updateControlObjects(ArrayList<ControlObject> objects) {
        try{
            queryService.updateControlObjects(objects);
        }catch (Exception ex){
            return ex;
        }
        return null;
    }

    public Object updateLinkObjects(ArrayList<LinkObject> objects) {
        try{
            queryService.updateLinkObjects(objects);
        }catch (Exception ex){
            return ex;
        }
        return null;
    }

    public Object saveTTPairs(long queryId, LinkedList<TDPair> pairs) {
        try{
            return queryService.saveTTPairs(queryId, pairs);
        }catch (Exception ex){
            return ex;
        }
    }

    public Object getTTPairs(long queryId) {
        try{
            return queryService.getTTPairs(queryId);
        }catch (Exception ex){
            return ex;
        }
    }

    public Object saveTTParameters(long queryId, TimedDiagrammDataSnapshot data){
        try{
            boolean res = queryService.saveTTParameters(queryId, data);
            return res;
        }catch (Exception ex){
            return ex;
        }
    }

    public Object getTTParameters(long queryId) {
        try{
            return queryService.getTTParameters(queryId);
        }catch (Exception ex){
            return ex;
        }
    }

}

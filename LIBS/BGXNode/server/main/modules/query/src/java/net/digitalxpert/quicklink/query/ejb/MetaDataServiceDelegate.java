package net.bgx.bgxnetwork.query.ejb;

import org.jboss.annotation.security.SecurityDomain;
import org.jboss.annotation.ejb.RemoteBinding;

import javax.ejb.Stateless;
import javax.ejb.EJB;
import javax.annotation.security.PermitAll;

import net.bgx.bgxnetwork.system.SystemSetting;
import net.bgx.bgxnetwork.query.interfaces.MetaDataServiceRemote;
import net.bgx.bgxnetwork.query.interfaces.MetaDataServiceLocal;
import net.bgx.bgxnetwork.persistence.metadata.*;
import net.bgx.bgxnetwork.exception.metadata.MetaDataBusinessException;

import java.util.List;
import java.util.ArrayList;

/**
 * User: O.Gerasimenko
 * Date: 13.02.2007
 * Time: 17:47:10
 * To change this template use File | Settings | File Templates.
 */
@Stateless
@SecurityDomain(SystemSetting.SECURITY_DOMAIN)
@PermitAll
@RemoteBinding(clientBindUrl = SystemSetting.QL_CLIENT_BIND_URL)
public class MetaDataServiceDelegate implements MetaDataServiceRemote {
        @EJB
        private MetaDataServiceLocal metaDataService;

        public Object getObjectTypeList() {
        try{
            return metaDataService.getObjectTypeList();
        }catch (Exception ex){
            return ex;
        }
    }

    public Object getLabelList() {
         try{
            return metaDataService.getLabelList();
        }catch (Exception ex){
            return ex;
        }
    }

    public Object getRequestListByUser(){
        try{

            return metaDataService.getRequestListByUser();
        }
        catch(Exception e){
            return e;
        }
    }

    public Object getQuestListByRequest(Long id) {
        try{
            return metaDataService.getQuestListByRequest(id);
        }
        catch(Exception e){
            return e;
        }
    }

    public Object getVisiblePropertyByObjectType(Long id){
        try{
            List<PropertyType> propertyTypes = new ArrayList<PropertyType>();
            for(PropertyTypeView propertyTypeView : metaDataService.getListPropertyTypeView()){
                if (propertyTypeView != null){
                    if ((long)propertyTypeView.getObjectType().getIdObjectType() == id)
                        propertyTypes.add(propertyTypeView.getPropertyType());
                }
            }
            return propertyTypes;
        }
        catch(Exception e){
            return e;
        }    
    }

    public Object getObjectAttributeOverview(){
        try{
            return getAttributeOverview("V_OBJ");
        }
        catch(Exception e){
            return e;
        }
    }

    public Object getLinkAttributeOverview(){
        try{
            return getAttributeOverview("V_CONN");
        }
        catch(Exception e){
            return e;
        }
    }

    private Object getAttributeOverview(String tn){
        return metaDataService.getObjectAttributeOverview(tn);
    }
}
